/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.market.controller.admin.pointsshop.duiba.order;

import com.hyjf.am.admin.mq.base.CommonProducer;
import com.hyjf.am.admin.mq.base.MessageContent;
import com.hyjf.am.market.service.pointsshop.duiba.order.DuibaOrderListService;
import com.hyjf.am.market.service.pointsshop.duiba.points.DuibaPointsListService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.StringResponse;
import com.hyjf.am.response.admin.DuibaOrderResponse;
import com.hyjf.am.response.admin.VirtualResultResponse;
import com.hyjf.am.resquest.admin.CouponUserRequest;
import com.hyjf.am.resquest.admin.DuibaOrderRequest;
import com.hyjf.am.resquest.admin.Paginator;
import com.hyjf.am.trade.dao.model.auto.CouponConfig;
import com.hyjf.am.trade.dao.model.auto.CouponUser;
import com.hyjf.am.trade.service.front.coupon.CouponConfigService;
import com.hyjf.am.trade.service.front.coupon.CouponUserService;
import com.hyjf.am.user.dao.model.auto.User;
import com.hyjf.am.user.dao.model.auto.UserInfo;
import com.hyjf.am.user.service.admin.promotion.ChannelService;
import com.hyjf.am.user.service.front.user.UserInfoService;
import com.hyjf.am.user.service.front.user.UserService;
import com.hyjf.am.vo.admin.DuibaOrderVO;
import com.hyjf.am.vo.admin.DuibaPointsVO;
import com.hyjf.am.vo.admin.VirtualVO;
import com.hyjf.am.vo.message.AppMsMessage;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.constants.MessageConstant;
import com.hyjf.common.exception.MQException;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetCode;
import com.hyjf.common.util.GetDate;
import com.hyjf.pay.lib.duiba.bean.DuiBaCallBean;
import com.hyjf.pay.lib.duiba.bean.DuiBaCallResultBean;
import com.hyjf.pay.lib.duiba.util.DuiBaCallConstant;
import com.hyjf.pay.lib.duiba.util.DuiBaCallUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兑吧订单（订单查询,订单发货,异常订单）列表
 *
 * @author wenxin
 * @version DuibaOrderListController, v0.1 2019/5/29 9:46
 */

@RestController
@RequestMapping("/am-market/pointsshop/duiba/order")
public class DuibaOrderListController {
    private static final Logger logger = LoggerFactory.getLogger(DuibaOrderListController.class);

    @Autowired
    private DuibaOrderListService duibaOrderListService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private CouponConfigService couponConfigService;

    @Autowired
    private CouponUserService couponUserService;

    @Autowired
    private CommonProducer commonProducer;

    @Autowired
    private DuibaPointsListService duibaPointsListService;

    @PostMapping("/findOrderList")
    public DuibaOrderResponse findOrderList(@RequestBody DuibaOrderRequest request) {
        DuibaOrderResponse response = new DuibaOrderResponse();
        Integer count = duibaOrderListService.selectOrderListCount(request);
        if (count > 0) {
            // 查询列表传入分页
            Paginator paginator;
            if (request.getPageSize() == 0) {
                // 前台传分页
                paginator = new Paginator(request.getCurrPage(), count);
            } else {
                // 前台未传分页那默认 10
                paginator = new Paginator(request.getCurrPage(), count, request.getPageSize());
            }
            request.setLimitStart(paginator.getOffset());
            request.setLimitEnd(paginator.getLimit());
            request.setPaginator(paginator);
            List<DuibaOrderVO> list = this.duibaOrderListService.selectOrderList(request);
            if (!CollectionUtils.isEmpty(list)) {
                response.setResultList(list);
                response.setRecordTotal(count);
            } else {
                response.setRtn(Response.FAIL);
            }
        }
        return response;
    }

