/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.controller.front.user;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.BooleanResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.trade.CorpOpenAccountRecordResponse;
import com.hyjf.am.response.user.*;
import com.hyjf.am.resquest.trade.CorpOpenAccountRecordRequest;
import com.hyjf.am.resquest.user.*;
import com.hyjf.am.user.controller.BaseController;
import com.hyjf.am.user.dao.model.auto.*;
import com.hyjf.am.user.dao.model.customize.*;
import com.hyjf.am.user.service.callcenter.CallCenterBankService;
import com.hyjf.am.user.service.front.user.UserManagerService;
import com.hyjf.am.vo.trade.CorpOpenAccountRecordVO;
import com.hyjf.am.vo.user.*;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version UserMemberController, v0.1 2018/6/20 9:13
 *          后台会员中心-会员管理
 */

@RestController
@RequestMapping("/am-user/userManager")
public class UserManagerController extends BaseController {
    @Autowired
    private UserManagerService userManagerService;
    @Autowired
    private CallCenterBankService callCenterBankService;

    /**
     * 根据筛选条件查找(用户管理列表显示)
     *
     * @param request
     * @return
     */
    @RequestMapping("/userslist")
    public UserManagerResponse findUserslist(@RequestBody @Valid UserManagerRequest request) {
        logger.info("---findUserslist by param---  " + JSONObject.toJSON(request));
        UserManagerResponse response = new UserManagerResponse();
        Map<String,Object> mapParam = paramSet(request);
        int usesrCount = userManagerService.countUserRecord(mapParam);
        Paginator paginator = new Paginator(request.getCurrPage(), usesrCount,request.getPageSize());
        if(request.getPageSize()==0){
            paginator = new Paginator(request.getCurrPage(), usesrCount);
        }

        int limitStart = paginator.getOffset();
        int limitEnd =  paginator.getLimit();
        if(request.isLimitFlg()){
            limitEnd = 0;
            limitStart = 0;
        }
        response.setCount(usesrCount);
        if(usesrCount>0){
            List<UserManagerCustomize> userManagerCustomizeList = userManagerService.selectUserMemberList(mapParam,limitStart,limitEnd);
            if (!CollectionUtils.isEmpty(userManagerCustomizeList)) {
                List<UserManagerVO> userVoList = CommonUtils.convertBeanList(userManagerCustomizeList, UserManagerVO.class);
                response.setResultList(userVoList);
                response.setRtn(Response.SUCCESS);
                response.setMessage(Response.SUCCESS_MSG);
            }
        }
        return response;
    }

    /**
     * 获取列表总数
     *
     * @param request
     * @return
     */
    @RequestMapping("/countUserList")
    public UserManagerResponse countUserList(@RequestBody @Valid UserManagerRequest request) {
        logger.info("---countUserList by param---  " + JSONObject.toJSON(request));
        UserManagerResponse response = new UserManagerResponse();
        Map<String,Object> mapParam = paramSet(request);
        int usesrCount = userManagerService.countUserRecord(mapParam);
        response.setCount(usesrCount);
        return response;
    }

    /**
     * 根据用户id来获取用户详情
     *
     * @param userId
     * @return
     */
    @RequestMapping("/selectUserDetail/{userId}")
    public UserManagerDetailResponse searchUserDetail(@PathVariable String userId) {
        logger.info("---searchUserDetail by userId---  " + userId);
        UserManagerDetailResponse userManagerDetailResponse = new UserManagerDetailResponse();
        if (StringUtils.isNotEmpty(userId)) {
            int intUserId = Integer.parseInt(userId);
            UserManagerDetailCustomize userManagerDetailCustomize = userManagerService.selectUserDetailById(intUserId);
            if (null != userManagerDetailCustomize) {
                UserManagerDetailVO userManagerDetailVO = new UserManagerDetailVO();
                BeanUtils.copyProperties(userManagerDetailCustomize, userManagerDetailVO);
                userManagerDetailResponse.setResult(userManagerDetailVO);
                userManagerDetailResponse.setRtn(Response.SUCCESS);//代表成功
            }
            return userManagerDetailResponse;
        }
        return null;
    }

