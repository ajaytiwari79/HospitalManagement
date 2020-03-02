package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.SkillKPIService;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.enums.kpi.CalculationType.STAFF_SKILLS_COUNT;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SkillKPIServiceTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlannedHoursCalculationServiceTest.class);

    @Mock
    private UserIntegrationService userIntegrationService;

    @InjectMocks
    private SkillKPIService skillKPIService;

    private List<Long> staffIds;
    private Long unitId;
    private DateTimeInterval dateTimeInterval;
    private KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo;
    private List<StaffPersonalDetail> staffPersonalDetails;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }

    @Before
    public void setUp(){
        this.staffIds=new ArrayList<>();
        this.staffIds.add(2364l);
        this.unitId =1172l;
        this.dateTimeInterval = new DateTimeInterval(asDate(asLocalDate("2020-02-01")),asDate(asLocalDate("2020-02-29")));
        this.kpiCalculationRelatedInfo = getKpiCalculationRelatedInfo();
        this.staffPersonalDetails = getStaffPersonalDetails();
    }


    private KPIBuilderCalculationService.KPICalculationRelatedInfo getKpiCalculationRelatedInfo(){
        KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPIBuilderCalculationService().new KPICalculationRelatedInfo();
        kpiCalculationRelatedInfo.setCalculationTypes(Arrays.asList(STAFF_SKILLS_COUNT));
        kpiCalculationRelatedInfo.setUnitId(unitId);
        kpiCalculationRelatedInfo.setDateTimeIntervals(Arrays.asList(dateTimeInterval));
        return kpiCalculationRelatedInfo;
    }

    private List<StaffPersonalDetail> getStaffPersonalDetails(){
        return ObjectUtils.newArrayList(getStaffPersonalDetail());
    }


    private StaffPersonalDetail getStaffPersonalDetail(){
        StaffPersonalDetail staffPersonalDetail = new StaffPersonalDetail();
        staffPersonalDetail.setId(staffIds.get(0));
        staffPersonalDetail.setSkills(ObjectMapperUtils.jsonStringToList(getSkill(),SkillLevelDTO.class));
        return  staffPersonalDetail;
    }

    private String getSkill() {
        return "[{\n" +
                " \"skillId\": 2015,\n" +
                " \"skillLevel\": \"BASIC\",\n" +
                " \"startDate\": \"2020-02-19\",\n" +
                " \"endDate\": null\n" +
                "}]";
    }

    @Test
    public void testTotalSkillOfStaffBetweenInterval(){
        Mockito.when(userIntegrationService.getAllSkillIdAndLevelByStaffIds(anyLong(),anyList())).thenReturn(staffPersonalDetails);
        int totalSkill = skillKPIService.getCountOfSkillByMonth(staffIds.get(0),staffPersonalDetails,dateTimeInterval.getStartLocalDate(),dateTimeInterval.getEndLocalDate());
        Assert.assertEquals(1,totalSkill);
    }












}
