package com.kairos.persistence.model.user.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_WINDOW;
import static com.kairos.persistence.model.constants.RelationshipConstants.LAT_LNG;

/**
 * Created by neuron on 12/6/17.
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalAreaTag extends UserBaseEntity {

    @NotNull
    private String name;

    private String color;

    @Relationship(type = LAT_LNG)
    private List<LatLng> paths = new ArrayList<>();


    //It tells Us When this area is Busiest in these day Time Window
    @Relationship(type = HAS_TIME_WINDOW)
    private List<DayTimeWindow> dayTimeWindows;


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    private boolean isDeleted;

    public LocalAreaTag(){

    }

    public List<DayTimeWindow> getDayTimeWindows() {
        return dayTimeWindows;
    }

    public void setDayTimeWindows(List<DayTimeWindow> dayTimeWindows) {
        this.dayTimeWindows = dayTimeWindows;
    }

    public LocalAreaTag(String name, List<LatLng> paths) {
        this.name = name;
        this.paths = paths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LatLng> getPaths() {
        return paths;
    }

    public void setPaths(List<LatLng> paths) {
        this.paths = paths;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String toString(){
        return this.name + "-with coordinates-"+this.paths;
    }
}
