package com.kairos.persistence.model.user.country.tag;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.tag.Tag;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import static com.kairos.persistence.model.constants.RelationshipConstants.COUNTRY_HAS_TAG;

/**
 * Created by prerna on 10/11/17.
 */
@RelationshipEntity(type = COUNTRY_HAS_TAG)
public class CountryTagRelationship extends UserBaseEntity {

    @StartNode
    private Country country;
    @EndNode
    private Tag tag;

    @Property(name = "masterDataType")
    @EnumString(MasterDataTypeEnum.class)
    private MasterDataTypeEnum masterDataType;

    public CountryTagRelationship(){}
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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
