package com.kairos.shiftplanning.domain.cta;

import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;
import com.kairos.shiftplanning.utils.StaticFields;
import org.joda.time.Interval;

import java.math.BigDecimal;

public class CTAInterval {

    private TimeInterval timeInterval;
    private CompensationType compensationType;
    private BigDecimal compensationValue;

    public CTAInterval(TimeInterval timeInterval, CompensationType compensationType, BigDecimal compensationValue) {
        this.timeInterval = timeInterval;
        this.compensationType = compensationType;
        this.compensationValue = compensationValue;
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    public CompensationType getCompensationType() {
        return compensationType;
    }

    public void setCompensationType(CompensationType compensationType) {
        this.compensationType = compensationType;
    }

    public BigDecimal getCompensationValue() {
        return compensationValue;
    }

    public void setCompensationValue(BigDecimal compensationValue) {
        this.compensationValue = compensationValue;
    }


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
