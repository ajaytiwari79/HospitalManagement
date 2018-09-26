package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.pay_out.PayOutDTO;

/**
 * @author pradeep
 * @date - 19/7/18
 */

public class TimeBankAndPayoutDTO {

    private TimeBankDTO timeBank;
    private PayOutDTO payOut;


    public TimeBankAndPayoutDTO() {
    }

    public TimeBankAndPayoutDTO(TimeBankDTO timeBank, PayOutDTO payOut) {
        this.timeBank = timeBank;
        this.payOut = payOut;
    }

    public TimeBankDTO getTimeBank() {
        return timeBank;
    }

    public void setTimeBank(TimeBankDTO timeBank) {
        this.timeBank = timeBank;
    }

    public PayOutDTO getPayOut() {
        return payOut;
    }

    public void setPayOut(PayOutDTO payOut) {
        this.payOut = payOut;
    }
}
