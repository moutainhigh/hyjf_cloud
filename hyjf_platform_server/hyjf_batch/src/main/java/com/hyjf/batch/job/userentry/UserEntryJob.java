/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.batch.job.userentry;

import com.hyjf.batch.job.BaseJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangjun
 * @version UserEntryJob, v0.1 2018/6/12 11:58
 * 员工入职，修改客户属性定时任务
 */
@DisallowConcurrentExecution
public class UserEntryJob extends BaseJob implements Job {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("UserEntryJob: {} execute...", context.getJobDetail().getKey().getName());
        restTemplate.getForEntity("http://CS-USER//cs-user/batch/entryUpdate", String.class);
        logger.info("UserEntryJob execute end...");
    }
}
