package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prabjot on 9/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationGeneral {

    @NotEmpty(message = "error.Organization.formalname.notnull") @NotNull(message = "error.Organization.formalname.notnull")
    private String name;
   // @NotEmpty(message = "error.Organization.shortname.notnull") @NotNull(message = "error.Organization.shortname.notnull")
    private String shortName;
    private String eanNumber;
    private String costCenterCode;
    private String costCenterName;
    //@NotEmpty(message = "error.description.notnull") @NotNull(message = "error.description.notnull")
    private String description;
    @NotNull(message = "error.OrganizationType.name.notEmpty")
    private Long organizationTypeId;
    @NotNull(message = "error.OrganizationType.name.notEmpty")
    private List<Long> organizationSubTypeId;
  //  @NotNull(message = "error.Organization.businesstype.notnull")
    private List<Long> businessTypeId;
    private String websiteUrl;
   // @NotNull(message = "error.Organization.industrytype.notnull")
    private Long industryTypeId;
  //  @NotNull(message = "error.Organization.noofemploy.notnull")
    private Long employeeLimitId;
   // @NotNull(message = "error.Organization.ownershiptype.notnull")
    private Long ownershipTypeId;
 //   @NotNull(message = "error.Organization.kairosstatus.notnull")
    private Long kairosStatusId;
    private String cvrNumber;
    private String pNumber;
   // @NotNull(message = "error.Organization.vattype.notnull")
    private Long vatTypeId;
   // @NotNull(message = "error.Organization.contract.notnull")
    private Long contractTypeId;
    private boolean isKairosHub;
    private String clientSince;
    private String municipality;
    private Long municipalityId;

    private String externalId;
    private String kmdExternalId;

    private int dayShiftTimeDeduction = 4; //in percentage

    private int nightShiftTimeDeduction = 7; //in percentage

    public void setKmdExternalId(String kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }

    public String getKmdExternalId() {

        return kmdExternalId;
    }

    public void setPercentageWorkDeduction(int percentageWorkDeduction) {
        this.percentageWorkDeduction = percentageWorkDeduction;
    }

    public int getPercentageWorkDeduction() {

        return percentageWorkDeduction;
    }

    private int percentageWorkDeduction;

    public String getEanNumber() {
        return eanNumber;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public String getDescription() {
        return description;
    }

    public int getDayShiftTimeDeduction() {
        return dayShiftTimeDeduction;
    }

    public void setDayShiftTimeDeduction(int dayShiftTimeDeduction) {
        this.dayShiftTimeDeduction = dayShiftTimeDeduction;
    }

    public int getNightShiftTimeDeduction() {
        return nightShiftTimeDeduction;
    }

    public void setNightShiftTimeDeduction(int nightShiftTimeDeduction) {
        this.nightShiftTimeDeduction = nightShiftTimeDeduction;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }


    public String getCvrNumber() {
        return cvrNumber;
    }

    public String getpNumber() {
        return pNumber;
    }


    public boolean isKairosHub() {
        return isKairosHub;
    }

    public void setIsKairosHub(boolean isKairosHub) {
        this.isKairosHub = isKairosHub;
    }

    public void setEanNumber(String eanNumber) {
        this.eanNumber = eanNumber;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setCvrNumber(String cvrNumber) {
        this.cvrNumber = cvrNumber;
    }

    public void setpNumber(String pNumber) {
        this.pNumber = pNumber;
    }


    public Long getIndustryTypeId() {
        return industryTypeId;
    }


    public Long getOwnershipTypeId() {
        return ownershipTypeId;
    }

    public Long getVatTypeId() {
        return vatTypeId;
    }

    public Long getContractTypeId() {
        return contractTypeId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getClientSince() {
        return clientSince;
    }

    public Long getEmployeeLimitId() {
        return employeeLimitId;
    }


    public Long getKairosStatusId() {
        return kairosStatusId;
    }


    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setIndustryTypeId(Long industryTypeId) {
        this.industryTypeId = industryTypeId;
    }

    public void setEmployeeLimitId(Long employeeLimitId) {
        this.employeeLimitId = employeeLimitId;
    }

    public void setOwnershipTypeId(Long ownershipTypeId) {
        this.ownershipTypeId = ownershipTypeId;
    }

    public void setKairosStatusId(Long kairosStatusId) {
        this.kairosStatusId = kairosStatusId;
    }

    public void setVatTypeId(Long vatTypeId) {
        this.vatTypeId = vatTypeId;
    }

    public void setContractTypeId(Long contractTypeId) {
        this.contractTypeId = contractTypeId;
    }

    public void setClientSince(String clientSince) {
        this.clientSince = clientSince;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getOrganizationTypeId() {
        return organizationTypeId;
    }

    public List<Long> getOrganizationSubTypeId() {
        return organizationSubTypeId;
    }

    public List<Long> getBusinessTypeId() {
        return businessTypeId;
    }

    public void setOrganizationTypeId(Long organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public void setOrganizationSubTypeId(List<Long> organizationSubTypeId) {
        this.organizationSubTypeId = organizationSubTypeId;
    }

    public void setBusinessTypeId(List<Long> businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Long getMunicipalityId() {

        return municipalityId;
    }
}
