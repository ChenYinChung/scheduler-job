package com.nexio.schedule.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.nexio.schedule.config.QuartzJob;

@QuartzJob(name = "MobileJob", cronExp = "4/10 * * * * ?")
public class MobileJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("MobileJob Key ： {}", context.getJobDetail().getKey());
    }
}