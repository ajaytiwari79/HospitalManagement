package com.kairos.persistence.model.organization;
/*
 *Created By Pavan on 27/5/19
 *
 */

import com.kairos.dto.user.organization.CompanyType;
import com.kairos.dto.user.organization.CompanyUnitType;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.UnionState;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.staff.position.Position;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Organization extends OrganizationBaseEntity {

    private boolean isKairosHub;
    //fixme need to remove
    private boolean isParentOrganization;

    // Relationships

    @Relationship(type = BELONGS_TO)
    private Country country;

    @Relationship(type = HAS_SUB_ORGANIZATION)
    private List<Organization> children = new ArrayList<>();

    @Relationship(type = HAS_UNIT)
    private List<Unit> units = new ArrayList<>();


    @Relationship(type = ORGANIZATION_HAS_ACCESS_GROUPS)
    private List<AccessGroup> accessGroups = new ArrayList<>();

    @Relationship(type = HAS_POSITIONS)
    private List<Position> positions = new ArrayList<>();

    private boolean union;

    private UnionState state;

    @Relationship(type= HAS_LOCATION)
    private List<Location> locations = new ArrayList<>();

    @Relationship(type=HAS_SECTOR)
    private List<Sector> sectors = new ArrayList<>();

    protected CompanyType companyType;


    //set o.nightStartTimeFrom="22:15",o.nightEndTimeTo="07:15"


    //constructor for creating Union
    public Organization(String name, boolean union, Country country) {
        this.name = name;
        this.union = union;
        this.country= country;
    }
    public Organization(String name, List<Sector> sectors, ContactAddress contactAddress, boolean boardingCompleted, Country country, boolean union) {
        this.name = name;
        this.sectors = sectors;
        this.contactAddress = contactAddress;
        this.union = union;
        this.boardingCompleted=boardingCompleted;
        this.country = country;
    }

    public Organization(Long id, String name, String description, boolean isPrekairos, String desiredUrl, String shortCompanyName, String kairosCompanyId, CompanyType companyType,
                String vatId, List<BusinessType> businessTypes, OrganizationType organizationType, List<OrganizationType> organizationSubTypes, CompanyUnitType companyUnitType,
                CompanyCategory companyCategory, ZoneId timeZone, boolean isParentOrganization, Country country, AccountType accountType, boolean boardingCompleted,
                List<Organization> children) {
        this.name = name;
        this.description = description;
        this.isKairosHub = isPrekairos;
        this.desiredUrl = desiredUrl;
        this.shortCompanyName = shortCompanyName;
        this.kairosCompanyId = kairosCompanyId;
        this.vatId = vatId;
        this.businessTypes = businessTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationType = organizationType;
        this.companyType = companyType;
        this.companyCategory = companyCategory;
        this.companyUnitType = companyUnitType;
        this.timeZone = timeZone;
        this.isParentOrganization = isParentOrganization;
        this.country = country;
        this.id = id;
        this.accountType = accountType;
        this.boardingCompleted = boardingCompleted;
        this.children = children;

    }

    public List<Organization> getChildren() {
        return java.util.Optional.ofNullable(children).orElse(new ArrayList<>());

    }

    public List<Position> getPositions() {
        return java.util.Optional.ofNullable(positions).orElse(new ArrayList<>());
    }


    public OrganizationCategory getOrganizationCategory(){
        return this.isKairosHub ? OrganizationCategory.HUB : this.union ? OrganizationCategory.UNION : OrganizationCategory.ORGANIZATION;
    }


}
