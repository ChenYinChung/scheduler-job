package com.nexio.schedule.quartz;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.nexio.schedule.config.QuartzJob;

@QuartzJob(name = "Hello2Job", cronExp = "2/10 * * * * ?")
public class Hello2Job extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void executeInternal(JobExecutionContext context){
        logger.info("Job Key ： {}", context.getJobDetail().getKey());
    }
}