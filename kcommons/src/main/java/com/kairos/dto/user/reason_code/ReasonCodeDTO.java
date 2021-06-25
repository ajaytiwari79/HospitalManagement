package com.kairos.dto.user.reason_code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.enums.reason_code.ReasonCodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by pavan on 23/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ReasonCodeDTO {
    private BigInteger id;
    @NotBlank(message = "message.reasonCode.name.notEmpty")
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;
    //this is only persist when we create any Absence type reason code
    private BigInteger timeTypeId;
    private Long unitId;
    private Long countryId;
    private Map<String, TranslationInfo> translations;

    //TODO will get Remove after prod build
    private List<ProtectedDaysOffSettingDTO> protectedDaysOffSettingDTO;
    private List<CountryHolidayCalenderDTO> countryHolidayCalenderDTOS;

    public String getName(){
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription(){
        return TranslationUtil.getDescription(translations,description);
    }

    public ReasonCodeDTO(BigInteger id, @NotBlank(message = "message.reasonCode.name.notEmpty") String name) {
        this.id = id;
        this.name = name;
    }

    public ReasonCodeDTO(BigInteger id,String name, String code, String description, ReasonCodeType reasonCodeType,BigInteger timeTypeId) {
        this.id=id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
        this.timeTypeId=timeTypeId;
    }

    public ReasonCodeDTO(BigInteger id, String name, String code, String description, ReasonCodeType reasonCodeType) {
        this.id=id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReasonCodeDTO that = (ReasonCodeDTO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(code, that.code)
                .append(description, that.description)
                .append(reasonCodeType, that.reasonCodeType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(code)
                .append(description)
                .append(reasonCodeType)
                .toHashCode();
    }
}
