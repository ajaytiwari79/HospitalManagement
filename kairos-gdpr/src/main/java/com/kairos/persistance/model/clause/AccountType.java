package com.kairos.persistance.model.clause;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.enums.AccountTypeEnum;
import com.kairos.persistance.model.organization.enums.EnumString;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "account_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountType extends MongoBaseEntity {


private String typeOfAccount;


    public String getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(String typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }

    public AccountType()
{}



}
