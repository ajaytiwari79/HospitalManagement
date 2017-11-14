package com.kairos.persistence.model.user.country.tag;

/**
 * Created by prerna on 10/11/17.
 */

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.country.tag.Tag;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_HAS_TAG;

@RelationshipEntity(type = ORGANIZATION_HAS_TAG)
public class OrganizationTagRelationship extends UserBaseEntity {

    @StartNode
    private Organization organization;
    @EndNode
    private Tag tag;

    @Property(name = "masterDataType")
    @EnumString(MasterDataTypeEnum.class)
    private MasterDataTypeEnum masterDataType;


    public OrganizationTagRelationship(){}
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public MasterDataTypeEnum getMasterDataType() {
        return masterDataType;
    }

    public void setMasterDataType(MasterDataTypeEnum masterDataType) {
        this.masterDataType = masterDataType;
    }
}
