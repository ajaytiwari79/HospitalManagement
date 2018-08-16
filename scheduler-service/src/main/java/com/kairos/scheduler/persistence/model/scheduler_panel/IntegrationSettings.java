package com.kairos.scheduler.persistence.model.scheduler_panel;

import com.kairos.scheduler.persistence.model.common.MongoBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class IntegrationSettings extends MongoBaseEntity {

    @NotEmpty(message = "name can not be null") @NotNull(message = "name can not be null")
    private String name;
    //@NotEmpty(message = "error.description.notnull") @NotNull(message = "error.description.notnull")
    private String description;

    @NotEmpty(message = "unique key can not be null") @NotNull(message = "unique key can not be null")
    private String uniqueKey;
    private boolean isEnabled = true;

    public BigInteger getCountryId() {
        return countryId;
    }

    public void setCountryId(BigInteger countryId) {
        this.countryId = countryId;
    }

    private BigInteger countryId;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
