package com.kairos.dto.kpermissions;

import java.util.ArrayList;
import java.util.List;

public class ModelDTO {

    private String modelName;

    private List<FieldDTO> fields = new ArrayList<>();

    public List<FieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<FieldDTO> fields) {
        this.fields = fields;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public ModelDTO() {
    }
}
