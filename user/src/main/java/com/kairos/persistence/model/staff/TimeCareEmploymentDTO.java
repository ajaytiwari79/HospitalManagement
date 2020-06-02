package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by prerna on 6/2/18.
 */
@Getter
@Setter
public class TimeCareEmploymentDTO{

    @JacksonXmlProperty
    private String Id;
    @JacksonXmlProperty
    private Date UpdateDate;
    @JacksonXmlProperty
    private String StartDate;
    @JacksonXmlProperty
    private String EndDate;
    @JacksonXmlProperty
    private String UpdateTypeFlag;
    @JacksonXmlProperty
    private String WeeklyHours;
    @JacksonXmlProperty
    private BigDecimal FullTimeHours;
    @JacksonXmlProperty
    private String WorkPlaceID;
    @JacksonXmlProperty
    private Long PersonID;
    @JacksonXmlProperty
    private String DutyCalcTypeID;
    @JacksonXmlProperty
    private Boolean UseBreak;
    @JacksonXmlProperty
    private BigDecimal MonthlyHours;
    @JacksonXmlProperty
    private String PositionId;
    @JacksonXmlProperty
    private String EmpNo;
}
