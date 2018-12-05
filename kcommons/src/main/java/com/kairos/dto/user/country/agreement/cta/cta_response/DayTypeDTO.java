package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.enums.Day;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DayTypeDTO {
   private Long id;
   private String name;
   private List<Day> validDays=new ArrayList<>();
   private List<LocalDate> holidayDate;
   private boolean holidayType;


    public DayTypeDTO() {
        //default constructor

    }


    public DayTypeDTO(Long id, String name, List<Day> validDays, List<LocalDate> holidayDate, boolean holidayType) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.holidayDate = holidayDate;
        this.holidayType = holidayType;
    }

    public DayTypeDTO(Long id, String name, List<Day> validDays) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
    }

    public boolean isHolidayType() {
        return holidayType;
    }

    public void setHolidayType(boolean holidayType) {
        this.holidayType = holidayType;
    }

    public List<LocalDate> getHolidayDate() {

        return holidayDate;
    }

    public void setHolidayDate(List<LocalDate> holidayDate) {
        this.holidayDate = holidayDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Day> getValidDays() {
        return validDays;
    }

    public void setValidDays(List<Day> validDays) {
        this.validDays = validDays;
    }
}
