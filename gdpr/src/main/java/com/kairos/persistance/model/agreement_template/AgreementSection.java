package com.kairos.persistance.model.agreement_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "agreement_section")
public class AgreementSection extends MongoBaseEntity {


    @NotNullOrEmpty(message = "Section Title cannot be empty")
    private String title;

    @NotNull
    private List<BigInteger> clauses;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


   public AgreementSection(Long countryId ,String title,List<BigInteger> clauses)
    {
        this.title=title;
        this.clauses=clauses;
        this.countryId=countryId;
    }
public AgreementSection(){ }


}
