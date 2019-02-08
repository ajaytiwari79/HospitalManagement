package com.kairos.shiftplanning.executioner;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintLevel;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.shiftplanning.domain.Shift;
import com.kairos.shiftplanning.dto.ShiftDTO;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.*;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kairos.enums.constraint.ConstraintSubType.*;


//@PropertySource("/media/pradeep/bak/multiOpta/task-shiftplanning/src/main/resources/taskplanner.properties")
public class ShiftConstrutionPhasePlanningSolverTest {
    @Test
    @Ignore
    public void test() {
        //RequestedTask requestedTask =  new RequestedTask();
        //requestedTask.loadXMLFromDB();
        SolverConfigDTO solverConfigDTO = getSolverConfigDTO();
        new ShiftPlanningSolver(solverConfigDTO).runSolver();

    }

    @Test
    @Ignore
    public void buildBenchmarker() {
        new ShiftPlanningSolver().buildBenchmarker();
    }
    @Test
    @Ignore
    public void runBenchmarker() {
        new ShiftPlanningSolver().runBenchmarker();
    }


    public SolverConfigDTO getSolverConfigDTO(){
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        /*constraintDTOS.add(new ConstraintDTO(null, DURATION_BETWEEN_SHIFTS.toString(), commonDescription+"ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS", ConstraintType.ACTIVITY, ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS, ConstraintLevel.HARD, penaltyHard, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));*/
        constraintDTOS.add(new ConstraintDTO("Shortest duration for this activity, relative to shift length","Shortest duration for this activity, relative to shift length", ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ConstraintLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO("Max number of allocations pr. shift for this activity per staff", "Max number of allocations pr. shift for this activity per staff",  ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ConstraintLevel.HARD, 5,5l));
   //     constraintDTOS.add(new ConstraintDTO("If this activity is used on a Tuesday", "If this activity is used on a Tuesday",  ConstraintType.ACTIVITY, ACTIVITY_VALID_DAYTYPE, ConstraintLevel.SOFT, 4,5l));
        return new SolverConfigDTO(constraintDTOS);
    }

	/*@Test
    @Ignore
	public  void testKieServiceApi() {
		KieServicesConfiguration kieServicesConfiguration = new KieServicesConfigurationImpl("http://localhost:8080/kie-server/services/rest/server", "kieserver", "kieserver", 100000000000000l);
		KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(kieServicesConfiguration);
	}*/


	@Test
    @Ignore
    public void sendDataToKairos(){
        ShiftRequestPhasePlanningSolution solution = new ShiftPlanningGenerator().loadUnsolvedSolutionFromXML("/home/pradeep/Downloads/shift_solution (1).xml");
        new ShiftPlanningSolver().sendSolutionToKairos(solution);
    }

    public List<ShiftDTO> getShiftDtos() {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        IntStream.range(0, 2).forEachOrdered(i -> {
            ShiftDTO shiftDTO = new ShiftDTO("" + 1, new DateTime().withTimeAtStartOfDay().plusHours(1).toDate(), new DateTime().withTimeAtStartOfDay().plusHours(12).toDate(), new BigInteger("320"));
            shiftDTO.setSubShifts(getSubShiftDtos());
            shiftDTOS.add(shiftDTO);
        });
        return shiftDTOS;
    }

    public List<ShiftDTO> getSubShiftDtos() {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();

        ShiftDTO shiftDTO = new ShiftDTO("" + 1, new DateTime().withTimeAtStartOfDay().plusHours(1).toDate(), new DateTime().withTimeAtStartOfDay().plusHours(2).toDate(), new BigInteger("320"));
        shiftDTOS.add(shiftDTO);

        shiftDTO = new ShiftDTO("" + 1, new DateTime().withTimeAtStartOfDay().plusHours(2).toDate(), new DateTime().withTimeAtStartOfDay().plusMinutes(30).toDate(), new BigInteger("375"));
        shiftDTOS.add(shiftDTO);

        shiftDTO = new ShiftDTO("" + 1, new DateTime().withTimeAtStartOfDay().plusHours(2).plusMinutes(30).toDate(), new DateTime().withTimeAtStartOfDay().plusHours(4).toDate(), new BigInteger("327"));
        shiftDTOS.add(shiftDTO);

        shiftDTO = new ShiftDTO("" + 1, new DateTime().withTimeAtStartOfDay().plusHours(4).toDate(), new DateTime().withTimeAtStartOfDay().plusHours(6).toDate(), new BigInteger("329"));
        shiftDTOS.add(shiftDTO);
        return shiftDTOS;
    }

    @Test public void getQuaterOfMonth(){
        Interval interval = new Interval(new DateTime(), new DateTime().withDayOfMonth(DateTimeConstants.MARCH).property(DateTimeFieldType.dayOfMonth()).withMaximumValue());
        System.out.print(interval);
    }



    @Test
    public void getCostOfEmpByShift() {
        double totalCost = 0;
        DateTime startTime = new DateTime().withTimeAtStartOfDay().plusHours(22);
        DateTime endTime = startTime.plusHours(4).plusMinutes(45);
        double baseCost = 10;
        int i = 0;
        double[][] costTimeAgreement = intializeCTA();
        while (startTime.getHourOfDay() != endTime.getHourOfDay() && startTime.isBefore(endTime)) {
            totalCost = totalCost + (baseCost * costTimeAgreement[startTime.getDayOfWeek() - 1][startTime.getHourOfDay()]);
            startTime = startTime.plusHours(1);

        }
        double minuteCost;
        if (startTime.getMinuteOfHour() > 0) {
            minuteCost = (60d - startTime.getMinuteOfHour()) / 60d;
            totalCost = totalCost - (baseCost * costTimeAgreement[startTime.minusHours(endTime.getHourOfDay()).getDayOfWeek() - 1][startTime.minusHours(endTime.getHourOfDay()).getHourOfDay()]);
            totalCost = totalCost + (baseCost * costTimeAgreement[startTime.getDayOfWeek() - 1][startTime.getHourOfDay()] * minuteCost);
        }
        if (endTime.getMinuteOfHour() > 0) {
            minuteCost = endTime.getMinuteOfHour() / 60d;
            totalCost = totalCost + (baseCost * costTimeAgreement[startTime.getDayOfWeek() - 1][startTime.getHourOfDay()] * minuteCost);
        }
        System.out.println(totalCost);
    }

    private double[][] intializeCTA() {
        //0-1,1-2,2-3,3-4,4-5,5-6,6-7,7-8,8-9,9-10,10-11,11-12,12-13,13-14,14-15,15-16,16-17,17-18,18-19,19-20,20-21,21-22,22-23,23-0
        double cta[][] =
                {{1.4d, 1.4d, 1.4d, 1.2d, 1, 1, 1, 1, 1, 1, 1.1d, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2},//Monday
                        {1.4d, 1.4d, 1.4d, 1.2d, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2},//Tuesday
                        {1.2d, 1.2d, 1.3d, 1.6d, 1.2d, 1.2d, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2},//Wednesday
                        {1.2d, 1.2d, 1.2d, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2},//Thursday
                        {1.3d, 1.3d, 1.3d, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2},//Friday
                        {1.3d, 1.3d, 1.4d, 1.2d, 1, 1, 1, 1, 1, 1, 1.2d, 1.2d, 1.2d, 1.2d, 1.2d, 2, 2, 2, 2, 2, 2, 2, 3, 3},//Satur
                        {1.3d, 1.4d, 1.4d, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3}};//Sun
        return cta;
    }


}
