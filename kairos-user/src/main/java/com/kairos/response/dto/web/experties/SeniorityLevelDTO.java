package com.kairos.response.dto.web.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeniorityLevelDTO {
    private  Long id;
    private Integer from;
    private Integer to;
    private Integer moreThan;
    private List<FunctionsDTO> functions;
    private Integer basePayGrade;  // this is payGrade Id which is coming from payTable
    private Set<Long> payGroupAreas;// applicable payGroup areas
    // TODO We are unclear about this just adding and make sure this will utilize in future.
    private BigDecimal pensionPercentage;
    private BigDecimal freeChoicePercentage;
    private BigDecimal freeChoiceToPension;


    public SeniorityLevelDTO() {
        // default
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(Set<Long> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getMoreThan() {
        return moreThan;
    }

    public void setMoreThan(Integer moreThan) {
        this.moreThan = moreThan;
    }

    public List<FunctionsDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionsDTO> functions) {
        this.functions = functions;
    }

    public Integer getBasePayGrade() {
        return basePayGrade;
    }

    public void setBasePayGrade(Integer basePayGrade) {
        this.basePayGrade = basePayGrade;
    }

    public BigDecimal getPensionPercentage() {
        return pensionPercentage;
    }

    public void setPensionPercentage(BigDecimal pensionPercentage) {
        this.pensionPercentage = pensionPercentage;
    }

    public BigDecimal getFreeChoicePercentage() {
        return freeChoicePercentage;
    }

    public void setFreeChoicePercentage(BigDecimal freeChoicePercentage) {
        this.freeChoicePercentage = freeChoicePercentage;
    }

    public BigDecimal getFreeChoiceToPension() {
        return freeChoiceToPension;
    }

    public void setFreeChoiceToPension(BigDecimal freeChoiceToPension) {
        this.freeChoiceToPension = freeChoiceToPension;
    }

    public SeniorityLevelDTO(Integer from, Integer to,  Integer basePayGrade, BigDecimal pensionPercentage, BigDecimal freeChoicePercentage, BigDecimal freeChoiceToPension) {
        this.from = from;
        this.to = to;


        this.basePayGrade = basePayGrade;

        this.pensionPercentage = pensionPercentage;
        this.freeChoicePercentage = freeChoicePercentage;
        this.freeChoiceToPension = freeChoiceToPension;
    }

    public SeniorityLevelDTO(Integer moreThan, Integer basePayGrade, BigDecimal pensionPercentage, BigDecimal freeChoicePercentage, BigDecimal freeChoiceToPension) {
        this.moreThan = moreThan;
        this.basePayGrade = basePayGrade;
        this.pensionPercentage = pensionPercentage;
        this.freeChoicePercentage = freeChoicePercentage;
        this.freeChoiceToPension = freeChoiceToPension;
    }
    // from 2-4(to)
//    @AssertTrue(message = "Incorrect interval")
//    public boolean isValid() {
//        if (!Optional.ofNullable(this.from).isPresent()) {
//            return false;
//        }
//        if (Optional.ofNullable(this.to).isPresent()) {
//            if (this.to < this.from)
//                return false;
//        }
//        return true;
//    }
}
