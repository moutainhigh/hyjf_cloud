/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.front.config.impl;

import com.hyjf.am.resquest.admin.AdminProtocolRequest;
import com.hyjf.am.resquest.admin.ProtocolLogRequest;
import com.hyjf.am.trade.dao.mapper.auto.ProtocolLogMapper;
import com.hyjf.am.trade.dao.mapper.auto.ProtocolTemplateMapper;
import com.hyjf.am.trade.dao.mapper.auto.ProtocolVersionMapper;
import com.hyjf.am.trade.dao.model.auto.*;
import com.hyjf.am.trade.service.front.config.ProtocolTemplateService;
import com.hyjf.am.vo.admin.ProtocolLogVO;
import com.hyjf.am.vo.admin.ProtocolTemplateCommonVO;
import com.hyjf.am.vo.admin.ProtocolVersionVO;
import com.hyjf.am.vo.trade.ProtocolTemplateVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.GetDate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Albert
 * @version ProtocolTemplateServiceImpl.java, v0.1 2018年7月26日 下午5:11:53
 */
@Service
public class ProtocolTemplateServiceImpl implements ProtocolTemplateService{

	
	@Autowired
	protected  ProtocolTemplateMapper protocolTemplateMapper;

	@Autowired
	private ProtocolVersionMapper protocolVersionMapper;

	@Autowired
	private ProtocolLogMapper protocolLogMapper;

	@Override
	public List<ProtocolTemplateVO> getProtocolTemplateVOByDisplayName(String displayName) {
		List<ProtocolTemplateVO> volist = null;
        ProtocolTemplateExample examplev = new ProtocolTemplateExample();
        ProtocolTemplateExample.Criteria criteria = examplev.createCriteria();
        criteria.andDisplayNameEqualTo(displayName);
        criteria.andStatusEqualTo(1);
        List<ProtocolTemplate> list = protocolTemplateMapper.selectByExample(examplev);
        if(CollectionUtils.isNotEmpty(list)){
        	volist = CommonUtils.convertBeanList(list, ProtocolTemplateVO.class);
        	return volist;
        }
		return null;
	}

	/**
	 * 统计全部个数
	 *
	 * @return
	 */
	@Override
	public Integer countRecord(AdminProtocolRequest request) {
		Integer count=0;
		ProtocolTemplateExample example = new ProtocolTemplateExample();
		ProtocolTemplateExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(1);
		List<ProtocolTemplate> protocolTemplates=protocolTemplateMapper.selectByExample(example);
		if(!org.springframework.util.CollectionUtils.isEmpty(protocolTemplates)){
			count=protocolTemplates.size();
		}
		return count;
	}

	/**
	 * 获取全部列表
	 *
	 * @return
	 */
	@Override
	public List<ProtocolTemplateCommonVO> getRecordList(AdminProtocolRequest request){
		List<ProtocolTemplateCommonVO> recordList = new ArrayList<>();
		//查询所有协议
		ProtocolTemplateExample example=new ProtocolTemplateExample();
		if (request.getLimitStart() != -1) {
			example.setLimitStart(request.getLimitStart());
			example.setLimitEnd(request.getLimitEnd());
		}
		ProtocolTemplateExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(1);
		// 条件查询
		example.setOrderByClause("`id` Desc,`create_time` ASC");
		List<ProtocolTemplate> protocolTemplates=protocolTemplateMapper.selectByExample(example);
		if(!org.springframework.util.CollectionUtils.isEmpty(protocolTemplates)){
			for(int i=0;i< protocolTemplates.size() ;i++){
				ProtocolTemplateCommonVO protocolTemplateCommon=new ProtocolTemplateCommonVO();
				ProtocolTemplateVO protocolTemplateVO = new ProtocolTemplateVO();
				//时间显示转换
				Date updateTime= protocolTemplates.get(i).getUpdateTime();
				String time = GetDate.dateToString2(updateTime, "yyyy-MM-dd HH:mm:ss");
				protocolTemplateCommon.setUpdateTime(time);
				BeanUtils.copyProperties(protocolTemplates.get(i),protocolTemplateVO);
				protocolTemplateCommon.setProtocolTemplateVO(protocolTemplateVO);
				recordList.add(protocolTemplateCommon);
			}
		}
		return recordList;
	}