    /**
     * 根据兑吧订单手动同步订单状态此接口“不可逆”可以（失败->成功），不可以（成功->失败）
     *
     * @param orderId
     * @return
     */
    @GetMapping("/synchronization/{orderId}")
    public String synchronization(@PathVariable Integer orderId) {
        // 同步接口开始 DuiBaCallUtils.duiBaCall()
        logger.info("订单同步接口开始同步，orderId  = {orderId}");
        // 查询需要同步的订单信息（兑吧订单表）
        DuibaOrderVO duibaOrderVO = duibaOrderListService.findOneOrder(orderId);
        DuiBaCallBean duiBaCallBean = new DuiBaCallBean();
        duiBaCallBean.setOrderNum(duibaOrderVO.getDuibaOrderId());
        duiBaCallBean.setUserId(duibaOrderVO.getUserId());
        duiBaCallBean.setBizId(duibaOrderVO.getHyOrderId());
        duiBaCallBean.setMsgType(DuiBaCallConstant.API_ORDER_QUERY);
        DuiBaCallResultBean duiBaCallResultBean = DuiBaCallUtils.duiBaCall(duiBaCallBean);
        if(duiBaCallResultBean!=null){
            // 根据兑吧回传的结果进行数据处理及赋值操作
            if (duiBaCallResultBean.isSuccess()) {
                // 判断虚拟商品还是实物
                if(("0").equals(duibaOrderVO.getProductType())){
                    // 更新订单发货状态（订单子状态，success为true的时候返回，分为：create 创建订单后的初始状态、process 处理中、waitAudit 待审核、waitSend 待发货、success 兑换成功、fail 兑换失败）
                    if ("waitSend".equals(duiBaCallResultBean.getSubStatus())) {
                        duibaOrderVO.setDeliveryStatus(0);
                    } else if ("success".equals(duiBaCallResultBean.getSubStatus())) {
                        duibaOrderVO.setDeliveryStatus(1);
                    }
                }
                // 返回成功更新订单状态（success为true的时候返回，分为：create 创建订单后的初始状态、process 处理中、success 兑换成功、fail 兑换失败）
                if ("process".equals(duiBaCallResultBean.getStatus())) {
                    duibaOrderVO.setOrderStatus(2);
                } else if ("success".equals(duiBaCallResultBean.getStatus())) {
                    duibaOrderVO.setOrderStatus(0);
                } else if ("fail".equals(duiBaCallResultBean.getStatus())) {
                    duibaOrderVO.setOrderStatus(1);
                }
                // 设置更新时间
                duibaOrderVO.setUpdateTime(new Date());
                // 执行更新订单表（更新订单状态，发货状态，订单有效状态，虚拟商品充值状态）
                duibaOrderListService.updateOneOrderByPrimaryKey(duibaOrderVO);
            } else {
                return duiBaCallResultBean.getErrorMessage();
            }
        }else{
            logger.error("【兑吧】兑吧接口调用失败！，操作失败！orderId：" + orderId);
            return "兑吧接口调用失败！";
        }
        return "success";
    }

