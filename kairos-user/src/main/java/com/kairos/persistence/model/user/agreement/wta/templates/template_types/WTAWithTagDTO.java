package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.web.tag.TagDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 30/11/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTAWithTagDTO {
    private String name;

    private String description;
    private List<TagDTO> tags;
}
