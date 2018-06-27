package com.kairos.persistence.model.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.CountryHolidayCalender;
import com.kairos.persistence.model.country.DayType;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
@NodeEntity
public class CTARuleTemplateDayType extends UserBaseEntity {
    @Relationship(type = BELONGS_TO)
    private  DayType dayType;
    @Relationship(type = BELONGS_TO)
    private List<CountryHolidayCalender>countryHolidayCalenders=new ArrayList<>();

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public List<CountryHolidayCalender> getCountryHolidayCalenders() {
        return countryHolidayCalenders;
    }

    public void setCountryHolidayCalenders(List<CountryHolidayCalender> countryHolidayCalenders) {
        this.countryHolidayCalenders = countryHolidayCalenders;
    }
}
