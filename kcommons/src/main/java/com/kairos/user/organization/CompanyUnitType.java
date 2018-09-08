package com.kairos.user.organization;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oodles on 17/4/18.
 */
public enum CompanyUnitType {
    COMPANY("company"), COMPANY_UNIT("company unit"), FUNCTIONAL_UNIT("functional unit");

    public String value;
    CompanyUnitType(String value) {
        this.value = value;
    }

    public static List<HashMap<String,String>> getListOfCompanyUnitType(){
        List<HashMap<String,String>> companyUnitTypeList = new ArrayList<>();
        for(CompanyUnitType companyUnitType : EnumSet.allOf(CompanyUnitType.class)){
            HashMap<String,String> currentValue = new HashMap<>();
            currentValue.put("name",companyUnitType.value);
            currentValue.put("value",companyUnitType.name());
            companyUnitTypeList.add(currentValue);
        }
        return companyUnitTypeList;
    }
}
