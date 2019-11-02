package com.kairos.persistence.model.user.expertise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Sector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.commons.utils.DateUtils.startDateIsEqualsOrBeforeEndDate;
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

    private boolean published;



    @Relationship(type = HAS_SENIOR_DAYS)
    private List<CareDays> seniorDays;

    @Relationship(type = HAS_PROTECTED_DAYS_OFF_SETTINGS)
    private List<ProtectedDaysOffSetting> protectedDaysOffSettings=new ArrayList<>();

    @Relationship(type = HAS_CHILD_CARE_DAYS)
    private List<CareDays> childCareDays;

    @Relationship(type = HAS_EXPERTISE_LINES)
    private List<ExpertiseLine> expertiseLines=new ArrayList<>();

    @Relationship(type = BELONGS_TO_SECTOR)
    private Sector sector;

    @Relationship(type = IN_ORGANIZATION_LEVEL)
    private Level organizationLevel;

    @Relationship(type = SUPPORTED_BY_UNION)
    private Organization union;

    public Expertise(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public Expertise(@NotBlank(message = ERROR_EXPERTISE_NAME_NOTNULL) String name, String description, LocalDate startDate, LocalDate endDate, Country country, boolean published, List<ExpertiseLine> expertiseLines) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.country = country;
        this.published = published;
        this.expertiseLines = expertiseLines;
    }

    public Expertise(Long id,@NotBlank(message = ERROR_EXPERTISE_NAME_NOTNULL) String name, String description, LocalDate startDate, LocalDate endDate, boolean published,List<ExpertiseLine> expertiseLines) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = published;
        this.expertiseLines=expertiseLines;
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

    public ExpertiseLine getCurrentlyActiveLine(){
        ExpertiseLine currentExpertiseLine=null;
        for (ExpertiseLine expertiseLine:this.getExpertiseLines()) {
            if(startDateIsEqualsOrBeforeEndDate(expertiseLine.getStartDate(),getCurrentLocalDate()) &&
                    (expertiseLine.getEndDate()==null || startDateIsEqualsOrBeforeEndDate(getCurrentLocalDate(),expertiseLine.getEndDate()))){
                currentExpertiseLine=expertiseLine;
                break;
            }
        }
        return currentExpertiseLine;
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
