package com.kairos.persistence.model.data_inventory.assessment;


import com.kairos.enums.DurationType;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.model.risk_management.Risk;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Assessment extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotNull(message = "error.message.due.date.not.Selected")
    private LocalDate endDate;
    private LocalDate completedDate;
    private String comment;
    @OneToOne
    private Asset asset;
    private boolean isRiskAssessment;
    @OneToOne
    private ProcessingActivity processingActivity;
    @OneToMany
    private List<Risk> risks = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<AssessmentAnswer> assessmentAnswers = new ArrayList<>();
    @NotNull
    @Valid
    @ElementCollection
    private List<Staff> assigneeList = new ArrayList<>();
    @NotNull
    private Staff approver;
    private AssessmentStatus  assessmentStatus=AssessmentStatus.NEW;
    @OneToOne
    private QuestionnaireTemplate questionnaireTemplate;
    @Embedded
    private UserVO assessmentLastAssistBy;
    private LocalDate assessmentLaunchedDate;
    @NotNull(message = "error.message.start.date.not.Selected")
    private LocalDate startDate;
    private AssessmentSchedulingFrequency assessmentSchedulingFrequency;
    private int relativeDeadlineDuration;
    private DurationType relativeDeadlineType;
    @NotNull
    private Long organizationId;

    public Assessment(@NotBlank String name, @NotNull(message = "error.message.start.date.not.Selected") LocalDate startDate,@NotNull LocalDate endDate, String comment, @NotNull List<Staff> assigneeList, @NotNull Staff approver,@NotNull Long organizationId) {
        this.name = name;
        this.endDate = endDate;
        this.assigneeList = assigneeList;
        this.approver = approver;
        this.comment=comment;
        this.startDate=startDate;
        this.organizationId=organizationId;
    }

}
