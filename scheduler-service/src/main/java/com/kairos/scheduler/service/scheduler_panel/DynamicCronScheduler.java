package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.config.env.EnvConfig;
import com.kairos.dto.IntegrationConfigurationDTO;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.enums.scheduler.OperationType;
//import com.kairos.persistence.model.organization.Organization;
//import com.kairos.persistence.model.user.control_panel.ControlPanel;
//import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.scheduler.config.env.EnvConfig;
import com.kairos.scheduler.kafka.producer.KafkaProducer;
import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationConfiguration;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.scheduler.persistence.repository.IntegrationConfigurationRepository;
import com.kairos.scheduler.utils.BeanFactoryUtil;
//import com.kairos.service.control_panel.ControlPanelService;
//import com.kairos.util.BeanFactoryUtil;
//import com.kairos.util.DateUtil;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
//import com.kairos.util.timeCareShift.Transstatus;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.*;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Scheduler;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

//import static com.kairos.constants.AppConstants.*;
import static com.kairos.scheduler.constants.AppConstants.*;

/**
 * Created by oodles on 11/1/17.
 */

@Service
public class DynamicCronScheduler implements  DisposableBean  {


    @Inject
    private SchedulerPanelService schedulerPanelService;

    @Inject
    private EnvConfig envConfig;

    @Inject
    private KafkaProducer kafkaProducer;

    @Inject
    private IntegrationConfigurationRepository integrationConfigurationRepository;



    private static final Logger logger = LoggerFactory.getLogger(DynamicCronScheduler.class);

