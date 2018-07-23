package com.kairos.persistance.model.data_inventory.asset;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;

@Document(collection = "asset")
public class Asset extends MongoBaseEntity {

    private String name;

    private String description;

    private Long countryId;

    private List<BigInteger> storageFormats;

    private List<BigInteger> orgSecurityMeasures;

    private List<BigInteger> technicalSecurityMeasures;

    private BigInteger hostingProvider;

    private BigInteger hostingType;

    private BigInteger assetType;

    private Integer dataRetentionPeriod;

    private Long minDataSubjectVolume;

    private Long maxDataSubjectVolume;

    private RiskSeverity risk;

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public List<BigInteger> getStorageFormats() { return storageFormats; }

    public void setStorageFormats(List<BigInteger> storageFormats) { this.storageFormats = storageFormats; }

    public List<BigInteger> getOrgSecurityMeasures() { return orgSecurityMeasures; }

    public void setOrgSecurityMeasures(List<BigInteger> orgSecurityMeasures) { this.orgSecurityMeasures = orgSecurityMeasures; }

    public List<BigInteger> getTechnicalSecurityMeasures() { return technicalSecurityMeasures; }

    public void setTechnicalSecurityMeasures(List<BigInteger> technicalSecurityMeasures) { this.technicalSecurityMeasures = technicalSecurityMeasures; }

    public BigInteger getHostingProvider() { return hostingProvider; }

    public void setHostingProvider(BigInteger hostingProvider) { this.hostingProvider = hostingProvider; }

    public BigInteger getHostingType() { return hostingType; }

    public void setHostingType(BigInteger hostingType) { this.hostingType = hostingType; }

    public BigInteger getAssetType() { return assetType; }

    public void setAssetType(BigInteger assetType) { this.assetType = assetType; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public Long getMinDataSubjectVolume() { return minDataSubjectVolume; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public Long getMaxDataSubjectVolume() { return maxDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public RiskSeverity getRisk() { return risk; }

    public void setRisk(RiskSeverity risk) { this.risk = risk; }
}


 enum  RiskSeverity {

    HIGH("high"),  LOW("low"), MEDIUM("medium");
    public String value;
    RiskSeverity(String value) {
        this.value = value;
    }

}
