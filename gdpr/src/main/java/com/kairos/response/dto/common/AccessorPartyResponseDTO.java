package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.SuggestedDataStatus;

import java.math.BigInteger;
import java.time.LocalDate;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessorPartyResponseDTO {


    private Long id;

    private String name;

    private Long organizationId;

    private SuggestedDataStatus suggestedDataStatus;

    private LocalDate suggestedDate;

    public AccessorPartyResponseDTO(Long id, String name, Long organizationId, SuggestedDataStatus suggestedDataStatus, LocalDate suggestedDate) {
        this.id = id;
        this.name = name;
        this.organizationId = organizationId;
        this.suggestedDataStatus = suggestedDataStatus;
        this.suggestedDate = suggestedDate;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