    /**
     * 根据用户id获取开户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/selectBankOpenAccountByUserId/{userId}")
    public UserBankOpenAccountResponse selectBankOpenAccountByUserId(@PathVariable String userId) {
        logger.info("---selectBankOpenAccountByUserId---  " + userId);
        UserBankOpenAccountResponse response = new UserBankOpenAccountResponse();
        if (StringUtils.isNotEmpty(userId)) {
            int intUserId = Integer.parseInt(userId);
            UserBankOpenAccountVO userBankOpenAccountVO = new UserBankOpenAccountVO();
            UserBankOpenAccountCustomize bankOpenAccountCustomize = userManagerService.selectBankOpenAccountByUserId(intUserId);
            if (null != bankOpenAccountCustomize) {
                BeanUtils.copyProperties(bankOpenAccountCustomize, userBankOpenAccountVO);
                response.setResult(userBankOpenAccountVO);
                response.setRtn(Response.SUCCESS);//代表成功
            }
            return response;
        }
        return null;
    }

    /**
     * 根据用户id获取企业用户开户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/selectCorpOpenAccountRecordByUserId/{userId}")
    public CorpOpenAccountRecordResponse selectCorpOpenAccountRecordByUserId(@PathVariable String userId) {
        logger.info("---selectCorpOpenAccountRecordByUserId---  " + userId);
        CorpOpenAccountRecordResponse response = new CorpOpenAccountRecordResponse();
        if (StringUtils.isNotEmpty(userId)) {
            int intUserId = Integer.parseInt(userId);
            CorpOpenAccountRecordVO corpOpenAccountRecordVO = new CorpOpenAccountRecordVO();
            CorpOpenAccountRecord corpOpenAccountRecord = userManagerService.selectCorpOpenAccountRecordByUserId(intUserId);
            if (null != corpOpenAccountRecord) {
                BeanUtils.copyProperties(corpOpenAccountRecord, corpOpenAccountRecordVO);
                response.setResult(corpOpenAccountRecordVO);
                response.setRtn(Response.SUCCESS);//代表成功
            }
            return response;
        }
        return null;
    }

    /**
     * 根据用户id获取第三方平台绑定信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/selectBindUserByUserId/{userId}")
    public BindUserResponse selectBindUserByUserId(@PathVariable String userId) {
        logger.info("---selectBindUserByUserId---  " + userId);
        BindUserResponse response = new BindUserResponse();
        if (StringUtils.isNotEmpty(userId)) {
            int intUserId = Integer.parseInt(userId);
            BindUserVo bindUserVo = new BindUserVo();
            BindUser bindUser = userManagerService.selectBindUserByUserId(intUserId);
            if (null != bindUser) {
                BeanUtils.copyProperties(bindUser, bindUserVo);
                response.setResult(bindUserVo);

            }
            return response;
        }
        return null;
    }

    /**
     * 根据用户id获取用户CA认证记录表
     *
     * @param userId
     * @return
     */
    @RequestMapping("/selectCertificateAuthorityByUserId/{userId}")
    public CertificateAuthorityResponse selectCertificateAuthorityByUserId(@PathVariable String userId) {
        logger.info("---selectCertificateAuthorityByUserId---  " + userId);
        CertificateAuthorityResponse response = new CertificateAuthorityResponse();
        if (StringUtils.isNotEmpty(userId)) {
            int intUserId = Integer.parseInt(userId);
            CertificateAuthorityVO certificateAuthorityVO = new CertificateAuthorityVO();
            CertificateAuthority certificateAuthority = userManagerService.selectCertificateAuthorityByUserId(intUserId);
            if (null != certificateAuthority) {
                BeanUtils.copyProperties(certificateAuthority, certificateAuthorityVO);
                response.setResult(certificateAuthorityVO);
                response.setRtn(Response.SUCCESS);//代表成功
            }
            return response;
        }
        return null;
    }

