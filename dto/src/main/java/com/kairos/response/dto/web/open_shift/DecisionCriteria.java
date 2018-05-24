package com.kairos.response.dto.web.open_shift;

public class DecisionCriteria {
    private boolean lowestTotalCost;
    private boolean lowestTimeBank;
    private boolean lowestPlannedHour;  // Per week
    private boolean moreRestingTime;   //Need to know in detail
    private boolean highestPoints;
    private boolean mostExperienceInUnit;
    private boolean isMostExperienceInActivity;

    public DecisionCriteria() {
        //Default Constructor
    }

    public boolean isLowestTotalCost() {
        return lowestTotalCost;
    }

    public void setLowestTotalCost(boolean lowestTotalCost) {
        this.lowestTotalCost = lowestTotalCost;
    }

    public boolean isLowestTimeBank() {
        return lowestTimeBank;
    }

    public void setLowestTimeBank(boolean lowestTimeBank) {
        this.lowestTimeBank = lowestTimeBank;
    }

    public boolean isLowestPlannedHour() {
        return lowestPlannedHour;
    }

    public void setLowestPlannedHour(boolean lowestPlannedHour) {
        this.lowestPlannedHour = lowestPlannedHour;
    }

    public boolean isMoreRestingTime() {
        return moreRestingTime;
    }

    public void setMoreRestingTime(boolean moreRestingTime) {
        this.moreRestingTime = moreRestingTime;
    }

    public boolean isHighestPoints() {
        return highestPoints;
    }

    public void setHighestPoints(boolean highestPoints) {
        this.highestPoints = highestPoints;
    }

    public boolean isMostExperienceInUnit() {
        return mostExperienceInUnit;
    }

    public void setMostExperienceInUnit(boolean mostExperienceInUnit) {
        this.mostExperienceInUnit = mostExperienceInUnit;
    }

    public boolean isMostExperienceInActivity() {
        return isMostExperienceInActivity;
    }

    public void setMostExperienceInActivity(boolean mostExperienceInActivity) {
        isMostExperienceInActivity = mostExperienceInActivity;
    }
}
