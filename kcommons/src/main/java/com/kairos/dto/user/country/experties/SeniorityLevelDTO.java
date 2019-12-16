package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by vipul on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
@Getter
@Setter
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
    private Long payGradeLevel;


    @AssertTrue(message = "Incorrect Data")
    public boolean isValid() {

            if (!Optional.ofNullable(this.from).isPresent()) {
                return false;
            }
            if (Optional.ofNullable(this.to).isPresent()) {
                return this.to > this.from;
            }

        return true;
    }
}
