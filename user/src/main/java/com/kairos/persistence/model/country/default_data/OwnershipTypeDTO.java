package com.kairos.persistence.model.country.default_data;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@QueryResult
@Getter
@Setter
public class OwnershipTypeDTO {
    private Long id;
    @NotBlank(message = "error.IndustryType.name.notEmpty")
    private String name;
    private String description;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;

    public OwnershipTypeDTO() {
        //Default Constructor
    }
}
