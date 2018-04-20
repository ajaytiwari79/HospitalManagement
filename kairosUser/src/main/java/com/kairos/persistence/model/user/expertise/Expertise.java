package com.kairos.persistence.model.user.expertise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 28/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class Expertise extends UserBaseEntity {

    @NotEmpty(message = "error.Expertise.name.notEmpty")
    @NotNull(message = "error.Expertise.name.notnull")
    private String name;

    //@NotEmpty(message = "error.Expertise.description.notEmpty") @NotNull(message = "error.Expertise.description.notnull")
    private String description;


    @Relationship(type = BELONGS_TO)
    Country country;

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


    private PaidOutFrequencyEnum paidOutFrequency;

    @Relationship(type = VERSION_OF)
    private Expertise parentExpertise;

    private boolean published;
    private boolean hasDraftCopy;
    private boolean history;


    @Relationship(type = FOR_SENIORITY_LEVEL)
    private List<SeniorityLevel> seniorityLevel;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Expertise(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }


    public Date getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Date startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Date getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Date endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Level getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(Level organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public Set<OrganizationService> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(Set<OrganizationService> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }

    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public Integer getNumberOfWorkingDaysInWeek() {
        return numberOfWorkingDaysInWeek;
    }

    public void setNumberOfWorkingDaysInWeek(Integer numberOfWorkingDaysInWeek) {
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
    }


    public PaidOutFrequencyEnum getPaidOutFrequency() {
        return paidOutFrequency;
    }

    public void setPaidOutFrequency(PaidOutFrequencyEnum paidOutFrequency) {
        this.paidOutFrequency = paidOutFrequency;
    }

    public List<SeniorityLevel> getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(List<SeniorityLevel> seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isHasDraftCopy() {
        return hasDraftCopy;
    }

    public void setHasDraftCopy(boolean hasDraftCopy) {
        this.hasDraftCopy = hasDraftCopy;
    }

    public Expertise getParentExpertise() {
        return parentExpertise;
    }

    public void setParentExpertise(Expertise parentExpertise) {
        this.parentExpertise = parentExpertise;
    }

    public boolean isHistory() {
        return history;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }

    public Expertise() {
    }


    public String getName() {
        return name;
    }


    public Expertise(Long id, @NotEmpty(message = "error.Expertise.name.notEmpty") @NotNull(message = "error.Expertise.name.notnull") String name, String description, Date startDateMillis, Date endDateMillis, int fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, PaidOutFrequencyEnum paidOutFrequency, boolean published) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.paidOutFrequency = paidOutFrequency;
        this.published = published;
    }

    public Expertise(Long id, @NotEmpty(message = "error.Expertise.name.notEmpty") @NotNull(message = "error.Expertise.name.notnull") String name, String description) {

        this.name = name;
        this.id = id;
        this.description = description;
    }


    public Expertise retrieveBasicDetails() {
        return new Expertise(this.id, this.name, this.description, this.startDateMillis, this.endDateMillis, this.fullTimeWeeklyMinutes, this.numberOfWorkingDaysInWeek, this.paidOutFrequency, this.published);

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
