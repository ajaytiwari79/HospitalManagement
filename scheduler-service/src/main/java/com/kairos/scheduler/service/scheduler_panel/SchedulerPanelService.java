package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.client.dto.ControlPanelDTO;
import com.kairos.config.scheduler.DynamicCronScheduler;
import com.kairos.dto.QueueDTO;
//import com.kairos.kafka.producer.KafkaProducer;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.control_panel.ControlPanel;
import com.kairos.persistence.model.user.control_panel.jobDetails.JobDetails;
import com.kairos.persistence.model.user.tpa_services.IntegrationConfiguration;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.control_panel.ControlPanelGraphRepository;
import com.kairos.persistence.repository.user.control_panel.jobDetails.JobDetailsRepository;
import com.kairos.persistence.repository.user.tpa_services.IntegrationConfigurationGraphRepository;
import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationConfiguration;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.scheduler.persistence.repository.IntegrationConfigurationRepository;
import com.kairos.scheduler.persistence.repository.SchedulerPanelRepository;
import com.kairos.scheduler.service.MongoBaseService;
import com.kairos.service.UserBaseService;
import com.kairos.service.integration.IntegrationService;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.timeCareShift.Transstatus;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static com.kairos.persistence.model.constants.RelationshipConstants.CONTROL_PANEL_INTERVAL_STRING;
import static com.kairos.persistence.model.constants.RelationshipConstants.CONTROL_PANEL_RUN_ONCE_STRING;


import com.kairos.scheduler.service.MongoBaseService;

/**
 * Created by oodles on 29/12/16.
 */
@Service
@Transactional
public class SchedulerPanelService extends MongoBaseService {

    @Inject
    private SchedulerPanelRepository schedulerPanelRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    IntegrationConfigurationRepository integrationConfigurationRepository;
    @Inject
    DynamicCronScheduler dynamicCronScheduler;
    @Inject
    JobDetailsRepository jobDetailsRepository;
    @Inject
    IntegrationService integrationService;
    @Inject
    private KafkaProducer kafkaProducer;


    private static final Logger logger = LoggerFactory.getLogger(SchedulerPanelService.class);


    public SchedulerPanel createControlPanel(long unitId, SchedulerPanel schedulerPanel, BigInteger integrationConfigurationId) {
        logger.info("integrationConfigurationId-----> "+integrationConfigurationId);
        Optional<IntegrationConfiguration> integrationConfigurationOpt = integrationConfigurationRepository.findById(integrationConfigurationId);
        IntegrationConfiguration integrationConfiguration = integrationConfigurationOpt.isPresent()?integrationConfigurationOpt.get(): null;
        schedulerPanel.setProcessType(integrationConfiguration.getName());
        schedulerPanel.setIntegrationConfigurationId(integrationConfigurationId);
        String interval = intervalStringBuilder(schedulerPanel.getDays(), schedulerPanel.getRepeat(), schedulerPanel.getRunOnce());
        schedulerPanel.setInterval(interval);
        String cronExpression;
        if(schedulerPanel.getRunOnce() == null) {
            cronExpression = cronExpressionSelectedHoursBuilder(schedulerPanel.getDays(), schedulerPanel.getRepeat(), schedulerPanel.getStartMinute(), schedulerPanel.getSelectedHours());
        } else
            cronExpression = cronExpressionRunOnceBuilder(schedulerPanel.getDays(), schedulerPanel.getRunOnce());
        schedulerPanel.setCronExpression(cronExpression);
        schedulerPanel.setActive(true);

        schedulerPanel.setUnitId(unitId);
        save(schedulerPanel);

        dynamicCronScheduler.setCronScheduling( schedulerPanel);
        System.out.println("log-----> "+logger.toString());
        return schedulerPanel;
    }

