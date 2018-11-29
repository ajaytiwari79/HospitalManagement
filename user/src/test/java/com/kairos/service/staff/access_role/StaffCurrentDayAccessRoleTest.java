package com.kairos.service.staff.access_role;

import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupDayTypesQueryResult;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupStaffQueryResult;
import com.kairos.persistence.model.access_permission.query_result.DayTypeCountryHolidayCalenderQueryResult;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.service.staff.StaffRetrievalService;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class StaffCurrentDayAccessRoleTest {

    @Mock
    AccessGroupRepository accessGroupRepository;

    @InjectMocks
    StaffRetrievalService staffRetrievalService;

    AccessGroupStaffQueryResult accessGroupStaffQueryResult=new AccessGroupStaffQueryResult();
    AccessGroup accessGroup=new AccessGroup();
    List<AccessGroupDayTypesQueryResult> dayTypesByAccessGroup;
    List<DayTypeCountryHolidayCalenderQueryResult> dayTypes;
    List<CountryHolidayCalendarQueryResult> countryHolidayCalenders;
    CountryHolidayCalendarQueryResult countryHolidayCalendarQueryResult=new CountryHolidayCalendarQueryResult();
    countryHolidayCalendarQueryResult.

}
