package com.kairos.response.dto.web.open_shift;

public class ShiftAssignmentCriteria {
    private boolean firstPick;
    private boolean pickableFibonacci;
    private boolean showInterestFibonacci;
    private boolean showInterestPlannerWillChoose;

    public boolean isFirstPick() {
        return firstPick;
    }

    public void setFirstPick(boolean firstPick) {
        this.firstPick = firstPick;
    }

    public boolean isPickableFibonacci() {
        return pickableFibonacci;
    }

    public void setPickableFibonacci(boolean pickableFibonacci) {
        this.pickableFibonacci = pickableFibonacci;
    }

    public boolean isShowInterestFibonacci() {
        return showInterestFibonacci;
    }

    public void setShowInterestFibonacci(boolean showInterestFibonacci) {
        this.showInterestFibonacci = showInterestFibonacci;
    }

    public boolean isShowInterestPlannerWillChoose() {
        return showInterestPlannerWillChoose;
    }

    public void setShowInterestPlannerWillChoose(boolean showInterestPlannerWillChoose) {
        this.showInterestPlannerWillChoose = showInterestPlannerWillChoose;
    }

}
