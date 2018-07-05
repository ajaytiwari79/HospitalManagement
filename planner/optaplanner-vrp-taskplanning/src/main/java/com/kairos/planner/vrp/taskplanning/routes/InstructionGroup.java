
package com.kairos.planner.vrp.taskplanning.routes;


public class InstructionGroup {

    private Integer firstInstructionIndex;
    private Integer lastInstructionIndex;
    private Integer groupLengthInMeters;

    public Integer getFirstInstructionIndex() {
        return firstInstructionIndex;
    }

    public void setFirstInstructionIndex(Integer firstInstructionIndex) {
        this.firstInstructionIndex = firstInstructionIndex;
    }

    public Integer getLastInstructionIndex() {
        return lastInstructionIndex;
    }

    public void setLastInstructionIndex(Integer lastInstructionIndex) {
        this.lastInstructionIndex = lastInstructionIndex;
    }

    public Integer getGroupLengthInMeters() {
        return groupLengthInMeters;
    }

    public void setGroupLengthInMeters(Integer groupLengthInMeters) {
        this.groupLengthInMeters = groupLengthInMeters;
    }

}
