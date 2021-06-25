package com.kairos.persistence.model.activity;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/*
 * @author: Mohit Shakya
 * @usage: This domain is for category Planned time type.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class PlannedTimeType extends MongoBaseEntity implements Serializable {
    private static final long serialVersionUID = 1124175811502442045L;
    private String name;
    private Long countryId;
    private String imageName;

    public PlannedTimeType(String name, Long countryId){
        this.name = name;
        this.countryId = countryId;
    }


    @Override
    public String toString() {
        return "PlannedTimeType{" +
                "name='" + name + '\'' +
                ", deleted=" + deleted +
                '}';
    }

}
