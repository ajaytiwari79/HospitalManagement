package com.kairos.dto.activity.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class PensionProviderDTO {
    private BigInteger id;
    @NotBlank(message = "name.absent")
    private String name;
    @NotBlank(message = "paymentNumber.absent")
    private String paymentNumber;
    private Long countryId;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }
}
