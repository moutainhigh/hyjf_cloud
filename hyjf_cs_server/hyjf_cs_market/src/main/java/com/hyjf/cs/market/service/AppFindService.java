/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.market.service;

import com.hyjf.am.vo.config.ContentArticleCustomizeVO;
import com.hyjf.am.vo.config.ContentArticleVO;

import java.util.List;
import java.util.Map;

/**
 * @author fq
 * @version AppFindService, v0.1 2018/7/20 9:49
 */
public interface AppFindService extends BaseMarketService {

    /**
     * 查询文章条数
     * @param params
     * @return
     */
    Integer countContentArticleByType(Map<String,Object> params);

    /**
     * 查询文章列表
     * @param params
     * @return
     */
    List<ContentArticleCustomizeVO> getContentArticleListByType(Map<String,Object> params);

    /**
     * 根据文章id查找文章
     * @param contentArticleId
     * @return
     */
    ContentArticleVO getContentArticleById(Integer contentArticleId);
}