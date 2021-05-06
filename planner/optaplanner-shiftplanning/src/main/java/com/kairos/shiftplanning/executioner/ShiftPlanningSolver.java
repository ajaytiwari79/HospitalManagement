package com.kairos.shiftplanning.executioner;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staffing_level.SkillLineInterval;
import com.kairos.shiftplanning.dto.ShiftDTO;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.solution.ShiftPlanningSolution;
import com.kairos.shiftplanning.utils.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import io.quarkus.runtime.QuarkusApplication;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.enums.constraint.ConstraintSubType.*;
import static org.optaplanner.core.config.solver.SolverConfig.createFromXmlFile;


@NoArgsConstructor
public class ShiftPlanningSolver implements QuarkusApplication {
    public static final String BASE_SRC = "src/main/resources/data/";
    public static final String STR = "\n------------------------\n";
    public static final String INFO = "info {}";
    public static final String SOLVER_XML = "com/kairos/shiftplanning/configuration/ShiftPlanning_Request_ActivityLine.solver.xml";
    public static final String ERROR = "Error {}";
    public static final String CONFIG_BREAKS = "com/kairos/shiftplanning/configuration/BreakAndIndirectActivityPlanning.solver.xml";
    public static final String CONFIG_WITH_WTA = "com/kairos/shiftplanning/configuration/ShiftPlanningRequest_activityLine_Wta.xml";
    public static final String DROOLS_FILE_SHIFT_PLANNING = "/droolsFile/Shift_Planning/";
    public static final String USER_HOME = "user.home";
    boolean readFromFile = false;
    boolean disablePrimarySolver = false;
    boolean readSecondaryFromFile = false;
    boolean enableSecondarySolver = false;
    public static final String BENCH_MARKER_CONFIG = "com/kairos/shiftplanning/configuration/ShiftPlanningBenchmark.solver.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningSolver.class);
    Solver<ShiftPlanningSolution> solver;
    SolverFactory<ShiftPlanningSolution> solverFactory;
    Solver<BreaksIndirectAndActivityPlanningSolution> solverBreaks;
    SolverFactory<BreaksIndirectAndActivityPlanningSolution> solverFactoryBreaks;
    public static String serverAddress;
    private ScoreDirector director;

    static {
        System.setProperty("user.timezone", "UTC");
    }

