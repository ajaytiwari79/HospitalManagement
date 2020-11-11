package com.kairos.persistence.model.country.default_data;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;

import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.ERROR_CITIZENSTATUS_NAME_NOTEMPTY;

@QueryResult
@Getter
@Setter
public class CitizenStatusDTO {

    private Long id;
    @NotBlank(message = ERROR_CITIZENSTATUS_NAME_NOTEMPTY)
    String name;
    String description;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;

    public CitizenStatusDTO() {
        //Default Constructor
    }
    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(translations,description);
    }
}
