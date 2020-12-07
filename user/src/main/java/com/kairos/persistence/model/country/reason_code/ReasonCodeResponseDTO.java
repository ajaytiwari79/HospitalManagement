package com.kairos.persistence.model.country.reason_code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.common.TranslationConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by pavan on 23/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class ReasonCodeResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;
    private BigInteger timeTypeId;
    private Long unitId;
    private Long countryId;
    @Convert(TranslationConverter.class)
    private Map<String, TranslationInfo> translations;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;

    public ReasonCodeResponseDTO(Long id, String name, String code, String description, ReasonCodeType reasonCodeType) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
    }
    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(translations,description);
    }
}
