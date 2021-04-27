package com.kairos.persistence.model.user.filter;

import com.kairos.enums.DurationType;
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
public class FilterSelection<T> extends UserBaseEntity {
    private static final long serialVersionUID = 4220970460034521898L;
    private FilterType name;
    private List<T> value;
    private int sequence;

    private FilterType.FilterComparisonType filterComparisonType = FilterType.FilterComparisonType.CONTAINS;
    private DurationType durationType = DurationType.MONTHS;

    public FilterSelection(FilterType name, List<T> value) {
        this.name = name;
        this.value = value;
    }

}