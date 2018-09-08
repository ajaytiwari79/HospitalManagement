package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by oodles on 10/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentOrganizationDTO {

    private Long id;
    private List<Long> businessTypeIds;

    //@NotEmpty(message = "error.description.notnull") @NotNull(message = "error.description.notnull")
    private String description;
    private String externalId;
    private boolean isAddressProtected;
    private boolean isVerifiedByGoogleMap;
    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;
    private boolean kairosHub;
    private boolean prekairos;
    AddressDTO homeAddress;
    Long levelId;
    private  Boolean union;


    private String desiredUrl;
    private String shortCompanyName;
    private Long companyCategoryId;
    private Integer kairosCompanyId;
    private CompanyType companyType;

    private String vatId;
    private boolean boardingCompleted;

    //list of ids of organization type
    @Size(min=1, max=1)
    private List<Long> typeId;
    //list of ids of organization subtype
    private List<Long> subTypeId;

    public ParentOrganizationDTO() {
        //default constructor
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public boolean isKairosHub() {
        return kairosHub;
    }

    public void setKairosHub(boolean kairosHub) {
        this.kairosHub = kairosHub;
    }

    public boolean isPrekairos() {
        return prekairos;
    }

    public void setPrekairos(boolean prekairos) {
        this.prekairos = prekairos;
    }

    public AddressDTO getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(AddressDTO homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public boolean isAddressProtected() {
        return isAddressProtected;
    }

    public void setAddressProtected(boolean addressProtected) {
        isAddressProtected = addressProtected;
    }

    public boolean isVerifiedByGoogleMap() {
        return isVerifiedByGoogleMap;
    }

    public void setVerifiedByGoogleMap(boolean verifiedByGoogleMap) {
        isVerifiedByGoogleMap = verifiedByGoogleMap;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getTypeId() {
        return Optional.fromNullable(typeId).or(Lists.newArrayList());
    }

    public List<Long> getSubTypeId() {
        return Optional.fromNullable(subTypeId).or(Lists.newArrayList());

    }

    public void setTypeId(List<Long> typeId) {
        this.typeId = typeId;
    }

    public void setSubTypeId(List<Long> subTypeId) {
        this.subTypeId = subTypeId;
    }

    public List<Long> getBusinessTypeIds() {
        return Optional.fromNullable(businessTypeIds).or(Lists.newArrayList());
    }

    public void setBusinessTypeIds(List<Long> businessTypeIds) {
        this.businessTypeIds = businessTypeIds;
    }

    public ParentOrganizationDTO(List<Long> businessTypeIds, String description, boolean isVerifiedByGoogleMap, String name,
                                 Long levelId, List<Long> typeId, List<Long> subTypeId) {
        this.businessTypeIds = businessTypeIds;
        this.description = description;
        this.isVerifiedByGoogleMap = isVerifiedByGoogleMap;
        this.name = name;
        this.levelId = levelId;
        this.typeId = typeId;
        this.subTypeId = subTypeId;
    }

    public Boolean getUnion() {
        return union;
    }

    public void setUnion(Boolean union) {
        this.union = union;
    }

    public String getDesiredUrl() {
        return desiredUrl;
    }

    public void setDesiredUrl(String desiredUrl) {
        this.desiredUrl = desiredUrl;
    }

    public String getShortCompanyName() {
        return shortCompanyName;
    }

    public void setShortCompanyName(String shortCompanyName) {
        this.shortCompanyName = shortCompanyName;
    }

    public Long getCompanyCategoryId() {
        return companyCategoryId;
    }

    public void setCompanyCategoryId(Long companyCategoryId) {
        this.companyCategoryId = companyCategoryId;
    }

    public Integer getKairosCompanyId() {
        return kairosCompanyId;
    }

    public void setKairosCompanyId(Integer kairosCompanyId) {
        this.kairosCompanyId = kairosCompanyId;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public void setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public boolean isBoardingCompleted() {
        return boardingCompleted;
    }

    public void setBoardingCompleted(boolean boardingCompleted) {
        this.boardingCompleted = boardingCompleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
