package com.kairos.persistence.model.system_setting;

import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.common.UserTranslationInfoConverter;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Map;

@QueryResult
@Getter
@Setter
public class SystemLanguageQueryResult {
    private Long id;
    private String name;
    private String code;
    private boolean active;
    private boolean defaultLanguage;
    @Convert(UserTranslationInfoConverter.class)
    private Map<String, TranslationInfo> translations;

    public SystemLanguageQueryResult() {
        //Default Constructor
    }
}
