package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
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
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.enums.kpi.CalculationType.STAFF_SKILLS_COUNT;

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
    private List<StaffKpiFilterDTO> staffKpiFilterDTOS;
    private Map<Long,StaffKpiFilterDTO> staffKpiFilterDTOMap = new HashMap<>();

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
        this.staffKpiFilterDTOS = getStaffKPIFilterDTOS();
        this.staffKpiFilterDTOMap =getStaffKpiFilterDTOMap();
        this.kpiCalculationRelatedInfo = getKpiCalculationRelatedInfo();

    }


    private KPIBuilderCalculationService.KPICalculationRelatedInfo getKpiCalculationRelatedInfo(){
        KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPIBuilderCalculationService().new KPICalculationRelatedInfo();
        kpiCalculationRelatedInfo.setCalculationTypes(Arrays.asList(STAFF_SKILLS_COUNT));
        kpiCalculationRelatedInfo.setUnitId(unitId);
        kpiCalculationRelatedInfo.setStaffKpiFilterDTOS(staffKpiFilterDTOS);
        kpiCalculationRelatedInfo.setStaffIdAndStaffKpiFilterMap(staffKpiFilterDTOMap);
        kpiCalculationRelatedInfo.setDateTimeIntervals(Arrays.asList(dateTimeInterval));
        return kpiCalculationRelatedInfo;
    }

    private Map<Long,StaffKpiFilterDTO> getStaffKpiFilterDTOMap(){
        Map<Long,StaffKpiFilterDTO> staffKpiFilterDTOMap = new HashMap<>();
        staffKpiFilterDTOMap.put(staffIds.get(0),staffKpiFilterDTOS.get(0));
        return staffKpiFilterDTOMap;
    }

    private List<StaffKpiFilterDTO> getStaffKPIFilterDTOS(){
        return ObjectUtils.newArrayList(getStaffKPIFilterDTO());
    }


    private StaffKpiFilterDTO getStaffKPIFilterDTO(){
        StaffKpiFilterDTO staffKpiFilterDTO = new StaffKpiFilterDTO();
        staffKpiFilterDTO.setId(staffIds.get(0));
        staffKpiFilterDTO.setSkills(ObjectMapperUtils.jsonStringToList(getSkill(),SkillLevelDTO.class));
        return  staffKpiFilterDTO;
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
        double totalSkill = skillKPIService.getCountOfSkillOfStaffIdOnSelectedDate(staffIds.get(0),dateTimeInterval.getStartLocalDate(),dateTimeInterval.getEndLocalDate(),kpiCalculationRelatedInfo);
        Assert.assertEquals(1.0d,totalSkill,1.0d);
    }

}
