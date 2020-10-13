package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.TranslationInfo;
import com.kairos.enums.Day;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DayTypeDTO {

    private BigInteger id;
    @NotBlank(message = "error.DayType.name.notEmpty")
    private String name;
    private List<Day> validDays = new ArrayList<>();
    private List<CountryHolidayCalenderDTO> countryHolidayCalenderData=new ArrayList<>();
    private boolean holidayType;
    private boolean allowTimeSettings = false;
    private String description;
    private String country;
    private Long countryId;
    private int code;
    private String colorCode;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations;

    public DayTypeDTO(BigInteger id, String name, List<Day> validDays, List<CountryHolidayCalenderDTO> countryHolidayCalenderData, boolean holidayType, boolean allowTimeSettings) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenderData = countryHolidayCalenderData;
        this.holidayType = holidayType;
        this.allowTimeSettings = allowTimeSettings;
    }

    public DayTypeDTO(BigInteger id, String name, List<Day> validDays, List<CountryHolidayCalenderDTO> countryHolidayCalenderData, boolean holidayType, boolean allowTimeSettings,String colorCode) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenderData = countryHolidayCalenderData;
        this.holidayType = holidayType;
        this.allowTimeSettings = allowTimeSettings;
        this.colorCode=colorCode;
    }

}
