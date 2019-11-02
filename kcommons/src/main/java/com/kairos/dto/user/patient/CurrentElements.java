package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.client.CitizenSupplier;
import com.kairos.dto.user.visitation.RepetitionNext;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by oodles on 26/4/17.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentElements {

    private Paragraph paragraph;

    private String elementGrouping;

    private Integer priority;

    private String type;

    @JsonIgnoreProperties
    private String pattern;

    @JsonIgnoreProperties
    private String count;

    @JsonIgnoreProperties
    private RepetitionNext next;

    @JsonIgnoreProperties
    private Date date;

    @JsonIgnoreProperties
    private Integer number;

    @JsonIgnoreProperties
    private CitizenSupplier supplier;

    @JsonIgnoreProperties
    private String text;


    @Override
    public String toString()
    {
        return "ClassPojo [paragraph = "+paragraph+", elementGrouping = "+elementGrouping+", priority = "+priority+", type = "+type+"]";
    }
}