package com.kairos.persistance.model.master_data.default_proc_activity_setting;


import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;
import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "master_processing_activity")
public class MasterProcessingActivity extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Description can't be empty")
    private String description;

    @NotNull
    private List<OrganizationTypeDTO> organizationTypes;

    @NotNull
    private List<OrganizationSubTypeDTO> organizationSubTypes;
    @NotNull
    private List<ServiceCategoryDTO> organizationServices;
    @NotNull
    private List<SubServiceCategoryDTO> organizationSubServices;

    @NotNull
    private List<BigInteger> subProcessingActivityIds;

    @NotNull(message = "error.message.countryId.cannot.be.null")
    private Long countryId;

    private Boolean isSubProcess=false;

    private Boolean hasSubProcessingActivity=false;

    public Boolean getHasSubProcessingActivity() {
        return hasSubProcessingActivity;
    }

    public void setHasSubProcessingActivity(Boolean hasSubProcessingActivity) {
        this.hasSubProcessingActivity = hasSubProcessingActivity;
    }

    public Boolean getSubProcess() {
        return isSubProcess;
    }

    public void setSubProcess(Boolean subProcess) {
        isSubProcess = subProcess;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<BigInteger> getSubProcessingActivityIds() {
        return subProcessingActivityIds;
    }

    public void setSubProcessingActivityIds(List<BigInteger> subProcessingActivityIds) {
        this.subProcessingActivityIds = subProcessingActivityIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrganizationTypeDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubTypeDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public MasterProcessingActivity() {

    }

    public MasterProcessingActivity(Long countryId,String name,String description) {

        this.name=name;
        this.description=description;
        this.countryId=countryId;
         }

    public MasterProcessingActivity(String name, String description,  List<OrganizationTypeDTO> organizationTypes,  List<OrganizationSubTypeDTO> organizationSubTypes,List<ServiceCategoryDTO> organizationServices, List<SubServiceCategoryDTO> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
    }
}
