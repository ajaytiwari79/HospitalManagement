package com.kairos.shiftplanning.domain.cta;

import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;
import com.kairos.shiftplanning.utils.StaticFields;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.Interval;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CTAInterval {

    private TimeInterval timeInterval;
    private CompensationType compensationType;
    private BigDecimal compensationValue;


    public BigDecimal getCostForThisIntervalByGranularity(int granularity,Interval interval,BigDecimal baseCost){
        BigDecimal costByInterval = new BigDecimal(0);
        Integer overLapMin = (int)timeInterval.overlap(interval).getTotalMinutes();
        if(overLapMin!=null){
            costByInterval = costByInterval.add(new BigDecimal(overLapMin).divide(new BigDecimal(granularity),BigDecimal.ROUND_UP).multiply(getCostByCompensationType(baseCost)));
        }
        return costByInterval;
    }

    private BigDecimal getCostByCompensationType(BigDecimal baseCost){
        switch (compensationType){
            case FIXED:return compensationValue;
            case MINUTES:return baseCost.divide(new BigDecimal(60)).multiply(compensationValue);
            case PERCENTAGE:return baseCost.divide(new BigDecimal(100), StaticFields.DECIMAL_PLACES,BigDecimal.ROUND_CEILING).multiply(compensationValue);

        }
        return null;
    }


}
