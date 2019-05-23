package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constaints.JsonConstaints;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.constaints.JsonConstaints.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.FileReaderUtil.getFileDataAsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimeBankCalculationServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankCalculationServiceTest.class);

    @InjectMocks
    private TimeBankCalculationService timeBankCalculationService;
    @Mock
    private TimeBankCalculationService timeBankCalculationServiceMock;
    @Mock
    private TimeBankRepository timeBankRepository;
    @Mock private PlanningPeriodMongoRepository planningPeriodMongoRepository;


    private StaffAdditionalInfoDTO staffAdditionalInfoDTO;
    private StaffEmploymentDetails staffEmploymentDetails;
    private DateTimeInterval interval;
    private List<ShiftWithActivityDTO> shiftWithActivityDTOS;
    private List<Shift> shifts;
    private Map<String, DailyTimeBankEntry> dailyTimeBankEntryMap;
    private Set<DateTimeInterval> planningPeriodIntervals;
    private List<DayTypeDTO> dayTypeDTOS;
    private boolean validatedByPlanner;
    private Activity activity;

    DailyTimeBankEntry todayDailyTimeBankEntry;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }

    @Before
    public void init(){
        staffEmploymentDetails = ObjectMapperUtils.jsonStringToObject(getFileDataAsString(EMPLOYMENT_DETAILS),StaffEmploymentDetails.class);
        staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(staffEmploymentDetails,dayTypeDTOS);
        interval = new DateTimeInterval(1555718400000l,1555804800000l);
        shiftWithActivityDTOS = ObjectMapperUtils.JsonStringToList(getFileDataAsString(SHIFT_FOR_TIMEBANK_CALCULATION),ShiftWithActivityDTO.class);;
        dailyTimeBankEntryMap = new HashMap<>();
        planningPeriodIntervals = newHashSet(new DateTimeInterval(1555286400000l, 1555804800000l));
        dayTypeDTOS = ObjectMapperUtils.JsonStringToList(getFileDataAsString(DAYTYPE), DayTypeDTO.class);
        activity = ObjectMapperUtils.jsonStringToObject(getFileDataAsString(ACTIVITY_FOR_TIMEBANK_CALCULATION), Activity.class);
        shifts = ObjectMapperUtils.JsonStringToList(getFileDataAsString(JsonConstaints.SHIFT), Shift.class);
        todayDailyTimeBankEntry = new DailyTimeBankEntry(115l,154l,LocalDate.now());
    }

    @Test
    public void calculateDailyTimeBank() {
        todayDailyTimeBankEntry.setPublishedBalances(new HashMap<>());
        when(timeBankRepository.findByEmploymentAndDate(any(Long.class), any(LocalDate.class))).thenReturn(todayDailyTimeBankEntry);
        when(timeBankRepository.save(todayDailyTimeBankEntry)).thenReturn(todayDailyTimeBankEntry);
        DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOS, null, planningPeriodIntervals, dayTypeDTOS, validatedByPlanner);
        Map<BigInteger, Integer> ctaDistributionCalculation = new HashMap<>(8);
        ctaDistributionCalculation.put(new BigInteger("3061"), 480);
        ctaDistributionCalculation.put(new BigInteger("3062"), 0);
        ctaDistributionCalculation.put(new BigInteger("3063"), 0);
        ctaDistributionCalculation.put(new BigInteger("3064"), 480);
        ctaDistributionCalculation.put(new BigInteger("3065"), 0);
        ctaDistributionCalculation.put(new BigInteger("3066"), 0);
        ctaDistributionCalculation.put(new BigInteger("3067"), 240);
        ctaDistributionCalculation.put(new BigInteger("3068"), 192);
        ctaDistributionCalculation.put(new BigInteger("3071"), 0);
        for (TimeBankCTADistribution timeBankCTADistribution : dailyTimeBankEntry.getTimeBankCTADistributionList()) {
            LOGGER.debug("RuleTemplateId {} {}",timeBankCTADistribution.getCtaRuleTemplateId(),timeBankCTADistribution.getMinutes());
            Assert.assertEquals(ctaDistributionCalculation.get(timeBankCTADistribution.getCtaRuleTemplateId()), timeBankCTADistribution.getMinutes());
        }
        Assert.assertEquals(dailyTimeBankEntry.getScheduledMinutesOfTimeBank(), 480);
        Assert.assertEquals(dailyTimeBankEntry.getTimeBankMinutesWithoutCta(), 480);
        Assert.assertEquals(dailyTimeBankEntry.getCtaBonusMinutesOfTimeBank(), 1392);
        Assert.assertEquals(dailyTimeBankEntry.getDeltaTimeBankMinutes(), 1392);
        Assert.assertEquals(dailyTimeBankEntry.getPlannedMinutesOfTimebank(), 1392);
        Assert.assertEquals(dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes(), 0);
    }


    @Test
    public void getTimeBankByIntervalForNightShift() {
        LOGGER.info("Shift time {}",shiftWithActivityDTOS.get(0).getStartDate());
        todayDailyTimeBankEntry.setPublishedBalances(new HashMap<>());
        when(timeBankRepository.findByEmploymentAndDate(any(Long.class), any(LocalDate.class))).thenReturn(todayDailyTimeBankEntry);
        when(timeBankRepository.save(todayDailyTimeBankEntry)).thenReturn(todayDailyTimeBankEntry);
        shiftWithActivityDTOS = ObjectMapperUtils.JsonStringToList(getFileDataAsString(NIGHT_SHIFT_FOR_TIMEBANK_CALCULATION), ShiftWithActivityDTO.class);
        DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOS, null, planningPeriodIntervals, dayTypeDTOS, validatedByPlanner);
        LOGGER.info("daily timebank : {}", dailyTimeBankEntry);
        Map<BigInteger, Integer> ctaDistributionCalculation = new HashMap<>(8);
        ctaDistributionCalculation.put(new BigInteger("3061"), 480);
        ctaDistributionCalculation.put(new BigInteger("3062"), 0);
        ctaDistributionCalculation.put(new BigInteger("3063"), 157);
        ctaDistributionCalculation.put(new BigInteger("3064"), 480);
        ctaDistributionCalculation.put(new BigInteger("3065"), 0);
        ctaDistributionCalculation.put(new BigInteger("3066"), 0);
        ctaDistributionCalculation.put(new BigInteger("3067"), 240);
        ctaDistributionCalculation.put(new BigInteger("3068"), 24);
        ctaDistributionCalculation.put(new BigInteger("3071"), 0);
        for (TimeBankCTADistribution timeBankCTADistribution : dailyTimeBankEntry.getTimeBankCTADistributionList()) {
            Assert.assertEquals(ctaDistributionCalculation.get(timeBankCTADistribution.getCtaRuleTemplateId()), timeBankCTADistribution.getMinutes());
        }
        Assert.assertEquals(dailyTimeBankEntry.getScheduledMinutesOfTimeBank(), 480);
        Assert.assertEquals(dailyTimeBankEntry.getTimeBankMinutesWithoutCta(), 480);
        Assert.assertEquals(dailyTimeBankEntry.getCtaBonusMinutesOfTimeBank(), 1381);
        Assert.assertEquals(dailyTimeBankEntry.getDeltaTimeBankMinutes(), 1381);
        Assert.assertEquals(dailyTimeBankEntry.getPlannedMinutesOfTimebank(), 1381);
        Assert.assertEquals(dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes(), 0);
    }

    @Test
    public void calculateScheduledAndDurationMinutes() {
        for (Shift shift : shifts) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                shiftActivity.setTimeType(TimeTypes.WORKING_TYPE.toString());
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activity, staffEmploymentDetails);
                Assert.assertEquals(shiftActivity.getScheduledMinutes(), 480);
                Assert.assertEquals(shiftActivity.getDurationMinutes(), 480);
                activity.getTimeCalculationActivityTab().setMethodForCalculatingTime(ENTERED_MANUALLY);
                shiftActivity.setDurationMinutes(180);
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activity, staffEmploymentDetails);
                Assert.assertEquals(shiftActivity.getScheduledMinutes(), 180);
                Assert.assertEquals(shiftActivity.getDurationMinutes(), 180);
                activity.getTimeCalculationActivityTab().setMethodForCalculatingTime(FIXED_TIME);
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activity, staffEmploymentDetails);
                Assert.assertEquals(shiftActivity.getScheduledMinutes(), 150);
                Assert.assertEquals(shiftActivity.getDurationMinutes(), 150);
                activity.getTimeCalculationActivityTab().setMethodForCalculatingTime(FULL_DAY_CALCULATION);
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activity, staffEmploymentDetails);
                Assert.assertEquals(shiftActivity.getScheduledMinutes(), 2220);
                Assert.assertEquals(shiftActivity.getDurationMinutes(), 2220);
                activity.getTimeCalculationActivityTab().setMethodForCalculatingTime(WEEKLY_HOURS);
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activity, staffEmploymentDetails);
                Assert.assertEquals(shiftActivity.getScheduledMinutes(), 1920);
                Assert.assertEquals(shiftActivity.getDurationMinutes(), 1920);
                activity.getTimeCalculationActivityTab().setMethodForCalculatingTime(WEEKLY_HOURS);
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activity, staffEmploymentDetails);
                Assert.assertEquals(shiftActivity.getScheduledMinutes(), 1920);
                Assert.assertEquals(shiftActivity.getDurationMinutes(), 1920);
            }
        }

    }

    @Test
    public void getAccumulatedTimebank(){
        /*StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByUnitPositionId(unitId, null, ORGANIZATION, unitPositionId, new HashSet<>());
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndBeforeAndEqualsDate(unitPositionId, asDate(endDate));
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = ObjectMapperUtils.jsonStringToObject(getUnitPositionDetailJson(),UnitPositionWithCtaDetailsDTO.class);
        Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(unitId, asDate(startDate), asDate(endDate));
        Map<LocalDate, TimeBankByDateDTO> localDateTimeBankByDateDTOMap = timeBankCalculationService.getAccumulatedTimebankDTO(planningPeriodIntervals, dailyTimeBankEntries, unitPositionWithCtaDetailsDTO, startDate, endDate);*/
    }


    private String getNightShiftJson() {
        return "[ {\n" +
                "  \"id\" : 2581,\n" +
                "  \"name\" : null,\n" +
                "  \"activities\" : [ {\n" +
                "    \"status\" : [ ],\n" +
                "    \"message\" : null,\n" +
                "    \"success\" : false,\n" +
                "    \"activity\" : {\n" +
                "      \"id\" : 268,\n" +
                "      \"name\" : \"Pleje ATA\",\n" +
                "      \"expertises\" : [ 795 ],\n" +
                "      \"description\" : \"Productive - nursing home\",\n" +
                "      \"unitId\" : 825,\n" +
                "      \"employmentTypes\" : [ 20604, 14045, 14046 ],\n" +
                "      \"generalActivityTab\" : {\n" +
                "        \"activityId\" : null,\n" +
                "        \"name\" : \"Pleje ATA\",\n" +
                "        \"code\" : null,\n" +
                "        \"printoutSymbol\" : null,\n" +
                "        \"categoryName\" : null,\n" +
                "        \"categoryId\" : 6,\n" +
                "        \"colorPresent\" : true,\n" +
                "        \"backgroundColor\" : \"\",\n" +
                "        \"description\" : \"Productive - nursing home\",\n" +
                "        \"shortName\" : \"Pleje\",\n" +
                "        \"eligibleForUse\" : true,\n" +
                "        \"ultraShortName\" : \"Pleje\",\n" +
                "        \"startDate\" : \"2018-01-01\",\n" +
                "        \"endDate\" : null,\n" +
                "        \"originalIconName\" : null,\n" +
                "        \"modifiedIconName\" : null,\n" +
                "        \"tags\" : [ ],\n" +
                "        \"addTimeTo\" : null,\n" +
                "        \"timeTypeId\" : null,\n" +
                "        \"onCallTimePresent\" : false,\n" +
                "        \"negativeDayBalancePresent\" : null,\n" +
                "        \"timeType\" : null,\n" +
                "        \"content\" : null,\n" +
                "        \"originalDocumentName\" : null,\n" +
                "        \"modifiedDocumentName\" : null,\n" +
                "        \"active\" : true\n" +
                "      },\n" +
                "      \"timeCalculationActivityTab\" : {\n" +
                "        \"activityId\" : null,\n" +
                "        \"methodForCalculatingTime\" : \"ENTERED_TIMES\",\n" +
                "        \"fullDayCalculationType\" : null,\n" +
                "        \"fullWeekCalculationType\" : null,\n" +
                "        \"allowBreakReduction\" : null,\n" +
                "        \"fixedTimeValue\" : 0,\n" +
                "        \"methodForCalculatingTimeInMonths\" : null,\n" +
                "        \"balanceType\" : null,\n" +
                "        \"multiplyWith\" : true,\n" +
                "        \"multiplyWithValue\" : 1.0,\n" +
                "        \"multiplyByVacationFactor\" : null,\n" +
                "        \"multiplyByFinalSchedule\" : null,\n" +
                "        \"breakTemplates\" : null,\n" +
                "        \"dayTypes\" : [ 13988, 20484, 13989, 20487, 13990, 20486, 13991, 20481, 13984 ],\n" +
                "        \"fullWeekStart\" : null,\n" +
                "        \"fullWeekEnd\" : null,\n" +
                "        \"historyDuration\" : 0,\n" +
                "        \"defaultStartTime\" : [ 7, 0 ],\n" +
                "        \"availableAllowActivity\" : false\n" +
                "      },\n" +
                "      \"rulesActivityTab\" : {\n" +
                "        \"activityId\" : null,\n" +
                "        \"eligibleForFinalSchedule\" : false,\n" +
                "        \"eligibleForDraftSchedule\" : false,\n" +
                "        \"eligibleForRequest\" : false,\n" +
                "        \"lockLengthPresent\" : false,\n" +
                "        \"eligibleToBeForced\" : false,\n" +
                "        \"dayTypes\" : [ 13988, 20484, 13989, 20487, 13990, 20486, 13991, 20481, 13984 ],\n" +
                "        \"eligibleForStaffingLevel\" : true,\n" +
                "        \"breakAllowed\" : false,\n" +
                "        \"approvalAllowed\" : false,\n" +
                "        \"cutOffStartFrom\" : \"1970-01-01\",\n" +
                "        \"cutOffIntervalUnit\" : null,\n" +
                "        \"cutOffdayValue\" : null,\n" +
                "        \"cutOffIntervals\" : [ ],\n" +
                "        \"cutOffBalances\" : null,\n" +
                "        \"earliestStartTime\" : [ 6, 0 ],\n" +
                "        \"latestStartTime\" : null,\n" +
                "        \"shortestTime\" : null,\n" +
                "        \"longestTime\" : null,\n" +
                "        \"eligibleForCopy\" : false,\n" +
                "        \"plannedTimeInAdvance\" : {\n" +
                "          \"value\" : 0,\n" +
                "          \"type\" : \"DAYS\"\n" +
                "        },\n" +
                "        \"maximumEndTime\" : null,\n" +
                "        \"allowedAutoAbsence\" : false,\n" +
                "        \"recurrenceDays\" : 0,\n" +
                "        \"recurrenceTimes\" : 0,\n" +
                "        \"pqlSettings\" : {\n" +
                "          \"approvalTimeInAdvance\" : {\n" +
                "            \"value\" : null,\n" +
                "            \"type\" : null\n" +
                "          },\n" +
                "          \"approvalPercentageWithoutMovement\" : null,\n" +
                "          \"approvalWithMovement\" : {\n" +
                "            \"approvalPercentage\" : null,\n" +
                "            \"approvalTime\" : null\n" +
                "          },\n" +
                "          \"appreciable\" : {\n" +
                "            \"approvalPercentage\" : null,\n" +
                "            \"approvalTime\" : null\n" +
                "          },\n" +
                "          \"acceptable\" : {\n" +
                "            \"approvalPercentage\" : null,\n" +
                "            \"approvalTime\" : null\n" +
                "          },\n" +
                "          \"critical\" : {\n" +
                "            \"approvalPercentage\" : null,\n" +
                "            \"approvalTime\" : null\n" +
                "          }\n" +
                "        },\n" +
                "        \"reasonCodeRequired\" : false,\n" +
                "        \"reasonCodeRequiredState\" : null\n" +
                "      },\n" +
                "      \"compositeActivities\" : [ ],\n" +
                "      \"balanceSettingsActivityTab\" : {\n" +
                "        \"activityId\" : null,\n" +
                "        \"addTimeTo\" : null,\n" +
                "        \"timeTypeId\" : 17,\n" +
                "        \"timeType\" : null,\n" +
                "        \"onCallTimePresent\" : false,\n" +
                "        \"negativeDayBalancePresent\" : false\n" +
                "      },\n" +
                "      \"parentId\" : 99,\n" +
                "      \"phaseSettingsActivityTab\" : {\n" +
                "        \"activityId\" : 268,\n" +
                "        \"phaseTemplateValues\" : [ {\n" +
                "          \"phaseId\" : 105,\n" +
                "          \"name\" : \"Request\",\n" +
                "          \"description\" : \"Request phase\",\n" +
                "          \"eligibleEmploymentTypes\" : [ 14045, 20604, 14046 ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : true,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\", \"STAFF\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ {\n" +
                "            \"shiftStatus\" : \"PUBLISH\",\n" +
                "            \"accessGroupIds\" : [ 750 ]\n" +
                "          }, {\n" +
                "            \"shiftStatus\" : \"UNPUBLISH\",\n" +
                "            \"accessGroupIds\" : [ 750 ]\n" +
                "          } ]\n" +
                "        }, {\n" +
                "          \"phaseId\" : 106,\n" +
                "          \"name\" : \"Puzzle\",\n" +
                "          \"description\" : \"Puzzle phase\",\n" +
                "          \"eligibleEmploymentTypes\" : [ 14045, 20604, 14046 ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : true,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\", \"STAFF\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ ]\n" +
                "        }, {\n" +
                "          \"phaseId\" : 107,\n" +
                "          \"name\" : \"Construction\",\n" +
                "          \"description\" : \"Construction phase\",\n" +
                "          \"eligibleEmploymentTypes\" : [ ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : false,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ ]\n" +
                "        }, {\n" +
                "          \"phaseId\" : 108,\n" +
                "          \"name\" : \"Draft\",\n" +
                "          \"description\" : \"Draft phase\",\n" +
                "          \"eligibleEmploymentTypes\" : [ 14045, 14046, 20604 ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : false,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ {\n" +
                "            \"shiftStatus\" : \"PUBLISH\",\n" +
                "            \"accessGroupIds\" : [ 750 ]\n" +
                "          }, {\n" +
                "            \"shiftStatus\" : \"UNPUBLISH\",\n" +
                "            \"accessGroupIds\" : [ 750 ]\n" +
                "          } ]\n" +
                "        }, {\n" +
                "          \"phaseId\" : 109,\n" +
                "          \"name\" : \"Tentative\",\n" +
                "          \"description\" : \"TENTATIVE PHASE\",\n" +
                "          \"eligibleEmploymentTypes\" : [ 14045, 14046, 20604 ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : false,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ ]\n" +
                "        }, {\n" +
                "          \"phaseId\" : 110,\n" +
                "          \"name\" : \"Realtime\",\n" +
                "          \"description\" : \"REALTIME PHASE\",\n" +
                "          \"eligibleEmploymentTypes\" : [ 14045 ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : false,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ ]\n" +
                "        }, {\n" +
                "          \"phaseId\" : 111,\n" +
                "          \"name\" : \"Time & Attendance\",\n" +
                "          \"description\" : \"TIME & ATTENDANCE PHASE\",\n" +
                "          \"eligibleEmploymentTypes\" : [ 14045 ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : false,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ ]\n" +
                "        }, {\n" +
                "          \"phaseId\" : 112,\n" +
                "          \"name\" : \"Payroll\",\n" +
                "          \"description\" : \"PAYROLL PHASE\",\n" +
                "          \"eligibleEmploymentTypes\" : [ ],\n" +
                "          \"eligibleForManagement\" : true,\n" +
                "          \"staffCanDelete\" : false,\n" +
                "          \"managementCanDelete\" : true,\n" +
                "          \"staffCanSell\" : false,\n" +
                "          \"managementCanSell\" : false,\n" +
                "          \"sequence\" : 0,\n" +
                "          \"allowedSettings\" : {\n" +
                "            \"canEdit\" : [ \"MANAGEMENT\" ]\n" +
                "          },\n" +
                "          \"activityShiftStatusSettings\" : [ ]\n" +
                "        } ]\n" +
                "      },\n" +
                "      \"skillActivityTab\" : {\n" +
                "        \"activityId\" : null,\n" +
                "        \"activitySkills\" : [ ],\n" +
                "        \"activitySkillIds\" : [ ]\n" +
                "      },\n" +
                "      \"activityCanBeCopied\" : false,\n" +
                "      \"tags\" : [ ],\n" +
                "      \"allowChildActivities\" : false,\n" +
                "      \"parentActivity\" : false\n" +
                "    },\n" +
                "    \"activityId\" : 268,\n" +
                "    \"startDate\" : 1555801200000,\n" +
                "    \"endDate\" : 1555830000000,\n" +
                "    \"scheduledMinutes\" : 480,\n" +
                "    \"durationMinutes\" : 480,\n" +
                "    \"activityName\" : \"Pleje ATA\",\n" +
                "    \"bid\" : 0,\n" +
                "    \"pId\" : 0,\n" +
                "    \"reasonCodeId\" : null,\n" +
                "    \"absenceReasonCodeId\" : null,\n" +
                "    \"remarks\" : \"\",\n" +
                "    \"id\" : 5224,\n" +
                "    \"timeType\" : null,\n" +
                "    \"backgroundColor\" : null,\n" +
                "    \"haltBreak\" : false,\n" +
                "    \"plannedTimeId\" : 2,\n" +
                "    \"breakShift\" : false,\n" +
                "    \"breakReplaced\" : false,\n" +
                "    \"reasonCode\" : null,\n" +
                "    \"allowedBreakDurationInMinute\" : null,\n" +
                "    \"timeBankCtaBonusMinutes\" : 0,\n" +
                "    \"timeBankCTADistributions\" : [ ],\n" +
                "    \"location\" : null,\n" +
                "    \"description\" : null,\n" +
                "    \"wtaRuleViolations\" : null,\n" +
                "    \"plannedMinutesOfTimebank\" : 0,\n" +
                "    \"startLocation\" : null,\n" +
                "    \"endLocation\" : null,\n" +
                "    \"scheduledMinutesOfTimebank\" : 0,\n" +
                "    \"scheduledMinutesOfPayout\" : 0\n" +
                "  } ],\n" +
                "  \"startDate\" : 1555801200000,\n" +
                "  \"endDate\" : 1555830000000,\n" +
                "  \"bonusTimeBank\" : 0,\n" +
                "  \"amount\" : 0,\n" +
                "  \"probability\" : 0,\n" +
                "  \"accumulatedTimeBankInMinutes\" : 0,\n" +
                "  \"remarks\" : null,\n" +
                "  \"unitPositionId\" : 18373,\n" +
                "  \"planningPeriodId\" : null,\n" +
                "  \"staffId\" : 413,\n" +
                "  \"phase\" : null,\n" +
                "  \"weekCount\" : null,\n" +
                "  \"shiftDate\" : null,\n" +
                "  \"unitId\" : 825,\n" +
                "  \"scheduledMinutes\" : 480,\n" +
                "  \"durationMinutes\" : 480,\n" +
                "  \"status\" : null,\n" +
                "  \"timeType\" : null,\n" +
                "  \"phaseId\" : 105,\n" +
                "  \"shiftType\" : null,\n" +
                "  \"wtaRuleViolations\" : null,\n" +
                "  \"startLocalDate\" : \"2019-04-20\",\n" +
                "  \"endLocalDate\" : \"2019-04-21\",\n" +
                "  \"minutes\" : 480\n" +
                "} ]";
    }

        });
        LOGGER.info("test {}",result);
    }

    boolean isValid(DiffNode arg0){
        Set<String> strings = newHashSet("int","long");
        return strings.contains(arg0.getValueType().getName()) && isNotNull(arg0.getParentNode()) && !arg0.getParentNode().getValueType().getPackage().getName().contains("java.time");
    }

    boolean isValidPa(DiffNode arg0){
        LOGGER.debug("property {}",arg0.getPropertyName());
        return isNotNull(arg0.getValueType().getPackage()) && isNotNull(arg0.getParentNode().getValueType().getPackage()) && !arg0.getParentNode().getValueType().getPackage().getName().contains("java.time");
    }

    //@Test
    public void updateMessageProperties() {
        File file = new File("/media/pradeep/bak/kairos/kairos-user/planner/src/main/resources/messages/messages.properties");
        File file2 = new File("/media/pradeep/bak/test.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            bufferedReader.lines().forEach(string->{
                if(!string.contains("#") && !string.isEmpty()){
                    String key = string.substring(0,string.indexOf("="));
                    try {
                        bufferedWriter.write("    public static final String "+key.replace(".","_").replace("-","_").toUpperCase()+" = "+'"'+key+'"'+";");
                        bufferedWriter.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(key);
                }
            });
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}