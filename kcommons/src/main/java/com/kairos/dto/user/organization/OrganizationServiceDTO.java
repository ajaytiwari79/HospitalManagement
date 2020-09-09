package com.kairos.dto.user.organization;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
