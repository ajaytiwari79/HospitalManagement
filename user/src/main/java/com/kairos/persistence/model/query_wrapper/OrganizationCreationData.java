package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.access_permission.AccessGroupQueryResult;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.Level;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 6/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
@Getter
@Setter
public class OrganizationCreationData {

    private List<Map<String,Object>> zipCodes;
    private java.util.List<BusinessType> businessTypes;
    private List<Map<String,Object>> organizationTypes;
    private List<Level> levels;
    private List<Map<String,Object>> serviceTypes;
    private List<CompanyCategory> companyCategories;
    private List<HashMap<String,String>> companyTypes;
    private List<HashMap<String,String>> companyUnitTypes;
    private Map<String, List<AccessGroupQueryResult>> accessGroups;
    private List<AccountType> accountTypes= new ArrayList<>();
    private List<UnitType> unitTypes= new ArrayList<>();
    private List<OrganizationWrapper> hubList;
    private Long countryId;
}
