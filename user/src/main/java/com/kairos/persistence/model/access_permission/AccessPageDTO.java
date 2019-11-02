package com.kairos.persistence.model.access_permission;

import com.kairos.enums.OrganizationCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

/**
 * Created by prabjot on 10/10/17.
 */
@QueryResult
@Getter
@Setter
public class AccessPageDTO {

    private Long id;
    @NotNull(message = ERROR_NAME_NOTNULL)
    private String name;
    private boolean module;
    private Long parentTabId;
    private String moduleId;
    private Boolean active;
    private boolean accessibleForHub;
    private boolean accessibleForUnion;
    private boolean accessibleForOrganization;
    private List<OrganizationCategory> accessibleFor = new ArrayList<>();
    //this value is true only in case of "moduleId" : "module_1"
    private Boolean editable;
    private boolean hasSubTabs;
    private int sequence;
}
