package com.kairos.persistance.model.agreement_template;


import com.kairos.persistance.country.Country;
import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "agreement_template")
public class AgreementTemplate extends MongoBaseEntity {

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
   private String description;

    private AccountType accountType;
    private List<BigInteger> clauses;

    Country country;

    Boolean isDefault=true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public List<BigInteger> getClauses() {
        return clauses;
    }

    public void setClauses(List<BigInteger> clauses) {
        this.clauses = clauses;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {

        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
