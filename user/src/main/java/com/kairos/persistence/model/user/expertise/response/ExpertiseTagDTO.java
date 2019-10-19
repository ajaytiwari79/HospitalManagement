package com.kairos.persistence.model.user.expertise.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.tag.TagDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 30/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
@Getter
@Setter
public class ExpertiseTagDTO {
    private Long id;
    private String name;
    private String description;
    private List<TagDTO> tags;
}
