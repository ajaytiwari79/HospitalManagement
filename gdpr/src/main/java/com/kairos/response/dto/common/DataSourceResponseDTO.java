package com.kairos.response.dto.common;

import com.kairos.enums.SuggestedDataStatus;

import java.math.BigInteger;

public class DataSourceResponseDTO {

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
