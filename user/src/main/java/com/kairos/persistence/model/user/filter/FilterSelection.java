package com.kairos.persistence.model.user.filter;

import com.kairos.enums.FilterType;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by prerna on 1/5/18.
 */
@NodeEntity
public class FilterSelection extends UserBaseEntity {

    private FilterType name;
    private List<String> value;

    public FilterSelection(){
        // default constructor
    }

    public FilterType getName() {
        return name;
    }

    public void setName(FilterType name) {
        this.name = name;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}