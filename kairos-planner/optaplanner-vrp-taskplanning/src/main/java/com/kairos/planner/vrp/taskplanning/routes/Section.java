
package com.kairos.planner.vrp.taskplanning.routes;


public class Section {

    private Integer startPointIndex;
    private Integer endPointIndex;
    private String sectionType;
    private String travelMode;

    public Integer getStartPointIndex() {
        return startPointIndex;
    }

    public void setStartPointIndex(Integer startPointIndex) {
        this.startPointIndex = startPointIndex;
    }

    public Integer getEndPointIndex() {
        return endPointIndex;
    }

    public void setEndPointIndex(Integer endPointIndex) {
        this.endPointIndex = endPointIndex;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

}
