package com.kairos.persistence.model.system_setting;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
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
    private Map<String, TranslationInfo> translations;

    public SystemLanguageQueryResult() {
        //Default Constructor
    }
}
