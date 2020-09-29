package com.kairos.dto.activity.counter.kpi_set;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.commons.utils.NotNullOrEmpty;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.KPISetType;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class KPISetDTO {
    private BigInteger id;
    @NotNullOrEmpty(message = "error.name.notnull")
    private String name;
    @NotEmpty(message = "message.kpi.absent")
    private Set<BigInteger> kpiIds;
    private TimeTypeEnum timeType;
    private BigInteger phaseId;
    private Long referenceId;
    private ConfLevel confLevel;
    private KPISetType kpiSetType;
    private String shortName;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        return name.trim();
    }
}
