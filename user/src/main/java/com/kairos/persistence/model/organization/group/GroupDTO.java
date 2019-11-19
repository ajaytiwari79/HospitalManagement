package com.kairos.persistence.model.organization.group;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import java.util.List;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@Getter
@Setter
public class GroupDTO {
    private Long id;
    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String name;
    private List<Long> staffIds;
    private Long unitId;
}
