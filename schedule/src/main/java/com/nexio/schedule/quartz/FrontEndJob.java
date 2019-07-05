package com.nexio.schedule.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.nexio.schedule.config.QuartzJob;

@QuartzJob(group ="Test" ,name = "FrontEndJob", cronExp = "1/10 * * * * ?")
@Component
public class FrontEndJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
            logger.info("FrontEndJob Key ： {}", context.getJobDetail().getKey());
    }
}