/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.batch.job.hjh.autoendcredit;

import com.hyjf.batch.job.BaseJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 汇计划自动结束前一天未完全承接的债转Job
 * @author liuyang
 * @version HjhAutoEndCreditJob, v0.1 2018/6/28 15:30
 */
@DisallowConcurrentExecution
public class HjhAutoEndCreditJob extends BaseJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("HjhAutoEndCreditJob: {} execute...", jobExecutionContext.getJobDetail().getKey().getName());
        restTemplate.getForEntity("http://CS-TRADE/cs-trade/hjhAutoEndCredit/hjhAutoEndCredit", String.class);
        logger.info("HjhAutoEndCreditJob execute end...");
    }
}
