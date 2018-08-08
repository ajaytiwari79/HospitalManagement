package com.kairos.persistance.model.agreement_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;

@Document(collection = "agreement_section")
public class AgreementSection extends MongoBaseEntity {


    @NotNullOrEmpty(message = "Section Title cannot be empty")
    private String title;

    private List<BigInteger> clauses;

    private List<BigInteger> subSections;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BigInteger> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<BigInteger> subSections) {
        this.subSections = subSections;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<BigInteger> getClauses() {
        return clauses;
    }

    public void setClauses(List<BigInteger> clauses) {
        this.clauses = clauses;
    }


    public AgreementSection(Long countryId , String title)
    {
        this.title=title;
        this.countryId=countryId;
    }
    public AgreementSection(){ }


}
