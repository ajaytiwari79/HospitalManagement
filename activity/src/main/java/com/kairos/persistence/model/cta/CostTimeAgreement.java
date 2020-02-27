package com.kairos.persistence.model.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.wta.Expertise;
import com.kairos.persistence.model.wta.OrganizationType;
import com.kairos.persistence.model.wta.WTAOrganization;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class CostTimeAgreement extends MongoBaseEntity {
    private String name;
    private String description;
    private Expertise expertise;
    private OrganizationType organizationType;
    private OrganizationType organizationSubType;
    private Long countryId;
    private BigInteger organizationParentId;
    private WTAOrganization organization;
    private BigInteger parentId;
    private BigInteger parentCountryCTAId;
    private List<BigInteger> ruleTemplateIds =new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private List<BigInteger> tags;
    private boolean disabled;
    private Long employmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostTimeAgreement that = (CostTimeAgreement) o;

        return new EqualsBuilder()
                .append(disabled, that.disabled)
                .append(name, that.name)
                .append(description, that.description)
                .append(expertise, that.expertise)
                .append(organizationType, that.organizationType)
                .append(organizationSubType, that.organizationSubType)
                .append(countryId, that.countryId)
                .append(parentId, that.parentId)
                .append(ruleTemplateIds, that.ruleTemplateIds)
                .append(startDate, that.startDate)
                .append(endDate, that.endDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(description)
                .append(expertise)
                .append(organizationType)
                .append(organizationSubType)
                .append(countryId)
                .append(parentId)
                .append(ruleTemplateIds)
                .append(startDate)
                .append(endDate)
                .append(disabled)
                .toHashCode();
    }
}

