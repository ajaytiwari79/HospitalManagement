package com.kairos.persistence.model.organization;

import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.organization.CompanyType;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 26/2/18.
 */
@QueryResult
@Getter
@Setter
public class OrganizationBasicResponse {
    private Long id;
    private String name;
    private String shortCompanyName;
    private String description;
    private String desiredUrl;
    private Long companyCategoryId;
    private List<Long> businessTypeIds;
    private CompanyType companyType;
    private String vatId;
    private String kairosCompanyId;
    private Long accountTypeId;
    private Boolean boardingCompleted;
    private Long zipCodeId;
    private Long typeId;
    private List<Long> subTypeId;
    // Used in case of child
    //Map<String, Object> contactAddress;
    private StaffPersonalDetailQueryResult unitManager;
    private Long unitTypeId;
    private boolean workcentre;
    private Long hubId;
    private Long levelId;
    private String timezone;
    private AddressDTO contactAddress;
}
