package com.kairos.dto.user.staff;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 19/4/17.
 */
@Getter
@Setter
public class GeoCoordinates {
    private String present;

    private String y;

    private String x;


    @Override
    public String toString()
    {
        return "ClassPojo [present = "+present+", y = "+y+", x = "+x+"]";
    }
}
