package com.kairos.utils.validate_list;

import javax.validation.Valid;
import java.util.List;

public class ValidateListOfRequestBody<T> {


    @Valid
    private List<T> requestBody;


    public List<T> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(List<T> requestBody) {
        this.requestBody = requestBody;
    }

    public ValidateListOfRequestBody( List<T> requestBody) {
        this.requestBody = requestBody;
    }

    public ValidateListOfRequestBody() {
    }
}
