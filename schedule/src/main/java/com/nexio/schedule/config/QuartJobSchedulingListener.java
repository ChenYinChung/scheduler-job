package com.nexio.schedule.config;


import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartJobSchedulingListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            loadAndRunQuartzJob(applicationContext);
        } catch (Exception e) {
            logger.error("startup error",e);
            System.exit(0);
        }
    }

    private void loadAndRunQuartzJob(ApplicationContext applicationContext) throws Exception {

        Map<String, Object> quartzJobBeans = applicationContext.getBeansWithAnnotation(QuartzJob.class);
        Set<String> beanNames = quartzJobBeans.keySet();

        beanNames.forEach(t -> {
            QuartzJobBean job = (QuartzJobBean) quartzJobBeans.get(t);
            if (Job.class.isAssignableFrom(job.getClass())) {
                try {
                    CronTriggerFactoryBean cronTriggerFactoryBean = buildCronTriggerFactoryBean(job);
                    JobDetailFactoryBean jobDetailFactoryBean = buidlJobDetailFactoryBean(job);
                    jobDetailFactoryBean.setApplicationContext(applicationContext);
                    jobDetailFactoryBean.afterPropertiesSet();

                    cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
                    cronTriggerFactoryBean.afterPropertiesSet();

                    if(!schedulerFactoryBean.getObject().checkExists(jobDetailFactoryBean.getObject().getKey())){
                        schedulerFactoryBean.getObject().scheduleJob(jobDetailFactoryBean.getObject(), cronTriggerFactoryBean.getObject());
                    }

                    schedulerFactoryBean.getObject().start();
                } catch (ParseException |SchedulerException pe) {
                    logger.error("CronTriggerFactoryBean error",pe);
                }

            }
        });
    }

    /**
     * create CronTrigger
     * @param job
     * @return
     */
    private CronTriggerFactoryBean buildCronTriggerFactoryBean(QuartzJobBean job) {

        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        QuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(job.getClass(), QuartzJob.class);
        cronTriggerFactoryBean.setCronExpression(quartzJobAnnotation.cronExp());
        cronTriggerFactoryBean.setName(quartzJobAnnotation.name() + "_TRIGGER");
        cronTriggerFactoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        return cronTriggerFactoryBean;
    }

    /**
     * create JobDetail
     * @param job
     * @return
     */
    private JobDetailFactoryBean buidlJobDetailFactoryBean(QuartzJobBean job) {
        QuartzJob quartzJob = AnnotationUtils.findAnnotation(job.getClass(), QuartzJob.class);
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setName(quartzJob.name());
        jobDetailFactoryBean.setJobClass(job.getClass());
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setBeanName(job.getClass().getName());
        jobDetailFactoryBean.setGroup(quartzJob.group());
        return jobDetailFactoryBean;
    }
}