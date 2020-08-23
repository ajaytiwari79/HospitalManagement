package com.kairos.dto.activity.activity;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class ActivityPriorityDTO {

    private BigInteger id;
    private Long countryId;
    private Long organizationId;
    @NotBlank(message = "error.name.notnull")
    private String name;
    private String description;
    @Range(min = 1,message = "message.activity.priority.sequence")
    private int sequence;
    private String colorCode;
    private Map<String, TranslationInfo> translations;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
