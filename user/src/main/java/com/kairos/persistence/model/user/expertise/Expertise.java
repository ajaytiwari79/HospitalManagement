package com.kairos.persistence.model.user.expertise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Sector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotBlank;
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

    @Relationship(type = BELONGS_TO)
    private Country country;

    @Relationship(type = BELONGS_TO_SECTOR)
    private Sector sector;

    @Relationship(type = HAS_TAG)
    private List<Tag> tags = new ArrayList<>();
    @DateLong
    private Date startDateMillis;
    @DateLong
    private Date endDateMillis;
    @Relationship(type = IN_ORGANIZATION_LEVEL)
    private Level organizationLevel;

    @Relationship(type = SUPPORTS_SERVICES)
    private Set<OrganizationService> organizationServices;

    @Relationship(type = SUPPORTED_BY_UNION)
    private Organization union;
    private int fullTimeWeeklyMinutes; // This is equals to 37 hours
    private Integer numberOfWorkingDaysInWeek; // 5 or 7
    private BreakPaymentSetting breakPaymentSetting;
    @Relationship(type = VERSION_OF)
    private Expertise parentExpertise;

    private boolean published;
    private boolean hasDraftCopy;
    private boolean history;


    @Relationship(type = FOR_SENIORITY_LEVEL)
    private List<SeniorityLevel> seniorityLevel;

    @Relationship(type = HAS_SENIOR_DAYS)
    private List<CareDays> seniorDays;

    @Relationship(type = HAS_CHILD_CARE_DAYS)
    private List<CareDays> childCareDays;

    public Expertise(String name, Country country) {
        this.name = name;
        this.country = country;
    }


    public List<SeniorityLevel> getSeniorityLevel() {
        return seniorityLevel = Optional.ofNullable(seniorityLevel).orElse(new ArrayList<>());
    }


    public void addSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = Optional.ofNullable(this.seniorityLevel).orElse(new ArrayList<>());
        this.seniorityLevel.add(seniorityLevel);
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



    public String getName() {
        return name;
    }


    public Expertise(Long id, @NotBlank(message = ERROR_EXPERTISE_NAME_NOTEMPTY)  String name, String description, Date startDateMillis, Date endDateMillis, int fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, boolean published) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.published = published;
    }

    public Expertise(@NotBlank(message = ERROR_EXPERTISE_NAME_NOTNULL) String name, String description, Country country, Date startDateMillis, Date endDateMillis, int fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, BreakPaymentSetting breakPaymentSetting, boolean published, boolean hasDraftCopy, boolean history,Sector sector) {
        this.name = name;
        this.description = description;
        this.country = country;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.breakPaymentSetting = breakPaymentSetting;
        this.published = published;
        this.hasDraftCopy = hasDraftCopy;
        this.history = history;
        this.sector = sector;
    }

    public Expertise(Long id, @NotBlank(message = ERROR_EXPERTISE_NAME_NOTEMPTY)  String name, String description) {

        this.name = name;
        this.id = id;
        this.description = description;
    }


    public Expertise retrieveBasicDetails() {
        return new Expertise(this.id, this.name, this.description, this.startDateMillis, this.endDateMillis, this.fullTimeWeeklyMinutes, this.numberOfWorkingDaysInWeek, this.published);

    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("country", this.country.getName());
        map.put("lastModificationDate", this.getLastModificationDate());
        map.put("creationDate", this.getCreationDate());
        return map;
    }


}
