package com.kairos.dto.user.visitation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.organization.Shifts;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RepetitionType {

    private List<Shifts> shifts;
    private String type;
    private int visits;


}
