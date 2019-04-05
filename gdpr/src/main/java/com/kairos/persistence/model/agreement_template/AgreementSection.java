package com.kairos.persistence.model.agreement_template;


import com.kairos.persistence.model.clause.AgreementSectionClause;
import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AgreementSection extends BaseEntity {


    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Column(columnDefinition = "text")
    protected String title;

    @Column(columnDefinition = "text")
    protected String titleHtml;

    @OrderColumn
    @ElementCollection
    protected List<AgreementSectionClause> clauses=new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "agreementSection")
    private List<AgreementSubSection> agreementSubSections =new ArrayList<>();


    protected Integer orderedIndex;
    protected Long countryId;
    protected Long organizationId;

    public List<AgreementSubSection> getAgreementSubSections() {
        return agreementSubSections.stream().filter(subSection -> !subSection.isDeleted()).collect(Collectors.toList());
    }

    public List<AgreementSectionClause> getClauses() {
        return clauses.stream().filter(clause -> !clause.isDeleted()).collect(Collectors.toList());
    }

    public AgreementSection(@NotBlank(message = "error.message.section.title.notNull") String title, @NotNull(message = "error.message.clause.order.notdefined") Integer orderedIndex, String titleHtml)
    {
        this.title=title;
        this.orderedIndex=orderedIndex;
        this.titleHtml=titleHtml;
    }

    @Override
    public void delete() {
        super.delete();
        this.getClauses().forEach( clause -> clause.setDeleted(false));
        this.agreementSubSections.forEach(subSection -> {
            subSection.delete();
            subSection.getClauses().forEach( subSectionClause -> subSectionClause.setDeleted(false));
        });
    }


    public void linkSubSectionsWithParentSectionAndCountryOrUnitId(boolean isOrganization, Long referenceId) {
        if (isOrganization)
            this.setOrganizationId(referenceId);
        else
            this.setCountryId(referenceId);
        this.agreementSubSections.forEach(subSection -> {
            if (isOrganization)
                subSection.setOrganizationId(referenceId);
            else
                subSection.setCountryId(referenceId);
            subSection.setAgreementSection(this);
        });
    }
}
