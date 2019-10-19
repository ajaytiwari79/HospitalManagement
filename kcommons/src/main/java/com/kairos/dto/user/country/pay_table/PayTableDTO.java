package com.kairos.dto.user.country.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by vipul on 15/3/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PayTableDTO {
    private Long id;
    @NotNull(message = "name can't be null")
    private String name;
    private String shortName;
    private String description;

    @NotNull(message = "Start date can't be null")
    private LocalDate startDateMillis;


    private LocalDate endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;

    @NotNull(message = "Please provide payment unit type")
    private String paymentUnit;

    private BigDecimal percentageValue; // this value is being used to update paygrade and functional amount

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PayTableDTO{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", shortName='").append(shortName).append('\'');
        sb.append(", startDateMillis=").append(startDateMillis);
        sb.append(", endDateMillis=").append(endDateMillis);
        sb.append(", paymentUnit=").append(paymentUnit);
        sb.append(", levelId=").append(levelId);
        sb.append('}');
        return sb.toString();
    }

    @AssertTrue(message = "'start date' must be less than 'end date'.")
    public boolean isValid() {
        if (!Optional.ofNullable(this.startDateMillis).isPresent()) {
            return false;
        }
        if (Optional.ofNullable(this.endDateMillis).isPresent()) {
            return endDateMillis.isAfter(startDateMillis.minusDays(1));
        }
        return true;
    }

    public String getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(String paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
