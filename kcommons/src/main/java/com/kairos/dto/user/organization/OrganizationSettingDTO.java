package com.kairos.dto.user.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 12/6/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSettingDTO {

    /*Walking Time setting*/
    private int walkingMeter;
    private int walkingMinutes;

}