	/**
	 * 根据协议id查询协议和版本
	 *
	 * @return
	 */
	@Override
	public ProtocolTemplateCommonVO getProtocolTemplateById(Integer id){
		ProtocolTemplateCommonVO protocolTemplateCommon=new ProtocolTemplateCommonVO();
		ProtocolTemplateVO protocolTemplateVO = new ProtocolTemplateVO();
		ProtocolVersionVO protocolVersionVO = new ProtocolVersionVO();
		List<ProtocolVersionVO> listProtocolVersionVO = new ArrayList<>();
		//根据协议的id查询协议
		ProtocolTemplate protocolTemplate= protocolTemplateMapper.selectByPrimaryKey(id);
		if(protocolTemplate != null){
			//时间显示转换
			Date t= protocolTemplate.getUpdateTime();


			String time = GetDate.dateToString2(t, "yyyy-MM-dd HH:mm:ss");
			protocolTemplateCommon.setUpdateTime(time);
			BeanUtils.copyProperties(protocolTemplate,protocolTemplateVO);
			protocolTemplateCommon.setProtocolTemplateVO(protocolTemplateVO);
			String protocolId= protocolTemplate.getProtocolId();
			if(StringUtils.isNotBlank(protocolId)){
				//根据protocolId查询版本
				ProtocolVersionExample protocolVersionExample=  new ProtocolVersionExample();
				ProtocolVersionExample.Criteria create=protocolVersionExample.createCriteria();
				create.andProtocolIdEqualTo(protocolId);
				List<ProtocolVersion> protocolVersions =protocolVersionMapper.selectByExample(protocolVersionExample);
				if( !org.springframework.util.CollectionUtils.isEmpty(protocolVersions)){
					for(int i=0;i<protocolVersions.size();i++){
						//时间显示转换
						Date updateTime= null;
						Integer updateUserId=0;
						if(protocolVersions.get(i).getUpdateUser().intValue() != 0){
							updateTime= protocolVersions.get(i).getUpdateTime();
							updateUserId= protocolVersions.get(i).getUpdateUser();
						}else{
							updateTime= protocolVersions.get(i).getCreateTime();
							updateUserId= protocolVersions.get(i).getCreateUser();
						}
						protocolVersions.get(i).setTime(GetDate.dateToString2(updateTime, "yyyy-MM-dd HH:mm:ss"));
						BeanUtils.copyProperties(protocolVersions.get(i),protocolVersionVO);
						listProtocolVersionVO.add(protocolVersionVO);
					}

					protocolTemplateCommon.setProtocolVersion(listProtocolVersionVO);
				}
			}
		}
		return protocolTemplateCommon;
	}

	/**
	 * 查询协议模板数量
	 *
	 * @return
	 */
	@Override
	public Integer getProtocolTemplateNum(AdminProtocolRequest request) {
		ProtocolTemplateExample example=new ProtocolTemplateExample();
		int num=0;
		List<ProtocolTemplate> lists=protocolTemplateMapper.selectByExample(example);
		if(!org.springframework.util.CollectionUtils.isEmpty(lists)){
			num=lists.size()+1;
		}
		return num;
	}

