package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by pavan on 26/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class AgeRangeDTO implements Comparable<AgeRangeDTO>,Serializable{
    private Long id;
    private int from;
    private Integer to;
    private Integer leavesAllowed;

    @Override
    public int compareTo(AgeRangeDTO o) {
        return this.from-o.from;
    }

}
