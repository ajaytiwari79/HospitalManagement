package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OrganizationBasicDTO {
    private Long id;
    @NotNull(message = "error.name.notnull")
    private String name;
    private String shortCompanyName;
    private String description;
    private String desiredUrl;
    private Long companyCategoryId;
    private List<Long> businessTypeIds;
    private CompanyType companyType;
    private String vatId;
    private Long accountTypeId;
    private Long levelId;
    private Long typeId;
    private String kairosCompanyId;
    private List<Long> subTypeId;
    private AddressDTO contactAddress; // used in case of child organization
    private UnitManagerDTO unitManager;  // Used in case of child organization only
    private Long unitTypeId;
    private boolean boardingCompleted;
    private boolean workcentre;
    private Long hubId;

    public void setShortCompanyName(String shortCompanyName) {
        this.shortCompanyName =shortCompanyName!=null? shortCompanyName.trim():null;
    }

    public void setDescription(String description) {
        this.description = description!=null?description.trim():null;
    }

}
