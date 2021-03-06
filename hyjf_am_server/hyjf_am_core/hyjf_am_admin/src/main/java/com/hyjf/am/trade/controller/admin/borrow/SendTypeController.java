package com.hyjf.am.trade.controller.admin.borrow;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.BorrowSendTypeResponse;
import com.hyjf.am.resquest.admin.BorrowSendTypeRequest;
import com.hyjf.am.trade.dao.model.auto.BorrowSendType;
import com.hyjf.am.trade.service.admin.borrow.SendTypeService;
import com.hyjf.am.vo.admin.BorrowSendTypeVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author by xiehuili on 2018/8/1.
 */
@RestController
@RequestMapping("/am-admin/config/sendtype")
public class SendTypeController {

    @Autowired
    private SendTypeService sendTypeService;
    private static Logger logger = LoggerFactory.getLogger(SendTypeController.class);
    /**
     * 分页查询配置中心发标复标
     * @return
     */
    @RequestMapping("/list")
    public BorrowSendTypeResponse selectSendTypeListByPage(@RequestBody BorrowSendTypeRequest adminRequest) {
        logger.info("发标复标列表..." + JSONObject.toJSON(adminRequest));
        BorrowSendTypeResponse  result=new BorrowSendTypeResponse();
        //查询发标复标条数
        int count = this.sendTypeService.selectSendTypeCount(new BorrowSendType(), -1, -1);
        if (count>0) {
            result.setCount(count);
            Paginator paginator = new Paginator(adminRequest.getCurrPage(), count,adminRequest.getPageSize()==0?10:adminRequest.getPageSize());
            //查询记录
            List<BorrowSendType> recordList = this.sendTypeService.selectSendTypeListByPage(new BorrowSendType(),paginator.getOffset(), paginator.getLimit());
            if(!CollectionUtils.isEmpty(recordList)){
                List<BorrowSendTypeVO> configList = CommonUtils.convertBeanList(recordList, BorrowSendTypeVO.class);
                result.setResultList(configList);
            }
        }
        return result;
    }
    /**
     * 查询发标复标详情
     * @return
     */
    @RequestMapping("/info/{sendCd}")
    public BorrowSendTypeResponse selectSendTypeInfo(@PathVariable String sendCd) {
        logger.info("发标复标详情..." + JSONObject.toJSON(sendCd));
        BorrowSendTypeResponse response = new BorrowSendTypeResponse();
        BorrowSendTypeVO borrowSendTypeVO=new BorrowSendTypeVO();
        //查询发标复标详情
        BorrowSendType borrowSendType = this.sendTypeService.selectSendTypeInfo(sendCd);
        if (null != borrowSendType) {
            BeanUtils.copyProperties(borrowSendType,borrowSendTypeVO);
            response.setResult(borrowSendTypeVO);
        }
        return response;
    }
    /**
     * 添加发标复标
     * @param req
     */
    @RequestMapping("/insert")
    public BorrowSendTypeResponse insertSendType(@RequestBody BorrowSendTypeRequest req) {
        BorrowSendTypeResponse resp = new BorrowSendTypeResponse();
        // 插入
        int cot = this.sendTypeService.insertSendType(req);
        if(cot > 0 ){
            resp.setRtn(Response.SUCCESS);
        }else{
            resp.setRtn(Response.FAIL);
            resp.setMessage("添加失败！");
        }
        return resp;
    }
    /**
     * 修改发标复标
     * @param req
     */
    @RequestMapping("/update")
    public BorrowSendTypeResponse updateSendType(@RequestBody BorrowSendTypeRequest req) {
        BorrowSendTypeResponse resp = new BorrowSendTypeResponse();
        // 插入
        int cot = this.sendTypeService.updateSendType(req);
        if(cot > 0 ){
            resp.setRtn(Response.SUCCESS);
        }else{
            resp.setRtn(Response.FAIL);
            resp.setMessage("修改失败！");
        }
        return resp;
    }
    /**
     * 删除发标复标
     * @param sendCd
     */
    @RequestMapping("/delete/{sendCd}")
    public BorrowSendTypeResponse deleteSendType(@PathVariable String sendCd) {
        BorrowSendTypeResponse resp = new BorrowSendTypeResponse();
        // 插入
        int cot = this.sendTypeService.deleteSendType(sendCd);
        if(cot > 0 ){
            resp.setRtn(Response.SUCCESS);
        }else{
            resp.setRtn(Response.FAIL);
            resp.setMessage("删除失败！");
        }
        return resp;
    }
}
