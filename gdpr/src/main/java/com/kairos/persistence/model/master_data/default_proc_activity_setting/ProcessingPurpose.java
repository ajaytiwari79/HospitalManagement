package com.kairos.persistence.model.master_data.default_proc_activity_setting;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Document
public class ProcessingPurpose extends MongoBaseEntity {


    @NotBlank(message = "error.message.name.cannot.be.null.or.empty")
    @Pattern(message = "Number and Special characters are not allowed for Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private Long countryId;

    private SuggestedDataStatus suggestedDataStatus;

    private LocalDate suggestedDate;

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

    public ProcessingPurpose(@NotBlank(message = "error.message.name.cannot.be.null.or.empty") String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public ProcessingPurpose(String name) {
        this.name = name;
    }
    public ProcessingPurpose() {
    }
}