    /**
     * 根据用户id获取用户修改信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/selectUserUpdateInfoByUserId/{userId}")
    public UserManagerUpdateResponse selectUserUpdateInfoByUserId(@PathVariable String userId) {
        logger.info("---selectUserUpdateInfoByUserId---  " + userId);
        //
        UserManagerUpdateResponse response = new UserManagerUpdateResponse();
        if (StringUtils.isNotEmpty(userId)) {
            int intUserId = Integer.parseInt(userId);
            UserManagerUpdateVO userManagerUpdateVo = new UserManagerUpdateVO();
            UserManagerUpdateCustomize userManagerUpdateCustomize = userManagerService.selectUserUpdateInfoByUserId(intUserId);
            if (null != userManagerUpdateCustomize) {
                BeanUtils.copyProperties(userManagerUpdateCustomize, userManagerUpdateVo);
                response.setResult(userManagerUpdateVo);
            }
            return response;
        }
        return null;
    }

    /**
     * 更新用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/updataUserInfo")
    public int updataUserInfo(@RequestBody UserManagerUpdateRequest request) {
        logger.info("---updataUserInfo---  " + JSONObject.toJSONString(request));
        int updateUser = userManagerService.updataUserInfo(request);
        return updateUser;
    }

    /**
     * 校验手机号
     *
     * @param userId
     * @param mobile
     * @return
     */
    @RequestMapping("/checkMobileByUserId/{userId}/{mobile}")
    public int countUserByMobile(@PathVariable int userId, @PathVariable String mobile) {
        logger.info("---countUserByMobile---  " + userId + "," + mobile);
        int checkFlg = userManagerService.countUserByMobile(userId, mobile);
        return checkFlg;
    }

    /**
     * 统计手机号
     * @param mobile
     * @return
     */
    @RequestMapping("/countByMobile/{mobile}")
    public int countByMobile(@PathVariable String mobile){
        logger.info("---countByMobile---  "  + mobile);
        int checkFlg = userManagerService.countByMobile(mobile);
        return checkFlg;
    }

    /**
     * 校验推荐人
     *
     * @param userId
     * @param recommendName
     * @return
     */
    @RequestMapping("/checkRecommend/{userId}/{recommendName}")
    public int checkRecommend(@PathVariable int userId, @PathVariable String recommendName) {
        logger.info("---checkRecommend---  " + userId + "," + recommendName);
        int checkFlg = userManagerService.selectCheckRecommend(userId, recommendName);
        return checkFlg;
    }

    /**
     * 根据用户id查找用户表
     *
     * @param userId
     * @return
     */
    @RequestMapping("/selectUserByUserId/{userId}")
    public UserResponse selectUserByUserId(@PathVariable int userId) {
        UserResponse response = new UserResponse();
        logger.info("---selectUserByUserId---  " + userId);
        UserVO userVO = new UserVO();
        User user = userManagerService.selectUserByUserId(userId);
        if (null != user) {
            BeanUtils.copyProperties(user,userVO);
            response.setResult(userVO);
            response.setRtn(Response.SUCCESS);//代表成功
            return response;
        }
        return null;
    }

    /**
     * 根据用户List id查找用户表
     *
     * @param userId
     * @return
     */
    @RequestMapping("/selectUserByListUserId/{userId}")
    public UserResponse selectUserByListUserId(@PathVariable List userId) {
        UserResponse response = new UserResponse();
        logger.info("---selectUserByUserId---  " + userId);
        List<UserVO> userVO = new ArrayList<UserVO>();
        List<User> user = userManagerService.selectUserByListUserId(userId);
        if (null != user) {
            BeanUtils.copyProperties(user,userVO);
            response.setResultList(userVO);
            response.setRtn(Response.SUCCESS);//代表成功
            return response;
        }
        return null;
    }


