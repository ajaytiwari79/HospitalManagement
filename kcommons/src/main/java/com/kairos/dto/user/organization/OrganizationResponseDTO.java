package com.kairos.dto.user.organization;


import com.kairos.dto.user.staff.client.ContactAddressDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by oodles on 25/4/18.
 */
@Getter
@Setter
public class OrganizationResponseDTO {
    private  Long  id;
    private  String name;
    private  boolean prekairos;
    private  boolean kairosHub;
    private  String description;
    private  List<Long> businessTypeIds;
    private  Long typeId;
    private  List<Long> subTypeId;
    private  String externalId;
    private ContactAddressDTO contactAddress;
    private  Long levelId;
    private String kairosId;
    private Boolean union;
    private String desiredUrl;
    private String shortCompanyName;
    private Long companyCategoryId;
    private String kairosCompanyId;
    private CompanyType companyType;
    private Long accountTypeId;
    private String vatId;
    private boolean costCenter;
    private Integer costCenterId;
    private CompanyUnitType companyUnitType;
    private boolean boardingCompleted;
    private UnitManagerDTO unitManager;
}
