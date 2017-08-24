package com.kairos.service.organization;
import com.kairos.persistence.model.organization.DayType;
import com.kairos.persistence.model.organization.OpeningHours;
import com.kairos.persistence.model.organization.OrganizationSetting;
import com.kairos.persistence.repository.organization.OpeningHourGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 6/4/17.
 */
@Transactional
@Service
public class OpenningHourService {

    @Inject
    private OpeningHourGraphRepository openingHourGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;


    public OrganizationSetting getDefaultSettings(){
        OrganizationSetting organizationSetting  = new OrganizationSetting();
        List<OpeningHours> openingHoursList = new ArrayList<>();
        OpeningHours openingHoursMonday = new OpeningHours(DayType.DayTypeEnum.MONDAY, "7-17",1);
        OpeningHours openingHoursTuesday = new OpeningHours(DayType.DayTypeEnum.TUESDAY, "7-17",2);
        OpeningHours openingHoursWednesday = new OpeningHours(DayType.DayTypeEnum.WEDNESDAY, "7-17",3);
        OpeningHours openingHoursThursday = new OpeningHours(DayType.DayTypeEnum.THURSDAY, "7-17",4);
        OpeningHours openingHoursFriday = new OpeningHours(DayType.DayTypeEnum.FRIDAY, "7-17",5);
        OpeningHours openingHoursSaturday = new OpeningHours(DayType.DayTypeEnum.SATURDAY, "7-17",6);
        OpeningHours openingHoursSunday = new OpeningHours(DayType.DayTypeEnum.SUNDAY, "7-17",7);
        openingHoursList.add(openingHoursMonday);
        openingHoursList.add(openingHoursTuesday);
        openingHoursList.add(openingHoursWednesday);
        openingHoursList.add(openingHoursThursday);
        openingHoursList.add(openingHoursFriday);
        openingHoursList.add(openingHoursSaturday);
        openingHoursList.add(openingHoursSunday);
        organizationSetting.setOpeningHour(openingHoursList);
        organizationSetting.setWorkingDays("5");
        return organizationSetting;
    }

    public OpeningHours updateOpeningHoursDetails(OpeningHours openingHours) {
        OpeningHours hours = openingHourGraphRepository.findOne(openingHours.getId());
        hours.setDay(openingHours.getDay());
        hours.setTiming(openingHours.getTiming());
        return openingHourGraphRepository.save(hours);
    }


    public Map<String, Object> getOpeningHoursAndHolidayDetails(Long organizationId) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("holidayList", getOrganizationHolidays(organizationId));
        objectMap.put("openingHours", organizationGraphRepository.getOpeningHours(organizationId));
        return objectMap;

    }

    public List<Object> getOrganizationHolidays(long unitId) {
        Long id = countryGraphRepository.getCountryOfUnit(unitId);
        List<Object> response = new ArrayList<>();
        if (id != null) {
            List<Map<String, Object>> data = countryGraphRepository.getAllCountryHolidays(id);
            ;
            for (Map map : data) {
                Object o = map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }


}
