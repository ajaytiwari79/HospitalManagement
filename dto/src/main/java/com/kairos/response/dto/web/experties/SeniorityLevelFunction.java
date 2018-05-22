package com.kairos.response.dto.web.experties;

import java.util.List;

public class SeniorityLevelFunction {
    private Long seniorityLevelId;
    private List<FunctionsDTO> functions;

    public SeniorityLevelFunction() {
    }

    public Long getSeniorityLevelId() {
        return seniorityLevelId;
    }

    public void setSeniorityLevelId(Long seniorityLevelId) {
        this.seniorityLevelId = seniorityLevelId;
    }

    public List<FunctionsDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionsDTO> functions) {
        this.functions = functions;
    }
}