    /**
     * 根據accounId獲取開戶信息
     *
     * @param accountId
     * @return
     */
    @RequestMapping("/selectBankOpenAccountByAccountId/{accountId}")
    public BankOpenAccountResponse selectBankOpenAccountByAccountId(@PathVariable String accountId) {
        BankOpenAccountResponse response = new BankOpenAccountResponse();
        logger.info("---selectBankOpenAccountByAccountId---  " + accountId);
        BankOpenAccountVO bankOpenAccountVO = new BankOpenAccountVO();
        BankOpenAccount bankOpenAccount = userManagerService.selectBankOpenAccountByAccountId(accountId);
        if (null != bankOpenAccount) {
            BeanUtils.copyProperties(bankOpenAccount, bankOpenAccountVO);
            response.setResult(bankOpenAccountVO);
            response.setRtn(Response.SUCCESS);//代表成功
            return response;
        }
        return null;
    }

    /**
     * 更新企业用户开户记录
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateCorpOpenAccountRecord")
    public int updateCorpOpenAccountRecord(@RequestBody @Valid CorpOpenAccountRecordRequest request) {
        logger.info("---updateCorpOpenAccountRecord---  " + JSONObject.toJSONString(request));
        CorpOpenAccountRecord corpOpenAccountRecord = new CorpOpenAccountRecord();
        BeanUtils.copyProperties(request, corpOpenAccountRecord);
        int ingFlg = userManagerService.updateCorpOpenAccountRecord(corpOpenAccountRecord);
        return ingFlg;
    }

    /**
     * 插入企业用户开户记录
     *
     * @param request
     * @return
     */
    @RequestMapping("/insertCorpOpenAccountRecord")
    public int insertCorpOpenAccountRecord(@RequestBody @Valid CorpOpenAccountRecordRequest request) {
        logger.info("---insertCorpOpenAccountRecord---  " + JSONObject.toJSONString(request));
        CorpOpenAccountRecord corpOpenAccountRecord = new CorpOpenAccountRecord();
        BeanUtils.copyProperties(request, corpOpenAccountRecord);
        int ingFlg = userManagerService.insertCorpOpenAccountRecord(corpOpenAccountRecord);
        return ingFlg;
    }

    /**
     * 插入企业用户开户记录
     *
     * @param userId
     * @return
     */
    @RequestMapping("/queryBankOpenAccountByUserId/{userId}")
    public BankOpenAccountResponse queryBankOpenAccountByUserId(@PathVariable int userId) {
        BankOpenAccountResponse response = new BankOpenAccountResponse();
        BankOpenAccountVO bankOpenAccountVO = new BankOpenAccountVO();
        BankOpenAccount bankOpenAccount = userManagerService.queryBankOpenAccountByUserId(userId);
        if (null != bankOpenAccount) {
            BeanUtils.copyProperties(bankOpenAccount, bankOpenAccountVO);
            response.setResult(bankOpenAccountVO);
            response.setRtn(Response.SUCCESS);//代表成功
        }
        return response;
    }

    /**
     *根据用户名获取开户信息
     * @auther: hesy
     * @date: 2018/7/14
     */
    @RequestMapping("/get_openaccount_byusername/{userName}")
    public BankOpenAccountResponse queryBankOpenAccountByUserName(@PathVariable String userName) {
        BankOpenAccountResponse response = new BankOpenAccountResponse();
        BankOpenAccountVO bankOpenAccountVO = new BankOpenAccountVO();
        BankOpenAccount bankOpenAccount = userManagerService.queryBankOpenAccountByUserName(userName);
        if (null != bankOpenAccount) {
            BeanUtils.copyProperties(bankOpenAccount, bankOpenAccountVO);
            response.setResult(bankOpenAccountVO);
            response.setRtn(Response.SUCCESS);//代表成功
        }
        return response;
    }

