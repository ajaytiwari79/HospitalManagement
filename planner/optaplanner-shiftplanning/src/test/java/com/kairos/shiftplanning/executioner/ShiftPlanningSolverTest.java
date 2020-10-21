
package com.kairos.shiftplanning.executioner;

//@PropertySource("/media/pradeep/bak/multiOpta/task-shiftplanning/src/main/resources/taskplanner.properties")
public class ShiftPlanningSolverTest {

    public static final String FIX_ACTIVITY_SHOULD_NOT_CHANGE = "Fix Activity should not change";
    public static final String IF_THIS_ACTIVITY_IS_USED_ON_A_TUESDAY = "If this activity is used on a Tuesday";
    public static final String MAX_SHIFT_OF_STAFF = "Max Shift of Staff";
    public static final String PRESENCE_AND_ABSENCE_SHOULD_NOT_BE_AT_SAME_TIME = "Presence And Absence should not be at same time";
    public static final String ACTIVITY_REQUIRED_TAG = "Activity required Tag";
    public static final String PREFER_PERMAMENT_EMPLOYEE = "Prefer Permament Employee";
    public static final String MINIMIZE_NO_OF_SHIFT_ON_WEEKEND = "Minimize No of Shift on weekend";
    public static final String MAX_NUMBER_OF_ALLOCATIONS_PR_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF = "Max number of allocations pr. shift for this activity per staff";
    public static final String SHORTEST_DURATION_FOR_THIS_ACTIVITY_RELATIVE_TO_SHIFT_LENGTH = "Shortest duration for this activity, relative to shift length";
    public static final String ACTIVITY_MUST_CONTINOUS_FOR_NUMBER_OF_HOURS_RELATIVE_TO_SHIFT_LENGTH = "Activity must continous for number of Hours, relative to shift length";

   /* @Test
    //@Ignore
    public void test() {
        SolverConfigDTO solverConfigDTO = getSolverConfigDTO();
        //new ShiftPlanningSolver(solverConfigDTO).runSolver();
        Assert.assertEquals(0,0);
    }

    @Test
    @Ignore
    public void buildBenchmarker() {
        new ShiftPlanningSolver(solverConfigDTO, droolFilePath, configurationFile).buildBenchmarker();
    }

    public void runBenchmarker() {
        new ShiftPlanningSolver(solverConfigDTO, droolFilePath, configurationFile).runBenchmarker();
    }*/


/*
    public SolverConfigDTO getSolverConfigDTO(){
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO(ACTIVITY_MUST_CONTINOUS_FOR_NUMBER_OF_HOURS_RELATIVE_TO_SHIFT_LENGTH, ACTIVITY_MUST_CONTINOUS_FOR_NUMBER_OF_HOURS_RELATIVE_TO_SHIFT_LENGTH, ConstraintType.ACTIVITY, ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS, ScoreLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO(SHORTEST_DURATION_FOR_THIS_ACTIVITY_RELATIVE_TO_SHIFT_LENGTH,SHORTEST_DURATION_FOR_THIS_ACTIVITY_RELATIVE_TO_SHIFT_LENGTH, ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO(MAX_NUMBER_OF_ALLOCATIONS_PR_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, MAX_NUMBER_OF_ALLOCATIONS_PR_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,  ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(MINIMIZE_NO_OF_SHIFT_ON_WEEKEND, MINIMIZE_NO_OF_SHIFT_ON_WEEKEND,  ConstraintType.ACTIVITY, MINIMIZE_SHIFT_ON_WEEKENDS, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(PREFER_PERMAMENT_EMPLOYEE, PREFER_PERMAMENT_EMPLOYEE,ConstraintType.ACTIVITY,PREFER_PERMANENT_EMPLOYEE, ScoreLevel.HARD,2,5l));
        constraintDTOS.add(new ConstraintDTO(ACTIVITY_REQUIRED_TAG, ACTIVITY_REQUIRED_TAG,  ConstraintType.ACTIVITY, ConstraintSubType.ACTIVITY_REQUIRED_TAG, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(PRESENCE_AND_ABSENCE_SHOULD_NOT_BE_AT_SAME_TIME, PRESENCE_AND_ABSENCE_SHOULD_NOT_BE_AT_SAME_TIME,  ConstraintType.UNIT, PRESENCE_AND_ABSENCE_SAME_TIME, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(MAX_SHIFT_OF_STAFF, MAX_SHIFT_OF_STAFF,  ConstraintType.ACTIVITY, ConstraintSubType.MAX_SHIFT_OF_STAFF, ScoreLevel.HARD, 5,5l));
        constraintDTOS.add(new ConstraintDTO(AVERAGE_SHEDULED_TIME.name(), AVERAGE_SHEDULED_TIME.name(),  ConstraintType.WTA, AVERAGE_SHEDULED_TIME, ScoreLevel.MEDIUM, 5,5l));
*/
/*
        constraintDTOS.add(new ConstraintDTO(FIX_ACTIVITY_SHOULD_NOT_CHANGE, FIX_ACTIVITY_SHOULD_NOT_CHANGE,  ConstraintType.ACTIVITY, ConstraintSubType.FIX_ACTIVITY_SHOULD_NOT_CHANGE, ConstraintLevel.HARD, 5,5l));
*//*

        constraintDTOS.add(new ConstraintDTO(IF_THIS_ACTIVITY_IS_USED_ON_A_TUESDAY, IF_THIS_ACTIVITY_IS_USED_ON_A_TUESDAY,  ConstraintType.ACTIVITY, ACTIVITY_VALID_DAYTYPE, ScoreLevel.SOFT, 4,5l));
        return new SolverConfigDTO(constraintDTOS);
    }

*/


}
