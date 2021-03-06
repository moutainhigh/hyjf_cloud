package com.hyjf.am.user.service.front.crm.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hyjf.am.user.dao.mapper.customize.UserCrmInfoCustomizeMapper;
import com.hyjf.am.user.dao.model.auto.ROaDepartment;
import com.hyjf.am.user.dao.model.auto.ROaDepartmentExample;
import com.hyjf.am.user.service.front.crm.CrmDepartmentService;
import com.hyjf.am.user.service.impl.BaseServiceImpl;

/**
 * @Description crm oa Department 表同步
 * @Author sunss
 * @Date 2018/7/26 13:52
 */
@Service
public class CrmDepartmentServiceImpl extends BaseServiceImpl implements CrmDepartmentService {
    @Resource
    private UserCrmInfoCustomizeMapper userCrmInfoCustomizeMapper;

    /**
     * 修改
     *
     * @param department
     * @return
     */
    @Override
    public Integer update(ROaDepartment department) {
        return rOaDepartmentMapper.updateByPrimaryKeySelective(department);
    }

    /**
     * 新增
     *
     * @param department
     * @return
     */
    @Override
    public Integer insert(ROaDepartment department) {
        return rOaDepartmentMapper.insertSelective(department);
    }

    /**
     * 删除
     *
     * @param department
     * @return
     */
    @Override
    public Integer delete(Integer department) {
        return rOaDepartmentMapper.deleteByPrimaryKey(department);
    }

    /**
     * 根据DepartmentExample 修改
     *
     * @param example
     */
    @Override
    public void updateByExample(ROaDepartment department, ROaDepartmentExample example) {
        rOaDepartmentMapper.updateByExample(department, example);
    }

    @Override
    public List<String> selectTwoDivisionByPrimaryDivision(String primaryDivision) {
        return userCrmInfoCustomizeMapper.selectTwoDivisionByPrimaryDivision(primaryDivision);
    }
}