    /**
     * 根据兑吧订单的兑吧订单号查询用户订单信息并发放优惠券
     *
     * @param orderNum
     * @return
     */
    @RequestMapping("/releaseCoupons/{orderNum}")
    public VirtualResultResponse releaseCouponsUserById(@PathVariable String orderNum) {
        VirtualResultResponse virtualResult = new VirtualResultResponse();
        String fail = "fail";
        String success = "success";
        String errorMessage = "";
        VirtualVO virtualVO = new VirtualVO();
        String pointsCurrent = "";
        // 是否发生错误（错误状态标识）
        boolean errorFlag = false;
        // 根据兑吧订单号查询订单信息
        DuibaOrderVO duibaOrderVO = duibaOrderListService.selectOrderByOrderId(orderNum);
        // 发放优惠券 需要的参数 优惠券编码  couponCode 用户id  userId 备注  content
        if (duibaOrderVO != null && duibaOrderVO.getUserId() != null) {
            // 订单流水号，开发者返回给兑吧的凭据
            virtualVO.setSupplierBizId(duibaOrderVO.getHyOrderId());
            // 根据用户id获取用户详情信息
            UserInfo userInfo = userInfoService.findUserInfoById(duibaOrderVO.getUserId());
            // 根据用户id获取用户信息
            User user = userService.findUserByUserId(duibaOrderVO.getUserId());
            // 根据用户id获取注册时渠道名
            String channelName = channelService.selectChannelName(duibaOrderVO.getUserId());
            // 根据优惠券编码查询优惠券
            CouponConfig configVO = couponConfigService.selectCouponConfig(duibaOrderVO.getCommodityCode());
            CouponUserRequest couponUserRequest = new CouponUserRequest();
            if(configVO!=null&&configVO.getDelFlag()==0){
                // 截止日
                if (configVO.getExpirationType() == 1) {
                    couponUserRequest.setEndTime(configVO.getExpirationDate());
                } else if (configVO.getExpirationType() == 2) {
                    Date endDate = GetDate.countDate(GetDate.getDate(), 2, configVO.getExpirationLength());
                    couponUserRequest.setEndTime((int) (endDate.getTime() / 1000));
                } else if (configVO.getExpirationType() == 3) {
                    Date endDate = GetDate.countDate(GetDate.getDate(), 5, configVO.getExpirationLengthDay());
                    couponUserRequest.setEndTime((int) (endDate.getTime() / 1000));
                }
                couponUserRequest.setUserId(duibaOrderVO.getUserId());
                couponUserRequest.setCouponCode(configVO.getCouponCode());
                couponUserRequest.setContent("兑吧积分兑换优惠券");
                couponUserRequest.setCouponUserCode(GetCode.getCouponUserCode(configVO.getCouponType()));
                // 优惠券 的发放信息创建人是用户自己本身记录用户的userid
                couponUserRequest.setCreateUserId(duibaOrderVO.getUserId());
                couponUserRequest.setCreateTime(GetDate.getDate());
                // 优惠券 的发放信息修改人是用户自己本身记录用户的userid
                couponUserRequest.setUpdateUserId(duibaOrderVO.getUserId());
                couponUserRequest.setUpdateTime(GetDate.getDate());
                // 发放优惠券时设置优惠券信息为无效（当兑吧兑换结果通知为成功时更新为有效状态）
                couponUserRequest.setDelFlag(CustomConstants.FALG_DEL);
                couponUserRequest.setUsedFlag(CustomConstants.USER_COUPON_STATUS_WAITING_PUBLISH);
                couponUserRequest.setReadFlag(CustomConstants.USER_COUPON_READ_FLAG_NO);
                couponUserRequest.setCouponSource(CustomConstants.USER_COUPON_SOURCE_INTEGRAL);
                couponUserRequest.setAttribute(userInfo.getAttribute());
                couponUserRequest.setChannel(channelName);
                int remain = couponConfigService.checkCouponSendExcess(duibaOrderVO.getCommodityCode());
                boolean countType = remain > 0 ? true : false;
                if (countType) {
                    try {
                        // 插入审核状态并确认优惠券状态
                        couponUserRequest.setAuditContent("兑吧积分兑换优惠券");
                        couponUserRequest.setUsedFlag(CustomConstants.USER_COUPON_STATUS_UNUSED);
                        // 推送通知消息
                        Map<String, String> param = new HashMap<String, String>();
                        param.put("val_number", String.valueOf(1));
                        param.put("val_coupon_type", configVO.getCouponType() == 1 ? "体验金" : configVO.getCouponType() == 2 ? "加息券" : configVO.getCouponType() == 3 ? "代金券" : "");
                        AppMsMessage appMsMessage = new AppMsMessage(couponUserRequest.getUserId(), param, null, MessageConstant.APP_MS_SEND_FOR_USER, CustomConstants.JYTZ_COUPON_SUCCESS);
                        try {
                            commonProducer.messageSend(new MessageContent(MQConstant.APP_MESSAGE_TOPIC, String.valueOf(couponUserRequest.getUserId()),
                                    appMsMessage));
                        } catch (MQException e) {
                            errorMessage = "优惠券发放失败，推送通知消息失败";
                            logger.error(e.getMessage());
                        }
                        // 插入优惠券信息
                        CouponUser couponUser = couponUserService.insertByDuibaOrder(couponUserRequest);
                        if (couponUser.getId() != null) {
                            DuibaOrderVO duibaOrderVO1 = new DuibaOrderVO();
                            // 设置更新主键
                            duibaOrderVO1.setId(duibaOrderVO.getId());
                            // 设置更新用户订单表的优惠券用户表id
                            duibaOrderVO1.setCouponUserId(couponUser.getId());
                            // 更新订单表信息插入优惠券用户表主键id
                            duibaOrderListService.updateOneOrderByPrimaryKey(duibaOrderVO1);
                            errorFlag = true;
                        } else {
                            errorMessage = "优惠券发放失败";
                        }
                    } catch (Exception e) {
                        logger.error("【兑吧】优惠券发放失败！异常如下：" + e.getMessage());
                    }
                } else {
                    errorMessage = "优惠券发行数量超出上限，不再发放，操作失败！";
                    logger.error("【兑吧】优惠券发行数量超出上限，不再发放，操作失败！");
                }
            }else{
                errorMessage = "优惠券发放失败，根据优惠券编码没有找到对应优惠券信息或该优惠券无效！";
                logger.error("【兑吧】优惠券发放失败！异常如下：根据优惠券编码没有找到对应优惠券信息！");
            }
            pointsCurrent = String.valueOf(user.getPointsCurrent());
        }else{
            errorMessage = "优惠券发放失败，根据兑吧订单号查询订单，没有找到订单信息或者订单没有绑定用户！";
            logger.error("【兑吧】优惠券发放失败！异常如下：根据兑吧订单号查询订单，没有找到订单信息或者订单没有绑定用户！");
        }
        if (errorFlag) {
            virtualVO.setStatus(success);
            // 优惠券发放成功（积分赋值）
            virtualVO.setCredits(pointsCurrent);
        } else {
            virtualVO.setStatus(fail);
            virtualVO.setErrorMessage(errorMessage);
        }
        virtualResult.setResult(virtualVO);
        return virtualResult;
    }

