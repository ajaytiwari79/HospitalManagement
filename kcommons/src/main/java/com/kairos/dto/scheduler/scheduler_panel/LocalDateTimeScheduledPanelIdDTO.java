package com.kairos.dto.scheduler.scheduler_panel;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalDateTimeScheduledPanelIdDTO {
    private LocalDateTime dateTime;
    private BigInteger id;
}

