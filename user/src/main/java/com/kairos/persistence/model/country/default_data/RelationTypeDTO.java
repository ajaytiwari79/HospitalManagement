package com.kairos.persistence.model.country.default_data;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;

import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.ERROR_RELATIONTYPE_NAME_NOTEMPTY;

@QueryResult
@Getter
@Setter
public class RelationTypeDTO {

    private Long id;
    @NotBlank(message = ERROR_RELATIONTYPE_NAME_NOTEMPTY)
    private String name;
    private String description;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;


    public RelationTypeDTO() {
        //Default Constructor
    }

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(translations,description);
    }
}
