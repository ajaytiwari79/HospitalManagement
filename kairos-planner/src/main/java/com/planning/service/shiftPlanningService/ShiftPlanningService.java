package com.planning.service.shiftPlanningService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.planning.utils.JodaTimeConverter;
import com.kairos.shiftplanning.domain.ShiftConstrutionPhase;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.StaffingLevel;
import com.kairos.shiftplanning.domain.StaffingLevelInterval;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planning.commonUtil.StaticField;
import com.planning.domain.taskPlanning.PlanningProblem;
import com.planning.enums.PlanningStatus;
import com.planning.repository.taskPlanningRepository.PlanningRepository;
import com.planning.responseDto.PlanningDto.shiftPlanningDto.RecomendationPlanningDTO;
import com.planning.responseDto.config.SolverConfigDTO;
import com.planning.service.config.DroolsConfigService;
import com.planning.service.config.SolverConfigService;
import com.planning.service.config.XmlConfigService;
import com.thoughtworks.xstream.XStream;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

@Service
public class ShiftPlanningService {

    private static Logger logger = LoggerFactory.getLogger(ShiftPlanningService.class);

    @Autowired private DroolsConfigService droolsConfigService;
    @Autowired private SolverConfigService solverConfigService;
    @Autowired private PlanningRepository planningRepository;
    @Autowired private XmlConfigService xmlConfigService;
    @Autowired private ShiftRequestPhasePlanningSolutionService shiftPlanningSolutionService;


    public RecomendationPlanningDTO submitRecomendationProblem(RecomendationPlanningDTO planningDTO){

        /*SolverConfigDTO solverConfigDTO = solverConfigService.getOneForPlanning(planningDTO.getSolverConfigId());
        boolean initialisedPlanner = initializeShiftPlanner(solverConfigDTO,planningDTO);
        if(initialisedPlanner){
            PlanningProblem planningProblem = new PlanningProblem();
            planningProblem.setSolverConfigId(solverConfigDTO.getOptaPlannerId());
            planningProblem.setUnitId(planningDTO.getUnitId());
            planningProblem.setStatus(PlanningStatus.UNSOLVED);
            // planningProblem.setProblemXml(getStringBySolutionObject(shiftPlanningSolution));
            planningProblem = (PlanningProblem) planningRepository.save(planningProblem);
            planningDTO.setOptaPlannerId(planningProblem.getId());
            planningDTO.setProblemStatus(planningProblem.getStatus().toValue());
        }
        return planningDTO;*/
        return null;
    }


    //Score Level defination is left
    private boolean initializeShiftPlanner(SolverConfigDTO solverConfigDTO,RecomendationPlanningDTO planningDTO){
        ShiftRequestPhasePlanningSolution shiftPlanningSolution = null;//shiftPlanningSolutionService.getShiftPlanningSolution(planningDTO);
        String drlFilePath = droolsConfigService.getDroolFilePath(solverConfigDTO);
        InputStream xmlInputStream = xmlConfigService.getXmlConfigStream(solverConfigDTO, StaticField.SHIFT_PLANNING_XMLPATH);
        ShiftPlanningSolver shiftPlanningSolver = new ShiftPlanningSolver(xmlInputStream,drlFilePath);
        shiftPlanningSolver.runSolverOnRequest(shiftPlanningSolution);
        return true;
    }


    public Map<String, Object> renderShiftSolutionFromXML(String xmlString) {
        ShiftRequestPhasePlanningSolution shiftPlanningSolution = getShiftPlanningSolutionByXml(xmlString);
        Map<String, Object> shiftPlanningSolutionMap = new HashMap<>();
        /*shiftPlanningSolutionMap.put("employeeList",shiftPlanningSolution.getEmployees());
        shiftPlanningSolutionMap.put("shiftAssignments",getShiftAssignments(shiftPlanningSolution.getShiftAssignments()));*/
        shiftPlanningSolutionMap.put("shiftAssignment",getShiftAssignments(shiftPlanningSolution.getShifts()));
        //shiftPlanningSolutionMap.put("staffingLevel",getStaffingLevel(shiftPlanningSolution.getStaffingLevels()));
        return shiftPlanningSolutionMap;
    }

    private List<Map> getShiftAssignments(List<ShiftRequestPhase> shiftList) {
        List<Map> shiftAssignments = new ArrayList<>();
        for (ShiftRequestPhase shift : shiftList) {
            Map<String, Object> shiftAssignmentMap = new HashMap<>();
            shiftAssignmentMap.put("day", "Tue");
            shiftAssignmentMap.put("date", shift.getStart().minusMinutes(330).toDate());
            shiftAssignmentMap.put("dummy", false);
            shiftAssignmentMap.put("currentPhase", "Puzzle");
            shiftAssignmentMap.put("editable", true);
            shiftAssignmentMap.put("id", shift.getId());
            shiftAssignmentMap.put("startDate", shift.getStart().minusMinutes(330).toDate());
            shiftAssignmentMap.put("created", false);
            shiftAssignmentMap.put("endDate", shift.getEnd().minusMinutes(330).toDate());
            shiftAssignmentMap.put("formattedActivityName", "Morning Day");
            shiftAssignmentMap.put("formattedStartDate", "");
            shiftAssignmentMap.put("formattedEndDate", "");
            shiftAssignmentMap.put("bId", "");
            shiftAssignmentMap.put("pId", "");
            shiftAssignmentMap.put("bonusTimeBank", "");
            shiftAssignmentMap.put("amount", "");
            shiftAssignmentMap.put("overStaff", "");
            shiftAssignmentMap.put("underStaff", "");
            shiftAssignmentMap.put("probability", "");
            shiftAssignmentMap.put("accumulatedTimeBank", "");
            shiftAssignmentMap.put("remarks", "");
            shiftAssignments.add(shiftAssignmentMap);
        }
        //writeObjectToJson(shiftAssignments,"/media/pradeep/bak/shiftAss.json");
        return shiftAssignments;
    }

