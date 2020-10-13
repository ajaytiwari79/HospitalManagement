package com.kairos.persistence.model.day_type;

import com.kairos.enums.Day;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
public class DayType extends MongoBaseEntity {
    @NotBlank(message = "error.DayType.name.notEmpty")
    private String name;
    @NotNull
    int code;
    private String description;
    private String colorCode;
    private Long countryId;
    private List<Day> validDays=new ArrayList<>();
    private boolean holidayType;
    private boolean isEnabled = true;
    private boolean allowTimeSettings;

    // Constructor
    public DayType() {
    }

    public DayType(@NotBlank(message = "error.DayType.name.notEmpty") String name, @NotNull int code, String description, String colorCode, Long countryId, List<Day> validDays, boolean holidayType, boolean isEnabled, boolean allowTimeSettings) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.colorCode = colorCode;
        this.countryId = countryId;
        this.validDays = validDays;
        this.holidayType = holidayType;
        this.isEnabled = isEnabled;
        this.allowTimeSettings = allowTimeSettings;
    }
}
