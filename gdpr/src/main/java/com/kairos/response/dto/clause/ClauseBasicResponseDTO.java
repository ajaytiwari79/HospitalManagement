package com.kairos.response.dto.clause;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.ClauseTagDTO;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
* clause basic response dto is for Agreement section
*
* */


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ClauseBasicResponseDTO {

    private Long id;
    private String title;
    private String titleHtml;
    private String description;
    private String descriptionHtml;
    private List<ClauseTagDTO> tags = new ArrayList<>();
    private boolean linkedWithOtherTemplate;
    private List<TemplateTypeResponseDTO> templateTypes = new ArrayList<>();

}
