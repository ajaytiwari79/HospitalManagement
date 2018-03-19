package com.kairos.persistence.model.user.pay_table;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by prabjot on 2/1/18.
 */
public class PayLevelUpdateDTO {

    private String name;
    @NotNull(message = "Start date can't be null")
    @FutureDate
    private Date startDate;
    private Date endDate;

    public PayLevelUpdateDTO(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
