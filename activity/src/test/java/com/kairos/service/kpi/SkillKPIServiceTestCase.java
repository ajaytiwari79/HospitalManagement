package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.service.counter.SkillKPIService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SkillKPIServiceTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlannedHoursCalculationServiceTest.class);

    @InjectMocks
    private SkillKPIService skillKPIService;
    private List<StaffPersonalDetail> staffPersonalDetailList;
    private DateTimeInterval  dateTimeInterval;



    @Test
    public void getCountRepresentPerStaff(){
        staffPersonalDetailList = new ArrayList<>();
    }


    @Test
    public String getStaffPersonalDetails(){
      return null;
    }





}
