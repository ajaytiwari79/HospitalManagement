package com.kairos.persistence.model.access_permission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCOUNT_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PARENT_ACCESS_GROUP;

/**
 * Created by prabjot on 9/27/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class AccessGroup extends UserBaseEntity {

    @NotBlank(message = "error.name.notnull")
    private String name;
    private boolean enabled = true;
    private boolean typeOfTaskGiver;
    private String description;
    @Property(name = "role")
    @EnumString(AccessGroupRole.class)
    private AccessGroupRole role;
    @Relationship(type = HAS_ACCOUNT_TYPE)
    private List<AccountType> accountType;
    @NotNull(message = "error.startDate.notnull")
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean allowedDayTypes;
    private List<DayType> dayTypes;
    @Relationship(type = HAS_PARENT_ACCESS_GROUP)
    private AccessGroup parentAccessGroup;

    public AccessGroup() {
        //Default Constructor
    }

    public AccessGroup(@NotBlank(message = "error.name.notnull") String name, String description, AccessGroupRole role) {
        this.name = name;
        this.description = description;
        this.role = role;
    }

    public AccessGroup(String name, String description, AccessGroupRole role, List<DayType> dayTypes,LocalDate startDate,LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.role = role;
        this.dayTypes=dayTypes;
        this.startDate=startDate;
        this.endDate=endDate;
    }

    public AccessGroup(@NotBlank(message = "error.name.notnull") @NotNull(message = "error.name.notnull") String name, String description, AccessGroupRole role, List<AccountType> accountType,List<DayType> dayTypes,LocalDate startDate,LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.role = role;
        this.accountType = accountType;
        this.dayTypes=dayTypes;
        this.startDate=startDate;
        this.endDate=endDate;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTypeOfTaskGiver() {
        return typeOfTaskGiver;
    }

    public void setTypeOfTaskGiver(boolean typeOfTaskGiver) {
        this.typeOfTaskGiver = typeOfTaskGiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccessGroupRole getRole() {
        return role;
    }

    public void setRole(AccessGroupRole role) {
        this.role = role;
    }

    public List<AccountType> getAccountType() {
        return accountType;
    }

    public void setAccountType(List<AccountType> accountType) {
        this.accountType = accountType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public AccessGroup getParentAccessGroup() {
        return parentAccessGroup;
    }

    public void setParentAccessGroup(AccessGroup parentAccessGroup) {
        this.parentAccessGroup = parentAccessGroup;
    }

    public boolean isAllowedDayTypes() {
        return allowedDayTypes;
    }

    public void setAllowedDayTypes(boolean allowedDayTypes) {
        this.allowedDayTypes = allowedDayTypes;
    }
}
