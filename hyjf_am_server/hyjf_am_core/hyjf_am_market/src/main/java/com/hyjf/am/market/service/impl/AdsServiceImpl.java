package com.hyjf.am.market.service.impl;

import com.hyjf.am.market.dao.mapper.auto.AdsMapper;
import com.hyjf.am.market.dao.model.auto.Ads;
import com.hyjf.am.market.dao.model.auto.AdsExample;
import com.hyjf.am.market.service.AdsService;
import com.hyjf.common.util.GetDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author xiasq
 * @version AdsImpl, v0.1 2018/4/19 15:42
 */
@Service
public class AdsServiceImpl implements AdsService {

	@Autowired
	AdsMapper adsMapper;

	@Override
	public void updateActivityEndStatus() {
		// 检索进行中活动列表
		List<Ads> adsList = this.selectAdsList();

		if (!CollectionUtils.isEmpty(adsList)) {
			for (Ads ads : adsList) {
				// 取得活动结束时间
				Integer endTime = GetDate.dateString2Timestamp(ads.getEndTime());
				// 活动时间小于当前时间
				if (endTime <= GetDate.getMyTimeInMillis()) {
					// 将活动结束状态更新为:已结束
					ads.setIsEnd(1);
					adsMapper.updateByPrimaryKey(ads);
				}
			}
		}
	}

	private List<Ads> selectAdsList() {
		AdsExample example = new AdsExample();
		AdsExample.Criteria cra = example.createCriteria();
		// 是否结束状态:0:进行中,1:已结束
		cra.andIsEndEqualTo(0);
		// 状态:0:启用
		cra.andStatusEqualTo( 1);
		cra.andTypeIdEqualTo(9);
		return adsMapper.selectByExample(example);
	}

	@Override
	public Ads findActivityById(Integer id) {
		Ads ads = adsMapper.selectByPrimaryKey(1);
		if (ads != null){
			return ads;
		}
		return null;
	}
}