    private Map<String, Object> getStaffingLevel(StaffingLevel staffingLevels) {
        Map<String, Object> staffingLevelMap = new HashMap<>();
        //staffingLevelMap.put("date",staffingLevel.getDate().toDate());
        staffingLevelMap.put("phaseId", 1);
        staffingLevelMap.put("currentDay", new Date().getTime());
        staffingLevelMap.put("typeOfDay", "abc");
        staffingLevelMap.put("weekCount", 20);
        //staffingLevelMap.put("staffingLevelSetting", getStaffingLevelSetting(staffingLevel));
        //staffingLevelMap.put("staffingLevelInterval", getStaffingLevelIntervals(staffingLevel.getIntervals()));
        //writeObjectToJson(staffingLevelMap,"/media/pradeep/bak/staffingLevel.json");
        return staffingLevelMap;
    }

    private void writeObjectToJson(Object object,String filePath){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String staffinglevelJson = objectMapper.writeValueAsString(object);
            PrintWriter out = null;
            out = new PrintWriter(new File(filePath));
            out.write(staffinglevelJson);
            out.close();
            logger.info("file Complete");
        } catch (JsonProcessingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*private Map<String, Object> getStaffingLevelSetting(StaffingLevel staffingLevel) {
        Map<String, Object> staffingLevelSettingMap = new HashMap<>();
        staffingLevelSettingMap.put("detailLevelMinutes", 15);
        staffingLevelSettingMap.put("duration", getStaffingLevelDuration(staffingLevel.getDate(), staffingLevel.getDate()));
        return staffingLevelSettingMap;
    }*/

    /*private Map<String,Object> getStaffingLevel(StaffingLevel staffingLevel){
        Map<String,Object> staffingLevelMap = new HashMap<>();
        staffingLevelMap.put("date",staffingLevel.getDate().toDate());
        staffingLevelMap.put("id",staffingLevel.getId());
        staffingLevelMap.put("intervals",getStaffingLevelIntervals(staffingLevel.getIntervals()));
        return staffingLevelMap;
    }*/

    private List<Map> getStaffingLevelIntervals(List<StaffingLevelInterval> staffingLevelIntervalList) {
        List<Map> staffinglevelIntervals = new ArrayList<>();
        for (StaffingLevelInterval staffinglevelInterval : staffingLevelIntervalList) {
            Map<String, Object> staffingLevelMap = new HashMap<>();
            staffingLevelMap.put("sequence", staffinglevelInterval.getMaximumStaffRequired());
            Random random = new Random();
            staffingLevelMap.put("minNoOfStaff", staffinglevelInterval.getMinimumStaffRequired());
            staffingLevelMap.put("maxNoOfStaff", staffinglevelInterval.getMaximumStaffRequired());
            staffingLevelMap.put("availableNoOfStaff", 0);//3+random.nextInt(20));
            staffingLevelMap.put("staffingLevelActivities", Arrays.asList());
            //staffingLevelMap.put("staffingLevelSkills", staffinglevelInterval.getSkillSet());
            staffingLevelMap.put("staffingLevelDuration", getStaffingLevelDuration(staffinglevelInterval.getStart(), staffinglevelInterval.getEnd()));
            staffinglevelIntervals.add(staffingLevelMap);
        }
        return staffinglevelIntervals;
    }

    private Map<String, Object> getStaffingLevelDuration(DateTime startTime, DateTime endTime) {
        Map<String, Object> staffingLevelDurationMap = new HashMap<>();
        Map<String, Object> fromMap = new HashMap<>();
        Map<String, Object> toMap = new HashMap<>();
        fromMap.put("hour", startTime.getHourOfDay());
        fromMap.put("minute", startTime.getMinuteOfHour());
        fromMap.put("second", startTime.getSecondOfMinute());
        fromMap.put("nano", startTime.getMillisOfSecond());
        toMap.put("hour", endTime.getHourOfDay());
        toMap.put("minute", endTime.getMinuteOfHour());
        toMap.put("second", endTime.getSecondOfMinute());
        toMap.put("nano", endTime.getMillisOfSecond());
        staffingLevelDurationMap.put("from", fromMap);
        staffingLevelDurationMap.put("to", toMap);
        return staffingLevelDurationMap;
    }

    public ShiftRequestPhasePlanningSolution getShiftPlanningSolutionByXml(String xml) {
        XStream xstream = new XStream();
        xstream.processAnnotations(com.kairos.shiftplanning.domain.Employee.class);
        xstream.processAnnotations(ShiftConstrutionPhase.class);
        xstream.processAnnotations(StaffingLevel.class);
        xstream.setMode(XStream.ID_REFERENCES);
        //xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
        xstream.ignoreUnknownElements();
        xstream.registerConverter(new JodaTimeConverter());
        //xstream.registerConverter(new JodaTimeConverterNoTZ());
        //xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
        ShiftRequestPhasePlanningSolution shiftPlanningSolution = (ShiftRequestPhasePlanningSolution) xstream.fromXML(this.getClass().getClassLoader().getResourceAsStream("data/shift_solution.xml"));
        return shiftPlanningSolution;
    }
}