    public SchedulerPanel updateControlPanel(SchedulerPanel schedulerPanel) throws IOException {
        logger.info("controlPanel.getId()-------------> "+schedulerPanel.getId());
        Optional<SchedulerPanel> panelOpt = schedulerPanelRepository.findById(schedulerPanel.getId());
        SchedulerPanel panel;
        if(!panelOpt.isPresent()){
            return null;
        }
        else {
            panel = panelOpt.get();
            logger.info("panel.getId()-------------> "+panel.getId());

        }

        String interval = intervalStringBuilder(schedulerPanel.getDays(), schedulerPanel.getRepeat(), schedulerPanel.getRunOnce());
        panel.setInterval(interval);


        String cronExpression;

        if(schedulerPanel.getRunOnce() == null) {
            cronExpression = cronExpressionSelectedHoursBuilder(schedulerPanel.getDays(), schedulerPanel.getRepeat(), schedulerPanel.getStartMinute(), schedulerPanel.getSelectedHours());
        } else
            cronExpression = cronExpressionRunOnceBuilder(schedulerPanel.getDays(), schedulerPanel.getRunOnce());
        panel.setCronExpression(cronExpression);
        save(panel);
        dynamicCronScheduler.stopCronJob("scheduler"+panel.getId());
        dynamicCronScheduler.startCronJob(panel);
        return  panel;
    }

    public Map<String, Object> getControlPanelById(BigInteger schedulerPanelId) {

        logger.info("controlPanelId ----> "+schedulerPanelId);
        Optional<SchedulerPanel> schedulerPanelOpt = schedulerPanelRepository.findById(schedulerPanelId);
        SchedulerPanel schedulerPanel = schedulerPanelOpt.get();

        Map<String,Object> map = new HashMap<>();
        map.put("controlPanel",schedulerPanel);
        return map;
    }

    public Map<String, Object> getControlPanelByUnitId(long unitId) {
            List<Map<String, Object>> controlPanels = controlPanelGraphRepository.getControlPanelByUnitId(unitId);
            List<Object> response = new ArrayList<>();
            for (Map<String, Object> map : controlPanels) {
                Object o = map.get("result");
                response.add(o);
            }
            
            Map<String, Object> map = new HashMap<>();
            map.put("controlPanels", response);
            return map;

    }


    public List<ControlPanel> getAllControlPanels() {
        List<ControlPanel> controlPanels = controlPanelGraphRepository.findByActive(true);


        return controlPanels;

    }

    public String intervalStringBuilder(List days, String repeat, String runOnce){
        String regex = "\\[|\\]";

        String interval;
        if(runOnce == null)
            interval  = days.toString().replaceAll(regex, "")+ MessageFormat.format(CONTROL_PANEL_INTERVAL_STRING, repeat);
        else
            interval  = days.toString().replaceAll(regex, "")+ MessageFormat.format(CONTROL_PANEL_RUN_ONCE_STRING, runOnce);
        logger.info("Interval--> "+interval);
        return interval;
    }

    public String cronExpressionSelectedHoursBuilder(List days, String repeat, Integer startTime, List selectedHours){
        String cronExpressionSelectedHours = "0 {0}/{1} {2} ? * {3}"; // 	0 5/60 14-18 ? * MON-FRI
        String regex = "\\[|\\]";
        String interval  = daysOfWeek(days);

        String selectedHoursString  = selectedHours.toString().replaceAll(" ", "").replaceAll("[-]\\d\\d","").replaceAll(regex, "").replaceAll("[:]\\d\\d", "");
        logger.info("selectedHoursString----> "+selectedHoursString);
        String cronExpression = MessageFormat.format(cronExpressionSelectedHours, startTime.toString(), repeat, selectedHoursString, interval);
        logger.info("cronExpression-selectedHours-> "+cronExpression);
        return cronExpression;
    }

    public String cronExpressionRunOnceBuilder(List days, String runOnce){
        String cronExpressionRunOnce = "0 {0} {1} ? * {2}";
        String interval  = daysOfWeek(days);
        String hours = runOnce.substring(0,runOnce.indexOf(":"));
        String minutes = runOnce.substring(runOnce.indexOf(":")+1);
        logger.info("hours--> "+hours);
        logger.info("minutes--> "+minutes);
        String cronExpression = MessageFormat.format(cronExpressionRunOnce, minutes, hours, interval);
        logger.info("cronExpression runOnce--> "+cronExpression);
        return cronExpression;
    }

    public String daysOfWeek(List interval){
        String regex = "\\[|\\]|";
        String intervalString;
        intervalString  = interval.toString().replaceAll(" ", "").replaceAll(regex, "").toUpperCase().replace("MONDAY","MON").replace("TUESDAY","TUE")
                .replace("WEDNESDAY","WED").replace("THURSDAY","THU").replace("FRIDAY","FRI").replace("SATURDAY","SAT").replace("SUNDAY","SUN");
        return intervalString;
    }

