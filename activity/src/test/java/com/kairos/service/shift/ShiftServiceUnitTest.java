package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ButtonConfig;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithViolatedInfoDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.phase.PhaseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Created by vipul on 19/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShiftServiceUnitTest {
    @InjectMocks
    private ShiftService shiftService;
    @InjectMocks
    private ShiftValidatorService shiftValidatorService;

    @Mock
    private PhaseMongoRepository phaseMongoRepository;
    @Mock
    private UserIntegrationService userIntegrationService;
    @Mock
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Mock
    private ShiftMongoRepository shiftMongoRepository;
    @Mock
    private ActivityMongoRepository activityMongoRepository;
    @Mock
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;
    @Mock private PhaseService phaseService;
    public ShiftDTO shiftDTO;
    public ShiftActivity activity;
    public ShiftActivityDTO activity1;
    public String timeZone;
    public ShiftState shiftState;
    public boolean realtime=true;
    boolean thrown=true;

    /**
     * This method is being used to check the all shift is not validated. So it should return false
     * send to payroll button will be shown only if all shifts is validated
     */
    @Test
    public void findButtonConfigForSendToPayrollNegativeCase() {
        LocalDate startdate = LocalDate.now();
        startdate = startdate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate enddate=startdate.plusDays(7);
        List<ShiftDTO> shifts = new ArrayList<>();

        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13870L),new Date(2018,11,19,13,0),new Date(2018,11,19,16,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13562L),new Date(2018,11,22,9,0),new Date(2018,11,22,14,0),35602L,32545L);
        shifts.add(shift);

        List<ShiftState> shiftStates = new ArrayList<>();
        ShiftState shiftState = new ShiftState(BigInteger.valueOf(13879L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shiftStates.add(shiftState);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());

        when(shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(shiftStates);

        ButtonConfig buttonConfig = shiftService.findButtonConfig(shifts,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),false);;


    }


    /**
     * This method is being used to check the all shift is  validated. So it should return true.
     * send to payroll button will be shown only if all shifts is validated
     */
    @Test
    public void findButtonConfigForSendToPayrollPositiveCase() {
        LocalDate startdate = LocalDate.now();
        startdate = startdate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate enddate=startdate.plusDays(7);
        List<ShiftDTO> shifts = new ArrayList<>();

        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);


        List<ShiftState> shiftStates = new ArrayList<>();
        ShiftState shiftState = new ShiftState(BigInteger.valueOf(13879L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shiftStates.add(shiftState);
        shiftState = new ShiftState(BigInteger.valueOf(13880L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,14139L);
        shiftStates.add(shiftState);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());

        when(shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(shiftStates);

        ButtonConfig buttonConfig = shiftService.findButtonConfig(shifts,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),true);;


    }

    @Test
    public void validateRealTimeShift(){
        timeZone="Asia/Kolkata";
        Long unitId=958l;
        Phase phase=new Phase();
        phase.setPhaseEnum(PhaseDefaultName.REALTIME);
        phase.setRealtimeDuration(5);
        phase.setId(BigInteger.valueOf(69));
        Map<String,Phase> phaseMap=new HashMap<>();
        phaseMap.put(phase.getPhaseEnum().toString(),phase);
        activity=new ShiftActivity();
        activity.setId(new BigInteger("12"));
        activity.setActivityId(BigInteger.valueOf(47));
        activity.setStartDate(new Date(2018,10,28,12,30));
        activity.setEndDate(new Date(2018,10,28,18,00));
        activity.setScheduledMinutes(360);
        activity.setScheduledMinutes(360);
        activity.setActivityName("12 Bronze");
        activity.setpId(0);
        activity.setBid(0);
        activity.setRemarks("");
        activity.setTimeType("WORKING_TYPE");
        activity.setBackgroundColor("");
        activity.setHaltBreak(false);
        activity.setBreakShift(false);
        Set<ShiftStatus> status=new HashSet<>();
        status.add(ShiftStatus.UNPUBLISH);
        activity.setStatus(status);
        activity.setStatus(new HashSet<>());
        activity1=new ShiftActivityDTO();
        activity1.setActivityId(BigInteger.valueOf(47));
        activity1.setStartDate(new Date(2018,10,28,12,30));
        activity1.setEndDate(new Date(2018,10,28,18,10));
        activity1.setScheduledMinutes(360);
        activity1.setScheduledMinutes(360);
        activity1.setActivityName("12 Bronze");
        activity1.setPId(0);
        activity1.setBid(0);
        activity1.setRemarks("");
        activity1.setTimeType("WORKING_TYPE");
        activity1.setBackgroundColor("");
        activity1.setHaltBreak(false);
        activity1.setBreakShift(false);
        status.add(ShiftStatus.UNPUBLISH);
        activity1.setStatus(status);
        activity1.setStatus(new HashSet<>());
        shiftState=new ShiftState();
        shiftState.setActivities(Arrays.asList(activity));
        shiftDTO=new ShiftDTO();
        shiftDTO.setId(BigInteger.valueOf(93));
        shiftDTO.setBid(0l);
        shiftDTO.setpId(0l);
        shiftDTO.setAmount(0);
        shiftDTO.setProbability(0);
        shiftDTO.setUnitId(958l);
        shiftDTO.setStaffId(834l);
        shiftDTO.setEmploymentId(1136l);
        shiftDTO.setActivities(Arrays.asList(activity1));
        shiftDTO.setScheduledMinutes(360);
        shiftDTO.setDurationMinutes(360);
        shiftDTO.setEditable(true);
        shiftDTO.setFunctionDeleted(false);
        shiftDTO.setShiftStatePhaseId(BigInteger.valueOf(69));
        shiftDTO.setShiftDate(LocalDate.of(2018,11,28));
        shiftDTO.setShiftId(BigInteger.valueOf(354));
        when(userIntegrationService.getTimeZoneByUnitId(unitId)).thenReturn(timeZone);
        when(shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhase(shiftDTO.getShiftId(), phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).thenReturn(shiftState);
        when(phaseService.shiftEditableInRealtime(timeZone,phaseMap,shiftDTO.getActivities().get(0).getStartDate(),shiftDTO.getActivities().get(shiftDTO.getActivities().size()-1).getEndDate())).thenReturn(realtime);
        try {
            shiftValidatorService.validateRealTimeShift(unitId,shiftDTO,phaseMap);
        }catch (Exception e){
            thrown=false;
        }
        assertTrue(thrown);

    }

   // @Test
    public void resolveEscalationOfShifts(){
        ShiftDTO shiftDTO=ObjectMapperUtils.jsonStringToObject(getShiftDTOJSON(),ShiftDTO.class);
        Shift shift=ObjectMapperUtils.jsonStringToObject(getShiftDTOJSON(),Shift.class);
        Date updatedStartDate=DateUtils.parseDate("2019-05-12T04:00:00.000Z");
        Date updatedEndDate=DateUtils.parseDate("2019-05-12T09:00:00.000Z");
        Date startDate = shiftDTO.getStartDate();
        Date endDate = shiftDTO.getEndDate();
        ActivityWrapper activityWrapper=ObjectMapperUtils.jsonStringToObject(getActivityDetailsJson(),ActivityWrapper.class);
        List<ShiftViolatedRules> shiftViolatedRules=ObjectMapperUtils.JsonStringToList(getListOfShiftViolationRules(),ShiftViolatedRules.class);
        List<Shift> overLappedShifts=ObjectMapperUtils.JsonStringToList(getOverLappedShift(),Shift.class);
        when(shiftMongoRepository.findOne(any(BigInteger.class))).thenReturn(shift);
        when(activityMongoRepository.findActivityAndTimeTypeByActivityId(any(BigInteger.class))).thenReturn(activityWrapper);
        when(shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(anyList())).thenReturn(shiftViolatedRules);
        when(shiftMongoRepository.findShiftBetweenDurationByEmploymentId(any(Long.class), any(Date.class),any(Date.class))).thenReturn(overLappedShifts);
        shiftDTO.setStartDate(updatedStartDate);
        shiftDTO.setEndDate(updatedEndDate);
        ShiftDTO result=shiftValidatorService.escalationCorrectionInShift(shiftDTO,startDate,endDate);
        List<BigInteger> escalationFreeShiftIds=new ArrayList<>();
        escalationFreeShiftIds.add(new BigInteger("2636"));
        escalationFreeShiftIds.add(new BigInteger("2637"));
        assertTrue(result.getEscalationFreeShiftIds().containsAll(escalationFreeShiftIds));
    }


  //  @Test
    public void escalationShouldNotResolveFromShifts(){
        ShiftDTO shiftDTO=ObjectMapperUtils.jsonStringToObject(getShiftDTOJSON(),ShiftDTO.class);
        Shift shift=ObjectMapperUtils.jsonStringToObject(getShiftDTOJSON(),Shift.class);
        Date updatedStartDate=DateUtils.parseDate("2019-05-12T04:00:00.000Z");
        Date updatedEndDate=DateUtils.parseDate("2019-05-12T09:00:00.000Z");
        Date startDate = shiftDTO.getStartDate();
        Date endDate = shiftDTO.getEndDate();
        ActivityWrapper activityWrapper=ObjectMapperUtils.jsonStringToObject(getActivityDetailsJson(),ActivityWrapper.class);
        when(shiftMongoRepository.findOne(shiftDTO.getId())).thenReturn(shift);
        when(activityMongoRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId())).thenReturn(activityWrapper);
        shiftDTO.setStartDate(updatedStartDate);
        shiftDTO.setEndDate(updatedEndDate);
        ShiftDTO result=shiftValidatorService.escalationCorrectionInShift(shiftDTO,startDate,endDate);
        List<BigInteger> escalationFreeShiftIds=new ArrayList<>();
        escalationFreeShiftIds.add(new BigInteger("2636"));
        escalationFreeShiftIds.add(new BigInteger("2637"));


        assertTrue(!result.getEscalationFreeShiftIds().containsAll(escalationFreeShiftIds));
    }

    private String getShiftDTOJSON(){
       return "{\n" +
               "   \"id\":2636,\n" +
               "   \"startDate\":1557640800000,\n" +
               "   \"endDate\":1557662400000,\n" +
               "   \"bid\":0,\n" +
               "   \"pId\":0,\n" +
               "   \"amount\":0,\n" +
               "   \"probability\":0,\n" +
               "   \"unitId\":1172,\n" +
               "   \"staffId\":1002,\n" +
               "   \"employmentId\":19902,\n" +
               "   \"shiftDate\":\"2019-05-12\",\n" +
               "   \"activities\":[\n" +
               "      {\n" +
               "         \"status\":[\n" +
               "\n" +
               "         ],\n" +
               "         \"message\":null,\n" +
               "         \"success\":false,\n" +
               "         \"activity\":null,\n" +
               "         \"activityId\":823,\n" +
               "         \"startDate\":1557640800000,\n" +
               "         \"endDate\":1557662400000,\n" +
               "         \"scheduledMinutes\":0,\n" +
               "         \"durationMinutes\":360,\n" +
               "         \"activityName\":\"Er tilgængelig\",\n" +
               "         \"bid\":0,\n" +
               "         \"pId\":0,\n" +
               "         \"reasonCodeId\":null,\n" +
               "         \"absenceReasonCodeId\":null,\n" +
               "         \"remarks\":\"\",\n" +
               "         \"id\":null,\n" +
               "         \"timeType\":\"NON_WORKING_TYPE\",\n" +
               "         \"backgroundColor\":null,\n" +
               "         \"haltBreak\":false,\n" +
               "         \"plannedTimeId\":null,\n" +
               "         \"breakShift\":false,\n" +
               "         \"breakReplaced\":true,\n" +
               "         \"reasonCode\":null,\n" +
               "         \"allowedBreakDurationInMinute\":null,\n" +
               "         \"timeBankCtaBonusMinutes\":0,\n" +
               "         \"timeBankCTADistributions\":[\n" +
               "\n" +
               "         ],\n" +
               "         \"location\":null,\n" +
               "         \"description\":null,\n" +
               "         \"wtaRuleViolations\":null,\n" +
               "         \"plannedMinutesOfTimebank\":0,\n" +
               "         \"startLocation\":\"\",\n" +
               "         \"endLocation\":\"\",\n" +
               "         \"scheduledMinutesOfTimebank\":0,\n" +
               "         \"scheduledMinutesOfPayout\":0\n" +
               "      }\n" +
               "   ],\n" +
               "   \"scheduledMinutes\":0,\n" +
               "   \"durationMinutes\":0,\n" +
               "   \"editable\":false,\n" +
               "   \"functionDeleted\":false,\n" +
               "   \"timeBankCtaBonusMinutes\":0,\n" +
               "   \"deltaTimeBankMinutes\":0,\n" +
               "   \"accumulatedTimeBankMinutes\":0,\n" +
               "   \"plannedMinutesOfTimebank\":0,\n" +
               "   \"multipleActivity\":false,\n" +
               "   \"restingMinutes\":0,\n" +
               "   \"escalationReasons\":[\n" +
               "\n" +
               "   ],\n" +
               "   \"escalationFreeShiftIds\":[\n" +
               "\n" +
               "   ],\n" +
               "   \"escalationResolved\":false\n" +
               "}" ;
    }

    private String getShiftViolation(){
        return "{\n" +
                "   \"shifts\":[\n" +
                "      {\n" +
                "         \"id\":2628,\n" +
                "         \"startDate\":1557208800000,\n" +
                "         \"endDate\":1557223200000,\n" +
                "         \"bid\":0,\n" +
                "         \"pId\":0,\n" +
                "         \"amount\":0,\n" +
                "         \"probability\":0,\n" +
                "         \"unitId\":1172,\n" +
                "         \"staffId\":1002,\n" +
                "         \"employmentId\":19902,\n" +
                "         \"activities\":[\n" +
                "            {\n" +
                "               \"status\":[\n" +
                "\n" +
                "               ],\n" +
                "               \"message\":null,\n" +
                "               \"success\":false,\n" +
                "               \"activity\":null,\n" +
                "               \"activityId\":823,\n" +
                "               \"startDate\":1557208800000,\n" +
                "               \"endDate\":1557223200000,\n" +
                "               \"scheduledMinutes\":0,\n" +
                "               \"durationMinutes\":240,\n" +
                "               \"activityName\":\"Er tilgængelig\",\n" +
                "               \"bid\":0,\n" +
                "               \"pId\":0,\n" +
                "               \"reasonCodeId\":null,\n" +
                "               \"absenceReasonCodeId\":null,\n" +
                "               \"remarks\":\"\",\n" +
                "               \"id\":5315,\n" +
                "               \"timeType\":\"NON_WORKING_TYPE\",\n" +
                "               \"backgroundColor\":null,\n" +
                "               \"haltBreak\":false,\n" +
                "               \"plannedTimeId\":2,\n" +
                "               \"breakShift\":false,\n" +
                "               \"breakReplaced\":true,\n" +
                "               \"reasonCode\":null,\n" +
                "               \"allowedBreakDurationInMinute\":null,\n" +
                "               \"timeBankCtaBonusMinutes\":0,\n" +
                "               \"timeBankCTADistributions\":[\n" +
                "\n" +
                "               ],\n" +
                "               \"location\":null,\n" +
                "               \"description\":null,\n" +
                "               \"wtaRuleViolations\":null,\n" +
                "               \"plannedMinutesOfTimebank\":0,\n" +
                "               \"startLocation\":\"\",\n" +
                "               \"endLocation\":\"\",\n" +
                "               \"scheduledMinutesOfTimebank\":0,\n" +
                "               \"scheduledMinutesOfPayout\":0\n" +
                "            }\n" +
                "         ],\n" +
                "         \"scheduledMinutes\":0,\n" +
                "         \"durationMinutes\":240,\n" +
                "         \"editable\":false,\n" +
                "         \"functionDeleted\":false,\n" +
                "         \"shiftType\":\"PRESENCE\",\n" +
                "         \"timeBankCtaBonusMinutes\":0,\n" +
                "         \"deltaTimeBankMinutes\":0,\n" +
                "         \"accumulatedTimeBankMinutes\":0,\n" +
                "         \"plannedMinutesOfTimebank\":0,\n" +
                "         \"multipleActivity\":false,\n" +
                "         \"planningPeriodId\":214,\n" +
                "         \"phaseId\":97,\n" +
                "         \"restingMinutes\":0,\n" +
                "         \"escalationReasons\":[\n" +
                "\n" +
                "         ],\n" +
                "         \"escalationFreeShiftIds\":[\n" +
                "\n" +
                "         ],\n" +
                "         \"escalationResolved\":false\n" +
                "      }\n" +
                "   ],\n" +
                "   \"violatedRules\":{\n" +
                "      \"workTimeAgreements\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"activities\":[\n" +
                "\n" +
                "      ]\n" +
                "   }\n" +
                "}";
    }

    private String getActivityDetailsJson(){
        return "{\n" +
                "   \"activity\":{\n" +
                "      \"id\":823,\n" +
                "      \"deleted\":false,\n" +
                "      \"name\":\"Er tilgængelig\",\n" +
                "      \"expertises\":[\n" +
                "         795,\n" +
                "         15278,\n" +
                "         14638,\n" +
                "         21993,\n" +
                "         14844,\n" +
                "         14685,\n" +
                "         20251,\n" +
                "         13523\n" +
                "      ],\n" +
                "      \"organizationTypes\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"organizationSubTypes\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"regions\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"levels\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"employmentTypes\":[\n" +
                "         14044,\n" +
                "         20605,\n" +
                "         14045,\n" +
                "         20604,\n" +
                "         14046\n" +
                "      ],\n" +
                "      \"tags\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"state\":\"DRAFT\",\n" +
                "      \"unitId\":1172,\n" +
                "      \"parentId\":770,\n" +
                "      \"generalActivityTab\":{\n" +
                "         \"name\":\"Er tilgængelig\",\n" +
                "         \"categoryId\":20,\n" +
                "         \"colorPresent\":true,\n" +
                "         \"eligibleForUse\":true,\n" +
                "         \"ultraShortName\":\"\",\n" +
                "         \"startDate\":\"2018-01-01\",\n" +
                "         \"tags\":[\n" +
                "\n" +
                "         ],\n" +
                "         \"active\":true\n" +
                "      },\n" +
                "      \"balanceSettingsActivityTab\":{\n" +
                "         \"addTimeTo\":null,\n" +
                "         \"timeTypeId\":9,\n" +
                "         \"timeType\":null,\n" +
                "         \"onCallTimePresent\":false,\n" +
                "         \"negativeDayBalancePresent\":false\n" +
                "      },\n" +
                "      \"rulesActivityTab\":{\n" +
                "         \"eligibleForFinalSchedule\":false,\n" +
                "         \"eligibleForDraftSchedule\":false,\n" +
                "         \"eligibleForRequest\":false,\n" +
                "         \"lockLengthPresent\":false,\n" +
                "         \"eligibleToBeForced\":false,\n" +
                "         \"dayTypes\":[\n" +
                "            13988,\n" +
                "            20484,\n" +
                "            13989,\n" +
                "            20487,\n" +
                "            13990,\n" +
                "            20486,\n" +
                "            13991,\n" +
                "            20481,\n" +
                "            13984\n" +
                "         ],\n" +
                "         \"eligibleForStaffingLevel\":false,\n" +
                "         \"breakAllowed\":false,\n" +
                "         \"approvalAllowed\":false,\n" +
                "         \"cutOffStartFrom\":\"1970-01-01\",\n" +
                "         \"cutOffIntervalUnit\":null,\n" +
                "         \"cutOffdayValue\":null,\n" +
                "         \"cutOffIntervals\":null,\n" +
                "         \"cutOffBalances\":null,\n" +
                "         \"earliestStartTime\":null,\n" +
                "         \"latestStartTime\":null,\n" +
                "         \"shortestTime\":null,\n" +
                "         \"longestTime\":null,\n" +
                "         \"eligibleForCopy\":false,\n" +
                "         \"plannedTimeInAdvance\":{\n" +
                "            \"value\":0,\n" +
                "            \"type\":\"DAYS\"\n" +
                "         },\n" +
                "         \"maximumEndTime\":null,\n" +
                "         \"allowedAutoAbsence\":false,\n" +
                "         \"recurrenceDays\":0,\n" +
                "         \"recurrenceTimes\":0,\n" +
                "         \"pqlSettings\":{\n" +
                "            \"approvalTimeInAdvance\":{\n" +
                "               \"value\":null,\n" +
                "               \"type\":null\n" +
                "            },\n" +
                "            \"approvalPercentageWithoutMovement\":null,\n" +
                "            \"approvalWithMovement\":{\n" +
                "               \"approvalPercentage\":null,\n" +
                "               \"approvalTime\":null\n" +
                "            },\n" +
                "            \"appreciable\":{\n" +
                "               \"approvalPercentage\":null,\n" +
                "               \"approvalTime\":null\n" +
                "            },\n" +
                "            \"acceptable\":{\n" +
                "               \"approvalPercentage\":null,\n" +
                "               \"approvalTime\":null\n" +
                "            },\n" +
                "            \"critical\":{\n" +
                "               \"approvalPercentage\":null,\n" +
                "               \"approvalTime\":null\n" +
                "            }\n" +
                "         },\n" +
                "         \"reasonCodeRequired\":false,\n" +
                "         \"reasonCodeRequiredState\":null\n" +
                "      },\n" +
                "      \"individualPointsActivityTab\":{\n" +
                "         \"individualPointsCalculationMethod\":\"addHourValues\",\n" +
                "         \"numberOfFixedPoints\":0.0\n" +
                "      },\n" +
                "      \"timeCalculationActivityTab\":{\n" +
                "         \"methodForCalculatingTime\":\"ENTERED_TIMES\",\n" +
                "         \"fixedTimeValue\":0,\n" +
                "         \"multiplyWith\":true,\n" +
                "         \"multiplyWithValue\":1.0,\n" +
                "         \"historyDuration\":0,\n" +
                "         \"defaultStartTime\":[\n" +
                "            7,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"dayTypes\":[\n" +
                "            13988,\n" +
                "            20484,\n" +
                "            13989,\n" +
                "            20487,\n" +
                "            13990,\n" +
                "            20486,\n" +
                "            13991,\n" +
                "            20481,\n" +
                "            13984\n" +
                "         ]\n" +
                "      },\n" +
                "      \"compositeActivities\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"childActivityIds\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"notesActivityTab\":{\n" +
                "         \"content\":null,\n" +
                "         \"originalDocumentName\":null,\n" +
                "         \"modifiedDocumentName\":null\n" +
                "      },\n" +
                "      \"communicationActivityTab\":{\n" +
                "         \"allowCommunicationReminder\":false,\n" +
                "         \"notifyAfterDeleteActivity\":false,\n" +
                "         \"activityReminderSettings\":null\n" +
                "      },\n" +
                "      \"skillActivityTab\":{\n" +
                "         \"activitySkills\":[\n" +
                "\n" +
                "         ],\n" +
                "         \"activitySkillIds\":[\n" +
                "\n" +
                "         ]\n" +
                "      },\n" +
                "      \"optaPlannerSettingActivityTab\":{\n" +
                "         \"maxThisActivityPerShift\":10,\n" +
                "         \"minLength\":0,\n" +
                "         \"eligibleForMove\":true\n" +
                "      },\n" +
                "      \"ctaAndWtaSettingsActivityTab\":{\n" +
                "         \"eligibleForCostCalculation\":false\n" +
                "      },\n" +
                "      \"locationActivityTab\":{\n" +
                "         \"glideTimeForCheckIn\":[\n" +
                "            {\n" +
                "               \"location\":\"OFFICE\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            },\n" +
                "            {\n" +
                "               \"location\":\"DEPOT\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            },\n" +
                "            {\n" +
                "               \"location\":\"OTHERS\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            },\n" +
                "            {\n" +
                "               \"location\":\"HOME\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            }\n" +
                "         ],\n" +
                "         \"glideTimeForCheckOut\":[\n" +
                "            {\n" +
                "               \"location\":\"DEPOT\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            },\n" +
                "            {\n" +
                "               \"location\":\"HOME\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            },\n" +
                "            {\n" +
                "               \"location\":\"OFFICE\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            },\n" +
                "            {\n" +
                "               \"location\":\"OTHERS\",\n" +
                "               \"before\":30,\n" +
                "               \"after\":30,\n" +
                "               \"eligible\":false\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      \"phaseSettingsActivityTab\":{\n" +
                "         \"activityId\":823,\n" +
                "         \"phaseTemplateValues\":[\n" +
                "            {\n" +
                "               \"phaseId\":97,\n" +
                "               \"name\":\"Request\",\n" +
                "               \"description\":\"Request phase\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14045,\n" +
                "                  20604,\n" +
                "                  14046,\n" +
                "                  14044,\n" +
                "                  20605\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"phaseId\":98,\n" +
                "               \"name\":\"Puzzle\",\n" +
                "               \"description\":\"Puzzle phase\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14044,\n" +
                "                  20605,\n" +
                "                  14045,\n" +
                "                  20604,\n" +
                "                  14046\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"phaseId\":99,\n" +
                "               \"name\":\"Construction\",\n" +
                "               \"description\":\"Construction phase\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14044,\n" +
                "                  20605,\n" +
                "                  14045,\n" +
                "                  20604,\n" +
                "                  14046\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"phaseId\":100,\n" +
                "               \"name\":\"Draft\",\n" +
                "               \"description\":\"Draft phase\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14045,\n" +
                "                  20604,\n" +
                "                  14046,\n" +
                "                  14044,\n" +
                "                  20605\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"phaseId\":101,\n" +
                "               \"name\":\"Tentative\",\n" +
                "               \"description\":\"TENTATIVE PHASE\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14045,\n" +
                "                  20604,\n" +
                "                  14046,\n" +
                "                  14044,\n" +
                "                  20605\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"phaseId\":102,\n" +
                "               \"name\":\"Realtime\",\n" +
                "               \"description\":\"REALTIME PHASE\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14045,\n" +
                "                  14044,\n" +
                "                  20605,\n" +
                "                  20604,\n" +
                "                  14046\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"phaseId\":103,\n" +
                "               \"name\":\"Time & Attendance\",\n" +
                "               \"description\":\"TIME & ATTENDANCE PHASE\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14044,\n" +
                "                  20605,\n" +
                "                  14045,\n" +
                "                  20604,\n" +
                "                  14046\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"phaseId\":104,\n" +
                "               \"name\":\"Payroll\",\n" +
                "               \"description\":\"PAYROLL PHASE\",\n" +
                "               \"eligibleEmploymentTypes\":[\n" +
                "                  14044,\n" +
                "                  20605,\n" +
                "                  14045,\n" +
                "                  20604,\n" +
                "                  14046\n" +
                "               ],\n" +
                "               \"eligibleForManagement\":true,\n" +
                "               \"staffCanDelete\":true,\n" +
                "               \"managementCanDelete\":true,\n" +
                "               \"staffCanSell\":true,\n" +
                "               \"managementCanSell\":true,\n" +
                "               \"sequence\":0,\n" +
                "               \"allowedSettings\":{\n" +
                "                  \"canEdit\":[\n" +
                "                     \"MANAGEMENT\",\n" +
                "                     \"STAFF\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               \"activityShiftStatusSettings\":[\n" +
                "\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      \"parentActivity\":false\n" +
                "   },\n" +
                "   \"timeType\":\"NON_WORKING_TYPE\",\n" +
                "   \"timeTypeInfo\":null\n" +
                "}";
    }

    private String getListOfShiftViolationRules(){
        return "[\n" +
                "   {\n" +
                "      \"id\":2328,\n" +
                "      \"deleted\":false,\n" +
                "      \"shiftId\":2636,\n" +
                "      \"workTimeAgreements\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"activities\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"escalationReasons\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"escalationResolved\":false\n" +
                "   },\n" +
                "   {\n" +
                "      \"id\":2329,\n" +
                "      \"deleted\":false,\n" +
                "      \"shiftId\":2637,\n" +
                "      \"workTimeAgreements\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"activities\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"escalationReasons\":[\n" +
                "         \"SHIFT_OVERLAPPING\"\n" +
                "      ],\n" +
                "      \"escalationResolved\":false\n" +
                "   }\n" +
                "]";
    }

    private String getOverLappedShift(){
        return "[\n" +
                "   {\n" +
                "      \"id\":2636,\n" +
                "      \"deleted\":false,\n" +
                "      \"startDate\":1557640800000,\n" +
                "      \"endDate\":1557662400000,\n" +
                "      \"disabled\":false,\n" +
                "      \"bid\":0,\n" +
                "      \"pId\":0,\n" +
                "      \"bonusTimeBank\":0,\n" +
                "      \"amount\":0,\n" +
                "      \"probability\":0,\n" +
                "      \"accumulatedTimeBankInMinutes\":0,\n" +
                "      \"remarks\":null,\n" +
                "      \"staffId\":1002,\n" +
                "      \"phaseId\":97,\n" +
                "      \"planningPeriodId\":214,\n" +
                "      \"weekCount\":null,\n" +
                "      \"unitId\":1172,\n" +
                "      \"scheduledMinutes\":0,\n" +
                "      \"durationMinutes\":240,\n" +
                "      \"activities\":[\n" +
                "         {\n" +
                "            \"activityId\":823,\n" +
                "            \"startDate\":1557554400000,\n" +
                "            \"endDate\":1557568800000,\n" +
                "            \"scheduledMinutes\":0,\n" +
                "            \"durationMinutes\":240,\n" +
                "            \"activityName\":\"Er tilgængelig\",\n" +
                "            \"bid\":0,\n" +
                "            \"pId\":0,\n" +
                "            \"reasonCodeId\":null,\n" +
                "            \"absenceReasonCodeId\":null,\n" +
                "            \"remarks\":\"\",\n" +
                "            \"id\":5329,\n" +
                "            \"timeType\":\"NON_WORKING_TYPE\",\n" +
                "            \"backgroundColor\":null,\n" +
                "            \"haltBreak\":false,\n" +
                "            \"plannedTimeId\":2,\n" +
                "            \"breakShift\":false,\n" +
                "            \"breakReplaced\":true,\n" +
                "            \"timeBankCTADistributions\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"payoutPerShiftCTADistributions\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"payoutCtaBonusMinutes\":0,\n" +
                "            \"allowedBreakDurationInMinute\":null,\n" +
                "            \"timeBankCtaBonusMinutes\":0,\n" +
                "            \"startLocation\":\"\",\n" +
                "            \"endLocation\":\"\",\n" +
                "            \"plannedMinutesOfTimebank\":0,\n" +
                "            \"plannedMinutesOfPayout\":0,\n" +
                "            \"scheduledMinutesOfTimebank\":0,\n" +
                "            \"scheduledMinutesOfPayout\":0,\n" +
                "            \"status\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"interval\":{\n" +
                "               \"start\":1557554400.000000000,\n" +
                "               \"end\":1557568800.000000000,\n" +
                "               \"startLocalTime\":[\n" +
                "                  6,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"endLocalTime\":[\n" +
                "                  10,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"startLocalDateTime\":[\n" +
                "                  2019,\n" +
                "                  5,\n" +
                "                  11,\n" +
                "                  6,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"endLocalDateTime\":[\n" +
                "                  2019,\n" +
                "                  5,\n" +
                "                  11,\n" +
                "                  10,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"startDate\":1557554400000,\n" +
                "               \"endDate\":1557568800000,\n" +
                "               \"milliSeconds\":14400000,\n" +
                "               \"startLocalDate\":\"2019-05-11\",\n" +
                "               \"endLocalDate\":\"2019-05-11\",\n" +
                "               \"startMillis\":1557554400000,\n" +
                "               \"endMillis\":1557568800000,\n" +
                "               \"days\":0,\n" +
                "               \"hours\":4,\n" +
                "               \"minutes\":240,\n" +
                "               \"seconds\":14400\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"externalId\":null,\n" +
                "      \"employmentId\":19902,\n" +
                "      \"parentOpenShiftId\":null,\n" +
                "      \"copiedFromShiftId\":null,\n" +
                "      \"sickShift\":false,\n" +
                "      \"functionId\":null,\n" +
                "      \"staffUserId\":1001,\n" +
                "      \"shiftType\":\"PRESENCE\",\n" +
                "      \"timeBankCtaBonusMinutes\":0,\n" +
                "      \"plannedMinutesOfTimebank\":0,\n" +
                "      \"payoutCtaBonusMinutes\":0,\n" +
                "      \"plannedMinutesOfPayout\":0,\n" +
                "      \"scheduledMinutesOfTimebank\":0,\n" +
                "      \"scheduledMinutesOfPayout\":0,\n" +
                "      \"interval\":{\n" +
                "         \"start\":1557554400.000000000,\n" +
                "         \"end\":1557568800.000000000,\n" +
                "         \"startLocalTime\":[\n" +
                "            6,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"endLocalTime\":[\n" +
                "            10,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"startLocalDateTime\":[\n" +
                "            2019,\n" +
                "            5,\n" +
                "            11,\n" +
                "            6,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"endLocalDateTime\":[\n" +
                "            2019,\n" +
                "            5,\n" +
                "            11,\n" +
                "            10,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"startDate\":1557554400000,\n" +
                "         \"endDate\":1557568800000,\n" +
                "         \"milliSeconds\":14400000,\n" +
                "         \"startLocalDate\":\"2019-05-11\",\n" +
                "         \"endLocalDate\":\"2019-05-11\",\n" +
                "         \"startMillis\":1557554400000,\n" +
                "         \"endMillis\":1557568800000,\n" +
                "         \"days\":0,\n" +
                "         \"hours\":4,\n" +
                "         \"minutes\":240,\n" +
                "         \"seconds\":14400\n" +
                "      },\n" +
                "      \"minutes\":240\n" +
                "   },\n" +
                "   {\n" +
                "      \"id\":2637,\n" +
                "      \"deleted\":false,\n" +
                "      \"startDate\":1557658800000,\n" +
                "      \"endDate\":1557680400000,\n" +
                "      \"disabled\":false,\n" +
                "      \"bid\":0,\n" +
                "      \"pId\":0,\n" +
                "      \"bonusTimeBank\":0,\n" +
                "      \"amount\":0,\n" +
                "      \"probability\":0,\n" +
                "      \"accumulatedTimeBankInMinutes\":0,\n" +
                "      \"remarks\":\"\",\n" +
                "      \"staffId\":1002,\n" +
                "      \"phaseId\":97,\n" +
                "      \"planningPeriodId\":214,\n" +
                "      \"weekCount\":null,\n" +
                "      \"unitId\":1172,\n" +
                "      \"scheduledMinutes\":300,\n" +
                "      \"durationMinutes\":300,\n" +
                "      \"activities\":[\n" +
                "         {\n" +
                "            \"activityId\":840,\n" +
                "            \"startDate\":1557658800000,\n" +
                "            \"endDate\":1557680400000,\n" +
                "            \"scheduledMinutes\":300,\n" +
                "            \"durationMinutes\":300,\n" +
                "            \"activityName\":\"Kursus\",\n" +
                "            \"bid\":0,\n" +
                "            \"pId\":0,\n" +
                "            \"reasonCodeId\":null,\n" +
                "            \"absenceReasonCodeId\":null,\n" +
                "            \"remarks\":null,\n" +
                "            \"id\":5328,\n" +
                "            \"timeType\":\"WORKING_TYPE\",\n" +
                "            \"backgroundColor\":\"\",\n" +
                "            \"haltBreak\":false,\n" +
                "            \"plannedTimeId\":2,\n" +
                "            \"breakShift\":false,\n" +
                "            \"breakReplaced\":true,\n" +
                "            \"timeBankCTADistributions\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"payoutPerShiftCTADistributions\":null,\n" +
                "            \"payoutCtaBonusMinutes\":0,\n" +
                "            \"allowedBreakDurationInMinute\":null,\n" +
                "            \"timeBankCtaBonusMinutes\":0,\n" +
                "            \"startLocation\":\"\",\n" +
                "            \"endLocation\":\"\",\n" +
                "            \"plannedMinutesOfTimebank\":0,\n" +
                "            \"plannedMinutesOfPayout\":0,\n" +
                "            \"scheduledMinutesOfTimebank\":0,\n" +
                "            \"scheduledMinutesOfPayout\":0,\n" +
                "            \"status\":[\n" +
                "\n" +
                "            ],\n" +
                "            \"interval\":{\n" +
                "               \"start\":1557572400.000000000,\n" +
                "               \"end\":1557590400.000000000,\n" +
                "               \"startLocalTime\":[\n" +
                "                  11,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"endLocalTime\":[\n" +
                "                  16,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"startLocalDateTime\":[\n" +
                "                  2019,\n" +
                "                  5,\n" +
                "                  11,\n" +
                "                  11,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"endLocalDateTime\":[\n" +
                "                  2019,\n" +
                "                  5,\n" +
                "                  11,\n" +
                "                  16,\n" +
                "                  0\n" +
                "               ],\n" +
                "               \"startDate\":1557572400000,\n" +
                "               \"endDate\":1557590400000,\n" +
                "               \"milliSeconds\":18000000,\n" +
                "               \"startLocalDate\":\"2019-05-11\",\n" +
                "               \"endLocalDate\":\"2019-05-11\",\n" +
                "               \"startMillis\":1557572400000,\n" +
                "               \"endMillis\":1557590400000,\n" +
                "               \"days\":0,\n" +
                "               \"hours\":5,\n" +
                "               \"minutes\":300,\n" +
                "               \"seconds\":18000\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"externalId\":null,\n" +
                "      \"employmentId\":19902,\n" +
                "      \"parentOpenShiftId\":null,\n" +
                "      \"copiedFromShiftId\":null,\n" +
                "      \"sickShift\":false,\n" +
                "      \"functionId\":null,\n" +
                "      \"staffUserId\":1001,\n" +
                "      \"shiftType\":\"PRESENCE\",\n" +
                "      \"timeBankCtaBonusMinutes\":0,\n" +
                "      \"plannedMinutesOfTimebank\":0,\n" +
                "      \"payoutCtaBonusMinutes\":0,\n" +
                "      \"plannedMinutesOfPayout\":0,\n" +
                "      \"scheduledMinutesOfTimebank\":0,\n" +
                "      \"scheduledMinutesOfPayout\":0,\n" +
                "      \"interval\":{\n" +
                "         \"start\":1557572400.000000000,\n" +
                "         \"end\":1557590400.000000000,\n" +
                "         \"startLocalTime\":[\n" +
                "            11,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"endLocalTime\":[\n" +
                "            16,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"startLocalDateTime\":[\n" +
                "            2019,\n" +
                "            5,\n" +
                "            11,\n" +
                "            11,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"endLocalDateTime\":[\n" +
                "            2019,\n" +
                "            5,\n" +
                "            11,\n" +
                "            16,\n" +
                "            0\n" +
                "         ],\n" +
                "         \"startDate\":1557572400000,\n" +
                "         \"endDate\":1557590400000,\n" +
                "         \"milliSeconds\":18000000,\n" +
                "         \"startLocalDate\":\"2019-05-11\",\n" +
                "         \"endLocalDate\":\"2019-05-11\",\n" +
                "         \"startMillis\":1557572400000,\n" +
                "         \"endMillis\":1557590400000,\n" +
                "         \"days\":0,\n" +
                "         \"hours\":5,\n" +
                "         \"minutes\":300,\n" +
                "         \"seconds\":18000\n" +
                "      },\n" +
                "      \"minutes\":300\n" +
                "   }\n" +
                "]";
    }



}