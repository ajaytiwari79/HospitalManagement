package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by vipul on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
public class SeniorityLevelDTO {
    private Long id;
    private Long parentId;
    @Min(value = 0,message = "can't be less than 0")
    private Integer from;
    private Integer to;
    @NotNull(message = "PayGradeId  can not be null")
    private Long payGradeId;  // this is payGrade Id which is coming from payTable
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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


    public Long getPayGradeId() {
        return payGradeId;
    }

    public void setPayGradeId(Long payGradeId) {
        this.payGradeId = payGradeId;
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

    public SeniorityLevelDTO(Integer from, Integer to, Long payGradeId, BigDecimal pensionPercentage, BigDecimal freeChoicePercentage, BigDecimal freeChoiceToPension) {
        this.from = from;
        this.to = to;
        this.payGradeId = payGradeId;
        this.pensionPercentage = pensionPercentage;
        this.freeChoicePercentage = freeChoicePercentage;
        this.freeChoiceToPension = freeChoiceToPension;
    }


    @AssertTrue(message = "Incorrect Data")
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
