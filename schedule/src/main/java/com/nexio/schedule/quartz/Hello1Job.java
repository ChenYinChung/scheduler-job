package com.nexio.schedule.quartz;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.nexio.schedule.config.QuartzJob;

@QuartzJob(name = "Hello1Job", cronExp = "6/10 * * * * ?")
@Component
public class Hello1Job extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void executeInternal(JobExecutionContext context) {
            logger.info("Job Key ： {}", context.getJobDetail().getKey());
    }
}