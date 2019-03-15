package com.kairos.persistence.model.master_data.default_proc_activity_setting;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
public class ResponsibilityType extends BaseEntity {


    @NotBlank(message = "error.message.name.cannot.be.null.or.empty")
    @Pattern(message = "error.message.name.special.character.notAllowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;
    private Long organizationId;

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResponsibilityType(@NotBlank(message = "error.message.name.cannot.be.null.or.empty")  String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public ResponsibilityType(@NotBlank(message = "error.message.name.cannot.be.null.or.empty") @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name, Long organizationId) {
        this.name = name;
        this.organizationId = organizationId;
    }

    public ResponsibilityType(Long countryId, @NotBlank(message = "error.message.name.cannot.be.null.or.empty") @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name) {
        this.name = name;
        this.countryId = countryId;
    }

    public ResponsibilityType(String name) {
        this.name = name;
    }
    public ResponsibilityType() {
    }

    public ResponsibilityType(Long id) {
        this.id = id;
    }
}
