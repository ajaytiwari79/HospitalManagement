package com.kairos.planner.vrp.taskplanning.model;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class LocationInfo {
    private int installationNo;
    private int distanceBycar;
    private int timeByCar;


    public LocationInfo() {
    }

    public LocationInfo(int installationNo, int distanceBycar, int timeByCar) {
        this.installationNo = installationNo;
        this.distanceBycar = distanceBycar;
        this.timeByCar = timeByCar;
    }

    public int getInstallationNo() {
        return installationNo;
    }

    public void setInstallationNo(int installationNo) {
        this.installationNo = installationNo;
    }

    public int getDistanceBycar() {
        return distanceBycar;
    }

    public void setDistanceBycar(int distanceBycar) {
        this.distanceBycar = distanceBycar;
    }

    public int getTimeByCar() {
        return timeByCar;
    }

    public void setTimeByCar(int timeByCar) {
        this.timeByCar = timeByCar;
    }
}
