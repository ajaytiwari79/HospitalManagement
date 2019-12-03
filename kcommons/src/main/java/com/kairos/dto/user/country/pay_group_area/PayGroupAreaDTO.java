package com.kairos.dto.user.country.pay_group_area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.pay_table.DateRange;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Created by prabjot on 21/12/17.
 *
 * @MOdified by vipul for additional property
 */

@DateRange
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PayGroupAreaDTO {

    private Long id;
    private Long payGroupAreaId;
    @NotNull(message = "Name can not be null")
    private String name;
    private String description;

    @NotNull(message = "Please select municipality")
    private Long municipalityId;

    @NotNull(message = "Start date can't be null")
    //@DateLong

    private Date startDateMillis;


    //@DateLong
    private Date endDateMillis;

    @NotNull(message = "Level can not be null")
    private Long levelId;

}