	/**
	 * 根据协议id查询协议和版本
	 *
	 * @return
	 */
	@Override
	public ProtocolTemplateCommonVO getProtocolTemplateByProtocolName(AdminProtocolRequest request){
		ProtocolTemplateCommonVO protocolTemplateCommonVO = new ProtocolTemplateCommonVO();
		ProtocolTemplateVO protocolTemplateVO = new ProtocolTemplateVO();
		ProtocolTemplateExample example=new ProtocolTemplateExample();
		example.createCriteria().andProtocolNameEqualTo(request.getProtocolTemplateVO().getProtocolName()).andStatusEqualTo(0);
		List<ProtocolTemplate> list=protocolTemplateMapper.selectByExample(example);
		if(!CollectionUtils.isEmpty(list)){
			ProtocolTemplate protocol = list.get(0);
			BeanUtils.copyProperties(protocol,protocolTemplateVO);
			protocolTemplateCommonVO.setProtocolTemplateVO(protocolTemplateVO);
		}
		return protocolTemplateCommonVO;
	}

	/**
	 * 保存 协议模板、协议版本、协议日志
	 *
	 * @return
	 */
	@Override
	public Integer insert(AdminProtocolRequest request){
		ProtocolTemplateCommonVO recordList = request.getRecordList().get(0);

		int i =0;
		//保存协议模板
		if(recordList.getProtocolTemplateVO() != null) {
			ProtocolTemplate protocolTemplate = new ProtocolTemplate();
			BeanUtils.copyProperties(recordList.getProtocolTemplateVO(),protocolTemplate);
			protocolTemplateMapper.insertSelective(protocolTemplate);
			i++;
		}

		//新增协议版本
		if(recordList.getProtocolVersion().size()>0) {
			ProtocolVersion protocolVersion = new ProtocolVersion();
			BeanUtils.copyProperties(recordList.getProtocolVersion().get(0),protocolVersion);
			protocolVersionMapper.insertSelective(protocolVersion);
			i++;
		}

		//新增协议日志
		if(recordList.getProtocolLog().size()>0) {
			ProtocolLog protocolLog = new ProtocolLog();
			BeanUtils.copyProperties(recordList.getProtocolLog().get(0),protocolLog);
			protocolLogMapper.insertSelective(protocolLog);
			i++;
		}

		return i;
	}

	/**
	 * 修改 协议模板
	 *
	 * @return
	 */
	@Override
	public Integer updateProtocolTemplate(AdminProtocolRequest request){
		ProtocolTemplate protocolTemplate = new ProtocolTemplate();
		BeanUtils.copyProperties(request.getRecordList().get(0).getProtocolTemplateVO(),protocolTemplate);
		return protocolTemplateMapper.updateByPrimaryKeySelective(protocolTemplate);
	}

	/**
	 * 修改 之前的版本的启用状态改成不启用
	 *
	 * @return
	 */
	@Override
	public Integer updateDisplayFlag(AdminProtocolRequest request){
		ProtocolTemplateVO protocolTemplateVO = request.getRecordList().get(0).getProtocolTemplateVO();
		//之前的版本的启用状态改成不启用
		ProtocolVersionExample examplev = new ProtocolVersionExample();
		ProtocolVersionExample.Criteria criteriav = examplev.createCriteria();
		criteriav.andProtocolIdEqualTo(protocolTemplateVO.getProtocolId()).andDisplayFlagEqualTo(1);
		List<ProtocolVersion> listsv= protocolVersionMapper.selectByExample(examplev);
		if(!org.springframework.util.CollectionUtils.isEmpty(listsv)){
			ProtocolVersion protocolVersion=listsv.get(0);
			protocolVersion.setDisplayFlag(0);
			protocolVersionMapper.updateByPrimaryKey(protocolVersion);
		}
		//根据协议模板名称和协议版本号查询版本表
		ProtocolVersionExample example = new ProtocolVersionExample();
		ProtocolVersionExample.Criteria criteria = example.createCriteria();
		criteria.andProtocolIdEqualTo(protocolTemplateVO.getProtocolId()).andVersionNumberEqualTo(protocolTemplateVO.getVersionNumber());
		List<ProtocolVersion> lists = protocolVersionMapper.selectByExample(example);
		if(!CollectionUtils.isEmpty(lists)){
			//2.21 存在，修改协议版本，设置为启用状态，
			ProtocolVersion protocolVersion= lists.get(0);
			protocolVersion.setDisplayFlag(1);
			protocolVersionMapper.updateByPrimaryKey(protocolVersion);
		}

		return lists.size();
	}

