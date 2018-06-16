Optaplanner Shift-Planning module 

Prerequisite before  start optaplanner:-

We need input Problem data:-

Here we use "ShiftPlanningGenerator" to generate input problem data.
Path:- (/kairos-user/kairos-planner/optaplanner-shiftplanning/src/main/java/com/kairos/shiftplanning/executioner/ShiftPlanningGenerator.java)

Then generator will save this data in "shift_problem.xml"  which will be used as Problem by optaplanner.
Path:-   (/kairos-user/kairos-planner/optaplanner-shiftplanning/src/main/resources/data/shift_problem.xml)

To start optaplanner:-

We need to start test case given below:-

@Test
public void test() {
    //RequestedTask requestedTask =  new RequestedTask();
    //requestedTask.loadXMLFromDB();
    new ShiftPlanningSolver().runSolver();

}
Path:-(/kairos-user/kairos-planner/optaplanner-shiftplanning/src/test/java/com/kairos/shiftplanning/executioner/ShiftConstrutionPhasePlanningSolverTest.java)