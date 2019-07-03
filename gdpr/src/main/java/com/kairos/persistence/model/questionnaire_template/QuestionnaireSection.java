package com.kairos.persistence.model.questionnaire_template;

import com.kairos.persistence.model.common.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireSection extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String title;

    @OneToMany(cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_section_id")
    private List<Question> questions;
    private Long countryId;
    private Long organizationId;

    public QuestionnaireSection(String title,  Long countryId, Long organizationId) {
        this.title = title;
        this.countryId = countryId;
        this.organizationId = organizationId;
    }


    @Override
    public void delete() {
        super.delete();
        this.questions.forEach(BaseEntity::delete);
    }
}