	/**
	 * 删除协议模板
	 * @param request
	 * @return
	 */
	@Override
	public ProtocolTemplateCommonVO deleteProtocolTemplate(AdminProtocolRequest request){
		ProtocolTemplateCommonVO protocolTemplateCommonVO = new ProtocolTemplateCommonVO();
		//根据协议的id查询协议
		ProtocolTemplateVO vo = request.getProtocolTemplateVO();
		ProtocolTemplate protocolTemplate= protocolTemplateMapper.selectByPrimaryKey(vo.getId());
		if(protocolTemplate != null){
			ProtocolTemplateVO protocolTemplateVO = new ProtocolTemplateVO();
			//将协议模板设置为删除状态状态（0：删除状态）
			ProtocolTemplate protocolTemplateNew=protocolTemplate;
			protocolTemplateNew.setUpdateUserId(Integer.valueOf(request.getIds()));
			protocolTemplateNew.setStatus(0);
			protocolTemplateMapper.updateByPrimaryKeySelective(protocolTemplateNew);
			String protocolId= protocolTemplate.getProtocolId();
			if(StringUtils.isNotBlank(protocolId)){
				//根据protocolId查询正在启用的版本号
				ProtocolVersionExample protocolVersionExample=  new ProtocolVersionExample();
				ProtocolVersionExample.Criteria create=protocolVersionExample.createCriteria();
				create.andProtocolIdEqualTo(protocolId).andDisplayFlagEqualTo(1);
				List<ProtocolVersion> protocolVersions =protocolVersionMapper.selectByExample(protocolVersionExample);
				if( !CollectionUtils.isEmpty(protocolVersions)){
					//启用的版本设置为不启用状态
					ProtocolVersion persion=protocolVersions.get(0);
					persion.setUpdateUser(Integer.valueOf(request.getIds()));
					persion.setDisplayFlag(0);
					protocolVersionMapper.updateByPrimaryKey(persion);
				}

				BeanUtils.copyProperties(protocolTemplate,protocolTemplateVO);
				protocolTemplateCommonVO.setProtocolTemplateVO(protocolTemplateVO);
			}
		}
		return protocolTemplateCommonVO;
	}

	@Override
	public Integer countRecordLog(ProtocolLogRequest request) {
		Integer count=0;
		ProtocolLogExample protocolLogExample =new ProtocolLogExample();
		ProtocolLogExample.Criteria criteria = protocolLogExample.createCriteria();
		List<ProtocolLog> protocolLogs=protocolLogMapper.selectByExample(protocolLogExample);
		if(!org.springframework.util.CollectionUtils.isEmpty(protocolLogs)){
			count=protocolLogs.size();
		}
		return count;
	}

	@Override
	public List<ProtocolLogVO> getProtocolLogVOAll(ProtocolLogRequest request) {
		List<ProtocolLogVO> listVO = new ArrayList<>();

		//查询所有协议
		ProtocolLogExample example=new ProtocolLogExample();

		ProtocolLogExample.Criteria criteria = example.createCriteria();
		// 条件查询
		example.setOrderByClause("`id` Desc,`create_time` ASC");
		List<ProtocolLog> protocolLogs=protocolLogMapper.selectByExample(example);
		ProtocolLogVO protocolLogVO = null;
		for (ProtocolLog protocolLog : protocolLogs){
			protocolLogVO = new ProtocolLogVO();
			BeanUtils.copyProperties(protocolLog,protocolLogVO);
			listVO.add(protocolLogVO);
		}
		return listVO;

	}

}