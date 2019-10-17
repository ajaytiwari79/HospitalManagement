package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.pay_out.PayOutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 19/7/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeBankAndPayoutDTO {

    private TimeBankDTO timeBank;
    private PayOutDTO payOut;
}
