package com.kairos.persistence.model.access_permission;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.persistence.model.country.default_data.DayType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by prerna on 5/3/18.
 */
@QueryResult
@Getter
@Setter
public class AccessGroupQueryResult {

    private long id;
    private String name;
    private boolean deleted;
    private boolean typeOfTaskGiver;
    private String description;
    private AccessGroupRole role;
    private boolean enabled = true;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<BigInteger> dayTypeIds;
    private List<DayType> dayTypes;
    private boolean allowedDayTypes;
    private AccessGroup parentAccessGroup;
    private Map<String, TranslationInfo> translations;
}