    /**
     * 更新开户信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateBankOpenAccount")
    public int updateBankOpenAccount(@RequestBody @Valid BankOpenAccountRequest request) {
        BankOpenAccount bankOpenAccount = new BankOpenAccount();
        BeanUtils.copyProperties(request, bankOpenAccount);
        int ingFlg = userManagerService.updateBankOpenAccount(bankOpenAccount);
        return ingFlg;
    }

    /**
     * 插入开户信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/insertBankOpenAccount")
    public int insertBankOpenAccount(@RequestBody @Valid BankOpenAccountRequest request) {
        BankOpenAccount bankOpenAccount = new BankOpenAccount();
        BeanUtils.copyProperties(request, bankOpenAccount);
        int ingFlg = userManagerService.insertBankOpenAccount(bankOpenAccount);
        return ingFlg;
    }

    /**
     * 更新用户信息表
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateUserInfoByUserInfo")
    public int updateUserInfoByUserInfo(@RequestBody @Valid UserInfoRequest request) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(request, userInfo);
        int ingFlg = userManagerService.updateUserInfoByUserInfo(userInfo);
        return ingFlg;
    }



    /**
     * 更新用户表
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateUser")
    public int updateUser(@RequestBody @Valid UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        int ingFlg = userManagerService.updateUser(user);
        return ingFlg;
    }



    /**
     * 查询条件设置
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> paramSet(UserManagerRequest userRequest) {
        Map<String, Object> mapParam = new HashMap<String, Object>();
        mapParam.put("regTimeStart", userRequest.getRegTimeStart());
        mapParam.put("regTimeEnd", userRequest.getRegTimeEnd());
        mapParam.put("userName", userRequest.getUserName());
        mapParam.put("realName", userRequest.getRealName());
        mapParam.put("mobile", userRequest.getMobile());
        mapParam.put("recommendName", userRequest.getRecommendName());
        mapParam.put("userRole", userRequest.getUserRole());
        mapParam.put("userType", userRequest.getUserType());
        mapParam.put("userProperty", userRequest.getUserProperty());
        mapParam.put("accountStatus", userRequest.getAccountStatus());
        mapParam.put("userStatus", userRequest.getUserStatus());
        mapParam.put("combotreeListSrch", userRequest.getCombotreeListSrch());
        mapParam.put("customerId", userRequest.getCustomerId());
        mapParam.put("instCodeSrch", userRequest.getInstCodeSrch());
        mapParam.put("whereFlag", getWhereFlag(mapParam));
        return mapParam;
    }

    /**
     * 没有limit外的检索条件
     *
     * @param user
     * @return
     */
    private String getWhereFlag(Map<String, Object> user) {
        String whereFlag = "0";
        for (Map.Entry<String, Object> entry : user.entrySet()) {
            // key!=whereFlag,limitStart,limitEnd时
            if (!("whereFlag".equals(entry.getKey()) || "limitStart".equals(entry.getKey())
                    || "limitEnd".equals(entry.getKey()))) {
                if (entry.getValue() != null) {
                    // 有limit外的检索条件
                    whereFlag = "1";
                    break;
                }
            }
        }
        return whereFlag;
    }

