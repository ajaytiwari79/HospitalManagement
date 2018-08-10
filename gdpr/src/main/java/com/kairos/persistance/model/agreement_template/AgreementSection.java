package com.kairos.persistance.model.agreement_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "agreement_section")
public class AgreementSection extends MongoBaseEntity {


    @NotNullOrEmpty(message = "Section Title cannot be empty")
    private String name;

    private List<BigInteger> clauses;

    private List<BigInteger> subAgreementSections;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public List<BigInteger> getSubAgreementSections() { return subAgreementSections; }

    public void setSubAgreementSections(List<BigInteger> subAgreementSections) { this.subAgreementSections = subAgreementSections; }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<BigInteger> getClauses() {
        return clauses;
    }

    public void setClauses(List<BigInteger> clauses) {
        this.clauses = clauses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AgreementSection(Long countryId , String name)
    {
        this.name=name;
        this.countryId=countryId;
    }
    public AgreementSection(){ }


}
