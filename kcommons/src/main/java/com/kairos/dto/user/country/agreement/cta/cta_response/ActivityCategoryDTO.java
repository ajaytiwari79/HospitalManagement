package com.kairos.dto.user.country.agreement.cta.cta_response;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by prerna on 22/3/18.
 */
public class ActivityCategoryDTO {

    private String name;
    private BigInteger id;

    public ActivityCategoryDTO(){
        // default constructor
    }

    public ActivityCategoryDTO(BigInteger id, String name) {
        this.name = name;
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityCategoryDTO that = (ActivityCategoryDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
