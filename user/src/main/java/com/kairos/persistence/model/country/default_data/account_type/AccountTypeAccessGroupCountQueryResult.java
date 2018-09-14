package com.kairos.persistence.model.country.default_data.account_type;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * CreatedBy vipulpandey on 14/9/18
 **/
@QueryResult
public class AccountTypeAccessGroupCountQueryResult {
    private Long id;
    private String name;
    private short count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getCount() {
        return count;
    }

    public void setCount(short count) {
        this.count = count;
    }
}
