package com.kairos.dto.activity.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.tag.TagDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prerna on 6/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TaskTypeResponseDTO {

    private BigInteger id;
    private String title;
    private String description;
    private Long subServiceId;
    private String expiresOn;
    private Boolean status;
    private String parentTaskTypeId;
    private List<TagDTO> tags;
}
