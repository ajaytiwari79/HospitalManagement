package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffInterest {
    private Date date;
    private AccountType accountType;


    static enum AccountType{
        TIME_BANK,PAY_OUT,BOTH
    }
}
