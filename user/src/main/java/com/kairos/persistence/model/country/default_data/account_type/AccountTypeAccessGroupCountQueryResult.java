package com.kairos.persistence.model.country.default_data.account_type;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Map;

/**
 * CreatedBy vipulpandey on 14/9/18
 **/
@QueryResult
@Getter
@Setter
public class AccountTypeAccessGroupCountQueryResult {
    private Long id;
    private String name;
    private short count;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }
}
