package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.SuggestedDataStatus;

import java.math.BigInteger;


@JsonIgnoreProperties(ignoreUnknown = true)

public class AccessorPartyResponseDTO {


    private BigInteger id;

    private String name;

    private Long organizationId;

    private SuggestedDataStatus suggestedDataStatus;

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }


    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
