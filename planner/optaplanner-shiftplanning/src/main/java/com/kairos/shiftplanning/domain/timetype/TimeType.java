package com.kairos.shiftplanning.domain.timetype;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TimeType")
public class TimeType {


    private String id;
    private String name;

    public TimeType() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public TimeType(String id, String name) {
        this.id = id;
        this.name = name;
    }


}
