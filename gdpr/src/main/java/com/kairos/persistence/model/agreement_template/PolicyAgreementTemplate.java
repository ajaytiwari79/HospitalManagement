package com.kairos.persistence.model.agreement_template;


import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PolicyAgreementTemplate extends BaseEntity {

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private Long countryId;

    @ElementCollection
    private List<AccountType> accountTypes;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderColumn
    private List<AgreementSection> agreementSections=new ArrayList<>();

    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @ElementCollection
    private List <ServiceCategory> organizationServices = new ArrayList<>();

    @ElementCollection
    private List <SubServiceCategory> organizationSubServices = new ArrayList<>();

    @OneToOne
    private TemplateType templateType;
    private boolean coverPageAdded;
    private boolean includeContentPage;
    private boolean signatureComponentAdded;
    private boolean signatureComponentLeftAlign;
    private boolean signatureComponentRightAlign;

    @Column(columnDefinition = "text")
    private String  signatureHtml;
    @Embedded
    private CoverPage coverPageData = new CoverPage();
    private Long organizationId;

    public PolicyAgreementTemplate(String name, String description, Long countryId, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
    }

    public PolicyAgreementTemplate(@NotBlank(message = "Name cannot be empty") String name, @NotBlank(message = "Description cannot be empty") String description, TemplateType templateType) {
        this.name = name;
        this.description = description;
        this.templateType = templateType;
    }

    public List<AgreementSection> getAgreementSections() {
        return agreementSections.stream().filter(agreementSection -> !agreementSection.isDeleted()).collect(Collectors.toList());
    }

    @Override
    public void delete() {
        super.delete();
        this.getAgreementSections().forEach(AgreementSection::delete);
    }
}
