package com.kairos.persistance.model.agreement_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Document(collection = "agreement_section")
public class AgreementSection extends MongoBaseEntity {


    @NotNullOrEmpty(message = "error.title.cannot.be.empty.or.null")
    private String title;

    private List<BigInteger> clauseIds;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<BigInteger> getClauseIds() {
        return clauseIds;
    }

    public void setClauseIds(List<BigInteger> clauseIds) {
        this.clauseIds = clauseIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


   public AgreementSection(Long countryId ,String title,List<BigInteger> clauseIds)
    {
        this.title=title;
        this.clauseIds=clauseIds;
        this.countryId=countryId;
    }



}
