package com.kairos.persistence.model.access_permission;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.persistence.model.common.TranslationConverter;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;
import java.time.LocalDate;
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
    private boolean allowedDayTypes;
    private AccessGroup parentAccessGroup;
    @Convert(TranslationConverter.class)
    private Map<String, TranslationInfo> translations;
    private int unitCount;
    private int staffCount;

    public String getName() {
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }


}
