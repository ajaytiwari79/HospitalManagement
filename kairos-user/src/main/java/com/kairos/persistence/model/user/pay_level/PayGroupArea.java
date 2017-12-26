package com.kairos.persistence.model.user.pay_level;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by prabjot on 20/12/17.
 */
@NodeEntity
public class PayGroupArea extends UserBaseEntity {
    private String name;
    private float value;

    public PayGroupArea() {
        //default constructor
    }

    public PayGroupArea(String name, String description) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
