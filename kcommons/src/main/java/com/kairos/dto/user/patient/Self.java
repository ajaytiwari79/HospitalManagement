package com.kairos.dto.user.patient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Self {
    private String href;

    @Override
    public String toString()
    {
        return "ClassPojo [href = "+href+"]";
    }
}