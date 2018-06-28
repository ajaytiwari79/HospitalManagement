package com.kairos.utils.validate_list;

import javax.validation.Valid;
import java.util.List;

public class ValidateDTOList<T> {


    @Valid
    private List<T> requestBody;

    public List<T> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(List<T> requestBody) {
        this.requestBody = requestBody;
    }

    public ValidateDTOList(List<T> requestBody) {
        this.requestBody = requestBody;
    }

    public ValidateDTOList() {
    }
}
