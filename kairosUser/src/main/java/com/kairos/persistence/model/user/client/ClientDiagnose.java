package com.kairos.persistence.model.user.client;


import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by oodles on 28/9/16.
 */
public class ClientDiagnose extends UserBaseEntity {


    private Long diagnoseStart;
    private Long diagnoseEnd;
    private String diagnoseName;
    private String description;
    private String result;
    private String remarks;


    public ClientDiagnose(String diagnoseName, String description, String result, String remarks) {
        this.diagnoseName = diagnoseName;
        this.description = description;
        this.result = result;
        this.remarks = remarks;
    }

    public ClientDiagnose() {
    }

    public Long getDiagnoseStart() {
        return diagnoseStart;
    }

    public void setDiagnoseStart(Long diagnoseStart) {
        this.diagnoseStart = diagnoseStart;
    }

    public Long getDiagnoseEnd() {
        return diagnoseEnd;
    }

    public void setDiagnoseEnd(Long diagnoseEnd) {
        this.diagnoseEnd = diagnoseEnd;
    }

    public String getDiagnoseName() {
        return diagnoseName;
    }

    public void setDiagnoseName(String diagnoseName) {
        this.diagnoseName = diagnoseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
