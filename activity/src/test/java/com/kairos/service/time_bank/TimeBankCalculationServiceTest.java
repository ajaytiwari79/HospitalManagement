package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constaints.JsonConstaints;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constaints.JsonConstaints.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.FileReaderUtil.getFileDataAsString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeBankCalculationServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankCalculationServiceTest.class);

    @InjectMocks
    @Spy
    private TimeBankCalculationService timeBankCalculationService;
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

    static{
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
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

    }

    @Test
    public void getTimeBankByInterval() {
        doNothing().when(timeBankCalculationService).updatePublishedBalances(new DailyTimeBankEntry(),staffAdditionalInfoDTO.getEmployment().getEmploymentLines(),staffAdditionalInfoDTO.getUnitId(),new DailyTimeBankEntry().getDeltaAccumulatedTimebankMinutes());
        //when(timeBankCalculationService.updatePublishedBalances(new DailyTimeBankEntry(),staffAdditionalInfoDTO.getEmployment().getEmploymentLines(),staffAdditionalInfoDTO.getUnitId(),new DailyTimeBankEntry().getDeltaAccumulatedTimebankMinutes())).thenReturn(new DailyTimeBankEntry());
        DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOS, null, planningPeriodIntervals, dayTypeDTOS, validatedByPlanner);
        LOGGER.info("daily timebank : {}", dailyTimeBankEntry);
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

  //  @Test
    public void diffChecker(){

        DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(153l,179l,LocalDate.now());
        DailyTimeBankEntry dailyTimeBankEntry2 = new DailyTimeBankEntry(253l,179l,LocalDate.of(2019,12,15));

        //DiffNode diff = ObjectDifferBuilder.buildDefault().compare(dailyTimeBankEntry, dailyTimeBankEntry2);

            ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
            //builder.inclusion().exclude().propertyNameOfType(User.class, "authorities");
            DiffNode diff = builder.build().compare(dailyTimeBankEntry, dailyTimeBankEntry2);
            final Map<String , Object> result = new  HashMap<String, Object>();
            diff.visit(new DiffNode.Visitor()
            {
                @Override
                public void node(DiffNode arg0, Visit arg1) {
                    String path = arg0.getPath().getLastElementSelector().toHumanReadableString().toString();
                    final Object oldValue = arg0.canonicalGet(dailyTimeBankEntry2);
                    final Object newValue = arg0.canonicalGet(dailyTimeBankEntry);
                    String properteyName = arg0.getPropertyName();
                    if(isNotNull(properteyName) && isValid(arg0) || isValidPa(arg0)) {
                        if(arg0.isChanged() && !path.toUpperCase().contains("UPDATEDATE") && !path.equals("/")) {
                            result.put("new_" + path, newValue);
                            result.put("old_" + path, oldValue);
                        }
                        if((oldValue == null || newValue == null)) {
                            result.put("new" + path, newValue);
                            result.put("old" + path, oldValue);
                        }
                        if(arg0.isChanged() && !path.toUpperCase().contains("UPDATEDATE") && !path.equals("/") && !arg0.hasChildren()) {
                            result.put("new" + path, newValue);
                            result.put("old" + path, oldValue);
                        }
                    }
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