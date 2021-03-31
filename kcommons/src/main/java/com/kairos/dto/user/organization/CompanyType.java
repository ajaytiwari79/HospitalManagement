package com.kairos.dto.user.organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oodles on 16/4/18.
 */
public enum CompanyType implements Serializable {
    HUB("HUB"), COMPANY("Company"),UNION("union"),UNIT("Unit");



    public String value;
    CompanyType(String value) {
        this.value = value;
    }

    public static List<HashMap<String,String>> getListOfCompanyType(){
        List<HashMap<String,String>> companyTypeList = new ArrayList<>();
        for(CompanyType companyType: EnumSet.allOf(CompanyType.class)){
            HashMap<String,String> currentValue = new HashMap<>();
            currentValue.put("name",companyType.value);
            currentValue.put("value",companyType.name());
            companyTypeList.add(currentValue);
        }
        return companyTypeList;
    }
}
