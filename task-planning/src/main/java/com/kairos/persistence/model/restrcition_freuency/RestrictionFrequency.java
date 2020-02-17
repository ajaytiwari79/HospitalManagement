package com.kairos.persistence.model.restrcition_freuency;

import com.kairos.persistence.model.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * To store restrcition_freuency frequency values {today,Next day}
 * based on thsese values, restrictions will be removed from tasks
 * Created by prabjot on 15/9/17.
 */
@Document
public class RestrictionFrequency extends MongoBaseEntity {

    @NotNull(message = "name cannot be null")
    @Indexed(unique = true)
    private String name;

    @Indexed(unique = true)
    private Integer value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
