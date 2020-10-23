package com.kairos.persistence.model.organization;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.organization.CompanyType;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

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

    private StaffPersonalDetailQueryResult unitManager;
    private Long unitTypeId;
    private boolean workcentre;
    private Long hubId;
    private Long levelId;
    private String timezone;
    private AddressDTO contactAddress;

    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        if(!StringUtils.isEmpty(translatedNames.get(UserContext.getUserDetails().getLanguage().toLowerCase()))) {
            return translatedNames.getOrDefault(UserContext.getUserDetails().getLanguage().toLowerCase(), name);
        }else {
            return name;
        }
    }

    public String getDescription() {
        if(!StringUtils.isEmpty(translatedDescriptions.get(UserContext.getUserDetails().getLanguage().toLowerCase()))) {
            return translatedDescriptions.getOrDefault(UserContext.getUserDetails().getLanguage().toLowerCase(), description);
        }else{
            return description;
        }
    }

}
