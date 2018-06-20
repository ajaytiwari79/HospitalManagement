package com.kairos.dto.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultContraintsDTO {

    private String category;
    private List<ConstraintDTO> contraints;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ConstraintDTO> getContraints() {
        return contraints;
    }

    public void setContraints(List<ConstraintDTO> contraints) {
        this.contraints = contraints;
    }
}
