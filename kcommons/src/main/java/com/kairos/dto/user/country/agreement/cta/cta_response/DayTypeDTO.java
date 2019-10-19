package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.enums.Day;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DayTypeDTO {

    private Long id;
    @NotBlank(message = "error.DayType.name.notEmpty")
    private String name;
    private List<Day> validDays = new ArrayList<>();
    private List<CountryHolidayCalenderDTO> countryHolidayCalenderData=new ArrayList<>();
    private boolean holidayType;
    private boolean allowTimeSettings = false;
    private String description;
    private String country;
    private int code;
    private String colorCode;

    public DayTypeDTO(Long id, String name, List<Day> validDays, List<CountryHolidayCalenderDTO> countryHolidayCalenderData, boolean holidayType, boolean allowTimeSettings) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenderData = countryHolidayCalenderData;
        this.holidayType = holidayType;
        this.allowTimeSettings = allowTimeSettings;
    }

    public DayTypeDTO(Long id, String name, List<Day> validDays, List<CountryHolidayCalenderDTO> countryHolidayCalenderData, boolean holidayType, boolean allowTimeSettings,String colorCode) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenderData = countryHolidayCalenderData;
        this.holidayType = holidayType;
        this.allowTimeSettings = allowTimeSettings;
        this.colorCode=colorCode;
    }

}
