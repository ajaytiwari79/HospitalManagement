package com.kairos.persistence.model.country.default_data.account_type;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class AccountTypeQueryResult extends UserBaseEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public AccountTypeQueryResult() {
    }


}
