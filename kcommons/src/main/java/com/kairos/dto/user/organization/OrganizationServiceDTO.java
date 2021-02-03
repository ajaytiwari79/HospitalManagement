package com.kairos.dto.user.organization;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by prerna on 15/11/17.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Long unitId;
    private List<OrganizationServiceDTO> children;

    public List<OrganizationServiceDTO> getOrganizationSubService() {
        if(this.organizationSubService==null){
            this.organizationSubService=new ArrayList<>();
        }
        return organizationSubService;
    }

    public OrganizationServiceDTO(@NotEmpty(message = "error.Organization.Service.customName.notEmptyOrNotNull") @NotNull(message = "error.Organization.Service.customName.notEmptyOrNotNull") String customName, Long id, String name, String description, Map<String, TranslationInfo> translations, Long unitId, List<OrganizationServiceDTO> children) {
        this.customName = customName;
        this.id = id;
        this.name = name;
        this.description = description;
        this.translations = translations;
        this.unitId = unitId;
        this.children = children;
    }

    public String getName() {
       return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }

    public String getDescription() {
       return TranslationUtil.getDescription(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),description);
    }
}
