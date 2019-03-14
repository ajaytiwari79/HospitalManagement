package com.kairos.scheduler.service.scheduler_panel;


import com.kairos.dto.scheduler.JobDetailsDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.dto.scheduler.scheduler_panel.LocalDateTimeIdDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDefaultDataDto;
import com.kairos.dto.user.organization.UnitTimeZoneMappingDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobTriggerType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.scheduler.custom_exception.DataNotFoundByIdException;
import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.scheduler.persistence.model.scheduler_panel.jobDetails.JobDetails;
import com.kairos.scheduler.persistence.repository.scheduler_panel.IntegrationConfigurationRepository;
import com.kairos.scheduler.persistence.repository.job_details.JobDetailsRepository;
import com.kairos.scheduler.persistence.repository.scheduler_panel.SchedulerPanelRepository;
import com.kairos.scheduler.service.MongoBaseService;

import com.kairos.scheduler.service.UserIntegrationService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;

import com.kairos.scheduler.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.scheduler.constants.AppConstants.SCHEDULER_PANEL_INTERVAL_STRING;
import static com.kairos.scheduler.constants.AppConstants.SCHEDULER_PANEL_RUN_ONCE_STRING;

/**
 * Created by oodles on 29/12/16.
 */
@Service
@Transactional
public class SchedulerPanelService extends MongoBaseService {

    @Inject
    private SchedulerPanelRepository schedulerPanelRepository;

    @Inject
    private IntegrationConfigurationRepository integrationConfigurationRepository;
    @Inject
    private DynamicCronScheduler dynamicCronScheduler;
    @Inject
    private JobDetailsRepository jobDetailsRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;


