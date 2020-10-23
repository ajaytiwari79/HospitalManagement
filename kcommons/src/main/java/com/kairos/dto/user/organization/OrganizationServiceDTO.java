package com.kairos.dto.user.organization;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user_context.UserContext;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

/**
 * Created by prerna on 15/11/17.
 */
@Getter
@Setter
public class OrganizationServiceDTO {
    @NotEmpty(message = "error.Organization.Service.customName.notEmptyOrNotNull") @NotNull(message = "error.Organization.Service.customName.notEmptyOrNotNull")
    private String customName;
    private Long id;
    private String name;
    private String description;
    private List<OrganizationServiceDTO> organizationSubService;
    private Map<String,String>  translatedNames ;
    private Map<String,String>  translatedDescriptions ;
    private Map<String, TranslationInfo> translations ;

    public Map<String, TranslationInfo> getTranslatedData() {
        Map<String, TranslationInfo> infoMap=new HashMap<>();
        translatedNames.forEach((k,v)-> infoMap.put(k,new TranslationInfo(v,translatedDescriptions.get(k))));
        return infoMap;
    }

    public List<OrganizationServiceDTO> getOrganizationSubService() {
        if(this.organizationSubService==null){
            this.organizationSubService=new ArrayList<>();
        }
        return organizationSubService;
    }

    public String getName() {
        if(isNotNull(translatedNames.get(UserContext.getUserDetails().getLanguage().toLowerCase())) && !translatedNames.get(UserContext.getUserDetails().getLanguage().toLowerCase()).equals("")) {
            return translatedNames.getOrDefault(UserContext.getUserDetails().getLanguage().toLowerCase(), name);
        }else {
            return name;
        }
    }

    public String getDescription() {
        if(isNotNull(translatedDescriptions.get(UserContext.getUserDetails().getLanguage().toLowerCase())) && !translatedDescriptions.get(UserContext.getUserDetails().getLanguage().toLowerCase()).equals("")) {
            return translatedDescriptions.getOrDefault(UserContext.getUserDetails().getLanguage().toLowerCase(), description);
        }else{
            return description;
        }
    }
}
