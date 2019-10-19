package com.kairos.dto.user.patient;

import com.kairos.dto.user.visitation.VisitationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParagraphGroup {
    private String id;

    private VisitationType visitationType;

    private String name;


    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", visitationType = "+visitationType+", name = "+name+"]";
    }
}