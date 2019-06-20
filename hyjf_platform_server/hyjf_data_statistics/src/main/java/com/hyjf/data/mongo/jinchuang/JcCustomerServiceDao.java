/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.data.mongo.jinchuang;

import com.hyjf.data.bean.jinchuang.JcCustomerService;
import com.hyjf.data.mongo.BaseMongoDao;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author yaoyong
 * @version JcCustomerServiceDao, v0.1 2019/6/20 15:25
 */
@Repository
public class JcCustomerServiceDao extends BaseMongoDao<JcCustomerService> {
    @Override
    protected Class<JcCustomerService> getEntityClass() {
        return JcCustomerService.class;
    }

    public JcCustomerService getCustomerService() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "createTime"));
        return mongoTemplate.findOne(query, getEntityClass());
    }
}
