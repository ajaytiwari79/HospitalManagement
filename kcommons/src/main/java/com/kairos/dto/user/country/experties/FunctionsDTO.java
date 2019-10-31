package com.kairos.dto.user.country.experties;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by vipul on 27/3/18.
 */
@JsonIgnoreProperties
@Getter
@Setter
public class FunctionsDTO {
    private Long id;
    private String name; // THIS is used for FE compactibility
    private BigDecimal amount; // amount which is added to this function
    private boolean amountEditableAtUnit;
    private Long functionId;


    public Long getFunctionId() {
        return functionId!=null?functionId:id;
    } // THIS IS for FE compactibility We need to remove this Impact on FUNCTION inside expertise


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FunctionsDTO{");
        sb.append(", functionId=").append(id);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
