package com.kairos.dto.activity.payroll;
/*
 *Created By Pavan on 14/12/18
 *
 */

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@Getter
@Setter
public class PayRollDTO {
    private BigInteger id;

    @NotBlank(message = "name.absent")
    private String name;
    private int code;
    private boolean active;
    private Set<Long> countryIds=new HashSet<>();

    private boolean applicableForCountry;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }
}
