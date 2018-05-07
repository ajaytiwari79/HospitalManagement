package com.kairos.persistence.model.user.filter;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by prerna on 1/5/18.
 */
@NodeEntity
public class FilterDetail extends UserBaseEntity {

    private String name;
    private List<String> value;

    public FilterDetail(){
        // default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}