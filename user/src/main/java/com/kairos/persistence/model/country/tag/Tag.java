package com.kairos.persistence.model.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PENALTY_SCORE;

/**
 * Created by prerna on 10/11/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Tag extends UserBaseEntity {

    @NotBlank(message = "error.Tag.name.notEmptyOrNotNull")
    private String name;

    @Property(name = "masterDataType")
    @EnumString(MasterDataTypeEnum.class)
    private MasterDataTypeEnum masterDataType;

    @Relationship(type=HAS_PENALTY_SCORE)
    private PenaltyScore penaltyScore;

    private boolean countryTag;

    private Long orgTypeId;

    private List<Long> orgSubTypeIds;

    public Tag(@NotBlank(message = "error.Tag.name.notEmptyOrNotNull") String name, MasterDataTypeEnum masterDataType, boolean countryTag) {
        this.name = name;
        this.masterDataType = masterDataType;
        this.countryTag = countryTag;
    }

    public Tag(String name, MasterDataTypeEnum masterDataType, boolean countryTag, Long orgTypeId, List<Long> orgSubTypeIds) {
        this.name = name;
        this.masterDataType = masterDataType;
        this.countryTag = countryTag;
        this.orgTypeId = orgTypeId;
        this.orgSubTypeIds = orgSubTypeIds;
    }

    public Tag(String name, MasterDataTypeEnum masterDataType, boolean countryTag, PenaltyScore penaltyScore) {
        this.name = name;
        this.masterDataType = masterDataType;
        this.countryTag = countryTag;
        this.penaltyScore = penaltyScore;
    }

    public Tag(TagDTO tagDTO, boolean countryTag){
        this.name = tagDTO.getName();
        this.masterDataType = tagDTO.getMasterDataType();
        this.countryTag = countryTag;
    }
}
