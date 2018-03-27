package com.kairos.response.dto.web.experties;

import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Created by vipul on 27/3/18.
 */
public class SeniorityLevelDTO {

    private Integer from;
    private Integer to;
    private Integer moreThan;
    private List<FunctionsDTO> functions;
    private Integer basePayGrade;
    private BigDecimal pensionPercentage;
    private BigDecimal freeChoicePercentage;
    private BigDecimal freeChoiceToPension;
    private List<Long> payGroupAreas;

    public SeniorityLevelDTO() {
    }

    public List<Long> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(List<Long> payGroupAreas) {
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

    // from 2-4(to)
    @AssertTrue(message = "Incorrect interval")
    public boolean isValid() {
        if (!Optional.ofNullable(this.from).isPresent()) {
            return false;
        }
        if (Optional.ofNullable(this.to).isPresent()) {
            if (this.to < this.from)
                return false;
        }
        return true;
    }
}
