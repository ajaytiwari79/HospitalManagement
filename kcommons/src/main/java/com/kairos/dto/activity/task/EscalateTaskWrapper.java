package com.kairos.dto.activity.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by Jasgeet on 6/8/17.
 */
@Getter
@Setter
public class EscalateTaskWrapper {
    private String id;
    private String name;
    private int duration;
    private Date dateFrom;
    private Date dateTo;
    private Date timeFrom;
    private Date timeTo;

    private Long citizenId;
    private String citizenName;
    private String gender;
    private Integer age;
    private Integer visitourId;

}
