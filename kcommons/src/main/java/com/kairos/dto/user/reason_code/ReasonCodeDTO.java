package com.kairos.dto.user.reason_code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.reason_code.ReasonCodeType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotBlank;

/**
 * Created by pavan on 23/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReasonCodeDTO {
    private Long id;
    @NotBlank(message = "message.reasonCode.name.notEmpty")
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;

    public ReasonCodeDTO() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = StringUtils.trim(code);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }

    public ReasonCodeType getReasonCodeType() {
        return reasonCodeType;
    }

    public void setReasonCodeType(ReasonCodeType reasonCodeType) {
        this.reasonCodeType = reasonCodeType;
    }

    public ReasonCodeDTO(Long id,String name, String code, String description, ReasonCodeType reasonCodeType) {
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
