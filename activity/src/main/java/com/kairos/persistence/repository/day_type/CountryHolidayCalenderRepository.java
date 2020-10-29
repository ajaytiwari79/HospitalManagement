package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.persistence.model.day_type.CountryHolidayCalender;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by oodles on 20/9/16.
 */

@Repository
public interface CountryHolidayCalenderRepository extends MongoBaseRepository<CountryHolidayCalender, BigInteger>,CustomCountryHolidayCalenderRepository {

    CountryHolidayCalender findByCountryId(Long countryId);

    List<CountryHolidayCalenderDTO> getAllByCountryIdAndHolidayDateBetween(Long countryId, LocalDate startDate, LocalDate endDate);

    CountryHolidayCalenderDTO getByCountryIdAndHolidayDateBetween(Long countryId, LocalDate startDate, LocalDate endDate);

    @Query("{deleted:false,countryId:?0,holidayDate:?1,'$or':[{endTime:{$exists:false}},{startTime:{$lte:?2},endTime:{$gte:2}}]}")
    CountryHolidayCalenderDTO getCurrentlyActiveByCountryId(Long countryId, LocalDate holidayDate, LocalTime currentTime);


}
