package com.kairos.dto.activity.presence_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by vipul on 7/12/17.
 * updation by Mohit Shakya on Jun/05/2018
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceTypeWithTimeTypeDTO {
    private List<PresenceTypeDTO> presenceTypes;
    private Long countryId;

}
