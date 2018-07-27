package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.IntegrationConfigurationDTO;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.enums.scheduler.OperationType;
import com.kairos.scheduler.kafka.producer.KafkaProducer;
import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.scheduler.persistence.repository.IntegrationConfigurationRepository;
import com.kairos.scheduler.utils.BeanFactoryUtil;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
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
import java.util.*;

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



    private static final Logger logger = LoggerFactory.getLogger(DynamicCronScheduler.class);

    public String setCronScheduling(SchedulerPanel schedulerPanel, String timezone) {
        logger.debug("cron----> " + schedulerPanel.getCronExpression());
        CronTrigger trigger = null;
        if(!schedulerPanel.isOneTimeTrigger()) {
            trigger = new CronTrigger(schedulerPanel.getCronExpression(), TimeZone.getTimeZone(timezone));
        }
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        //threadPoolTaskScheduler.setThreadNamePrefix(schedulerPanel.getIntegrationConfigurationId().toString());
        threadPoolTaskScheduler.setThreadNamePrefix(schedulerPanel.getJobSubType().toString());

        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.initialize();
        Runnable runnable = getTask(schedulerPanel, trigger, TimeZone.getTimeZone(timezone));

        if(!schedulerPanel.isOneTimeTrigger()) {
            threadPoolTaskScheduler.schedule(runnable, trigger);
        }
        else {
            threadPoolTaskScheduler.schedule(runnable,DateUtils.asDate(schedulerPanel.getOneTimeTriggerDate()));
        }

        logger.info("Name of cron job is --> " + "scheduler" + schedulerPanel.getId());
        BeanFactoryUtil.registerSingleton("scheduler" + schedulerPanel.getId(), threadPoolTaskScheduler);
        logger.info("Name of cron job is --> " + "scheduler" + schedulerPanel.getId());

        return "scheduler" + schedulerPanel.getId();


    }

    public Date getNextExecutionTime(CronTrigger trigger, Date lastScheduledExecutionTime, TimeZone timeZone){
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


            ThreadPoolTaskScheduler scheduler2 = BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean(scheduler, ThreadPoolTaskScheduler.class);
            logger.info("scheduler2-----> "+scheduler2);
            if (scheduler2 != null){
                scheduler2.getScheduledExecutor().shutdownNow();
            }
        }catch (NoSuchBeanDefinitionException exception){
            logger.error("No bean registered for cron job, May be this is your first time to scheduling cron job!!");
        }

    }

    public void startCronJob(SchedulerPanel schedulerPanel, String timezone){
        try {
            String scheduler = "scheduler"+schedulerPanel.getId();
            logger.info("Start scheduler from BootStrap--> "+scheduler);
            CronTrigger trigger = null;
            if(!schedulerPanel.isOneTimeTrigger()) {
                trigger = new CronTrigger(schedulerPanel.getCronExpression(),  TimeZone.getTimeZone(timezone));
            }
            Runnable task = getTask(schedulerPanel,  trigger, TimeZone.getTimeZone(timezone));

            ThreadPoolTaskScheduler scheduler2 = BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean(scheduler, ThreadPoolTaskScheduler.class);
            logger.info("scheduler2-----> "+scheduler2);
            if (scheduler2 != null){
                scheduler2.initialize();
                if(!schedulerPanel.isOneTimeTrigger()) {
                    scheduler2.schedule(task, trigger);
                }
                else {
                    scheduler2.schedule(task,DateUtils.asDate(schedulerPanel.getOneTimeTriggerDate()));
                }
            }
        }catch (NoSuchBeanDefinitionException exception){
            logger.error("No bean registered for cron job, May be this is your first time to scheduling cron job!!");
        }

    }

    public Runnable getTask(SchedulerPanel schedulerPanel,  CronTrigger trigger, TimeZone timeZone){
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
                IntegrationConfigurationDTO integrationConfigurationDTO = null;
                if(Optional.ofNullable(schedulerPanel.getIntegrationConfigurationId()).isPresent()) {
                    integrationConfigurationDTO = new IntegrationConfigurationDTO();
                    Optional<IntegrationSettings> integrationConfiguration = integrationConfigurationRepository.findById(schedulerPanel.getIntegrationConfigurationId());
                    ObjectMapperUtils.copyProperties(integrationConfiguration.get(),integrationConfigurationDTO);
                }

                KairosSchedulerExecutorDTO jobToExecute = new KairosSchedulerExecutorDTO(schedulerPanel.getId(),schedulerPanel.getUnitId(),schedulerPanel.getJobType(), schedulerPanel.getJobSubType(),schedulerPanel.getEntityId(),
                        integrationConfigurationDTO,DateUtils.getMillisFromLocalDateTime(schedulerPanel.getOneTimeTriggerDate()));

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
