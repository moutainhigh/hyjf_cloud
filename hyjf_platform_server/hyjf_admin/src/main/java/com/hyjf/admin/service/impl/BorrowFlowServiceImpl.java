package com.hyjf.admin.service.impl;

import com.hyjf.admin.client.AmConfigClient;
import com.hyjf.admin.client.AmTradeClient;
import com.hyjf.admin.client.BorrowFlowClient;
import com.hyjf.admin.service.BorrowFlowService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.AdminBorrowFlowResponse;
import com.hyjf.am.response.config.ParamNameResponse;
import com.hyjf.am.resquest.admin.AdminBorrowFlowRequest;
import com.hyjf.am.vo.admin.HjhAssetTypeVO;
import com.hyjf.am.vo.config.ParamNameVO;
import com.hyjf.am.vo.trade.borrow.BorrowProjectTypeVO;
import com.hyjf.am.vo.trade.hjh.HjhAssetBorrowTypeVO;
import com.hyjf.am.vo.user.HjhInstConfigVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by xiehuili on 2018/7/30.
 */
@Service
public class BorrowFlowServiceImpl implements BorrowFlowService {
    @Autowired
    private BorrowFlowClient borrowFlowClient;
    @Autowired
    private AmTradeClient amTradeClient;
    @Autowired
    private AmConfigClient amConfigClient;


    /**
     * 项目类型
     *
     * @return
     */
    @Override
    public List<BorrowProjectTypeVO> borrowProjectTypeList(String borrowTypeCd){
        return borrowFlowClient.borrowProjectTypeList(borrowTypeCd);
    }

    /**
     * 资金来源
     * @param instCode
     * @return
     */
    @Override
    public List<HjhInstConfigVO> hjhInstConfigList(String instCode){
        return borrowFlowClient.hjhInstConfigList(instCode);
    }

    /**
     * 根据表的类型,期数,项目类型检索管理费件数
     * @author liubin
     * @param instCode assetType
     * @return
     */
    @Override
    public int countRecordByPK(String instCode, Integer assetType){
        return borrowFlowClient.countRecordByPK(instCode,assetType);
    }
    /**
     * 根据资金来源查询产品类型
     * @param instCode
     * @return
     */
    @Override
    public List<HjhAssetTypeVO> hjhAssetTypeList(String instCode){
        return borrowFlowClient.hjhAssetTypeList(instCode);
    }
    /**
     * 分页查询
     * @param adminRequest
     * @return
     */
    @Override
    public AdminBorrowFlowResponse selectBorrowFlowList(AdminBorrowFlowRequest adminRequest){
        List<HjhAssetBorrowTypeVO>  hjhAssetBorrowType=new ArrayList<>();
        ParamNameResponse amResponse = amConfigClient.getNameCd("FLOW_STATUS");
        AdminBorrowFlowResponse response= amTradeClient.selectBorrowFlowList(adminRequest);
        if( Response.isSuccess(amResponse)&& Response.isSuccess(response)){
            List<ParamNameVO>  paramNameS =amResponse.getResultList();
            List<HjhAssetBorrowTypeVO>  hjhAssetBorrowTypeVOS =response.getResultList();
            if( !CollectionUtils.isEmpty(hjhAssetBorrowTypeVOS)&&!CollectionUtils.isEmpty(paramNameS)){
                for(int i=0;i<paramNameS.size();i++){
                    for(int j=0;j<hjhAssetBorrowTypeVOS.size();j++){
                        if(paramNameS.get(i).getNameCd().equals(String.valueOf(hjhAssetBorrowTypeVOS.get(j).getIsOpen()))){
                            hjhAssetBorrowTypeVOS.get(j).setStatus(paramNameS.get(i).getName());
                            hjhAssetBorrowType.add(hjhAssetBorrowTypeVOS.get(j));
                        }
                    }
                }
            }
            response.setResultList(hjhAssetBorrowType);
            return response;
        }else{
            response.setRtn(Response.FAIL);
        }
        return response;
    }
    /**
     * 详情
     * @param adminRequest
     * @return
     */
    @Override
    public AdminBorrowFlowResponse selectBorrowFlowInfo(AdminBorrowFlowRequest adminRequest){
        return borrowFlowClient.selectBorrowFlowInfo(adminRequest);
    }
    /**
     * 添加
     * @param adminRequest
     * @return
     */
    @Override
    public void insertRecord(AdminBorrowFlowRequest adminRequest){
         borrowFlowClient.insertRecord(adminRequest);
    }
    /**
     * 修改
     * @param adminRequest
     * @return
     */
    @Override
    public void updateRecord(AdminBorrowFlowRequest adminRequest){
        borrowFlowClient.updateRecord(adminRequest);
    }
    /**
     * 删除
     * @param adminRequest
     * @return
     */
    @Override
   public void deleteRecord(AdminBorrowFlowRequest adminRequest){
        borrowFlowClient.deleteRecord(adminRequest);
    }

}
