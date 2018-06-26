package com.kairos.activity.response.dto.counter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class ModulewiseModuleCounterDTO {
    private String moduleId;
    private List<ModulewiseCounterDTO> modulewiseCounterDTOs = new ArrayList<>();

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<ModulewiseCounterDTO> getModulewiseCounterDTOs() {
        return modulewiseCounterDTOs;
    }

    public void setModulewiseCounterDTOs(List<ModulewiseCounterDTO> modulewiseCounterDTOs) {
        this.modulewiseCounterDTOs = modulewiseCounterDTOs;
    }
}
