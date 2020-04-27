package com.kairos.shiftplanning.domain.staff;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Function {
    private Long id;
    private BigDecimal value;
}
