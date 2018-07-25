package com.kairos.user.country.localAreaTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 11/6/18
 */

public class LocalAreaTagDTO {

    private String name;

    private String color;

    private List<LatLngDTO> paths = new ArrayList<>();


    //It tells Us When this area is Busiest in these day Time Window
    private List<DayTimeWindowDTO> dayTimeWindows = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<LatLngDTO> getPaths() {
        return paths;
    }

    public void setPaths(List<LatLngDTO> paths) {
        this.paths = paths;
    }

    public List<DayTimeWindowDTO> getDayTimeWindows() {
        return dayTimeWindows;
    }

    public void setDayTimeWindows(List<DayTimeWindowDTO> dayTimeWindows) {
        this.dayTimeWindows = dayTimeWindows;
    }
}