    /**
     * 获取某一用户的信息修改列表
     * @param request
     * @return
     */
    @RequestMapping("/selectUserChageLog")
    public UserChangeLogResponse selectUserChageLog(@RequestBody @Valid UserChangeLogRequest request){
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserChangeLogResponse response = new UserChangeLogResponse();
        Map<String,Object> mapParam = new HashMap<>();
        String returnCode = Response.FAIL;
        if(null!=request){
            mapParam.put("userId",request.getUserId());
            mapParam.put("changeType",request.getChangeType());
        }
        List<UserChangeLog>userChangeLogsList= userManagerService.queryChangeLogList(mapParam);
        List<UserChangeLogVO> listChangeLog = new ArrayList<UserChangeLogVO>();
        if (!CollectionUtils.isEmpty(userChangeLogsList)) {
//            List<UserChangeLogVO> userChangeLogVOList = CommonUtils.convertBeanList(userChangeLogsList, UserChangeLogVO.class);
            for(UserChangeLog changeLog:userChangeLogsList){
                UserChangeLogVO userChangeLogVO = new UserChangeLogVO();
                BeanUtils.copyProperties(changeLog,userChangeLogVO);
                userChangeLogVO.setUpdateTime(smp.format(changeLog.getUpdateTime()));
                listChangeLog.add(userChangeLogVO);
            }
            response.setResultList(listChangeLog);
            response.setCount(userChangeLogsList.size());
            returnCode = Response.SUCCESS;
        }
        response.setRtn(returnCode);//代表成功
        return response;
    }

    /**
     * 获取姓名查找用户
     * @param recommendName
     * @return
     */
    @RequestMapping("/selectUserByRecommendName/{recommendName}")
    public UserResponse selectUserByRecommendName(@PathVariable String recommendName){
        UserResponse response = new UserResponse();
        String returnCode = Response.FAIL;
        User userChangeLogs= userManagerService.selectUserByRecommendName(recommendName);
        UserVO userVO = new UserVO();
        if(null!=userChangeLogs){
            BeanUtils.copyProperties(userChangeLogs, userVO);
            response.setResult(userVO);
            returnCode = Response.SUCCESS;
        }
        response.setRtn(returnCode);//代表成功
        return response;
    }

    /**
     * 获取推荐人姓名查找用户
     * @param userId
     * @return
     */
    @RequestMapping("/selectUserRecommendUserId/{userId}")
    public UserRecommendCustomizeResponse selectUserRecommendUserId(@PathVariable String userId){
        UserRecommendCustomizeResponse response = new UserRecommendCustomizeResponse();
        String returnCode = Response.FAIL;
        if(StringUtils.isNotBlank(userId)){
            int userInt = Integer.parseInt(userId);
            UserRecommendCustomize userRecommendCustomize = userManagerService.searchUserRecommend(userInt);
            UserRecommendCustomizeVO userRecommendCustomizeVO = new UserRecommendCustomizeVO();
            if(null!=userRecommendCustomize){
                BeanUtils.copyProperties(userRecommendCustomize, userRecommendCustomizeVO);
                response.setResult(userRecommendCustomizeVO);
                returnCode = Response.SUCCESS;
            }
        }
        response.setRtn(returnCode);//代表成功
        return response;
    }

    @RequestMapping("/selectSpreadsUsersByUserId/{userId}")
    public SpreadsUserResponse selectSpreadsUsersByUserId(@PathVariable String userId){
        SpreadsUserResponse response = new SpreadsUserResponse();
        String returnCode = Response.FAIL;
        if(StringUtils.isNotEmpty(userId)){
            int intUserId = Integer.parseInt(userId);
            SpreadsUser spreadsUser = userManagerService.selectSpreadsUsersByUserId(intUserId);
            SpreadsUserVO spreadsUserVO = new SpreadsUserVO();
            if(null!=spreadsUser){
                BeanUtils.copyProperties(spreadsUser, spreadsUserVO);
                response.setResult(spreadsUserVO);
                returnCode = Response.SUCCESS;
            }
        }
        response.setRtn(returnCode);
        return response;
    }
    @RequestMapping("/getTiedCardForBank/{userId}")
    public BankCardResponse getTiedCardOfAccountBank(@PathVariable Integer userId){
        BankCardResponse bankCardResponse = new BankCardResponse();
        List<BankCard> bankCardList = callCenterBankService.getTiedCardOfAccountBank(userId);
        if(!CollectionUtils.isEmpty(bankCardList)){
            List<BankCardVO> bankCardVOList = CommonUtils.convertBeanList(bankCardList,BankCardVO.class);
            bankCardResponse.setResultList(bankCardVOList);
        }
        return bankCardResponse;
    }

