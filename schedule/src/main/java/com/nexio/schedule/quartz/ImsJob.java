package com.nexio.schedule.quartz;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alibaba.fastjson.JSON;
import com.nexio.schedule.config.QuartzJob;
import com.nexio.schedule.util.PropertiesUtils;

@QuartzJob(name = "ImsJob", cronExp = "1/10 * * * * ?")
public class ImsJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void executeInternal(JobExecutionContext context)throws JobExecutionException {
        logger.info("ImsJob Key ： {}", context.getJobDetail().getKey());
    }
}