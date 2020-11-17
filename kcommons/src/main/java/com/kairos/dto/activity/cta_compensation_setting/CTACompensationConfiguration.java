package com.kairos.dto.activity.cta_compensation_setting;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.DurationType;
import com.kairos.enums.cta.CompensationType;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static com.kairos.commons.utils.DateUtils.getDateByIntervalType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CTACompensationConfiguration {

    private int from;
    private int to;
    private DurationType intervalType;
    private CompensationType compensationType;
    private int value;

    public DateTimeInterval getInterval(ZonedDateTime zonedDateTime){
        ZonedDateTime startDate = getDateByIntervalType(this.intervalType, from, zonedDateTime);
        ZonedDateTime endDate = getDateByIntervalType(this.intervalType, to, zonedDateTime);
        endDate = !intervalType.equals(DurationType.HOURS) ? endDate.plusDays(1) : endDate;
        return new DateTimeInterval(this.getFrom()==0 ? zonedDateTime : startDate, this.to==0 ? zonedDateTime : endDate);
    }
}
