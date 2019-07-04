package com.nexio.schedule.service;

import java.util.Date;
import java.util.List;
import java.util.Set;


import org.apache.commons.collections.CollectionUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class JobService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Scheduler scheduler;


    /**
     * 新新增一個排程作業
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClazz
     * @param cron
     * @param dataMap
     * @return
     * @throws SchedulerException
     */
    public boolean addCronJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
        Class<? extends Job> jobClazz, String cron, JobDataMap dataMap) throws SchedulerException {

        logger.info("新增一個job");
        JobKey jobKey = new JobKey(jobName, jobGroupName);
        logger.info("校驗job是否存在");
        boolean flag = isExistJob(jobKey);
        if(flag){
            logger.error("已存在該任務，jobName："+jobName+" jobGroupName:"+jobGroupName);
            return false;
        }

        boolean checkResult = isValidExpression(cron);
        if(!checkResult){
            logger.error("非法的cron表示式，cron："+cron);
        }


        JobDetail jobDetail = JobBuilder.newJob(jobClazz).withIdentity(jobKey).usingJobData(dataMap).build();
        TriggerKey triggerKey = new TriggerKey(triggerName,triggerGroupName);
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
            .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
        cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        scheduler.scheduleJob(jobDetail,cronTrigger);
        scheduler.start();
        return true;
    }


    /**
     * 檢查一個job是否存在
     * @param jobKey                jobKey
     * @return                      true存在，false不存在
     * @throws SchedulerException
     */
    public boolean isExistJob(JobKey jobKey) throws SchedulerException {
        return  CollectionUtils.isNotEmpty(scheduler.getTriggersOfJob(jobKey));
    }

    /**
     * 是否存在job
     * @param jobName           jobName
     * @param jobGroupName      jobGroupName
     * @return
     * @throws SchedulerException
     */
    public boolean isExistJob( String jobName,  String jobGroupName) throws SchedulerException {
        return  isExistJob(new JobKey(jobName, jobGroupName));
    }

    /**
     * 判斷是否是正確的cron quarz 表示式
     * @param cronExpression  cron表示式
     * @return boolean
     */
    public boolean isValidExpression(String cronExpression){
        return cronExpression != null && CronExpression.isValidExpression(cronExpression);
    }



    /**
     * 更改一個job的排程時間
     * @param triggerName
     * @param triggerGroupName
     * @param cron
     * @return
     * @throws SchedulerException
     */
    public boolean modifyJobTriggerTime(String triggerName,String triggerGroupName,String cron) throws SchedulerException {

        boolean flag = isValidExpression(cron);
        if(flag){
            logger.error("不是一個正確的cron表示式");
            return false;
        }

        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupStartsWith(triggerGroupName));
        if(CollectionUtils.isEmpty(triggerKeys)){
            logger.error("找不到觸發器");
            return false;
        }
        for(TriggerKey triggerKey : triggerKeys){
            scheduler.unscheduleJob(triggerKey);
        }

        TriggerKey triggerKey = new TriggerKey(triggerName,triggerGroupName);

        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(new TriggerKey(triggerName, triggerGroupName))
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build();
        CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
        cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        scheduler.rescheduleJob(triggerKey,cronTrigger);
        return true;
    }

    /**
     * 移除一個job
     * @param jobName
     * @param jobGroupName
     * @return
     * @throws SchedulerException
     */
    public boolean removeJob(String jobName,String jobGroupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName,jobGroupName);
        return scheduler.deleteJob(jobKey);
    }

    /**
     * 暫停一個作業
     * @param jobName
     * @param groupName
     * @return
     * @throws SchedulerException
     */
    public boolean pauseJob(String jobName ,String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.pauseJob(jobKey);
        return true;
    }

    /**
     * 恢復一個作業
     * @param jobName
     * @param groupName
     * @return
     * @throws SchedulerException
     */
    public boolean resumeJob(String jobName ,String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.resumeJob(jobKey);
        return true;
    }

    public boolean shutDownScheduler() throws SchedulerException {
        scheduler.shutdown(true);
        return true;
    }



    /**
     * 獲取下次的執行時間列表
     * @param cron      cron表示式
     * @param numTimes  多少個
     * @return          Date
     */
    public static List<Date> getNextFireTimeDate(String cron,Integer numTimes) {

        List<Date> dates = null;
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null,numTimes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dates;
    }

    /**
     * 得到正在執行job的taskIds
     * @return
     * @throws SchedulerException
     */
    public List<Integer> getCurrentExecutingTaskIds()  {
        List<Integer> taskIds = Lists.newArrayList();
        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            if(CollectionUtils.isNotEmpty(jobContexts)){
                for (JobExecutionContext context: jobContexts ) {
                    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
                    // String taskId = (String) jobDataMap.get(Constants.TASK_ID);
                    // taskIds.add(Integer.valueOf(taskId));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  taskIds;
    }

    /**
     * 獲取trigger狀態:
     * None：Trigger已經完成，且不會在執行，或者找不到該觸發器，或者Trigger已經被刪除
     * NORMAL:   正常狀態
     * PAUSED：  暫停狀態
     * COMPLETE：觸發器完成，但是任務可能還正在執行中
     * BLOCKED： 執行緒阻塞狀態
     * ERROR：   出現錯誤
     * @param triggerName
     * @param triggerGroup
     * @return
     * @throws SchedulerException
     */
    public String getTriggerState(String triggerName,String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(triggerName,triggerGroup);
        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
        return triggerState.name();

    }
}