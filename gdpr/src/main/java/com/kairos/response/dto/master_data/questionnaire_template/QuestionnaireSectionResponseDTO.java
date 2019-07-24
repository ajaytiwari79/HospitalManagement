package com.kairos.response.dto.master_data.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireSectionResponseDTO {

    private Long id;

    @NotBlank(message = "name.cannot.be.empty.or.null")
    private String title;

    private List<QuestionBasicResponseDTO> questions=new ArrayList<>();

}
