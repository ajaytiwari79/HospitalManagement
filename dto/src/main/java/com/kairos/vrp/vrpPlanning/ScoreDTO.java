package com.kairos.vrp.vrpPlanning;

/**
 * @author pradeep
 * @date - 9/7/18
 */

public class ScoreDTO {

    private int hard;
    private int medium;
    private int soft;


    public ScoreDTO() {
    }

    public ScoreDTO(int hard, int medium, int soft) {
        this.hard = hard;
        this.medium = medium;
        this.soft = soft;
    }

    public int getHard() {
        return hard;
    }

    public void setHard(int hard) {
        this.hard = hard;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getSoft() {
        return soft;
    }

    public void setSoft(int soft) {
        this.soft = soft;
    }
}
