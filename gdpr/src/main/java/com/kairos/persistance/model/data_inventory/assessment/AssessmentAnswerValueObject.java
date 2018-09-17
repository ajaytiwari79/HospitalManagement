package com.kairos.persistance.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentAnswerValueObject {


    private List<AssetAssessmentAnswerVO> assetAssessmentAnswers;

    private List<ProcessingActivityAssessmentAnswerVO> processingActivityAssessmentAnswers;

    public List<AssetAssessmentAnswerVO> getAssetAssessmentAnswers() { return assetAssessmentAnswers;}

    public void setAssetAssessmentAnswers(List<AssetAssessmentAnswerVO> assetAssessmentAnswers) { this.assetAssessmentAnswers = assetAssessmentAnswers; }

    public List<ProcessingActivityAssessmentAnswerVO> getProcessingActivityAssessmentAnswers() { return processingActivityAssessmentAnswers; }

    public void setProcessingActivityAssessmentAnswers(List<ProcessingActivityAssessmentAnswerVO> processingActivityAssessmentAnswers) { this.processingActivityAssessmentAnswers = processingActivityAssessmentAnswers; }
}
