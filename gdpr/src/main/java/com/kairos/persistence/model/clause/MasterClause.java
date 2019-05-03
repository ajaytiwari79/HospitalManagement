package com.kairos.persistence.model.clause;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MasterClause extends Clause {

    private Long countryId;

    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @ElementCollection
    private List <ServiceCategory> organizationServices = new ArrayList<>();

    @ElementCollection
    private List <SubServiceCategory> organizationSubServices = new ArrayList<>();

    @ElementCollection
    private List<AccountType> accountTypes = new ArrayList<>();


    public MasterClause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags, List<TemplateType> templateTypes, Long countryId, UUID tempClauseId,List<AccountType> accountTypes
                          ,List<OrganizationType> organizationTypes,List <OrganizationSubType> organizationSubTypes ,List <ServiceCategory> organizationServices,List<SubServiceCategory> organizationSubServices ) {
        super(title, description, tags, templateTypes,tempClauseId);
        this.countryId = countryId;
        this.accountTypes=accountTypes;
        this.organizationTypes=organizationTypes;
        this.organizationServices=organizationServices;
        this.organizationSubTypes=organizationSubTypes;
        this.organizationSubServices=organizationSubServices;
    }
}
