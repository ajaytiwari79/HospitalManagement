package com.kairos.persistence.model.user.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Map;

@QueryResult
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvinceQueryResult {
    private String name;
    private Long id;
    private String geoFence;
    private String code;
    private Double latitude;
    private Double Longitude;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }
}