    /**
     * 修改推荐人信息
     * @param request
     * @return
     */
    @RequestMapping("/updateUserRecommend")
    public int updateUserRecommend(@RequestBody @Valid  AdminUserRecommendRequest request){
        return userManagerService.updateUserRe(request);
    }
    /**
     * 修改用户身份证
     * @param request
     * @return
     */
    @RequestMapping("/updateUserIdCard")
    public int updateUserIdCard(@RequestBody @Valid AdminUserRecommendRequest request){
        return userManagerService.updateUserIdCard(request);
    }

    /**
     * 保存公司信息
     * @param updCompanyRequest
     * @return
     */
    @RequestMapping("/saveCompanyInfo")
    public Response saveCompanyInfo(@RequestBody @Valid  UpdCompanyRequest updCompanyRequest){
        Response bankCardResponse = userManagerService.saveCompanyInfo(updCompanyRequest);
        return bankCardResponse;
    }
    @RequestMapping("/getUserIdByBind/{bindUniqueId}/{bindPlatformId}")
    public Integer getUserIdByBind(@PathVariable Integer bindUniqueId,@PathVariable Integer bindPlatformId){
    	return userManagerService.getUserIdByBind(bindUniqueId, bindPlatformId);
    }

    /**
     * 查询第三方绑定用户信息
     * @param bindUniqueId
     * @param bindPlatformId
     * @return
     */
    @RequestMapping("/getBindUser/{bindUniqueId}/{bindPlatformId}")
    public BindUserResponse getBindUser(@PathVariable Integer bindUniqueId,@PathVariable Integer bindPlatformId){
        BindUserResponse response = new BindUserResponse();
        BindUser bindUser = userManagerService.getUsersByUniqueId(bindUniqueId, bindPlatformId);
        if(null!=bindUser){
            BindUserVo bindUserVo = new BindUserVo();
            BeanUtils.copyProperties(bindUser,bindUserVo);
            response.setResult(bindUserVo);
            response.setRtn(Response.SUCCESS);
        }
        return response;
    }
    /**
     * 根据绑定信息取得用户id
     * @param userId
     * @param bindPlatformId
     * @return
     */
    @RequestMapping("/getBindUniqueIdByUserId/{userId}/{bindPlatformId}")
    public String getBindUniqueIdByUserId(@PathVariable Integer userId,@PathVariable Integer bindPlatformId) {
    	return userManagerService.getBindUniqueIdByUserId( userId,  bindPlatformId);
    }
	/**
	 * 给第三方平台用户登陆授权
	 * @param userId
	 * @param bindUniqueId
	 * @param bindPlatformId
	 * @return
	 */
    @RequestMapping("/bindThirdUser/{userId}/{bindUniqueId}/{bindPlatformId}")
    public BooleanResponse bindThirdUser(@PathVariable Integer userId, @PathVariable Integer bindUniqueId, @PathVariable Integer bindPlatformId) {
        BooleanResponse response = new BooleanResponse();
    	Boolean result =  userManagerService.bindThirdUser( userId,  bindUniqueId,  bindPlatformId);
    	response.setResultBoolean(result);
    	return response;
    }

    /**
     * 根据关联关系查询OA表的内容,得到部门的线上线下属性
     * @param userId
     * @return
     */
    @GetMapping("/queryUserAndDepartment/{userId}")
    public UserUpdateParamCustomizeResponse queryUserAndDepartment(@PathVariable Integer userId) {
        UserUpdateParamCustomizeResponse response = new UserUpdateParamCustomizeResponse();
        response.setRtn(Response.FAIL);
        List<UserUpdateParamCustomize> userUpdateParamCustomizeList = userManagerService.queryUserAndDepartment(userId);
        if(!CollectionUtils.isEmpty(userUpdateParamCustomizeList)){
            List<UserUpdateParamCustomizeVO> userUpdateParamCustomizeVOList = CommonUtils.convertBeanList(userUpdateParamCustomizeList,UserUpdateParamCustomizeVO.class);
            response.setResultList(userUpdateParamCustomizeVOList);
            response.setRtn(Response.SUCCESS);
        }
        return response;
    }

