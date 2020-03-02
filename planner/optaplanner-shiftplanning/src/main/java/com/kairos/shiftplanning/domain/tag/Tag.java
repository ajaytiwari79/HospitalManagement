package com.kairos.shiftplanning.domain.tag;

import com.kairos.enums.MasterDataTypeEnum;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Tag")
public class Tag {
    BigInteger tagId;
    private String name;
    @Indexed
    private MasterDataTypeEnum masterDataType;

    private boolean countryTag;

    private long countryId;

    private long organizationId;

    public Tag(BigInteger tagId,String name, MasterDataTypeEnum masterDataType, boolean countryTag, long countryOrOrdId){
        this.tagId = tagId;
        this.name = name;
        this.masterDataType = masterDataType;
        this.countryTag = countryTag;
        if(countryTag) {
            this.countryId = countryOrOrdId;
        }else {
            this.organizationId = countryOrOrdId;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return countryTag == tag.countryTag &&
                countryId == tag.countryId &&
                organizationId == tag.organizationId &&
                Objects.equals(tagId, tag.tagId) &&
                Objects.equals(name, tag.name) &&
                masterDataType == tag.masterDataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, name, masterDataType, countryTag, countryId, organizationId);
    }
}
