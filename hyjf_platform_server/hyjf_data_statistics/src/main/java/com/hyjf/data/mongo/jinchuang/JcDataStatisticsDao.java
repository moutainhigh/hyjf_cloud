/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.data.mongo.jinchuang;

import com.hyjf.data.bean.JcDataStatistics;
import com.hyjf.data.mongo.BaseMongoDao;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yaoyong
 * @version JcDataStatisticsDao, v0.1 2019/6/20 10:06
 */
@Repository
public class JcDataStatisticsDao extends BaseMongoDao<JcDataStatistics> {
    @Override
    protected Class<JcDataStatistics> getEntityClass() {
        return JcDataStatistics.class;
    }

    public List<JcDataStatistics> getDataStatistics() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "createTime"));
        query.limit(2);
        return mongoTemplate.find(query, getEntityClass());
    }
}
