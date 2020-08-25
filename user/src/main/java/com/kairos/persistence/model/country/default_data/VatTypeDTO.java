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
public class VatTypeDTO {

    private Long id;
    @NotBlank(message = "error.VatType.name.notEmpty")
    private String name;
    private int code;
    private String description;
    @NotBlank(message = "error.VatType.percentage.notEmpty")
    private String percentage;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;


    public VatTypeDTO() {
    }

    public VatTypeDTO(@NotBlank(message = "error.VatType.name.notEmpty") String name, int code, String description, @NotBlank(message = "error.VatType.percentage.notEmpty") String percentage) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.percentage = percentage;
    }
}
