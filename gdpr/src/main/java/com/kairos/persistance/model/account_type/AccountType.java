package com.kairos.persistance.model.account_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Pattern;


@Document(collection = "account_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountType extends MongoBaseEntity {

    @NotNullOrEmpty(message = "Name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$",message = "special character and numberic data is not allowed")
    private String name;

    private Long CountryId;

    public Long getCountryId() {
        return CountryId;
    }

    public void setCountryId(Long countryId) {
        CountryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType(){}


}
