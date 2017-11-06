package com.kairos.persistence.model.user.agreement.wta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.response.dto.web.WtaDTO;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.beans.BeanUtils;

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

    @Relationship(type = BELONGS_TO_ORG_TYPE)
    private OrganizationType organizationType;//

    @Relationship(type = BELONGS_TO_ORG_SUB_TYPE)
    private OrganizationType organizationSubType;//

    @JsonIgnore
    @Relationship(type = BELONGS_TO)
    private Country country;

    @Relationship(type = HAS_RULE_TEMPLATE)
    private List<WTABaseRuleTemplate> ruleTemplates;//



    // to make a history
    @Relationship(type = HAS_WTA)
    private WorkingTimeAgreement wta;

    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    private boolean isEnabled = true;


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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

    public OrganizationType getOrganizationTypes() {
        return organizationType;
    }

    public void setOrganizationTypes(OrganizationType organizationTypes) {
        this.organizationType = organizationTypes;
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

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationType getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationType organizationSubType) {
        this.organizationSubType = organizationSubType;
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

    public static WorkingTimeAgreement copyProperties(WorkingTimeAgreement source, WorkingTimeAgreement target){
        BeanUtils.copyProperties(source,target);
        return target;
    }

    public WorkingTimeAgreement() {
    }

    public WorkingTimeAgreement(String name, String description, Expertise expertise, OrganizationType organizationType, OrganizationType organizationSubType, Country country, List<WTABaseRuleTemplate> ruleTemplates, WorkingTimeAgreement wta, Long startDateMillis, Long endDateMillis, Long expiryDate, boolean isEnabled) {
        this.name = name;
        this.description = description;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.country = country;
        this.ruleTemplates = ruleTemplates;
        this.wta = wta;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
        this.isEnabled = isEnabled;
    }

    public WorkingTimeAgreement(String name, String description, Expertise expertise, OrganizationType organizationType, OrganizationType organizationSubType, List<WTABaseRuleTemplate> ruleTemplates, Long startDateMillis, Long endDateMillis, Long expiryDate) {
        this.name = name;
        this.description = description;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.ruleTemplates = ruleTemplates;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.expiryDate = expiryDate;
    }
    public WtaDTO buildwtaDTO() {
        WtaDTO wtaDTO=new WtaDTO(this.name,this.description ,this.expertise.getId() ,this.organizationType.getId(), this.organizationSubType.getId()  ,this.startDateMillis,this.endDateMillis,this.expiryDate );
        return wtaDTO;
    }
}
