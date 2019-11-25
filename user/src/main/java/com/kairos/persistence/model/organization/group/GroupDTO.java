package com.kairos.persistence.model.organization.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private Long id;
    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String name;
    private String description;
    private List<FilterSelectionDTO> filtersData;
    private List<Long> excludedStaffs;

    public GroupDTO(Long id, String name, String description, List<Long> excludedStaffs){
        this.id = id;
        this.name = name;
        this.description = description;
        this.excludedStaffs = excludedStaffs;
    }
}
