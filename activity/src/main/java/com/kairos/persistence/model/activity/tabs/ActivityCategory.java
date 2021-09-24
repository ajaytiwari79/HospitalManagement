package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * Created by pawanmandhan on 22/8/17.
 */

@Document(collection = "activity_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ActivityCategory extends MongoBaseEntity{

    private String name;
    private String description;
    private Long countryId;
    private BigInteger timeTypeId;



    public ActivityCategory(String name, String description, Long countryId, BigInteger timeTypeId) {
        this.name = name;
        this.description = description;
        this.countryId=countryId;
        this.timeTypeId = timeTypeId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ActivityCategory)) return false;

        ActivityCategory that = (ActivityCategory) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(this.getId(), that.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(this.getId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id",this.getId())
                .append("name", name)
                .append("description", description)
                .toString();
    }

}
