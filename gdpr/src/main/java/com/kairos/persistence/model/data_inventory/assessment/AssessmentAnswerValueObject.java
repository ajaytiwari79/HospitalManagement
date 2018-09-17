package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentAnswerValueObject {


    private List<AssetAssessmentAnswer> assetAssessmentAnswers;

    private List<ProcessingActivityAssessmentAnswer> processingActivityAssessmentAnswers;

    public List<AssetAssessmentAnswer> getAssetAssessmentAnswers() { return assetAssessmentAnswers;}

    public void setAssetAssessmentAnswers(List<AssetAssessmentAnswer> assetAssessmentAnswers) { this.assetAssessmentAnswers = assetAssessmentAnswers; }

    public List<ProcessingActivityAssessmentAnswer> getProcessingActivityAssessmentAnswers() { return processingActivityAssessmentAnswers; }

    public void setProcessingActivityAssessmentAnswers(List<ProcessingActivityAssessmentAnswer> processingActivityAssessmentAnswers) { this.processingActivityAssessmentAnswers = processingActivityAssessmentAnswers; }
}
