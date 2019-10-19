package com.kairos.dto.user.staff;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 19/4/17.
 */
@Getter
@Setter
public class AvailableCountries {
    private String href;

    @Override
    public String toString()
    {
        return "ClassPojo [href = "+href+"]";
    }

}
