package com.kairos.util.external_plateform_shift;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by oodles on 25/1/17.
 */
public class ArrayOfChildShift {
    @JacksonXmlProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ChildShift> ChildShift;

    public List getChildShift ()
    {
        return ChildShift;
    }

    public void setChildShift (List ChildShift)
    {
        this.ChildShift = ChildShift;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ChildShift = "+ChildShift+"]";
    }
}
