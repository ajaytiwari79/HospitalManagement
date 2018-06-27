package com.kairos.persistence.model.user.client;


import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by oodles on 28/9/16.
 */
public class ClientDisease extends UserBaseEntity {
    private String diseaseName;
    private String diseaseType;
    private String remarks;

    private long diseaseStart;
    private long diseaseEnd;

    public ClientDisease(String diseaseName, String diseaseType, String remarks) {
        this.diseaseName = diseaseName;
        this.diseaseType = diseaseType;
        this.remarks = remarks;
    }

    public ClientDisease() {
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getDiseaseStart() {
        return diseaseStart;
    }

    public void setDiseaseStart(long diseaseStart) {
        this.diseaseStart = diseaseStart;
    }

    public long getDiseaseEnd() {
        return diseaseEnd;
    }

    public void setDiseaseEnd(long diseaseEnd) {
        this.diseaseEnd = diseaseEnd;
    }
}
