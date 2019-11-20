package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.organization.CompanyUnitType;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.resources.Resource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Organization Domain & it's properties
 */
//@JsonSerialize(using = OrganizationSerializer.class)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Unit extends OrganizationBaseEntity {

    private boolean showPersonNames;

    // Relationships
    @Relationship(type = HAS_TEAMS)
    private List<Team> teams = new ArrayList<>();

    @Relationship(type = HAS_SETTING)
    private OrganizationSetting organizationSetting;


    @Relationship(type = UNIT_HAS_ACCESS_GROUPS)
    private List<AccessGroup> accessGroups = new ArrayList<>();

    @Relationship(type = UNIT_HAS_RESOURCE)
    private List<Resource> resourceList;

    @Relationship(type = HAS_PAYMENT_SETTINGS)
    private PaymentSettings paymentSettings;

    @Relationship(type = HAS_GROUPS)
    private List<Group> groups = new ArrayList<>();

    private boolean workcentre;
    private boolean gdprUnit;

    public List<Resource> getResourceList() {
        return java.util.Optional.ofNullable(resourceList).orElse(new ArrayList<>());
    }

    public void addResource(Resource resource) {
        List<Resource> resourceList = this.getResourceList();
        resourceList.add(resource);
        this.resourceList = resourceList;
    }

    public Unit(Long id, String name, String description,  String desiredUrl, String shortCompanyName, String kairosCompanyId,
                        String vatId, List<BusinessType> businessTypes, OrganizationType organizationType, List<OrganizationType> organizationSubTypes, CompanyUnitType companyUnitType,
                        CompanyCategory companyCategory, ZoneId timeZone,  AccountType accountType, boolean boardingCompleted,boolean workcentre) {
        this.name = name;
        this.description = description;
        this.desiredUrl = desiredUrl;
        this.shortCompanyName = shortCompanyName;
        this.kairosCompanyId = kairosCompanyId;
        this.vatId = vatId;
        this.businessTypes = businessTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationType = organizationType;
        this.companyCategory = companyCategory;
        this.companyUnitType = companyUnitType;
        this.timeZone = timeZone;
        this.id = id;
        this.accountType = accountType;
        this.boardingCompleted = boardingCompleted;
        this.workcentre=workcentre;

    }



}