package com.kairos.dto.activity.phase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by vipul on 26/9/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class PhaseWeeklyDTO {
    private BigInteger id;

    private String name;
    private String description;
    private int duration;
    private int sequence;
    private Long organizationId;
    private int weekCount;
    private int year;


    public PhaseWeeklyDTO(BigInteger id, String name, String description, int duration, int sequence,  Long organizationId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.sequence = sequence;
        this.organizationId = organizationId;
    }
}
