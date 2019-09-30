package com.kairos.persistence.model.user.expertise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.union.Sector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.UserMessagesConstants.ERROR_EXPERTISE_NAME_NOTEMPTY;
import static com.kairos.constants.UserMessagesConstants.ERROR_EXPERTISE_NAME_NOTNULL;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 28/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Expertise extends UserBaseEntity {
    @NotBlank(message = ERROR_EXPERTISE_NAME_NOTNULL)
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Relationship(type = BELONGS_TO)
    private Country country;

    private int fullTimeWeeklyMinutes; // This is equals to 37 hours
    private Integer numberOfWorkingDaysInWeek; // 5 or 7
    private BreakPaymentSetting breakPaymentSetting;

    private boolean published;

    @Relationship(type = FOR_SENIORITY_LEVEL)
    private List<SeniorityLevel> seniorityLevel;

    @Relationship(type = HAS_SENIOR_DAYS)
    private List<CareDays> seniorDays;

    @Relationship(type = HAS_PROTECTED_DAYS_OFF_SETTINGS)
    private List<ProtectedDaysOffSetting> protectedDaysOffSettings=new ArrayList<>();

    @Relationship(type = HAS_CHILD_CARE_DAYS)
    private List<CareDays> childCareDays;

    @Relationship(type = HAS_EXPERTISE_LINES)
    private List<ExpertiseLine> expertiseLines;

    public Expertise(String name, Country country) {
        this.name = name;
        this.country = country;
    }


    public Expertise(@NotBlank(message = ERROR_EXPERTISE_NAME_NOTNULL) String name, String description, LocalDate startDate, LocalDate endDate, Country country, int fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, boolean published, List<SeniorityLevel> seniorityLevel, List<ExpertiseLine> expertiseLines) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.country = country;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.published = published;
        this.seniorityLevel = seniorityLevel;
        this.expertiseLines = expertiseLines;
    }

    public Expertise(@NotBlank(message = ERROR_EXPERTISE_NAME_NOTNULL) String name, String description, LocalDate startDate, LocalDate endDate, int fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek,  boolean published, List<SeniorityLevel> seniorityLevel) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.published = published;
        this.seniorityLevel = seniorityLevel;
    }

    public List<CareDays> getSeniorDays() {
        return seniorDays = Optional.ofNullable(seniorDays).orElse(new ArrayList<>());
    }


    public void addSeniorDay(CareDays seniorDays) {
        this.seniorDays = Optional.ofNullable(this.seniorDays).orElse(new ArrayList<>());
        this.seniorDays.add(seniorDays);
    }

    public void addChildCareDay(CareDays childCareDay) {
        this.childCareDays = Optional.ofNullable(this.childCareDays).orElse(new ArrayList<>());
        this.childCareDays.add(childCareDay);
    }

    public List<CareDays> getChildCareDays() {
        return childCareDays = Optional.ofNullable(childCareDays).orElse(new ArrayList<>());
    }

    public List<SeniorityLevel> getSeniorityLevel() {
        return seniorityLevel = Optional.ofNullable(seniorityLevel).orElse(new ArrayList<>());
    }

    public void addSeniorityLevel(SeniorityLevel seniorityLevel) {
        getSeniorityLevel().add(seniorityLevel);
    }

    public Expertise(Long id, @NotBlank(message = ERROR_EXPERTISE_NAME_NOTEMPTY)  String name, String description, LocalDate startDate, LocalDate endDate, int fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, boolean published) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.published = published;
    }

    public Expertise(@NotBlank(message = ERROR_EXPERTISE_NAME_NOTNULL) String name, String description, Country country, LocalDate startDate, LocalDate endDate, int fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, BreakPaymentSetting breakPaymentSetting, boolean published, Sector sector) {
        this.name = name;
        this.description = description;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.breakPaymentSetting = breakPaymentSetting;
        this.published = published;
        //this.sector = sector;
    }

    public Expertise(Long id, @NotBlank(message = ERROR_EXPERTISE_NAME_NOTEMPTY)  String name, String description) {

        this.name = name;
        this.id = id;
        this.description = description;
    }


    public Expertise retrieveBasicDetails() {
        return new Expertise(this.id, this.name, this.description, this.startDate, this.endDate, this.fullTimeWeeklyMinutes, this.numberOfWorkingDaysInWeek, this.published);

    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap<>(6);
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("country", this.country.getName());
        map.put("lastModificationDate", this.getLastModificationDate());
        map.put("creationDate", this.getCreationDate());
        return map;
    }


}