    public SchedulerPanel setScheduleLastRunTime(SchedulerPanel controlPanel){
        SchedulerPanel panel = schedulerPanelRepository.findById(controlPanel.getId());
        if(panel == null){
            return null;
        }
        panel.setLastRunTime(controlPanel.getLastRunTime());
        panel.setNextRunTime(controlPanel.getNextRunTime());
        controlPanelGraphRepository.save(panel);
        return panel;

    }

    public void createJobScheduleDetails(ControlPanel controlPanel, Transstatus transstatus, Date started, Date stopped) throws IOException {
        JobDetails jobDetails = new JobDetails();
        jobDetails.setControlPanelId(controlPanel.getId());
        String loggingString = StringEscapeUtils.escapeHtml4(transstatus.getLogging_string());
        loggingString = loggingString.substring(loggingString.indexOf("[CDATA[")+7,loggingString.indexOf("]]&gt"));
        byte[] bytes = Base64.decodeBase64(loggingString);
        GZIPInputStream zi = null;
        String unzipped;
        try {
            zi = new GZIPInputStream(new ByteArrayInputStream(bytes));
            unzipped = IOUtils.toString(zi);
        } finally {
            IOUtils.closeQuietly(zi);
        }
        jobDetails.setLog(unzipped);
        jobDetails.setStarted(started);
        jobDetails.setStopped(stopped);
        jobDetails.setProcessName(controlPanel.getProcessType());
        String result = "Success";
        if(transstatus.getResult().getNr_errors() > 0) result = "Error";
        jobDetails.setResult(result);
        logger.info("============>>Job logs get saved<<============");
        jobDetailsRepository.save(jobDetails);

    }

    public List<JobDetails> getJobDetails(long controlPanelId){
        return jobDetailsRepository.findByControlPanelId(controlPanelId, new Sort(Sort.Direction.DESC, "started"));
    }

    public Boolean deleteJob(long controlPanelId){
        try {
            ControlPanel panel = controlPanelGraphRepository.findOne(controlPanelId);
            List<JobDetails> jobDetailsList = jobDetailsRepository.findByControlPanelId(controlPanelId);
            for (JobDetails jobDetails : jobDetailsList) {
                jobDetailsRepository.delete(jobDetails);
            }
            dynamicCronScheduler.stopCronJob("scheduler"+panel.getId());
            panel.setActive(false);
            controlPanelGraphRepository.save(panel);
            return true;
        }catch (Exception exception){
            return false;
        }
    }


    /**
     * data needs to move into neo4j
     * @param controlPanelId
     * @return
     */
    public String getRecentJobId(Long controlPanelId){
       /* Aggregation aggregation = Aggregation.newAggregation(
                match(
                        Criteria.where("controlPanelId").is(controlPanelId)
                ),
                sort(Sort.Direction.DESC, "createdAt"),
                limit(1)
        );
        AggregationResults<Map> finalResult =
                mongoTemplate.aggregate(aggregation, JobDetails.class, Map.class);
        if(finalResult.getMappedResults().size() > 0) {
            String jobDetailId = finalResult.getMappedResults().get(0).get("_id").toString();
            return jobDetailId;
        }else {
            return null;
        }*/

       return "";


    }




    public Long getControlPanelUnitId(Long controlPanelId){
        ControlPanel panel = controlPanelGraphRepository.findOne(controlPanelId);
        return panel.getUnitId();
    }

    public ControlPanelDTO getControlPanelData(long controlPanelId){
        String jobId = getRecentJobId(controlPanelId);
        Long unitId = getControlPanelUnitId(controlPanelId);
        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        ControlPanelDTO controlPanelDTO = new ControlPanelDTO();
        controlPanelDTO.setFlsCredentails(flsCredentials);
        controlPanelDTO.setJobId(jobId);
        controlPanelDTO.setUnitId(unitId);
        return controlPanelDTO;
    }

    public void pushToQueue() {

        ControlPanel panel = controlPanelGraphRepository.findOne(14491L);
        QueueDTO job = new QueueDTO();
        ObjectMapperUtils.copyProperties(panel,job);
         kafkaProducer.pushToQueue(job);


    }


}

