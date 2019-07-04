package com.nexio.schedule.config;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.nexio.schedule.util.PropertiesUtils;

@Component
public class QuartJobSchedulingListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
        SchedulerFactoryBean schedulerFactoryBean =  buildSchedulerFactoryBean();
        schedulerFactoryBean.afterPropertiesSet();

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

    private CronTriggerFactoryBean buildCronTriggerFactoryBean(QuartzJobBean job) {

        QuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(job.getClass(), QuartzJob.class);
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(quartzJobAnnotation.cronExp());
        cronTriggerFactoryBean.setName(quartzJobAnnotation.name() + "_TRIGGER");
        cronTriggerFactoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        return cronTriggerFactoryBean;
    }

    private JobDetailFactoryBean buidlJobDetailFactoryBean(QuartzJobBean job) {
        QuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(job.getClass(), QuartzJob.class);
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setName(quartzJobAnnotation.name());
        jobDetailFactoryBean.setJobClass(job.getClass());
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setBeanName(job.getClass().getName());

        return jobDetailFactoryBean;
    }

    private SchedulerFactoryBean buildSchedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setQuartzProperties(PropertiesUtils.quartzProperties());
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        return schedulerFactoryBean;
    }



}