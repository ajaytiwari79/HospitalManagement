package com.kairos.dto.activity.payroll;
/*
 *Created By Pavan on 17/12/18
 *
 */

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class BankDTO {
    private BigInteger id;
    @NotBlank(message = "name.absent")
    private String name;
    private String description;
    @NotBlank(message = "registrationNumber.absent")
    private String registrationNumber;
    @NotBlank(message = "internationalAccountNumber.absent")
    private String internationalAccountNumber;
    @NotBlank(message = "swiftCode.absent")
    private String swiftCode; //stands for Society for Worldwide Interbank Financial Telecommunication
    private Long organizationId;
    private Long staffId;
    @Range(message = "accountNumber.greater_than.provided_value")
    private Long accountNumber;
    private Map<String, TranslationInfo> translations;
    private Long countryId;

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(translations,description);
    }
}
