package com.kairos.persistence.model.user.filter;

import com.kairos.enums.FilterType;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by prerna on 1/5/18.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterSelection extends UserBaseEntity {
    private FilterType name;
    private List<String> value;

    public FilterSelection(FilterType name, List<String> value) {
        this.name = name;
        this.value = value;
    }

    private int sequence;

    public FilterSelection(FilterType name, List<String> value) {
        this.name = name;
        this.value = value;
    }
}