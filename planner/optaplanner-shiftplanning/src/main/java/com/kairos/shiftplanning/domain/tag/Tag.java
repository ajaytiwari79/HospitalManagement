package com.kairos.shiftplanning.domain.tag;

import com.kairos.enums.MasterDataTypeEnum;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.util.Objects;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Tag")
public class Tag {
    Long id;
    private String name;
    @Indexed
    private MasterDataTypeEnum masterDataType;
    private LocalDate startDate;
    private LocalDate endDate;

    public Tag(Long id,String name, MasterDataTypeEnum masterDataType){
        this.id = id;
        this.name = name;
        this.masterDataType = masterDataType;
    }

    public boolean isValidTag(LocalDate localDate){
        return (isNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate)) || (isNotNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate) && !this.getEndDate().isBefore(localDate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
