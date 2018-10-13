package com.hyjf.cs.user.controller.web.bankopen;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.cs.common.bean.result.ApiResult;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.user.bean.OpenAccountPageBean;
import com.hyjf.cs.user.config.SystemConfig;
import com.hyjf.cs.user.constants.ErrorCodeConstant;
import com.hyjf.cs.user.controller.BaseUserController;
import com.hyjf.cs.user.service.bankopen.BankOpenService;
import com.hyjf.cs.user.vo.BankOpenVO;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 *
 * @author sunss
 *
 */
@Api(value = "web端-用户开户接口",tags = "web端-用户开户接口")
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/hyjf-web/user/secure/open")
public class WebBankOpenController extends BaseUserController {
    private static final Logger logger = LoggerFactory.getLogger(WebBankOpenController.class);

    @Autowired
    private BankOpenService bankOpenService;

    @Autowired
    SystemConfig systemConfig;

    @ApiOperation(value = "获取开户信息", notes = "获取开户信息")
    @GetMapping(value = "/init")
    @ResponseBody
    public WebResult<Object> init(@RequestHeader(value = "userId") int userId) {
        UserVO user = this.bankOpenService.getUsersById(userId);
        WebResult<Object> result = new WebResult<Object>();
        if(user==null){
            throw new CheckException(MsgEnum.ERR_USER_NOT_LOGIN);
        }
        if(user.getBankOpenAccount()==1){
            throw new CheckException(MsgEnum.ERR_BANK_ACCOUNT_ALREADY_OPEN);
        }
        result.setStatus(ApiResult.SUCCESS);
        Map<String,String> map = new HashedMap();
        map.put("mobile",user.getMobile());
        result.setData(map);
        return result;
    }

    /**
     * @Description 开户
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/12 10:17
     */
    @ApiOperation(value = "用户开户", notes = "用户开户")
    @PostMapping(value = "/openBankAccount")
    @ResponseBody
    public WebResult<Object> openBankAccount(@RequestHeader(value = "userId") int userId, @RequestBody @Valid BankOpenVO bankOpenVO, HttpServletRequest request) {
        logger.info("web  openBankAccount start, bankOpenVO is :{}", JSONObject.toJSONString(bankOpenVO));
        WebResult<Object> result = new WebResult<Object>();
        UserVO user = this.bankOpenService.getUsersById(userId);
        // 检查请求参数
        bankOpenService.checkRequestParam(user, bankOpenVO);

        OpenAccountPageBean openBean = new OpenAccountPageBean();
        try {
            PropertyUtils.copyProperties(openBean, bankOpenVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        openBean.setChannel(BankCallConstant.CHANNEL_PC);
        openBean.setUserId(user.getUserId());
        openBean.setIp(CustomUtil.getIpAddr(request));
        openBean.setPlatform(ClientConstants.WEB_CLIENT+"");
        openBean.setClientHeader(ClientConstants.CLIENT_HEADER_PC);
        // 开户角色
        openBean.setIdentity(BankCallConstant.ACCOUNT_USER_IDENTITY_1);
        // 组装参数
        Map<String,Object> data = bankOpenService.getOpenAccountMV(openBean, null);
        result.setData(data);
        //保存开户日志
        int uflag = this.bankOpenService.updateUserAccountLog(user.getUserId(), user.getUsername(), openBean.getMobile(), openBean.getOrderId(),CustomConstants.CLIENT_PC ,openBean.getTrueName(),openBean.getIdNo(),"", "");
        if (uflag == 0) {
            logger.info("保存开户日志失败,手机号:[" + openBean.getMobile() + "],用户ID:[" + user.getUserId() + "]");
            throw new CheckException(MsgEnum.STATUS_CE999999);
        }
        logger.info("开户end");
        return result;
    }

    /**
     * web页面开户异步处理
     *
     * @param bean
     * @return
     */
    @ApiOperation(value = "页面开户异步处理", notes = "页面开户异步处理")
    @PostMapping("/bgReturn")
    @ResponseBody
    public BankCallResult openAccountBgReturn(@RequestBody BankCallBean bean, @RequestParam("phone") String mobile,@RequestParam("roleId")String roleId,@RequestParam("openclient")String openclient) {
        logger.info("web端开户异步处理start,userId:{}", bean.getLogUserId());
        bean.setMobile(mobile);
        bean.setLogClient(Integer.parseInt(openclient));
        bean.setIdentity(roleId);
        BankCallResult result = bankOpenService.openAccountBgReturn(bean);
        return result;
    }

    /**
     * @Description 查询开户失败原因
     * @Author sunss
     */
    @ApiOperation(value = "查询开户失败原因", notes = "查询开户失败原因")
    @PostMapping("/seachFiledMess")
    @ResponseBody
    public WebResult<Object> seachFiledMess(@RequestHeader(value = "userId") int userId,@RequestParam("logOrdId") String logOrdId) {
        logger.info("查询开户失败原因start,logOrdId:{}   userId:{}", logOrdId,userId);
        WebViewUserVO user = RedisUtils.getObj(RedisConstants.USERID_KEY + userId, WebViewUserVO.class);
        if(user!=null){
            if(user.isBankOpenAccount()&&"0".equals(user.getIsSetPassword()+"")){
                // 已经开户  未设置交易密码  开户成功，设置交易密码失败
               throw new CheckException(MsgEnum.ERR_BANK_ACCOUNT_ERROR);
            }
        }
        WebResult<Object> result = bankOpenService.getFiledMess(logOrdId);
        return result;
    }
}