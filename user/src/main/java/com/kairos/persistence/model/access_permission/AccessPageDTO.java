package com.kairos.persistence.model.access_permission;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.OrganizationCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
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
    private Boolean editable;
    private boolean hasSubTabs;
    private int sequence;
    private Map<String,String> translatedNames;
    private String helperText;
    private String url;
    private List<AccessPageDTO> children = new ArrayList<>();

    public String getName(){
        boolean isNullOrEmptyString = isNotNull(translatedNames) && isNotNull(translatedNames.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase())) && !StringUtils.isEmpty(translatedNames.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).trim());
        if(isNullOrEmptyString) {
            return translatedNames.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase());
        }else {
            return name;
        }
    }
}
