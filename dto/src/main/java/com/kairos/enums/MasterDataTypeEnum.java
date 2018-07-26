package com.kairos.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 10/11/17.
 */
public enum MasterDataTypeEnum {
    SKILL("Skill"), EXPERTISE("Expertise"), ACTIVITY("Activity"), WTA("WTA"),CTA("CTA"), RULE_TEMPLATE_CATEGORY("Rule Template Category"), TASK_TYPE("Task Type");
    public String value;

    MasterDataTypeEnum(String value) {
        this.value = value;
    }

    public MasterDataTypeEnum getByValue(String value){
        for(MasterDataTypeEnum masterDataTypeEnum : MasterDataTypeEnum.values()){
            if(masterDataTypeEnum.value.equals(value)){
                return masterDataTypeEnum;
            }
        }
        return null;
    }

    public static List<HashMap<String,String>> getListOfMasterDataType(){
        List<HashMap<String,String>> masterData = new ArrayList<>();
        for(MasterDataTypeEnum masterDataTypeEnum : EnumSet.allOf(MasterDataTypeEnum.class)){
            HashMap<String,String> currentValue = new HashMap<>();
            currentValue.put("name",masterDataTypeEnum.value);
            currentValue.put("value",masterDataTypeEnum.name());
            masterData.add(currentValue);
        }
        return masterData;
    }

}
