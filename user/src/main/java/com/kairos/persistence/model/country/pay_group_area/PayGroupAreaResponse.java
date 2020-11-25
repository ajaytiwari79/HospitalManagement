package com.kairos.persistence.model.country.pay_group_area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.LevelDTO;
import com.kairos.persistence.model.user.region.MunicipalityQueryResults;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 12/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
public class PayGroupAreaResponse {
    private List<LevelDTO> organizationLevels = new ArrayList<>();
    private List<MunicipalityQueryResults> municipalities = new ArrayList<>();

    public PayGroupAreaResponse() {
        // default constructor
    }

}
