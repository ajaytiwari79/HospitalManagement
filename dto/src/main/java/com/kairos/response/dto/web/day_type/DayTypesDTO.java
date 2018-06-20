package com.kairos.response.dto.web.day_type;

import com.kairos.persistence.model.user.country.Day;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DayTypesDTO {
    @NotEmpty(message = "error.DayType.name.notEmpty") @NotNull(message = "error.DayType.name.notnull")
    private String name;
    @NotNull
    int code;

    // @NotEmpty(message = "error.DayType.description.notEmpty") @NotNull(message = "error.DayType.description.notnull")
    private String description;

    @NotEmpty(message = "error.DayType.colorCode.notEmpty") @NotNull(message = "error.DayType.colorCode.notnull")
    private String colorCode;

    private List<Day> validDays=new ArrayList<>();
    private boolean holidayType;
    private boolean isEnabled = true;
    private boolean allowTimeSettings = false;

    public DayTypesDTO() {
        //Default Constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public List<Day> getValidDays() {
        return validDays;
    }

    public void setValidDays(List<Day> validDays) {
        this.validDays = validDays;
    }

    public boolean isHolidayType() {
        return holidayType;
    }

    public void setHolidayType(boolean holidayType) {
        this.holidayType = holidayType;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isAllowTimeSettings() {
        return allowTimeSettings;
    }

    public void setAllowTimeSettings(boolean allowTimeSettings) {
        this.allowTimeSettings = allowTimeSettings;
    }
}