    /**
     * 兑吧兑换结果通知接口（失败时设置订单无效）
     *
     * @param orderNum
     * @return
     */
    @RequestMapping("/activation/{orderNum}/{errorMessage}")
    public StringResponse activation(@PathVariable String orderNum, @PathVariable String errorMessage) {
        logger.info("兑吧兑换结果通知接口（失败时设置订单无效）开始 orderNum："+orderNum);
        StringResponse response = new StringResponse();
        // 1.兑换失败，根据orderNum，对用户的金币进行返还，回滚操作
        int res;
        // 商品类型为充值
        String productTypeCZ = "2";
        // 商品类型为实物
        String productTypeSW = "0";
        // 卡密和优惠券
        String productTypeKM = "1";
        try {
            // 2.将发放的优惠券设置成无效, 3.兑换失败将对应的"订单"设置成无效并给出失败信息
            DuibaOrderVO duibaOrderVO = new DuibaOrderVO();
            // 根据兑吧订单号查询订单信息
            DuibaOrderVO duibaOrderVOStr = duibaOrderListService.selectOrderByOrderId(orderNum);
            if (duibaOrderVOStr != null) {
                logger.info("兑吧兑换结果通知接口（失败时设置订单无效）订单信息不为空继续 orderNum：" + orderNum);
                // - - - - - - - - - - - - - - - - 调用兑吧接口返回失败回滚积分逻辑 - start - - - - -  - - - - - - -
                DuiBaCallBean duiBaCallBean = new DuiBaCallBean();
                duiBaCallBean.setOrderNum(duibaOrderVOStr.getDuibaOrderId());
                duiBaCallBean.setUserId(duibaOrderVOStr.getUserId());
                duiBaCallBean.setBizId(duibaOrderVOStr.getHyOrderId());
                duiBaCallBean.setMsgType(DuiBaCallConstant.API_ORDER_QUERY);
                DuiBaCallResultBean duiBaCallResultBean = DuiBaCallUtils.duiBaCall(duiBaCallBean);
                if (duiBaCallResultBean.isSuccess()) {
                    logger.info("兑吧兑换结果通知接口（失败时设置订单无效）兑吧接口调用成功 继续 orderNum：" + orderNum);
                    // 返回成功更新订单状态（success为true的时候返回，分为：create 创建订单后的初始状态、process 处理中、success 兑换成功、fail 兑换失败）
                    if ("fail".equals(duiBaCallResultBean.getStatus())) {
                        // 判断是否插入了优惠券
                        if(duibaOrderVOStr.getCouponUserId()==null&&(productTypeCZ.equals(duibaOrderVOStr.getProductType())||productTypeSW.equals(duibaOrderVOStr.getProductType())||productTypeKM.equals(duibaOrderVOStr.getProductType()))) {
                            logger.info("兑吧兑换结果通知接口（失败时设置订单无效）判断状态已完成进入扣积分逻辑 orderNum：" + orderNum);
                            if (duibaOrderVOStr.getActivationType() == 1) {
                                // - - - - - - - - - - - - - - - - （失败）回滚用户积分，加积分 - start - - - - - - - - - - - - - -
                                // 根据订单号查询积分明细表(订单取消)
                                DuibaPointsVO duibaPointsVOQc = duibaPointsListService.getDuibaPointsByOrdId(orderNum, 2);
                                // 根据订单号查询积分明细表(商品兑换)
                                DuibaPointsVO duibaPointsVO = duibaPointsListService.getDuibaPointsByOrdId(orderNum, 1);
                                // 回滚积分（当前用户最新积分+积分明细表积分）
                                // 兑吧积分明细不为空且订单为“非订单取消状态”回滚积分
                                if (duibaPointsVO != null) {
                                    logger.info("兑吧兑换结果通知接口（失败时设置订单无效）根据订单号查询积分明细表(商品兑换)不为空 继续 orderNum：" + orderNum);
                                    if (duibaPointsVOQc == null) {
                                        logger.info("兑吧兑换结果通知接口（失败时设置订单无效）根据订单号查询积分明细表(订单取消)为空 继续 orderNum：" + orderNum);
                                        // 查询用户最新积分
                                        User user = userService.findUserByUserId(duibaPointsVO.getUserId());
                                        Integer points = user.getPointsCurrent() + duibaPointsVO.getPoints();
                                        if (user != null) {
                                            logger.info("兑吧兑换结果通知接口（失败时设置订单无效）查询用户最新积分不为空 继续 orderNum：" + orderNum);
                                            // 更新积分
                                            User userUp = new User();
                                            userUp.setUserId(user.getUserId());
                                            userUp.setPointsCurrent(points);
                                            int userUpCont = userService.updateUserById(userUp);
                                            if (userUpCont == 0) {
                                                throw new Exception("【兑吧】根据userid：[" + duibaPointsVO.getUserId() + "]，对用户的金币进行返还，没有更新到用户信息，回滚操作失败！");
                                            } else {
                                                // 插入积分明细
                                                DuibaPointsVO duibaPoints = new DuibaPointsVO();
                                                // 用户id
                                                duibaPoints.setUserId(Integer.valueOf(duibaOrderVOStr.getUserId()));
                                                // 用户名
                                                duibaPoints.setUserName(duibaOrderVOStr.getUserName());
                                                // 用户真实姓名
                                                duibaPoints.setTrueName(duibaOrderVOStr.getTrueName());
                                                // 操作积分数
                                                duibaPoints.setPoints(duibaPointsVO.getPoints());
                                                // 用户操作后总积分
                                                duibaPoints.setTotal(points);
                                                // 积分类型 0:获取，1:使用
                                                duibaPoints.setType(0);
                                                // 积分业务类型  0出借 1商品兑换 2订单取消
                                                duibaPoints.setBusinessName(2);
                                                // 兑吧订单号
                                                duibaPoints.setDuibaOrderId(duibaOrderVOStr.getDuibaOrderId());
                                                // 汇盈订单号
                                                duibaPoints.setHyOrderId(duibaOrderVOStr.getHyOrderId());
                                                duibaPointsListService.insertDuibaPoints(duibaPoints);
                                                logger.info("兑吧兑换结果通知接口（失败时设置订单无效）插入积分明细 orderNum：" + orderNum);
                                            }
                                        } else {
                                            logger.error("【兑吧】根据userid查询用户表数据为空，操作失败！userid：" + duibaPointsVO.getUserId());
                                            response.setResultStr("error");
                                        }
                                    } else {
                                        logger.error("【兑吧】此订单回滚已完成，无需再次回滚！userid：" + duibaPointsVO.getUserId());
                                    }
                                } else {
                                    logger.error("【兑吧】根据订单号查询积分明细表数据为空，操作失败！orderNum：" + orderNum);
                                    response.setResultStr("error");
                                }
                                // - - - - - - - - - - - - - - - - （失败）回滚用户积分，加积分 - start - - - - - - - - - - - - - -
                                logger.info("兑吧兑换结果通知接口（失败时设置订单无效）扣积分业务已完成 orderNum：" + orderNum);
                            }
                        }
                        logger.info("兑吧兑换结果通知接口（失败时设置订单无效）开始配置订单表信息 orderNum："+orderNum);
                        duibaOrderVO.setId(duibaOrderVOStr.getId());
                        // 设置订单无效
                        duibaOrderVO.setActivationType(1);
                        // 设置更新日期
                        duibaOrderVO.setUpdateTime(new Date());
                        // 设置订单状态为失败
                        duibaOrderVO.setOrderStatus(1);
                        // 判断该笔订单是否为“充值”（优惠券）
                        if (productTypeCZ.equals(duibaOrderVOStr.getProductType())) {
                            // 更新虚拟商品充值状态
                            if (StringUtils.isNotEmpty(errorMessage)) {
                                duibaOrderVO.setRechargeState("失败（" + errorMessage + "）");
                            }
                        }
                        // 设置失败原因
                        duibaOrderVO.setRemark("兑吧兑换结果通知接口返回失败，回滚该笔数据，操作失败！");
                        logger.info("兑吧兑换结果通知接口（失败时设置订单无效）开始更新订单表 orderNum："+orderNum);
                        // 完成时间
                        duibaOrderVO.setCompletionTime(GetDate.getNowTime10());
                        // 根据订单信息更新订单状态
                        res = duibaOrderListService.updateOneOrderByPrimaryKey(duibaOrderVO);
                        if (res == 0) {
                            throw new Exception("【兑吧】根据兑吧订单表id：[" + duibaOrderVOStr.getId() + "]，更新订单状态设置订单为无效，没有更新到订单信息，回滚操作失败！");
                        }
                        response.setResultStr("success");
                        logger.info("兑吧兑换结果通知接口（失败时设置订单无效）调用兑吧接口返回"+duiBaCallResultBean.getStatus()+" orderNum："+orderNum);
                    }else{
                        // 调用订单同步接口，返回订单状态为成功！
                        response.setResultStr(this.success(orderNum).getResultStr());
                        logger.info("兑吧兑换结果通知接口（失败时设置订单无效）调用兑吧接口返回"+duiBaCallResultBean.getStatus()+" orderNum："+orderNum);
                    }
                }
            } else {
                logger.error("【兑吧】回滚操作失败！，操作失败，没有查询到兑吧订单信息 订单id：" + orderNum);
                response.setResultStr("error");
            }
        } catch (Exception e) {
            logger.error("【兑吧】回滚操作失败！，操作失败，异常如下：" + e.getMessage());
            response.setResultStr("error");
        }
        logger.info("兑吧兑换结果通知接口（失败时设置订单无效）结束 orderNum："+orderNum+"__ResultStr："+response.getResultStr());
        return response;
    }

