package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.persistence.model.day_type.CountryHolidayCalender;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by oodles on 20/9/16.
 */

@Repository
public interface CountryCalenderRepo extends MongoBaseRepository<CountryHolidayCalender, BigInteger>, CustomCountryCalenderRepo {

    @Query("{'deleted':false,'countryId':?0,'holidayDate':?1,'$or':[{'endTime':{$exists:false}},{'startTime':{$lte:?2},'endTime':{$gte:?2}}]}")
    CountryHolidayCalender findActiveByCountryId(Long countryId, LocalDate holidayDate,LocalTime time);

    CountryHolidayCalender findByCountryId(Long countryId);

    List<CountryHolidayCalenderDTO> getAllByCountryIdAndHolidayDateBetween(Long countryId, LocalDate startDate, LocalDate endDate);

    CountryHolidayCalenderDTO getByCountryIdAndHolidayDateBetween(Long countryId, LocalDate startDate, LocalDate endDate);

    @Query("{deleted:false,publicHolidayCategory:?0,holidayDate:{$gte:?1},holidayDate:{$lt:?2}}")
    List<CountryHolidayCalender> getPublicHolidayByCategoryAndHolidayDateBetween(String publicHolidayCategory, LocalDate startDate, LocalDate endDate);

    @Query("{'deleted':false, 'holidayDate':{'$in':?0}}")
    List<CountryHolidayCalender> findByHolidayDates(List<LocalDate> holidayDates);
}
