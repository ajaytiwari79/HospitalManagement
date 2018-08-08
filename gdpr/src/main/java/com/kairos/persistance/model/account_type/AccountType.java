package com.kairos.persistance.model.account_type;

import com.kairos.persistance.model.common.MongoBaseEntity;
import org.javers.core.metamodel.annotation.TypeName;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "account_type")
@TypeName("account_type")
public class AccountType extends MongoBaseEntity {

    private String name;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType(){}


}
