package com.kairos.persistence.model.master_data.default_asset_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;


@Entity
public class HostingProvider extends BaseEntity {

    @NotBlank(message = "Name can't be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed for Name",regexp = "^[a-zA-Z\\s]+$")
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

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public String getName() { return name.trim(); }

    public void setName(String name) { this.name = name; }

    public HostingProvider(String name) { this.name = name; }

    public HostingProvider(@NotBlank(message = "Name can't be empty ") String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public HostingProvider(@NotBlank(message = "Name can't be empty ") String name, Long countryId) {
        this.name = name;
        this.countryId = countryId;
    }

    public HostingProvider() { }
}
