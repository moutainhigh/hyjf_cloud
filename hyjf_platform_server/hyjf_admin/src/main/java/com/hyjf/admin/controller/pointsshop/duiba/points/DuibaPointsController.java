/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.pointsshop.duiba.points;

import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.pointsshop.duiba.points.DuibaPointsService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.DuibaPointsUserResponse;
import com.hyjf.am.resquest.admin.DuibaPointsRequest;
import com.hyjf.am.vo.admin.DuibaPointsModifyVO;
import com.hyjf.am.vo.admin.DuibaPointsUserVO;
import com.hyjf.am.vo.config.AdminSystemVO;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.GetOrderIdUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 兑吧积分账户列表
 *
 * @author PC-LIUSHOUYI
 * @version DuibaPointsController, v0.1 2019/5/29 9:46
 */
@Api(value = "积分商城-兑吧积分账户", tags = "积分商城-兑吧积分账户")
@RestController
@RequestMapping("/hyjf-admin/duiba/points")
public class DuibaPointsController extends BaseController {

    @Autowired
    DuibaPointsService duibaPointsService;

    private static final String PERMISSIONS = "dbpoints";

    @ApiOperation(value = "兑吧积分账户查询", notes = "兑吧积分账户查询")
    @PostMapping("/selectDuibaPointsUser")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult<ListResult<DuibaPointsUserVO>> selectDuibaPointsUser(@RequestBody DuibaPointsRequest requestBean) {
        DuibaPointsUserResponse response = duibaPointsService.selectDuibaPointsUser(requestBean);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        List<DuibaPointsUserVO> vos = new ArrayList<DuibaPointsUserVO>();
        if (null != response.getResultList() && response.getResultList().size() > 0) {
            vos = CommonUtils.convertBeanList(response.getResultList(), DuibaPointsUserVO.class);
        }
        return new AdminResult<>(ListResult.build(vos, response.getRecordTotal()));
    }

    @ApiOperation(value = "兑吧批量调整积分", notes = "兑吧批量调整积分")
    @PostMapping("/modifyPointsByUserList")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_MODIFY)
    public AdminResult modifyPointsByUserList(HttpServletRequest request, @RequestBody DuibaPointsRequest requestBean) {

        // 获取操作人id
        AdminSystemVO adminSystemVO = this.getUser(request);
        requestBean.setModifyName(adminSystemVO.getUsername());
        requestBean.setModifyId(adminSystemVO.getId());
        // 判断参数
        if (null == requestBean.getModifyType()) {
            return new AdminResult<>(FAIL, "请勾选调整类型！");
        }
        if (null == requestBean.getModifyPoints() || 0 == requestBean.getModifyPoints()) {
            return new AdminResult<>(FAIL, "请填写调整积分数额！");
        }
        if (null == requestBean.getUserIdList() || requestBean.getUserIdList().size() <= 0) {
            return new AdminResult<>(FAIL, "请勾选调整用户！");
        }

        // 调减的情况
        if (1 == requestBean.getModifyType()) {
            // 查询这些用户是否够扣分的
            boolean re = this.duibaPointsService.selectRemainPoints(requestBean);
            if (!re) {
                return new AdminResult<>(FAIL, "批量调整积分记录生成失败（存在用户积分不足）");
            }
        }

        // 记录到积分调整列表
        // 后期接入工作流后、在审批表创建相应审批节点
        List<Integer> userIdList = requestBean.getUserIdList();
        for (Integer userId : userIdList) {
            UserVO user = duibaPointsService.searchUserByUserId(userId);
            if (null == user) {
                logger.error("获取用户信息失败，userId：" + userId);
                return new AdminResult<>(FAIL, "批量调整积分记录生成失败（存在用户信息不存在）");
            }

            UserInfoVO userInfo = duibaPointsService.findUsersInfoById(userId);
            if (null == userInfo) {
                logger.error("获取用户详细信息失败，userId：" + userId);
                return new AdminResult<>(FAIL, "批量调整积分记录生成失败（存在用户详细信息不存在）");
            }

            // 计算用户积分调整后剩余
            Integer remainPoints = 0;
            if (0 == requestBean.getModifyType()) {
                remainPoints = requestBean.getModifyPoints() + user.getPointsCurrent();
            } else {
                if (user.getPointsCurrent() < requestBean.getModifyPoints()) {
                    logger.error("数据插入过程中发现用户积分不足，userId：" + userId + ",调减积分数：" + requestBean.getModifyPoints() + ",当前用户积分数：" + user.getPointsCurrent());
                    continue;
                }
                remainPoints = user.getPointsCurrent() - requestBean.getModifyPoints();
            }

            // 生成一笔订单号
            String modifyOrderId = GetOrderIdUtils.getOrderId2(userId);

            DuibaPointsModifyVO duibaPointsModify = new DuibaPointsModifyVO();
            duibaPointsModify.setUserId(userId);
            duibaPointsModify.setUserName(user.getUsername());
            duibaPointsModify.setTrueName(userInfo.getTruename());
            duibaPointsModify.setModifyOrderId(modifyOrderId);
            duibaPointsModify.setPoints(requestBean.getModifyPoints());
            duibaPointsModify.setTotal(remainPoints);
            duibaPointsModify.setPointsType(requestBean.getModifyType());
            duibaPointsModify.setModifyName(requestBean.getModifyName());
            duibaPointsModify.setModifyReason(requestBean.getReason());
            // 当前审批节点、默认0
            duibaPointsModify.setFlowOrder(0);
            // 审批状态：0待审批
            duibaPointsModify.setStatus(0);
            duibaPointsModify.setCreateBy(requestBean.getModifyId());
            duibaPointsModify.setCreateTime(new Date());
            duibaPointsModify.setUpdateBy(requestBean.getModifyId());
            duibaPointsModify.setUpdateTime(new Date());
            boolean re = this.duibaPointsService.insertPointsModifyList(duibaPointsModify);
            if (!re) {
                logger.error("数据插入过程中数据报错，userId：" + userId + ",调减积分数：" + requestBean.getModifyPoints() + ",当前用户积分数：" + user.getPointsCurrent());
            }
        }
        return new AdminResult<>(SUCCESS, SUCCESS_DESC);
    }

}
