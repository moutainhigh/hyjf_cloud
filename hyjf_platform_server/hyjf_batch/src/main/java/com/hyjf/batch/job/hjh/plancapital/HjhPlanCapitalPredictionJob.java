/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.batch.job.hjh.plancapital;

import com.hyjf.am.response.BooleanResponse;
import com.hyjf.batch.job.BaseJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 汇计划资本预估统计定时任务 "0 30 23 * * ?"
 * @author liushouyi
 * @version HjhPlanCapitalJob, v0.1 2019/4/24 13:56
 */
@DisallowConcurrentExecution  // 禁止并发执行
public class HjhPlanCapitalPredictionJob extends BaseJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("HjhPlanCapitalPredictionJob: {} execute...", context.getJobDetail().getKey().getName());

        BooleanResponse result = restTemplate.getForEntity(
                "http://CS-TRADE/cs-trade/hjhPlanAutoCapital/autoCapitalPrediction", BooleanResponse.class).getBody();

        logger.info("HjhPlanCapitalPredictionJob execute end...");
    }
}
