
package com.kairos.shiftplanning.executioner;

import com.kairos.dto.user.country.system_setting.SystemLanguageDTO;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.user_context.UserContext;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.generator.StaffingLevelGenerator;
import com.kairos.shiftplanningNewVersion.solver.StaffingLevelSolver;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    static{
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        CurrentUserDetails currentUserDetails  = new CurrentUserDetails();
        UserContext.setUserDetails(currentUserDetails);
    }

    @Test
    public void solveStaffingLevelProblem(){
        try {
            new StaffingLevelSolver().run();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void mergedInterval(){
        Map<LocalDate,Map<BigInteger, List<ALI>>> localDateMapMap = new HashMap<>();
        Map<BigInteger, List<ALI>> bigIntegerTreeSetMap = new HashMap<>();
        List<ALI> aliTreeSet = new ArrayList<>();
        ZonedDateTime zonedDateTime = ZonedDateTime.now().with(LocalTime.MIN);
        aliTreeSet.add(new ALI(zonedDateTime,false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(15),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(30),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(45),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(60),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(90),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(105),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(120),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(135),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(150),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(165),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(180),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(195),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(210),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(225),false,15));
        bigIntegerTreeSetMap.put(BigInteger.valueOf(6),aliTreeSet);
        aliTreeSet = new ArrayList<>();
        aliTreeSet.add(new ALI(zonedDateTime,false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(15),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(30),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(45),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(75),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(105),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(120),false,15));
        aliTreeSet.add(new ALI(zonedDateTime.plusMinutes(135),false,15));
        bigIntegerTreeSetMap.put(BigInteger.valueOf(7),aliTreeSet);
        localDateMapMap.put(LocalDate.now(),bigIntegerTreeSetMap);
        Object[] objects = new StaffingLevelGenerator().getMergedALI(localDateMapMap);
        List<ALI> alis = (List<ALI>)objects[0];
        Assert.assertEquals(alis.size(),8);
    }

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