    private static final Logger logger = LoggerFactory.getLogger(SchedulerPanelService.class);


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public void initSchedulerPanels() {
        List<SchedulerPanel> schedulerPanels = schedulerPanelRepository.findAllByDeletedFalse();
        logger.debug("Inside initSchedulerPanels");
        if (!schedulerPanels.isEmpty()) {
            List<Long> unitIds = schedulerPanels.stream().map(schedulerPanel -> schedulerPanel.getUnitId()).
                    collect(Collectors.toList());
            List<UnitTimeZoneMappingDTO> unitTimeZoneMappingDTOS = userIntegrationService.getTimeZoneOfAllUnits();

            Map<Long, String> unitIdTimeZoneMap = unitTimeZoneMappingDTOS.stream().filter(unitTimeZoneMappingDTO -> Optional.ofNullable(unitTimeZoneMappingDTO.getTimezone()).isPresent()).
                    collect(Collectors.toMap(unitTimeZoneMapping -> {
                        return unitTimeZoneMapping.getUnitId();
                    }, unitTimeZoneMapping -> {
                        return unitTimeZoneMapping.getTimezone();
                    }));

            for (SchedulerPanel schedulerPanel : schedulerPanels) {
                if (!(schedulerPanel.isOneTimeTrigger() && schedulerPanel.getJobTriggerDate().isBefore(LocalDateTime.now()))) {
                    logger.info("Inside initSchedulerPanels" + schedulerPanel.getUnitId() + " unitId = " + unitIdTimeZoneMap.containsKey(schedulerPanel.getUnitId()));
                    dynamicCronScheduler.setCronScheduling(schedulerPanel, unitIdTimeZoneMap.get(schedulerPanel.getUnitId()));
                }
            }
        }


    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public List<SchedulerPanelDTO> createSchedulerPanel(long unitId, List<SchedulerPanelDTO> schedulerPanelDTOs) {

        //logger.info("integrationConfigurationId-----> "+integrationConfigurationId);
        String timezone = null;
        if (schedulerPanelDTOs.isEmpty()) {
            exceptionService.invalidRequestException("request.invalid");
        }
        List<SchedulerPanel> schedulerPanels = new ArrayList<>();
        for (SchedulerPanelDTO schedulerPanelDTO : schedulerPanelDTOs) {
            SchedulerPanel schedulerPanel = ObjectMapperUtils.copyPropertiesByMapper(schedulerPanelDTO,SchedulerPanel.class);
            //ObjectMapperUtils.copyProperties(schedulerPanelDTO, schedulerPanel);
            if (Optional.ofNullable(schedulerPanelDTO.getIntegrationConfigurationId()).isPresent()) {
                Optional<IntegrationSettings> integrationConfigurationOpt = integrationConfigurationRepository.findById(schedulerPanelDTO.getIntegrationConfigurationId());
                //    IntegrationSettings integrationSettings = integrationConfigurationOpt.isPresent()?integrationConfigurationOpt.get(): null;
                if (integrationConfigurationOpt.isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.integrationsettings.notfound", schedulerPanelDTO.getIntegrationConfigurationId());
                }
                schedulerPanel.setIntegrationConfigurationId(schedulerPanelDTO.getIntegrationConfigurationId());
            }

            //schedulerPanel.setProcessType(integrationConfiguration.getName());
            String interval;
            String cronExpression = null;
            if (JobTriggerType.MONTHS.equals(schedulerPanel.getJobTriggerType())) {
                cronExpression = cronExpressionEveryMonthBuilder(schedulerPanel.getJobTriggerDate());
            } else if (!schedulerPanel.isOneTimeTrigger()) {
                if (schedulerPanel.getRunOnce() == null) {
                    cronExpression = cronExpressionSelectedHoursBuilder(schedulerPanel.getDays(), schedulerPanel.getRepeat(), schedulerPanel.getStartMinute(), schedulerPanel.getSelectedHours());
                } else {
                    cronExpression = cronExpressionRunOnceBuilder(schedulerPanel.getDays(), schedulerPanel.getRunOnce());
                }

                interval = intervalStringBuilder(schedulerPanel.getDays(), schedulerPanel.getRepeat(), schedulerPanel.getRunOnce());
                schedulerPanel.setInterval(interval);

            } else {
                schedulerPanel.setJobTriggerDate(schedulerPanelDTO.getJobTriggerDate());
            }
            schedulerPanel.setCronExpression(cronExpression);

            schedulerPanel.setActive(true);

            schedulerPanel.setUnitId(unitId);
            //dynamicCronScheduler.setCronScheduling(schedulerPanel,timezone);

            schedulerPanels.add(schedulerPanel);
            timezone = schedulerPanelDTO.getTimezone();
        }
        save(schedulerPanels);
        if (!Optional.ofNullable(timezone).isPresent())
            timezone = userIntegrationService.getTimeZoneOfUnit(unitId);
//        String defaultTimezone = timezone;
//        schedulerPanels.stream().map(schedulerPanel -> dynamicCronScheduler.setCronScheduling(schedulerPanel, defaultTimezone));

        for (SchedulerPanel schedulerPanel : schedulerPanels) {
            dynamicCronScheduler.setCronScheduling(schedulerPanel, timezone);
        }
        //      System.out.println("log-----> "+logger.toString());
        return ObjectMapperUtils.copyPropertiesOfListByMapper(schedulerPanels, SchedulerPanelDTO.class);
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public SchedulerPanelDTO updateSchedulerPanel(SchedulerPanelDTO schedulerPanelDTO, BigInteger schedulerPanelId) {
        logger.info("schedulerPanel.getId()-------------> " + schedulerPanelId);
        Optional<SchedulerPanel> panelOpt = schedulerPanelRepository.findById(schedulerPanelId);

        if (!panelOpt.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.schedulerpanel.notfound", schedulerPanelId);
        }
        SchedulerPanel panel = panelOpt.get();
        String interval;
        String cronExpression;

        if (!schedulerPanelDTO.isOneTimeTrigger()) {
            interval = intervalStringBuilder(schedulerPanelDTO.getDays(), schedulerPanelDTO.getRepeat(), schedulerPanelDTO.getRunOnce());
            panel.setInterval(interval);
            if (schedulerPanelDTO.getRunOnce() == null) {
                cronExpression = cronExpressionSelectedHoursBuilder(schedulerPanelDTO.getDays(), schedulerPanelDTO.getRepeat(), schedulerPanelDTO.getStartMinute(), schedulerPanelDTO.getSelectedHours());
                if (Optional.ofNullable(schedulerPanelDTO.getRepeat()).isPresent()) {
                    panel.setRepeat(schedulerPanelDTO.getRepeat());
                }
                panel.setStartMinute(schedulerPanelDTO.getStartMinute());

            } else if (JobTriggerType.MONTHS.equals(panel.getJobTriggerType())) {
                cronExpression = cronExpressionEveryMonthBuilder(panel.getJobTriggerDate());
            } else {
                cronExpression = cronExpressionRunOnceBuilder(schedulerPanelDTO.getDays(), schedulerPanelDTO.getRunOnce());
                panel.setRunOnce(schedulerPanelDTO.getRunOnce());

            }
            panel.setCronExpression(cronExpression);
            panel.setDays(schedulerPanelDTO.getDays());
            panel.setSelectedHours(schedulerPanelDTO.getSelectedHours());
            panel.setJobTriggerDate(null);

        } else {
            panel.setJobTriggerDate(schedulerPanelDTO.getJobTriggerDate());
        }
        panel.setOneTimeTrigger(schedulerPanelDTO.isOneTimeTrigger());
        save(panel);
        String timezone = userIntegrationService.getTimeZoneOfUnit(schedulerPanelDTO.getUnitId());

        dynamicCronScheduler.stopCronJob("scheduler" + panel.getId());
        dynamicCronScheduler.startCronJob(panel, timezone);
        return ObjectMapperUtils.copyPropertiesByMapper(panel, SchedulerPanelDTO.class);
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public List<LocalDateTimeIdDTO> updateSchedulerPanelsOneTimeTriggerDate(List<LocalDateTimeIdDTO> localDateTimeIdDTOS, Long unitId) {

        Set<BigInteger> schedulerPanelIDs = localDateTimeIdDTOS.stream().map(localDateTimeIdDTO -> localDateTimeIdDTO.getId()).collect(Collectors.toSet());

        List<SchedulerPanel> schedulerPanels = schedulerPanelRepository.findByIdsIn(schedulerPanelIDs);
        Map<BigInteger, SchedulerPanel> schedulerPanelsById = schedulerPanels.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        String timezone = userIntegrationService.getTimeZoneOfUnit(unitId);

        SchedulerPanel schedulerPanel;

        List<SchedulerPanel> schedulerPanelsUpdated = new ArrayList<>();
        for (LocalDateTimeIdDTO localDateTimeIdDTO : localDateTimeIdDTOS) {
            schedulerPanel = schedulerPanelsById.get(localDateTimeIdDTO.getId());
            schedulerPanel.setJobTriggerDate(localDateTimeIdDTO.getDateTime());
            schedulerPanelsUpdated.add(schedulerPanel);
            dynamicCronScheduler.stopCronJob("scheduler" + localDateTimeIdDTO.getId());
            dynamicCronScheduler.startCronJob(schedulerPanel, timezone);


        }
        save(schedulerPanelsUpdated);

        return localDateTimeIdDTOS;
    }

    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public boolean updateSchedulerPanelByJobSubTypeAndEntityId(SchedulerPanelDTO schedulerPanelDTO) {

        SchedulerPanel schedulerPanelDB = schedulerPanelRepository.findByJobSubTypeAndEntityIdAndUnitId(schedulerPanelDTO.getJobSubType(), schedulerPanelDTO.getEntityId(), schedulerPanelDTO.getUnitId());
        if (!Optional.ofNullable(schedulerPanelDB).isPresent()) {
            createSchedulerPanel(schedulerPanelDTO.getUnitId(), Stream.of(schedulerPanelDTO).collect(Collectors.toList()));
        } else {

            String interval;
            String cronExpression;

            if (!schedulerPanelDTO.isOneTimeTrigger()) {
                interval = intervalStringBuilder(schedulerPanelDTO.getDays(), schedulerPanelDTO.getRepeat(), schedulerPanelDTO.getRunOnce());
                schedulerPanelDB.setInterval(interval);
                if (schedulerPanelDTO.getRunOnce() == null) {
                    cronExpression = cronExpressionSelectedHoursBuilder(schedulerPanelDTO.getDays(), schedulerPanelDTO.getRepeat(), schedulerPanelDTO.getStartMinute(), schedulerPanelDTO.getSelectedHours());
                } else if (JobTriggerType.MONTHS.equals(schedulerPanelDTO.getJobTriggerType())) {
                    cronExpression = cronExpressionEveryMonthBuilder(schedulerPanelDTO.getJobTriggerDate());
                } else {
                    cronExpression = cronExpressionRunOnceBuilder(schedulerPanelDTO.getDays(), schedulerPanelDTO.getRunOnce());
                }
                schedulerPanelDB.setCronExpression(cronExpression);
                schedulerPanelDB.setDays(schedulerPanelDTO.getDays());
                schedulerPanelDB.setSelectedHours(schedulerPanelDTO.getSelectedHours());

            } else {
                schedulerPanelDB.setJobTriggerDate(schedulerPanelDTO.getJobTriggerDate());
            }

            save(schedulerPanelDB);
            String timezone = userIntegrationService.getTimeZoneOfUnit(schedulerPanelDTO.getUnitId());

            dynamicCronScheduler.stopCronJob("scheduler" + schedulerPanelDB.getId());
            dynamicCronScheduler.startCronJob(schedulerPanelDB, timezone);
        }
        return true;
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public SchedulerPanelDTO findSchedulerPanelById(BigInteger schedulerPanelId) {

        logger.info("schedulerPanelId ----> " + schedulerPanelId);
        Optional<SchedulerPanel> schedulerPanelOpt = schedulerPanelRepository.findById(schedulerPanelId);
        SchedulerPanelDTO schedulerPanelDTO;
        if (!schedulerPanelOpt.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.schedulerpanel.notfound", schedulerPanelId);
        }
        schedulerPanelDTO = ObjectMapperUtils.copyPropertiesByMapper(schedulerPanelOpt.get(), SchedulerPanelDTO.class);

        return schedulerPanelDTO;
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public List<SchedulerPanelDTO> getSchedulerPanelByUnitId(long unitId) {
        //List<Map<String, Object>> controlPanels = schedulerPanelRepository.findByUnitId(unitId);
        List<SchedulerPanel> schedulerPanels = schedulerPanelRepository.findByUnitIdAndDeletedFalse(unitId);
        List<SchedulerPanelDTO> schedulerPanelDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(schedulerPanels, SchedulerPanelDTO.class);
        return schedulerPanelDTOS;

    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public List<SchedulerPanel> getAllControlPanels() {
        List<SchedulerPanel> schedulerPanels = schedulerPanelRepository.findByActive(true);
        return schedulerPanels;
    }

    /**
     * @author yatharth
     * @lastmodifiedby
     */
    private String intervalStringBuilder(List days, Integer repeat, LocalTime runOnce) {
        String regex = "\\[|\\]";

        String interval;
        if (runOnce == null)
            interval = days.toString().replaceAll(regex, "") + MessageFormat.format(SCHEDULER_PANEL_INTERVAL_STRING, repeat);
        else
            interval = days.toString().replaceAll(regex, "") + MessageFormat.format(SCHEDULER_PANEL_RUN_ONCE_STRING, runOnce);
        logger.info("Interval--> " + interval);
        return interval;
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    private String cronExpressionSelectedHoursBuilder(List days, Integer repeat, Integer startTime, List selectedHours) {
        String cronExpressionSelectedHours = "0 {0}/{1} {2} ? * {3}"; // 	0 5/60 14-18 ? * MON-FRI
        String cronExpressionSelectedHoursWithoutRepeat = "0 {0} {1} ? * {2}";
        String regex = "\\[|\\]";
        String interval = daysOfWeek(days);

        String selectedHoursString = selectedHours.toString().replaceAll(" ", "").replaceAll("[-]\\d\\d", "").replaceAll(regex, "").replaceAll("[:]\\d\\d", "");
        logger.info("selectedHoursString----> " + selectedHoursString);
        String cronExpression = Optional.ofNullable(repeat).isPresent() ? MessageFormat.format(cronExpressionSelectedHours, startTime.toString(), repeat, selectedHoursString, interval) :
                MessageFormat.format(cronExpressionSelectedHoursWithoutRepeat, startTime.toString(), selectedHoursString, interval);
        logger.info("cronExpression-selectedHours-> " + cronExpression);
        return cronExpression;
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    private String cronExpressionRunOnceBuilder(List days, LocalTime runOnce) {
        String cronExpressionRunOnce = "0 {0} {1} ? * {2}";
        String interval = daysOfWeek(days);
        String hours = String.valueOf(runOnce.getHour());
        String minutes = String.valueOf(runOnce.getMinute());
        logger.info("hours--> " + hours);
        logger.info("minutes--> " + minutes);
        String cronExpression = MessageFormat.format(cronExpressionRunOnce, minutes, hours, interval);
        logger.info("cronExpression runOnce--> " + cronExpression);
        return cronExpression;
    }


    private String cronExpressionEveryMonthBuilder(LocalDateTime localDateTime) {
        String cronExpressionRunOnce = "0 {0} {1} {2} * ?";
        String cronExpression = MessageFormat.format(cronExpressionRunOnce, String.valueOf(localDateTime.get(ChronoField.MINUTE_OF_HOUR)), String.valueOf(localDateTime.get(ChronoField.HOUR_OF_DAY)), String.valueOf(localDateTime.getDayOfMonth()));
        return cronExpression;
    }

    /**
     * @author yatharth
     * @lastmodifiedby
     */
    private String daysOfWeek(List interval) {
        String regex = "\\[|\\]|";
        String intervalString;
        intervalString = interval.toString().replaceAll(" ", "").replaceAll(regex, "").toUpperCase().replace("MONDAY", "MON").replace("TUESDAY", "TUE")
                .replace("WEDNESDAY", "WED").replace("THURSDAY", "THU").replace("FRIDAY", "FRI").replace("SATURDAY", "SAT").replace("SUNDAY", "SUN");
        return intervalString;
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public SchedulerPanel setScheduleLastRunTime(SchedulerPanel schedulerPanel) {
        Optional<SchedulerPanel> panelOptional = schedulerPanelRepository.findById(schedulerPanel.getId());
        SchedulerPanel panel = null;
        if (panelOptional.isPresent()) {
            panel = panelOptional.get();
            panel.setLastRunTime(schedulerPanel.getLastRunTime());
            panel.setNextRunTime(schedulerPanel.getNextRunTime());
            schedulerPanelRepository.save(panel);
        }

        return panel;

    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public void createJobScheduleDetails(KairosSchedulerLogsDTO logs) {
        JobDetails jobDetails = new JobDetails();
        ObjectMapperUtils.copyProperties(logs, jobDetails);
        jobDetails.setStarted(DateUtils.getLocalDatetimeFromLong(logs.getStartedDate()));
        jobDetails.setStopped(DateUtils.getLocalDatetimeFromLong(logs.getStoppedDate()));
        logger.info("============>>Job logs get saved<<============");
        save(jobDetails);

    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public List<JobDetailsDTO> getAllJobDetailsByUnitId(Long unitId, int offset) {
        return jobDetailsRepository.findAllSchedulerPanelsByUnitIdAndOffset(unitId, offset);
    }

    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public List<JobDetails> getJobDetails(BigInteger schedulerPanelId) {
        return jobDetailsRepository.findAllBySchedulerPanelIdOrderByStartedDesc(schedulerPanelId);
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public Boolean deleteJobs(Set<BigInteger> schedulerPanelIds) {
        try {
            if (schedulerPanelIds.isEmpty()) {
                exceptionService.invalidRequestException("request.invalid");
            }
            List<SchedulerPanel> schedulerPanels = schedulerPanelRepository.findByIdsIn(schedulerPanelIds);
            Set<BigInteger> schedulerPanelIdsDB = schedulerPanels.stream().map(schedulerPanel -> schedulerPanel.getId()).collect(Collectors.toSet());
            for (BigInteger schedulerPanelId : schedulerPanelIds) {
                if (!schedulerPanelIdsDB.contains(schedulerPanelId)) {
                    exceptionService.dataNotFoundByIdException("message.schedulerpanel.notfound", schedulerPanelId);
                }
            }

            for (SchedulerPanel schedulerPanel : schedulerPanels) {
                schedulerPanel.setDeleted(true);
                dynamicCronScheduler.stopCronJob("scheduler" + schedulerPanel.getId());
                schedulerPanel.setActive(false);
            }
            save(schedulerPanels);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }


    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public Boolean deleteJob(BigInteger schedulerPanelId) {

        if (!Optional.ofNullable(schedulerPanelRepository.safeDeleteById(schedulerPanelId)).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.schedulerpanel.notfound", schedulerPanelId);
        }

        dynamicCronScheduler.stopCronJob("scheduler" + schedulerPanelId);

        return true;
    }

    /**
     * @author yatharth
     * @lastmodifiedby
     */
    public boolean deleteJobBySubTypeAndEntityId(SchedulerPanelDTO schedulerPanel) {

        SchedulerPanel schedulerPanelDB = schedulerPanelRepository.findByJobSubTypeAndEntityIdAndUnitId(schedulerPanel.getJobSubType(), schedulerPanel.getEntityId(),
                schedulerPanel.getUnitId());
        if (!Optional.ofNullable(schedulerPanelDB).isPresent()) {
            throw new DataNotFoundByIdException("Scheduler Panel not found by entity id");
        }
        schedulerPanelDB.setDeleted(true);
        schedulerPanelDB.setActive(false);
        save(schedulerPanelDB);
        return true;
    }

    /**
     * @return
     * @Author Yatharth
     */
    public SchedulerPanelDefaultDataDto getDefaultData() {
        return new SchedulerPanelDefaultDataDto(Arrays.asList(JobSubType.values()), Arrays.asList(JobType.values()));
    }
//Dont remove
   /* public ControlPanelDTO getControlPanelData(BigInteger schedulerPanelId){
        String jobId = getRecentJobId(schedulerPanelId);
        Long unitId = getControlPanelUnitId(schedulerPanelId);
        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        ControlPanelDTO controlPanelDTO = new ControlPanelDTO();
        controlPanelDTO.setFlsCredentails(flsCredentials);
        controlPanelDTO.setJobId(jobId);
        controlPanelDTO.setUnitId(unitId);
        return controlPanelDTO;
    }*/


}
