package com.kairos.persistence.model.data_inventory.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AssessmentAnswer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long questionId;
    private String attributeName;
    @OneToOne(cascade = CascadeType.ALL)
    private SelectedChoice value;
    private QuestionType questionType;


    public AssessmentAnswer(Long questionId, String attributeName, SelectedChoice value, QuestionType questionType) {

        this.questionId = questionId;
        this.attributeName = attributeName;
        this.value = value;
        this.questionType = questionType;
    }

    public String getAttributeName() { return attributeName.trim(); }
}
