package com.kairos.persistance.model.agreement_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "agreement_section")
public class AgreementSection extends MongoBaseEntity {


    @NotBlank(message = "Section Title cannot be empty")
    private String title;

    private List<BigInteger> clauses;

    private List<BigInteger> subSections;

    @NotNull(message = "Clause order is Not defined")
    private Integer orderedIndex;

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

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public AgreementSection(Long countryId , @NotBlank(message = "Section Title cannot be empty") String title,@NotNull(message = "Clause order is Not defined") Integer orderedIndex)
    {
        this.title=title;
        this.countryId=countryId;
        this.orderedIndex=orderedIndex;
    }
    public AgreementSection(){ }


}
