/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.admin.impl;

import com.hyjf.am.resquest.admin.ProtocolsRequest;
import com.hyjf.am.trade.dao.mapper.auto.FddTempletMapper;
import com.hyjf.am.trade.dao.mapper.customize.FddTempletCustomizeMapper;
import com.hyjf.am.trade.dao.model.auto.FddTemplet;
import com.hyjf.am.trade.dao.model.auto.FddTempletExample;
import com.hyjf.am.trade.dao.model.customize.FddTempletCustomize;
import com.hyjf.am.trade.service.admin.ProtocolsService;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.GetDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fuqiang
 * @version ProtocolsServiceImpl, v0.1 2018/7/10 18:15
 */
@Service
public class ProtocolsServiceImpl implements ProtocolsService {
	@Resource
	private FddTempletMapper fddTempletMapper;
	@Resource
	private FddTempletCustomizeMapper fddTempletCustomizeMapper;
    private static final String TEMPLET_ID_REFIX = "HYHT";

	@Override
	public int getFddTempletCount() {
		return fddTempletCustomizeMapper.countFddTemplet();
	}

	@Override
	public List<FddTempletCustomize> selectFddTempletList(ProtocolsRequest request,boolean flag) {
		FddTempletCustomize fddTemplet = new FddTempletCustomize();
		if (request.getLimitStart() != -1 && flag == true) {
			fddTemplet.setLimitStart(request.getLimitStart());
			fddTemplet.setLimitEnd(request.getLimitEnd());
		}
		return fddTempletCustomizeMapper.selectFddTempletList(fddTemplet);
	}

	@Override
	public void insertAction(ProtocolsRequest request) {
		FddTemplet record = new FddTemplet();
		BeanUtils.copyProperties(request, record);
		// 获取当前模板的下载地址
		String key = RedisConstants.TEMPLATE_UPLOAD_URL + record.getTempletId();
		if(RedisUtils.exists(key)){
			String httpUrl = RedisUtils.get(key);
			if(StringUtils.isNotBlank(httpUrl)){
				record.setFileUrl(httpUrl);
			}
		}
		int nowTime = GetDate.getNowTime10();
		//认证时间
		record.setCertificateTime(nowTime);
		// 登陆信息
		record.setCreateTime(new Date());
		fddTempletMapper.insertSelective(record);
	}

	@Override
	public int updateAction(ProtocolsRequest request) {
		String templetId = request.getTempletId();
		FddTempletExample example = new FddTempletExample();
		example.createCriteria().andTempletIdEqualTo(templetId);
		List<FddTemplet> list = fddTempletMapper.selectByExample(example);
		if (!CollectionUtils.isEmpty(list)) {
			FddTemplet fddTemplet = list.get(0);
			fddTemplet.setRemark(request.getRemark());
			fddTemplet.setIsActive(request.getIsActive());
			return fddTempletMapper.updateByPrimaryKeySelective(fddTemplet);
		}
		return 0;
	}

	@Override
	public String getNewTempletId(Integer protocolType) {
		String templetId = null;
		templetId = fddTempletCustomizeMapper.getMaxTempletId(protocolType);
		if (templetId == null){
			return TEMPLET_ID_REFIX + String.format("%02d",protocolType) + String.format("%04d",1);
		}
		String preFixStr = templetId.substring(0, templetId.length()-4);
		String postSNStr = templetId.substring(templetId.length()-4);
		postSNStr = String.format("%04d",Integer.parseInt(postSNStr) + 1);
		return preFixStr + postSNStr;
	}

    /**
     * 协议管理-画面迁移
     *
     * @param id
     * @return
     */
    @Override
    public FddTempletCustomize getRecordInfoById(Integer id) {
        FddTempletCustomize list = new FddTempletCustomize();
        FddTemplet fddTemplet = new FddTemplet();
        fddTemplet.setId(id);
        FddTemplet fddTempletT = fddTempletMapper.selectByPrimaryKey(id);
        if (fddTemplet != null) {
            list = CommonUtils.convertBean(fddTempletT, FddTempletCustomize.class);
        }
        return list;
    }
}
