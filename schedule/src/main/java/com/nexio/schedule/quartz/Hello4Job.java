package com.nexio.schedule.quartz;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alibaba.fastjson.JSON;
import com.nexio.schedule.config.QuartzJob;
import com.nexio.schedule.util.PropertiesUtils;

@QuartzJob(name = "Hello4Job", cronExp = "4/10 * * * * ?")
public class Hello4Job extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void executeInternal(JobExecutionContext context){
        logger.info("Job Key ： {}", context.getJobDetail().getKey());
    }
}