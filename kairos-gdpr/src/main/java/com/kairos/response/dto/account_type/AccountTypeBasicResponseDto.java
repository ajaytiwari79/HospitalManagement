package com.kairos.response.dto.account_type;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountTypeBasicResponseDto {

    private BigInteger id;

    @NotNullOrEmpty
    private String typeOfAccount;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(String typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }
}
