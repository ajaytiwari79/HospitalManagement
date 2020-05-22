package com.kairos.dto.activity.cta;

import com.kairos.enums.cta.AccountType;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PlannedTimeWithFactor {
    private float scale;
    private boolean add;
    private boolean subtract;
    private AccountType accountType;

}
