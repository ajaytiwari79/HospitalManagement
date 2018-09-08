package com.kairos.config.scheduler;

import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.control_panel.ControlPanel;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.service.control_panel.ControlPanelService;
import com.kairos.utils.BeanFactoryUtil;
import com.kairos.utils.DateUtil;
import com.kairos.utils.external_plateform_shift.Transstatus;
import org.apache.commons.codec.binary.Base64;
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

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by oodles on 11/1/17.
 */
@Service
public class DynamicCronScheduler implements  DisposableBean  {


    @Inject
    ControlPanelService controlPanelService;

    @Inject
    EnvConfig envConfig;

    @Inject
    OrganizationGraphRepository organizationGraphRepository;



    private static final Logger logger = LoggerFactory.getLogger(DynamicCronScheduler.class);

    public String setCronScheduling(ControlPanel controlPanel){
        logger.debug("cron----> " + controlPanel.getCronExpression());
        CronTrigger trigger = new CronTrigger(controlPanel.getCronExpression(), TimeZone.getTimeZone("Denmark"));
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix(controlPanel.getIntegrationConfiguration().getId().toString());
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.initialize();
        Runnable runnable = getTask(controlPanel, trigger, TimeZone.getTimeZone("Denmark"));
        threadPoolTaskScheduler.schedule(runnable, trigger);

        logger.info("Name of cron job is --> " + "scheduler" + controlPanel.getId());
        BeanFactoryUtil.registerSingleton("scheduler" + controlPanel.getId(), threadPoolTaskScheduler);
        //   context.getBeanFactory().registerSingleton("scheduler" + controlPanel.getId(), threadPoolTaskScheduler);
        //    context.register(ThreadPoolTaskScheduler.class);
        //      context.refresh();
        logger.info("Name of cron job is --> " + "scheduler" + controlPanel.getId());
        //  context.close();

        return "scheduler" + controlPanel.getId();


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

    public void startCronJob(ControlPanel controlPanel){
        try {
            String scheduler = "scheduler"+controlPanel.getId();
            logger.info("Start scheduler from BootStrap--> "+scheduler);
            CronTrigger trigger = new CronTrigger(controlPanel.getCronExpression(),  TimeZone.getTimeZone("Denmark"));
            Runnable task = getTask(controlPanel,  trigger, TimeZone.getTimeZone("Denmark"));

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

    public Runnable getTask(ControlPanel controlPanel,  CronTrigger trigger, TimeZone timeZone){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.info("control pannel exist--> " + controlPanel.getId());
                controlPanel.setLastRunTime(DateUtil.getCurrentDate());
                controlPanel.setNextRunTime(getNextExecutionTime(trigger, controlPanel.getLastRunTime(), timeZone));
                controlPanelService.setScheduleLastRunTime(controlPanel);

                String plainClientCredentials = "cluster:cluster";
                String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));


                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
                headers.add("Authorization", "Basic " + base64ClientCredentials);
                HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
                String importShiftStatusXMLURI = envConfig.getCarteServerHost()+KETTLE_TRANS_STATUS;
                //   String startDate = DateFormatUtils.format(DateUtil.getCurrentDate(), "yyyy-MM-dd");
                //     String endDate = DateFormatUtils.format(DateUtil.addWeeksInDate(DateUtil.getCurrentDate(), 5), "yyyy-MM-dd");
                //  String startDate = DateFormatUtils.format(controlPanel.getStartDate(), "yyyy-MM-dd");
                //  String endDate = DateFormatUtils.format(controlPanel.getEndDate(), "yyyy-MM-dd");
                Long workplaceId = Long.valueOf(String.valueOf("15"));
                if(controlPanel.getUnitId() != null){
                    Organization organization = organizationGraphRepository.findOne(controlPanel.getUnitId());
                    if(organization.getExternalId() != null) workplaceId = Long.valueOf(organization.getExternalId());
                }
                String importShiftURI = "";
                int weeks = 35;
                String uniqueKey = controlPanel.getIntegrationConfiguration().getUniqueKey();
                logger.info("uniqueKey----> "+uniqueKey);
                RestTemplate restTemplate = new RestTemplate();
                switch(uniqueKey){
                    case IMPORT_TIMECARE_SHIFTS:
                        logger.info("!!===============Hit to carte server from Kairos==============!!");
                        importShiftURI = envConfig.getCarteServerHost()+KETTLE_EXECUTE_TRANS+IMPORT_TIMECARE_SHIFTS_PATH+"&intWorkPlaceId="+workplaceId+"&weeks="+weeks+"&jobId="+controlPanel.getId();
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

                                controlPanelService.createJobScheduleDetails(controlPanel, transstatus, started, stopped);
                            } catch (JAXBException exception) {
                                logger.info("trans status---exception > " + exception);
                            }
                            catch(IOException exception){
                                logger.info("exception while logging job details-- > " + exception);
                            }

                        }
                        break;
                    case IMPORT_KMD_CITIZEN:
                        importShiftURI = envConfig.getServerHost()+KMD_CARE_CITIZEN_URL+controlPanel.getUnitId();
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
                        importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+controlPanel.getUnitId()+"/getShifts/"+controlPanel.getFilterId();
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;
                    case IMPORT_KMD_TASKS:
                        importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+controlPanel.getUnitId()+"/getTasks/"+controlPanel.getFilterId();
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;
                    case IMPORT_KMD_TIME_SLOTS:
                        importShiftURI=envConfig.getServerHost()+API_KMD_CARE_URL+controlPanel.getUnitId()+"/getTimeSlots";
                        restTemplate.exchange(importShiftURI, HttpMethod.GET, entity, String.class);
                        break;

                }




            }
        };

        return runnable;

    }

    public void destroy(){
        List<ControlPanel> controlPanels = controlPanelService.getAllControlPanels();
        if(controlPanels.size() != 0) {
            for (ControlPanel controlPanel : controlPanels) {
                logger.info("Shutdown Cron Job of process name " + controlPanel.getName());
                stopCronJob("scheduler"+controlPanel.getId());
            }
        }
    }




}
