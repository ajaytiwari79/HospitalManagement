
package com.kairos.planner.vrp.taskplanning.routes;

import java.util.List;

public class Guidance {

    private List<Instruction> instructions = null;
    private List<InstructionGroup> instructionGroups = null;

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public List<InstructionGroup> getInstructionGroups() {
        return instructionGroups;
    }

    public void setInstructionGroups(List<InstructionGroup> instructionGroups) {
        this.instructionGroups = instructionGroups;
    }

}
