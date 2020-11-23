package com.kairos.persistence.model.country.default_data;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.common.UserTranslationInfoConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class ContractTypeDTO {

    private Long id;
    @NotBlank(message = "error.ContractType.name.notEmpty")
    private String name;
    @NotNull(message = "error.ContractType.code.notEmpty")
    @Range(min = 1, message = "error.ContractType.code.greaterThenOne")
    private int code;
    private String description;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    @Convert(UserTranslationInfoConverter.class)
    private Map<String, TranslationInfo> translations ;

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(translations,description);
    }
}
