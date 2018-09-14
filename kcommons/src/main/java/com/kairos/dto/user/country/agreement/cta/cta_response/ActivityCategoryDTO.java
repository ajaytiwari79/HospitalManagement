package com.kairos.dto.user.country.agreement.cta.cta_response;

import java.math.BigInteger;

/**
 * Created by prerna on 22/3/18.
 */
public class ActivityCategoryDTO {

    private String name;
    private BigInteger id;

    public ActivityCategoryDTO(){
        // default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
