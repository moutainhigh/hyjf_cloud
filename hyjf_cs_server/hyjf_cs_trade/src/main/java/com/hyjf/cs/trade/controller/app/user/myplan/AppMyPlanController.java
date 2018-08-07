package com.hyjf.cs.trade.controller.app.user.myplan;

import com.hyjf.am.resquest.trade.AssetManageBeanRequest;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.assetmanage.*;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.trade.bean.app.MyPlanDetailResultBean;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.myplan.AppMyPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author pangchengchao
 * @version WechatMyAssetController, v0.1 2018/7/24 12:02
 */

@Api(value = "app端用户我的计划接口",tags = "app端用户我的计划接口")
@Controller
@RequestMapping("/hyjf-app/user/plan")
public class AppMyPlanController extends BaseTradeController {
    private final String ILLEGAL_PARAMETER_STATUS_DESC = "请求参数非法";
    private final String TOKEN_ISINVALID_STATUS = "Token失效，请重新登录";
    /** 计息时间 */
    private final String PLAN_ON_ACCRUAL = "计划进入锁定期后开始计息";

    private static DecimalFormat DF_FOR_VIEW = new DecimalFormat("#,##0.00");
    @Autowired
    private AppMyPlanService appMyPlanService;
    /**
     * 微信端获取首页散标列表
     * @date 2018/7/2 16:27
     */
    @ApiOperation(value = "App端:获取我的散标信息", notes = "App端:获取我的散标信息")
    @PostMapping(value = "/getMyPlanList", produces = "application/json; charset=utf-8")
    public MyPlanListResultBean getMyPlanList( HttpServletRequest request,@RequestHeader(value = "token", required = false) String token) {
        MyPlanListResultBean result = new MyPlanListResultBean();
        result.setStatus(CustomConstants.APP_STATUS_SUCCESS);      result.setStatusDesc(CustomConstants.APP_STATUS_DESC_SUCCESS);

        // 计划的状态：1为持有中，2为已退出
        String type = request.getParameter("type");
        String sign = request.getParameter("sign");

        // 检查参数正确性
        if (Validator.isNull(sign) || Validator.isNull(type) || !Arrays.asList("1", "2").contains(type)) {
            result.setStatus(CustomConstants.APP_STATUS_FAIL);
            result.setStatusDesc(ILLEGAL_PARAMETER_STATUS_DESC);
            return result;
        }
        Integer userId = null;
        try {
            WebViewUserVO webViewUserVO = RedisUtils.getObj(RedisConstants.USER_TOKEN_REDIS + token, WebViewUserVO.class);
            userId = webViewUserVO.getUserId();
        } catch (Exception e) { // token失效
            result.setStatus(CustomConstants.APP_STATUS_FAIL);
            result.setStatusDesc(TOKEN_ISINVALID_STATUS);
            return result;
        }
        if (userId == null) {
            result.setStatus(CustomConstants.APP_STATUS_FAIL);
            result.setStatusDesc(TOKEN_ISINVALID_STATUS);
            return result;
        }
        logger.info("request params: type is: {}, userId is: {}", type, userId);

        // 构建查询条件
        AssetManageBeanRequest params = buildQueryParameter(request);
        params.setUserId(userId+"");
        params.setType(type);
        AccountVO accountVO=appMyPlanService.getAccountByUserId(userId);
        // 待收金额
        result.setMoney(DF_FOR_VIEW.format(accountVO.getPlanAccountWait()));

        // 查询我的汇计划总数
        Integer count = appMyPlanService.countAppMyPlan(params);
        if (count != null && count > 0) {
            result.setProjectTotal(count);
            List<AppMyPlanCustomizeVO> projectList = appMyPlanService.selectAppMyPlanList(params);
            result.setProjectList(convertAppMyPlanToReturnBean(projectList, request));

        } else {
            result.setProjectTotal(0);
            result.setProjectList(new ArrayList<MyPlanListResultBean.ProjectList>());
        }

        result.setType(type);
        return result;
    }
    /**
     * 计划列表响应
     *
     * @param customizeProjectList
     * @return
     */
    private List<MyPlanListResultBean.ProjectList> convertAppMyPlanToReturnBean(
            List<AppMyPlanCustomizeVO> customizeProjectList, HttpServletRequest request) {
        List<MyPlanListResultBean.ProjectList> projectList = new ArrayList<>();
        MyPlanListResultBean.ProjectList project;

        if (CollectionUtils.isEmpty(customizeProjectList)) {
            return projectList;
        }

        for (AppMyPlanCustomizeVO entity : customizeProjectList) {
            project = new MyPlanListResultBean.ProjectList();
            BeanUtils.copyProperties(entity, project);
            project.setBorrowTheFirst(DF_FOR_VIEW.format(new BigDecimal(entity.getAccedeAmount())));
            project.setBorrowTheFirstDesc("加入金额");
            project.setBorrowTheSecond(entity.getLockPeriod());
            project.setBorrowTheSecondDesc("锁定期限");

            String label = entity.getLabel();
            switch (label) {
                case "1":
                    project.setLabel("体验金");
                    break;
                case "2":
                    project.setLabel("加息券");
                    break;
                case "3":
                    project.setLabel("代金券");
                    break;
                default:
                    project.setLabel("");
            }

            if ("1".equals(project.getType())) {
                project.setBorrowTheThirdDesc("加入时间");
                project.setBorrowTheThird(entity.getCreateTime());
            } else {
                // mod 汇计划二期前端优化 已退出的计划将回款时间改为退出时间  nxl 20180426 start
                project.setBorrowTheThirdDesc("退出时间");
                // mod 汇计划二期前端优化 已退出的计划将回款时间改为退出时间  nxl 20180426 end
                project.setBorrowTheThird(entity.getRecoverTime());
            }

            // 项目详情url
            String hostUrl = "" + "/" + entity.getOrderId() + "?type="
                    + entity.getType() + "&couponType=" + label;
            // project.setBorrowUrl(CommonUtils.concatReturnUrl(request,
            // hostUrl));
            project.setBorrowUrl(hostUrl);
            projectList.add(project);
        }
        return projectList;
    }
    /**
     * 构造查询参数map
     * @param request
     * @return
     */
    private AssetManageBeanRequest buildQueryParameter(HttpServletRequest request) {
        AssetManageBeanRequest params = new AssetManageBeanRequest();
        Integer page = Integer.parseInt(request.getParameter("page"));
        Integer pageSize = Integer.parseInt(request.getParameter("pageSize"));
        params.setLimitStart((page - 1) * pageSize);
        params.setLimitEnd(pageSize);
        return params;
    }


    /**
     * App端:获取我的散标详情
     * @author zhangyk
     * @date 2018/7/30 18:27
     * 原接口：com.hyjf.app.user.credit.AppTenderCreditBorrowController.searchTenderCreditDetail()
     */
    @ApiOperation(value = "App端:获取我的计划详情", notes = "App端:获取我的计划详情")
    @PostMapping(value = "/{orderId}", produces = "application/json; charset=utf-8")
    public MyPlanDetailResultBean getMyPlanList(@RequestParam Integer couponType, @RequestParam String type, @PathVariable String orderId, HttpServletRequest request, @RequestHeader(value = "userId", required = false) String userId) {
        MyPlanDetailResultBean result = appMyPlanService.getMyPlanDetail(couponType,type,orderId,request,userId);
        return  result;
    }
}