    /**
     * 兑吧兑换结果通知接口（成功设置优惠券有效，更新虚拟商品充值状态为完成）
     *
     * @param orderNum
     * @return
     */
    @RequestMapping("/success/{orderNum}")
    public StringResponse success(@PathVariable String orderNum) {
        StringResponse response = new StringResponse();
        // 商品类型为充值
        String productTypeCZ = "2";
        // 商品类型为实物
        String productTypeSW = "0";
        // 卡密和优惠券
        String productTypeKM = "1";
        // 执行更新影响行数
        int flagCount = 0;
        // 更新虚拟商品充值状态为完成
        DuibaOrderVO duibaOrderVO = new DuibaOrderVO();
        // 根据兑吧订单号查询订单信息
        DuibaOrderVO duibaOrderVOStr = duibaOrderListService.selectOrderByOrderId(orderNum);
        if (duibaOrderVOStr != null) {
            // 判断该笔订单是否为“充值”（优惠券）
            if (productTypeCZ.equals(duibaOrderVOStr.getProductType())) {
                CouponUserRequest couponUserRequest = new CouponUserRequest();
                couponUserRequest.setId(duibaOrderVOStr.getCouponUserId());
                // 更新优惠券用户表为有效
                flagCount = couponUserService.updateCouponUserDelFlag(couponUserRequest);
                // 虚拟商品充值状态(充值)
                duibaOrderVO.setRechargeState("处理完成");
            }
            // 虚拟商品充值状态(卡密优惠券)
            if(productTypeKM.equals(duibaOrderVOStr.getProductType())){
                // 虚拟商品充值状态(卡密优惠券)
                duibaOrderVO.setRechargeState("处理完成");
            }
            // 判断如果为实物则更改已发货状态
            if(productTypeSW.equals(duibaOrderVOStr.getProductType())){
                duibaOrderVO.setDeliveryStatus(1);
            }
            duibaOrderVO.setId(duibaOrderVOStr.getId());
            // 更新订单为有效
            duibaOrderVO.setActivationType(0);
            // 更新订单状态为成功
            duibaOrderVO.setOrderStatus(0);
            // 完成时间
            duibaOrderVO.setCompletionTime(GetDate.getNowTime10());
            int orderFlag = duibaOrderListService.updateOneOrderByPrimaryKey(duibaOrderVO);
            if (flagCount > 0 && orderFlag > 0) {
                response.setResultStr("success");
            } else {
                response.setResultStr("error");
                logger.error("【兑吧】操作失败！更新优惠券用户表或更新虚拟商品充值状态失败 优惠券用户表id：" + duibaOrderVOStr.getCouponUserId());
            }
        } else {
            response.setResultStr("error");
            logger.error("【兑吧】操作失败！没有查询到兑吧订单信息 订单表id：" + orderNum);
        }
        return response;
    }
}
