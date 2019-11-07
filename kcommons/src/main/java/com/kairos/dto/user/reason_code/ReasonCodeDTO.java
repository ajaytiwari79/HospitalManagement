package com.kairos.dto.user.reason_code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.reason_code.ReasonCodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

/**
 * Created by pavan on 23/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ReasonCodeDTO {
    private Long id;
    @NotBlank(message = "message.reasonCode.name.notEmpty")
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;
    //this is only persist when we create any Absence type reason code
    private BigInteger timeTypeId;


    public ReasonCodeDTO(Long id, @NotBlank(message = "message.reasonCode.name.notEmpty") String name) {
        this.id = id;
        this.name = name;
    }

    public ReasonCodeDTO(Long id,String name, String code, String description, ReasonCodeType reasonCodeType,BigInteger timeTypeId) {
        this.id=id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
        this.timeTypeId=timeTypeId;
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
