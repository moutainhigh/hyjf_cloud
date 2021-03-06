package com.hyjf.admin.service;

import com.hyjf.admin.beans.BorrowCommonImage;
import com.hyjf.am.response.admin.AdminProtocolResponse;
import com.hyjf.am.resquest.admin.AdminProtocolRequest;
import com.hyjf.am.resquest.admin.AdminProtocolVersionRequest;
import com.hyjf.am.vo.trade.ProtocolTemplateVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author：yinhui
 * @Date: 2018/8/8  15:59
 */
public interface ProtocolService {

    /**
     * 获取全部列表分页
     *
     * @return
     */
    AdminProtocolResponse searchPage(AdminProtocolRequest request);

    /**
     * 根据协议id查询协议和版本
     *
     * @return
     */
    AdminProtocolResponse getProtocolTemplateById(AdminProtocolRequest request);

    /**
     * 查询协议模板数量
     *
     * @return
     */
    Integer getProtocolTemplateNum(AdminProtocolRequest request);

    /**
     * 添加协议模板
     *
     * @return
     */
    void insertProtocolTemplate(AdminProtocolRequest request,String userId);

    /**
     * 获得最新协议模版 前台展示信息
     *
     * @return
     */
    List<ProtocolTemplateVO> getNewInfo();

    /**
     * 修改协议模板
     *
     * @return
     */
    void updateProtocolTemplate(AdminProtocolRequest request,String userId);

    /**
     * 删除协议模板
     *
     */
    void deleteProtocolTemplate(AdminProtocolRequest request,String userId);

    /**
     * 资料上传
     *
     * @param request
     * @return
     * @throws Exception
     */
    LinkedList<BorrowCommonImage> uploadFile(HttpServletRequest request, HttpServletResponse response);


    AdminProtocolResponse updateExistAction(AdminProtocolVersionRequest form, String userId);

    Map<String,String> validatorFieldCheck(AdminProtocolRequest request,String protocolName, String versionNumber, String displayName, String protocolUrl, String protocolType, String oldDisplayName, String flagT);
}
