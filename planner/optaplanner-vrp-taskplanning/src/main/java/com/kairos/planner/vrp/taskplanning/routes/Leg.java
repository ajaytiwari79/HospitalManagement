
package com.kairos.planner.vrp.taskplanning.routes;

import java.util.List;

public class Leg {

    private Summary summary;
    private List<Point> points = null;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

}