    public String setCronScheduling(SchedulerPanel schedulerPanel){
        logger.debug("cron----> " + schedulerPanel.getCronExpression());
        CronTrigger trigger = new CronTrigger(schedulerPanel.getCronExpression(), TimeZone.getTimeZone("Denmark"));
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix(schedulerPanel.getIntegrationConfigurationId().toString());
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.initialize();
        Runnable runnable = getTask(schedulerPanel, trigger, TimeZone.getTimeZone("Denmark"));
        threadPoolTaskScheduler.schedule(runnable, trigger);

        logger.info("Name of cron job is --> " + "scheduler" + schedulerPanel.getId());
        BeanFactoryUtil.registerSingleton("scheduler" + schedulerPanel.getId(), threadPoolTaskScheduler);
        //   context.getBeanFactory().registerSingleton("scheduler" + controlPanel.getId(), threadPoolTaskScheduler);
        //    context.register(ThreadPoolTaskScheduler.class);
        //      context.refresh();
        logger.info("Name of cron job is --> " + "scheduler" + schedulerPanel.getId());
        //  context.close();

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

    public void startCronJob(SchedulerPanel schedulerPanel){
        try {
            String scheduler = "scheduler"+schedulerPanel.getId();
            logger.info("Start scheduler from BootStrap--> "+scheduler);
            CronTrigger trigger = new CronTrigger(schedulerPanel.getCronExpression(),  TimeZone.getTimeZone("Denmark"));
            Runnable task = getTask(schedulerPanel,  trigger, TimeZone.getTimeZone("Denmark"));

            ThreadPoolTaskScheduler scheduler2 = BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean(scheduler, ThreadPoolTaskScheduler.class);
            logger.info("scheduler2-----> "+scheduler2);
            if (scheduler2 != null){
                scheduler2.initialize();
                scheduler2.schedule(task, trigger);
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
                schedulerPanel.setNextRunTime(getNextExecutionTime(trigger, schedulerPanel.getLastRunTime(), timeZone));
                schedulerPanelService.setScheduleLastRunTime(schedulerPanel);
                IntegrationConfigurationDTO integrationConfigurationDTO = new IntegrationConfigurationDTO();
                if(Optional.ofNullable(schedulerPanel.getIntegrationConfigurationId()).isPresent()) {
                    Optional<IntegrationConfiguration> integrationConfiguration = integrationConfigurationRepository.findById(schedulerPanel.getIntegrationConfigurationId());
                    ObjectMapperUtils.copyProperties(integrationConfiguration.get(),integrationConfigurationDTO);
                }
                KairosSchedulerExecutorDTO jobToExecute = new KairosSchedulerExecutorDTO(JobType.FUNCTIONAL, JobSubType.INTEGRATION,null,OperationType.EXECUTE,
                        integrationConfigurationDTO);

                kafkaProducer.pushToQueue(jobToExecute);




             /*   String plainClientCredentials = "cluster:cluster";
                String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));


                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
                headers.add("Authorization", "Basic " + base64ClientCredentials);
                HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
                String importShiftStatusXMLURI = envConfig.getCarteServerHost()+KETTLE_TRANS_STATUS;
                //   String startDate = DateFormatUtils.format(DateUtil.getCurrentDate(), "yyyy-MM-dd");
                //     String endDate = DateFormatUtils.format(DateUtil.addWeeksInDate(DateUtil.getCurrentDate(), 5), "yyyy-MM-dd");
                //  String startDate = DateFormatUtils.format(controlPanel.getStartDateMillis(), "yyyy-MM-dd");
                //  String endDate = DateFormatUtils.format(controlPanel.getEndDateMillis(), "yyyy-MM-dd");
                Long workplaceId = Long.valueOf(String.valueOf("15"));
                if(schedulerPanel.getUnitId() != null){
                    Organization organization = organizationGraphRepository.findOne(schedulerPanel.getUnitId());
                    if(organization.getExternalId() != null) workplaceId = Long.valueOf(organization.getExternalId());
                }
                String importShiftURI = "";
                int weeks = 35;
                String uniqueKey = schedulerPanel.getIntegrationConfiguration().getUniqueKey();
                logger.info("uniqueKey----> "+uniqueKey);
                RestTemplate restTemplate = new RestTemplate();
                switch(uniqueKey){
                    case IMPORT_TIMECARE_SHIFTS:
                        logger.info("!!===============Hit to carte server from Kairos==============!!");
                        importShiftURI = envConfig.getCarteServerHost()+KETTLE_EXECUTE_TRANS+IMPORT_TIMECARE_SHIFTS_PATH+"&intWorkPlaceId="+workplaceId+"&weeks="+weeks+"&jobId="+schedulerPanel.getId();
                        logger.info("importShiftURI----> "+importShiftURI);
                        Date started = DateUtil.getCurrentDate();
                        ResponseEntity<String> importResult = restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        if (importResult.getStatusCodeValue() == 200) {
                            ResponseEntity<String> resultStatusXml = restTemplate.exchange(importShiftStatusXMLURI, HttpMethod.GET, entity, String.class);
                            Date stopped = DateUtil.getCurrentDate();
                            try {
                                JAXBContext jaxbContext = JAXBContext.newInstance(Transstatus.class);
                                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                                StringReader reader = new StringReader(resultStatusXml.getBody());
                                Transstatus transstatus = (Transstatus) jaxbUnmarshaller.unmarshal(reader);
                                logger.info("trans status---> " + transstatus.getId());

                                controlPanelService.createJobScheduleDetails(schedulerPanel, transstatus, started, stopped);
                            } catch (JAXBException exception) {
                                logger.info("trans status---exception > " + exception);
                            }
                            catch(IOException exception){
                                logger.info("exception while logging job details-- > " + exception);
                            }

                        }
                        break;
                    case IMPORT_KMD_CITIZEN:
                        importShiftURI = envConfig.getServerHost()+KMD_CARE_CITIZEN_URL+schedulerPanel.getUnitId();
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;
                    case IMPORT_KMD_CITIZEN_NEXT_TO_KIN:
                        importShiftURI = envConfig.getServerHost()+API_KMD_CARE_CITIZEN_RELATIVE_DATA;
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;
                    case IMPORT_KMD_CITIZEN_GRANTS:
                        importShiftURI = envConfig.getServerHost()+API_KMD_CARE_CITIZEN_GRANTS;
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;
                    case IMPORT_KMD_STAFF_AND_WORKING_HOURS:
                        importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+schedulerPanel.getUnitId()+"/getShifts/"+schedulerPanel.getFilterId();
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;
                    case IMPORT_KMD_TASKS:
                        importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+schedulerPanel.getUnitId()+"/getTasks/"+schedulerPanel.getFilterId();
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;
                    case IMPORT_KMD_TIME_SLOTS:
                        importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+schedulerPanel.getUnitId()+"/getTimeSlots";
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;

                }*/




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