    public static SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, MINIMIZE_SHIFT_ON_WEEKENDS, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, PREFER_PERMANENT_EMPLOYEE, ScoreLevel.HARD, 2));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ConstraintSubType.ACTIVITY_REQUIRED_TAG, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.UNIT, PRESENCE_AND_ABSENCE_SAME_TIME, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ConstraintSubType.MAX_SHIFT_OF_STAFF, ScoreLevel.HARD, 5));
        //constraintDTOS.add(new ConstraintDTO(ConstraintType.WTA, AVERAGE_SHEDULED_TIME, ScoreLevel.MEDIUM, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ACTIVITY_VALID_DAYTYPE, ScoreLevel.SOFT, 4));
        return new SolverConfigDTO(constraintDTOS);
    }

    private static void updateServerAddress(String[] args) {
        switch (args[0]) {
            case "development":
                serverAddress = "http://dev.kairosplanning.com/";
                break;
            case "qa":
                serverAddress = "http://qa.kairosplanning.com/";
                break;
            case "production":
                serverAddress = "http://app.kairosplanning.com/";
                break;
            default:
                throw new RuntimeException("Invalid profile");
        }
    }

    private SolverFactory getSolverFactory(List<File> droolsFiles, File solverConfigFile) {
        SolverConfig solverConfig = createFromXmlFile(solverConfigFile);
        //solverConfig.setMoveThreadCount(String.valueOf(8));
        solverConfig.getScoreDirectorFactoryConfig().setScoreDrlFileList(droolsFiles);
        SolverFactory<Object> solverFactory = SolverFactory.create(solverConfig);
        director = solverFactory.getScoreDirectorFactory().buildScoreDirector();
        return solverFactory;
    }

    public ShiftPlanningSolver(List<File> droolsFiles, File configurationFile) {
        DateUtils.LOGGER.info("solver file exists {}", configurationFile.exists());
        solverFactory = getSolverFactory(droolsFiles, configurationFile);
        solver = solverFactory.buildSolver();
    }

    public ShiftPlanningSolution runSolver() {
        Object[] solvedSolution = getSolution(null);
        printSolvedSolution((ShiftPlanningSolution) solvedSolution[0]);
        printIndictment((Map<Object, Indictment>) solvedSolution[1]);
        return (ShiftPlanningSolution) solvedSolution[0];
    }

    public Object[] getSolution(ShiftPlanningSolution unsolvedSolution) {
        if (unsolvedSolution == null) {
            unsolvedSolution = getUnsolvedSolution(readFromFile);
        }
        if (!readFromFile)
            toXml(unsolvedSolution, "shift_problem");
        long start = System.currentTimeMillis();
        ShiftPlanningSolution solution = disablePrimarySolver ? unsolvedSolution : solver.solve(unsolvedSolution);
        ShiftPlanningUtility.printStaffingLevelMatrix(ShiftPlanningUtility.reduceStaffingLevelMatrix
                (solution.getStaffingLevelMatrix().getStaffingLevelMatrix(), solution.getShifts(), null, null, 15), null);
        LOGGER.info("Solver took: {}", (System.currentTimeMillis() - start) / 1000);
        if (!readFromFile)
            toXml(solution, "shift_solution");
        director = solver.getScoreDirectorFactory().buildScoreDirector();
        director.setWorkingSolution(solution);
        if (enableSecondarySolver) {
            BreaksIndirectAndActivityPlanningSolution secondarySolution = runAndGetBreaksSolution(readSecondaryFromFile ? null : solution);
            BreaksIndirectAndActivityPlanningSolution solvedBreaksSolution = solverBreaks.solve(secondarySolution);
            printBreaksAndIndirectActivities(solvedBreaksSolution);
            ShiftPlanningUtility.printStaffingLevelMatrix(
                    ShiftPlanningUtility.reduceStaffingLevelMatrix(solvedBreaksSolution.getStaffingLevelMatrix().getStaffingLevelMatrix(),
                            solvedBreaksSolution.getShifts(), solvedBreaksSolution.getShiftBreaks(), solvedBreaksSolution.getIndirectActivities(), 15),
                    ShiftPlanningUtility.reduceStaffingLevelMatrix
                            (solvedBreaksSolution.getStaffingLevelMatrix().getStaffingLevelMatrix(), solvedBreaksSolution.getShifts(), solvedBreaksSolution.getShiftBreaks(), null, 15));
            if (!readSecondaryFromFile)
                toXml(solvedBreaksSolution, "shift_solution_secondary");
        }
        return new Object[]{solution, director.getIndictmentMap()};//,director.getConstraintMatchTotals()
    }

    private void printBreaksAndIndirectActivities(BreaksIndirectAndActivityPlanningSolution solvedBreaksSolution) {
        StringBuilder sb = new StringBuilder();
        solvedBreaksSolution.getShifts().forEach(s -> {
            sb.append("\n" + "shift:" + s.getId() + "," + s.getInterval() + "," + s.getEmployee().getName());
            sb.append(solvedBreaksSolution.getShiftBreaks().stream().filter(sbrk -> sbrk.getShift().getId().equals(s.getId())).sorted(Comparator.comparing(ShiftBreak::getOrder)).
                    map(sbrk -> ShiftPlanningUtility.getIntervalAsString(sbrk.getInterval())).collect(Collectors.toList()).toString());
        });
        sb.append("\n");
        solvedBreaksSolution.getIndirectActivities().
                forEach(ia -> sb.append("[" + ia.getEmployees().stream().map(e -> e.getName()).collect(Collectors.toList()) + ":" + ia.getStartTime() + "]"));
        LOGGER.info(INFO, sb);
    }

    private BreaksIndirectAndActivityPlanningSolution runAndGetBreaksSolution(ShiftPlanningSolution solution) {
        if (solution == null)
            return new ShiftPlanningGenerator().loadUnsolvedBreakAndIndirectActivityPlanningSolution(BASE_SRC + "shift_solution_secondary.xml");
        return new ShiftPlanningGenerator().loadUnsolvedBreakAndIndirectActivityPlanningSolution(solution);
    }

    public ShiftPlanningSolution runSolverOnRequest(ShiftPlanningSolution unSolvedsolution) throws Exception{
        //solver.addEventListener(()->{}); Todo Add Listner for BestSolution changeIndictment End
        Object[] solvedSolution = getSolution(unSolvedsolution);
        printSolvedSolution((ShiftPlanningSolution) solvedSolution[0]);
        printIndictment((Map<Object, Indictment>) solvedSolution[1]);
        return (ShiftPlanningSolution) solvedSolution[0];
    }

    private void printIndictment(Map<Object, Indictment> indictmentMap) {
        LOGGER.info("*************Indictment**************");
        MutableInt unassignedIntervals = new MutableInt(0);
        indictmentMap.forEach((entity, indictment) -> {
            if (entity instanceof ShiftImp && !((ShiftImp) entity).isLocked() && ((ShiftImp) entity).getInterval() != null) {
                printInctmentMapOfShift((ShiftImp) entity, indictment);
            } else if (entity instanceof ActivityLineInterval) {
                printInctmentMapOfActivityLineInterval(unassignedIntervals, entity, indictment);
            } else if (entity instanceof Activity) {
                printInctmentMapOfActivity(entity, indictment);
            } else if (entity instanceof Employee) {
                printInctmentMapOfEmployee(entity, indictment);
            }
        });
        LOGGER.info("unassignedIntervals: {}", unassignedIntervals);
        LOGGER.info("*************Indictment End**************");
    }

    private void printInctmentMapOfShift(ShiftImp entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);
        sb.append(getShiftPlanInfo(entity) + "\n");
        MutableBoolean any = new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if (((HardMediumSoftLongScore) constraintMatch.getScore()).getHardScore() == 0 && ((HardMediumSoftLongScore) constraintMatch.getScore()).getMediumScore() == 0) {
                return;
            }
            any.setTrue();
            sb.append(constraintMatch.getConstraintName() + "--" + constraintMatch.getScore().toString() + "\n");
        });
        if (any.isTrue()) {
            LOGGER.info(INFO, sb);
        }
    }

    private void printInctmentMapOfActivityLineInterval(MutableInt unassignedIntervals, Object entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);
        if (((ActivityLineInterval) entity).getShift() == null) {
            unassignedIntervals.increment();
            return;
        }

        sb.append(entity.toString() + "--" + ((ActivityLineInterval) entity).getShift() + "\n");
        MutableBoolean any = new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if (((HardMediumSoftLongScore) constraintMatch.getScore()).getHardScore() == 0 && ((HardMediumSoftLongScore) constraintMatch.getScore()).getMediumScore() == 0) {
                return;
            }
            any.setTrue();
            sb.append("-------------" + constraintMatch.getConstraintName() + "----------" + constraintMatch.getScore().toString() + "\n");
        });
        if (any.isTrue()) {
            LOGGER.info(INFO, sb);
        }
    }

    private void printInctmentMapOfEmployee(Object entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);
        sb.append(entity.toString() + "---\n");
        MutableBoolean any = new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if (((HardMediumSoftLongScore) constraintMatch.getScore()).getHardScore() == 0 && ((HardMediumSoftLongScore) constraintMatch.getScore()).getMediumScore() == 0) {
                return;
            }
            any.setTrue();
            sb.append("------" + constraintMatch.getConstraintName() + "-------" + constraintMatch.getScore().toString() + "\n");
        });
        if (any.isTrue()) {
            LOGGER.info(INFO, sb);
        }
    }

    private void printInctmentMapOfActivity(Object entity, Indictment indictment) {
        StringBuilder sb = new StringBuilder();
        sb.append(STR);

        sb.append(entity.toString() + "---\n");
        MutableBoolean any = new MutableBoolean(false);
        indictment.getConstraintMatchSet().forEach(constraintMatch -> {
            if (((HardMediumSoftLongScore) constraintMatch.getScore()).getHardScore() == 0 && ((HardMediumSoftLongScore) constraintMatch.getScore()).getMediumScore() == 0) {
                return;
            }
            any.setTrue();
            sb.append("------" + constraintMatch.getConstraintName() + "--------" + constraintMatch.getScore().toString() + "\n");
        });
        if (any.isTrue()) {
            LOGGER.info(INFO, sb);
        }
    }

    private String getShiftPlanInfo(Shift shift) {
        return "" + shift.getStart().format(DateTimeFormatter.ofPattern("dd/MM-HH:mm")) + "--" + shift.getEnd().format(DateTimeFormatter.ofPattern("dd/MM-HH:mm"));
    }

    private void printSolvedSolution(ShiftPlanningSolution solution) {
        LOGGER.info("-------Printing solution:-------");
        LOGGER.info("total intervals: {}", solution.getActivityLineIntervals().stream().count());
        LOGGER.info("total assigned intervals: {}", solution.getActivityLineIntervals().stream().filter(i -> i.getShift() != null).count());
        solution.getEmployees().forEach(emp ->
                solution.getShifts().forEach(shift -> {
                    if (!emp.getId().equals(shift.getEmployee().getId())) {
                        return;
                    }
                    LOGGER.info("Shift A--------" + shift.getId() + "," + shift.getEmployee().getId() + "," + shift.getStartDate() + ":[" + shift.getInterval() + "(" + shift.getShiftActivities().size() + ")" + "]:" + shift.getShiftActivities() +
                            "[" + Optional.ofNullable(shift.getBreaks()).orElse(Collections.emptyList()).stream().collect(StringBuilder::new, (b1, b2) -> b1.append(b2.toString()), (b1, b2) -> b2.append(",").append(b1)) + "]");
                })
        );
        Map<ShiftImp, List<SkillLineInterval>> shiftsAssignedToSkillIntervals = new HashMap<>();
        solution.getSkillLineIntervals().stream().forEach(skillLineInterval -> {
            if (skillLineInterval.getShift() == null) return;
            if (shiftsAssignedToSkillIntervals.containsKey(skillLineInterval.getShift())) {
                shiftsAssignedToSkillIntervals.get(skillLineInterval.getShift()).add(skillLineInterval);
            } else {
                shiftsAssignedToSkillIntervals.put(skillLineInterval.getShift(), new ArrayList<>());
            }
        });
        LOGGER.info("-------Printing solution Finished:-------");
    }

    private ShiftPlanningSolution getUnsolvedSolution(boolean loadFromFile) {
        ShiftPlanningSolution unsolvedSolution = null;
        if (loadFromFile) {
            unsolvedSolution = new ShiftPlanningGenerator().loadUnsolvedSolutionFromXML(BASE_SRC + "shift_solution.xml");
        } else {
            unsolvedSolution = new ShiftPlanningGenerator().loadUnsolvedSolution();
        }
        return unsolvedSolution;
    }


    public void sendSolutionToKairos(ShiftPlanningSolution solvedSolution) {
        List<ShiftDTO> shiftDTOS = getShift(solvedSolution.getShifts());
        try {
            ShiftPlanningUtility.solvedShiftPlanningProblem(shiftDTOS, solvedSolution.getUnit().getId());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private List<ShiftDTO> getShift(List<ShiftImp> shiftImp) {
        List<ShiftDTO> shiftDTOS = new ArrayList<>(shiftImp.size());
        shiftImp.forEach(s -> {
            ShiftDTO shiftDTO = new ShiftDTO(asDate(s.getStart()), asDate(s.getEnd()), BigInteger.valueOf(320l), 95l, 1005l);
            shiftDTO.setUnitEmploymentPositionId(12431l);
            if (s.getActivityLineIntervals().size() > 1) {
                shiftDTO.setSubShifts(getSubShift(s));
            }
            shiftDTOS.add(shiftDTO);
        });
        return shiftDTOS;
    }

    private List<ShiftDTO> getSubShift(ShiftImp shift) {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        shift.getActivityLineIntervals().sort(Comparator.comparing(ActivityLineInterval::getStart));
        List<ActivityLineInterval> alis = getMergedALIs(shift.getActivityLineIntervals());
        if (alis.size() == 1) return new ArrayList<>();
        alis.forEach(a -> {
            ShiftDTO shiftDTO = new ShiftDTO(asDate(a.getStart().minusHours(5).minusMinutes(30)), asDate(a.getEnd().minusHours(5).minusMinutes(30)), BigInteger.valueOf(375), 95l, 1005l);
            shiftDTOS.add(shiftDTO);
        });
        return shiftDTOS;
    }

    private List<ActivityLineInterval> getMergedALIs(List<ActivityLineInterval> intervals) {
        List<ActivityLineInterval> activityLineIntervals = new ArrayList<>();
        ActivityLineInterval activityLineInterval = intervals.get(0);
        for (ActivityLineInterval ali : intervals.subList(1, intervals.size() - 1)) {
            if (activityLineInterval.getEnd().equals(ali.getStart()) && activityLineInterval.getActivity().getId().equals(ali.getActivity().getId())) {
                activityLineInterval.setDuration(activityLineInterval.getDuration() + ali.getDuration());
            } else {
                activityLineIntervals.add(activityLineInterval);
                activityLineInterval = ali;
            }
        }
        activityLineInterval.setDuration(activityLineInterval.getDuration() + 15);
        activityLineIntervals.add(activityLineInterval);
        return activityLineIntervals;
    }

    public static void toXml(Object solution, String fileName) {
        try {
            XStream xstream = new XStream(new PureJavaReflectionProvider());
            xstream.setMode(XStream.ID_REFERENCES);
            xstream.registerConverter(new ZonedDateTimeConverter());
            xstream.registerConverter(new LocalTimeConverter());
            xstream.registerConverter(new LocalDateConverter());
            xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
            String xmlString = xstream.toXML(solution);
            writeXml(xmlString, fileName);
        } catch (Exception e) {
            LOGGER.error(ERROR, e.getMessage());
        }
    }

    public static void writeXml(String xmlString, String fileName) {
        try (PrintWriter out = new PrintWriter(new File("" + fileName + ".xml"))) {
            out.write(xmlString);
        } catch (FileNotFoundException e) {
            LOGGER.error(ERROR, e.getMessage());
        }
    }

    @Override
    public int run(String... args) throws Exception{
        long startTime = new Date().getTime();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(System.getProperty(USER_HOME)+"/problem.json")))){
            StringBuilder stringBuilder = new StringBuilder();
            bufferedReader.lines().forEach(s -> stringBuilder.append(s));
            ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO = ObjectMapperUtils.jsonStringToObject(stringBuilder.toString(), ShiftPlanningProblemSubmitDTO.class);
            SolverConfigDTO solverConfigDTO = getSolverConfigDTO();
            List<File> droolFiles = getDroolFiles(solverConfigDTO);
            LocalDate localDate = LocalDate.of(2020, 11, 9);
            shiftPlanningProblemSubmitDTO.getStaffingLevels().removeIf(presenceStaffingLevelDto -> !asLocalDate(presenceStaffingLevelDto.getCurrentDate()).equals(localDate));
            shiftPlanningProblemSubmitDTO.getShifts().removeIf(shiftDTO -> !localDate.equals(asLocalDate(shiftDTO.getStartDate())));
            File configurationFile = getFile("/com/kairos/shiftplanning/configuration/", "ShiftPlanning_Request_ActivityLine.solver.xml");
            ShiftPlanningSolver shiftPlanningSolver = new ShiftPlanningSolver(droolFiles, configurationFile);
            ShiftPlanningSolution unSolvedsolution = new ShiftPlanningInitializer().initializeShiftPlanning(shiftPlanningProblemSubmitDTO);
            System.out.println("total starting time "+(new Date().getTime() - startTime));
            unSolvedsolution = shiftPlanningSolver.runSolverOnRequest(unSolvedsolution);
            //writeSolutionToFile(unSolvedsolution);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            File file = new File(System.getProperty(USER_HOME) + "/" + "exception.text");
            if(!file.exists()){
                file.createNewFile();
            }
            writeStringToFile(e.getMessage(), file);
        }
        return 0;
    }

    private void writeSolutionToFile(ShiftPlanningSolution unSolvedsolution) throws IOException {
        String objectString = ObjectMapperUtils.objectToJsonString(unSolvedsolution);
        File file = new File(System.getProperty(USER_HOME) + "/" + "solution.json");
        if(!file.exists()){
            file.createNewFile();
        }
        writeStringToFile(objectString,file);
    }

    private List<File> getDroolFiles(SolverConfigDTO solverConfigDTO) throws IOException{
        List<File> files = new ArrayList<>();
        files.add(getFile(DROOLS_FILE_SHIFT_PLANNING, "SHIFTPLANNING_BASE.drl"));
        //files.add(new File(System.getProperty("user.home") + "/" +"COMMON_WTA.drl"));
        for (ConstraintDTO constraint : solverConfigDTO.getConstraints()) {
            File file = getFile(DROOLS_FILE_SHIFT_PLANNING, constraint.getConstraintSubType() + ".drl");
            if (file != null) {
                files.add(file);
            }
        }
        return files;
    }

    private File getFile(String filePath, String fileName) throws IOException{
        InputStream inputStream = getClass().getResourceAsStream(filePath + fileName);
        StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(s -> {
            sb.append(s);
            sb.append("\n");
        });
        filePath = System.getProperty(USER_HOME) + "/" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        writeStringToFile(sb.toString(), file);
        return file;
    }

    private void writeStringToFile(String string, File file) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.write(string);
        printWriter.close();
    }
}
