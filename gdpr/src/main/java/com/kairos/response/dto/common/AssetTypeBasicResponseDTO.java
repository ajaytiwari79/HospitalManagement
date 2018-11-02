package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.SuggestedDataStatus;

import java.math.BigInteger;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeBasicResponseDTO {

    private BigInteger id;
    private String name;
    private Long organizationId;
    private SuggestedDataStatus suggestedDataStatus;
    private Boolean subAssetType;
    private Set<BigInteger> risks;

    public Set<BigInteger> getRisks() { return risks; }

    public void setRisks(Set<BigInteger> risks) { this.risks = risks; }

    public Boolean getSubAssetType() { return subAssetType; }

    public void setSubAssetType(Boolean subAssetType) { this.subAssetType = subAssetType; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
