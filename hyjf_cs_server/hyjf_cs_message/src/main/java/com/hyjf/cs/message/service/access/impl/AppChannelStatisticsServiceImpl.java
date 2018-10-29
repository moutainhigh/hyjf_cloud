package com.hyjf.cs.message.service.access.impl;

import com.hyjf.am.resquest.admin.AppChannelStatisticsRequest;
import com.hyjf.am.vo.datacollect.AppAccesStatisticsVO;
import com.hyjf.am.vo.datacollect.AppChannelStatisticsDetailVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.GetDate;
import com.hyjf.cs.message.bean.ic.AppAccesStatistics;
import com.hyjf.cs.message.bean.ic.AppChannelStatisticsDetail;
import com.hyjf.cs.message.mongo.ic.AppAccesStatisticsDao;
import com.hyjf.cs.message.mongo.ic.AppChannelStatisticsDetailDao;
import com.hyjf.cs.message.service.access.AppChannelStatisticsService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author：yinhui
 * @Date: 2018/10/22  16:39
 */
@Service
public class AppChannelStatisticsServiceImpl implements AppChannelStatisticsService {

    @Autowired
    private AppAccesStatisticsDao appAccesStatisticsDao;

    @Autowired
    private AppChannelStatisticsDetailDao appChannelStatisticsDetailDao;

    /**
     * 根据开始时间、结束时间和来源查询数据
     * @param request
     * @return
     */
    @Override
    public List<AppAccesStatisticsVO> getAppAccesStatisticsVO(AppChannelStatisticsRequest request){
        String timeStartSrch = request.getTimeStartSrch();
        String timeEndSrch = request.getTimeEndSrch();
        String sourceId = request.getSourceId();

        DBObject obj = new BasicDBObject();
        DBObject object = new BasicDBObject();

        try{
            //开始时间查询
            if(StringUtils.isNotBlank(timeStartSrch)){
                object.put("$gte", GetDate.stringToDate(timeStartSrch));
                obj.put("accessTime",object);
            }

            //结束时间查询
            if(StringUtils.isNotBlank(timeEndSrch)){
                object.put("$lte",GetDate.stringToDate(timeEndSrch));
                obj.put("accessTime",object);
            }

            if(StringUtils.isNotBlank(sourceId)){
                obj.put("sourceId",Integer.valueOf(sourceId));
            }

            Query query = new BasicQuery(obj.toString());
            List<AppAccesStatistics> list = appAccesStatisticsDao.getAppAccesStatistics(query);
            List<AppAccesStatisticsVO> appAccesStatisticsVO = CommonUtils.convertBeanList(list, AppAccesStatisticsVO.class);

            return appAccesStatisticsVO;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据开始时间、结束时间和来源查询数据
     * @param request
     * @return
     */
    @Override
    public List<AppChannelStatisticsDetailVO> getAppChannelStatisticsDetailVO(AppChannelStatisticsRequest request){
        String timeStartSrch = request.getTimeStartSrch();
        String timeEndSrch = request.getTimeEndSrch();
        String sourceId = request.getSourceId();

        DBObject obj = new BasicDBObject();
        DBObject object = new BasicDBObject();

        try{
            if("registerTime".equals(request.getSourceIdSrch())){

                //开始时间查询
                if(StringUtils.isNotBlank(timeStartSrch)){
                    object.put("$gte", GetDate.stringToDate(timeStartSrch));
                    obj.put("registerTime",object);
                }

                //结束时间查询
                if(StringUtils.isNotBlank(timeEndSrch)){
                    object.put("$lte",GetDate.stringToDate(timeEndSrch));
                    obj.put("registerTime",object);
                }
            }

            if("openAccountTime".equals(request.getSourceIdSrch())){

                //开始时间查询
                if(StringUtils.isNotBlank(timeStartSrch)){
                    object.put("$gte", GetDate.stringToDate(timeStartSrch));
                    obj.put("openAccountTime",object);
                }

                //结束时间查询
                if(StringUtils.isNotBlank(timeEndSrch)){
                    object.put("$lte",GetDate.stringToDate(timeEndSrch));
                    obj.put("openAccountTime",object);
                }
            }

            if(StringUtils.isNotBlank(sourceId)){
                obj.put("sourceId",Integer.valueOf(sourceId));
            }

            Query query = new BasicQuery(obj.toString());
            List<AppChannelStatisticsDetail> list = appChannelStatisticsDetailDao.getAppChannelStatisticsDetail(query);
            List<AppChannelStatisticsDetailVO> appAccesStatisticsVO = CommonUtils.convertBeanList(list, AppChannelStatisticsDetailVO.class);

            return appAccesStatisticsVO;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}