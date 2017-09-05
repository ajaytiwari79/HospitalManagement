package com.kairos.persistence.model.user.agreement.wta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.region.Region;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class WorkingTimeAgreement extends UserBaseEntity {

    @NotEmpty(message = "error.wta.name.notempty")
    @NotNull(message = "error.wta.name.notnull")
    private String name;

    private String description;

    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;//

    @Relationship(type = BELONGS_TO)
    private List<OrganizationType> organizationTypes;//

    @JsonIgnore
    @Relationship(type = BELONGS_TO)
    private Country country;

    @Relationship(type = HAS_RULE_TEMPLATE)
    private List<WTABaseRuleTemplate> ruleTemplates;//

    @Relationship(type = HAS_REGIONS)
    private Region region;//

    // to make a history
    @Relationship(type = HAS_WTA)
    private WorkingTimeAgreement wta;

    private Long startDate;
    private Long endDate;
    private Long expiryDate;
    private boolean isEnabled = true;


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public WorkingTimeAgreement getWta() {
        return wta;
    }

    public void setWta(WorkingTimeAgreement wta) {
        this.wta = wta;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }


    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("code", this.description);

        map.put("lastModificationDate", this.getLastModificationDate());
        map.put("creationDate", this.getCreationDate());
        return map;
    }

    public List<WTABaseRuleTemplate> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }
}
