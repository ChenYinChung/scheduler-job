package com.nexio.schedule.quartz;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.nexio.schedule.config.QuartzJob;

@QuartzJob(group ="Test" ,name = "InfraJob", cronExp = "3/10 * * * * ?")
@Component
public class InfraJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void executeInternal(JobExecutionContext context) {
        logger.info("InfraJob Key ： {}", context.getJobDetail().getKey());
    }
}