    /**
     * 获取所有用户信息
     * @return
     */
    @RequestMapping("/selectAllUser")
    public UserResponse selectAllUser() {
        UserResponse response = new UserResponse();
        response.setRtn(Response.FAIL);
        List<User> userList = userManagerService.selectAllUser();
        if(!CollectionUtils.isEmpty(userList)){
            List<UserVO> userUpdateParamCustomizeVOList = CommonUtils.convertBeanList(userList,UserVO.class);
            response.setResultList(userUpdateParamCustomizeVOList);
            response.setRtn(Response.SUCCESS);
        }
        return response;
    }

    /**
     * 查询此段时间的用户推荐人的修改记录
     * @param userId
     * @param repairStartDate
     * @param repairEndDate
     * @return
     */
    @GetMapping("/searchSpreadUsersLogByDate/{userId}/{repairStartDate}/{repairEndDate}")
    public SpreadsUserLogResponse searchSpreadUsersLogByDate(@PathVariable Integer userId,@PathVariable String repairStartDate, @PathVariable String repairEndDate) {
        SpreadsUserLogResponse response = new SpreadsUserLogResponse();
        response.setRtn(Response.FAIL);
        List<SpreadsUserLog> spreadsUserLogList = userManagerService.searchSpreadUsersLogByDate(userId,repairStartDate,repairEndDate);
        if(!CollectionUtils.isEmpty(spreadsUserLogList)){
            List<SpreadsUserLogVO> spreadsUserLogVOList = CommonUtils.convertBeanList(spreadsUserLogList,SpreadsUserLogVO.class);
            response.setResultList(spreadsUserLogVOList);
            response.setRtn(Response.SUCCESS);
        }
        return response;
    }

    /**
     * 查找员工信息
     * @param userId
     * @return
     */
    @GetMapping("/selectEmployeeInfoByUserId/{userId}")
    public EmployeeCustomizeResponse selectEmployeeInfoByUserId(@PathVariable Integer userId) {
        EmployeeCustomizeResponse response = new EmployeeCustomizeResponse();
        response.setRtn(Response.FAIL);
        EmployeeCustomize employeeCustomize = userManagerService.selectEmployeeInfoByUserId(userId);
        if(null!=employeeCustomize){
            EmployeeCustomizeVO employeeCustomizeVO = new EmployeeCustomizeVO();
            BeanUtils.copyProperties(employeeCustomize,employeeCustomizeVO);
            response.setResult(employeeCustomizeVO);
            response.setRtn(Response.SUCCESS);
        }
        return response;
    }

    /**
     * 根据用户id获取离职信息
     * @param userId
     * @return
     */
    @GetMapping("/selectUserLeaveByUserId/{userId}")
    public AdminEmployeeLeaveCustomizeResponse selectUserLeaveByUserId(@PathVariable Integer userId) {
        AdminEmployeeLeaveCustomizeResponse response = new AdminEmployeeLeaveCustomizeResponse();
        response.setRtn(Response.FAIL);
        AdminEmployeeLeaveCustomize adminEmployeeLeaveCustomize = userManagerService.selectUserLeaveByUserId(userId);
        if(null!=adminEmployeeLeaveCustomize){
            AdminEmployeeLeaveCustomizeVO employeeCustomizeVO = new AdminEmployeeLeaveCustomizeVO();
            BeanUtils.copyProperties(adminEmployeeLeaveCustomize,employeeCustomizeVO);
            response.setResult(employeeCustomizeVO);
            response.setRtn(Response.SUCCESS);
        }
        return response;
    }
}
