package com.kairos.scheduler.service.scheduler_panel;


import com.kairos.commons.utils.BeanFactoryUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.scheduler.IntegrationSettingsDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.scheduler.kafka.producer.KafkaProducer;
import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.scheduler.persistence.repository.scheduler_panel.IntegrationConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import static com.kairos.scheduler.constants.AppConstants.activitySubTypes;
import static com.kairos.scheduler.constants.AppConstants.userSubTypes;



/**
 * Created by oodles on 11/1/17.
 */

@Service
public class DynamicCronScheduler implements  DisposableBean  {


    @Inject
    private SchedulerPanelService schedulerPanelService;



    @Inject
    private KafkaProducer kafkaProducer;

    @Inject
    private IntegrationConfigurationRepository integrationConfigurationRepository;
    @Inject
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;



    private static final Logger logger = LoggerFactory.getLogger(DynamicCronScheduler.class);

    public String setCronScheduling(SchedulerPanel schedulerPanel, String timezone) {
        logger.debug("cron----> " + schedulerPanel.getCronExpression());
        CronTrigger trigger = null;
        if(!schedulerPanel.isOneTimeTrigger()) {
            trigger = new CronTrigger(schedulerPanel.getCronExpression(), TimeZone.getTimeZone(timezone));
        }

        ScheduledFuture<?> future;
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.initialize();
        Runnable runnable = getTask(schedulerPanel, trigger, TimeZone.getTimeZone(timezone));

        if(!schedulerPanel.isOneTimeTrigger()) {
           future =  threadPoolTaskScheduler.schedule(runnable, trigger);
        }
        else {

            future = threadPoolTaskScheduler.schedule(runnable,DateUtils.asDate(schedulerPanel.getOneTimeTriggerDate()));
        }

        logger.info("Name of cron job is --> " + "scheduler" + schedulerPanel.getId());
        BeanFactoryUtil.registerSingleton("scheduler" + schedulerPanel.getId(), future);
        logger.info("Name of cron job is --> " + "scheduler" + schedulerPanel.getId());

        return "scheduler" + schedulerPanel.getId();


    }

    private Date getNextExecutionTime(CronTrigger trigger, Date lastScheduledExecutionTime, TimeZone timeZone){
        TriggerContext triggerContext = getTriggerContext(lastScheduledExecutionTime);
        triggerContext.lastActualExecutionTime();
        return trigger.nextExecutionTime(triggerContext);
    }

    private static TriggerContext getTriggerContext(Date lastCompletionTime) {
        SimpleTriggerContext context = new SimpleTriggerContext();
        context.update(null, null, lastCompletionTime);
        return context;
    }

    public void stopCronJob(String scheduler){
        try {
             logger.info("Check scheduler --> "+scheduler);

             ScheduledFuture<?> future =  BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean(scheduler, ScheduledFuture.class);


            if (future != null){
                future.cancel(true);
                BeanFactoryUtil.getDefaultListableBeanFactory().destroySingleton(scheduler);
            }
        }catch (NoSuchBeanDefinitionException exception){
            logger.error("No bean registered for cron job, May be this is your first time to scheduling cron job!!");
        }

    }

    public void startCronJob(SchedulerPanel schedulerPanel, String timezone){

            String scheduler = "scheduler"+schedulerPanel.getId();
            logger.info("Start scheduler from BootStrap--> "+scheduler);
            CronTrigger trigger = null;
            if(!schedulerPanel.isOneTimeTrigger()) {
                trigger = new CronTrigger(schedulerPanel.getCronExpression(),  TimeZone.getTimeZone(timezone));
            }
        ScheduledFuture<?> future;
            Runnable task = getTask(schedulerPanel,  trigger, TimeZone.getTimeZone(timezone));

           /* ThreadPoolTaskScheduler scheduler2 = BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean(scheduler, ThreadPoolTaskScheduler.class);*/
            if(!schedulerPanel.isOneTimeTrigger()) {
                 future =  threadPoolTaskScheduler.schedule(task, trigger);
            }
            else {
                 future =  threadPoolTaskScheduler.schedule(task,DateUtils.asDate(schedulerPanel.getOneTimeTriggerDate()));
            }

        BeanFactoryUtil.registerSingleton("scheduler" + schedulerPanel.getId(), future);

    }

    /**
     * This method is useed to execute a job.
     * @param schedulerPanel
     * @param trigger
     * @param timeZone
     * @return
     */
    private Runnable getTask(SchedulerPanel schedulerPanel,  CronTrigger trigger, TimeZone timeZone){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.info("control pannel exist--> " + schedulerPanel.getId());
                schedulerPanel.setLastRunTime(DateUtils.getCurrentDate());
                if(!schedulerPanel.isOneTimeTrigger()) {
                    schedulerPanel.setNextRunTime(getNextExecutionTime(trigger, schedulerPanel.getLastRunTime(), timeZone));
                }
                else {
                    schedulerPanel.setNextRunTime(DateUtils.asDate(schedulerPanel.getOneTimeTriggerDate()));
                }
                schedulerPanelService.setScheduleLastRunTime(schedulerPanel);
                IntegrationSettingsDTO integrationSettingsDTO = null;
                if(Optional.ofNullable(schedulerPanel.getIntegrationConfigurationId()).isPresent()) {
                    integrationSettingsDTO = new IntegrationSettingsDTO();
                    Optional<IntegrationSettings> integrationConfiguration = integrationConfigurationRepository.findById(schedulerPanel.getIntegrationConfigurationId());
                    ObjectMapperUtils.copyProperties(integrationConfiguration.get(), integrationSettingsDTO);
                }

                KairosSchedulerExecutorDTO jobToExecute = new KairosSchedulerExecutorDTO(schedulerPanel.getId(),schedulerPanel.getUnitId(),schedulerPanel.getJobType(), schedulerPanel.getJobSubType(),schedulerPanel.getEntityId(),
                        integrationSettingsDTO,DateUtils.getMillisFromLocalDateTime(schedulerPanel.getOneTimeTriggerDate()));

                if(userSubTypes.contains(jobToExecute.getJobSubType())) {
                    kafkaProducer.pushToUserQueue(jobToExecute);
                }
                else if(activitySubTypes.contains(jobToExecute.getJobSubType())) {
                    kafkaProducer.pushToActivityQueue(jobToExecute);
                }
            }
        };

        return runnable;

    }

    public void destroy(){
        List<SchedulerPanel> schedulerPanels = schedulerPanelService.getAllControlPanels();
        if(schedulerPanels.size() != 0) {
            for (SchedulerPanel schedulerPanel : schedulerPanels) {
                logger.info("Shutdown Cron Job of process name " + schedulerPanel.getName());
                stopCronJob("scheduler"+schedulerPanel.getId());
            }
        }
    }




}
