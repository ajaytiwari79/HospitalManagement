package com.kairos.shiftplanningNewVersion.solver;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.dto.ShiftDTO;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.utils.LocalDateConverter;
import com.kairos.shiftplanning.utils.LocalTimeConverter;
import com.kairos.shiftplanning.utils.ZonedDateTimeConverter;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.entity.Staff;
import com.kairos.shiftplanningNewVersion.generator.StaffingLevelGenerator;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
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

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.enums.constraint.ConstraintSubType.*;
import static org.optaplanner.core.config.solver.SolverConfig.createFromXmlFile;

@NoArgsConstructor
public class StaffingLevelSolver {

    public  static final Logger LOGGER = LoggerFactory.getLogger(StaffingLevelSolver.class);
        public static final String BASE_SRC = "src/main/resources/data/";
        public static final String STR = "\n------------------------\n";
        public static final String INFO = "info {}";
        public static final String SOLVER_XML = "com/kairos/shiftplanning/configuration/StaffingLevelConfiguration.xml.xml";
        public static final String ERROR = "Error {}";
        public static final String CONFIG_BREAKS = "com/kairos/shiftplanning/configuration/BreakAndIndirectActivityPlanning.solver.xml";
        public static final String CONFIG_WITH_WTA = "com/kairos/shiftplanning/configuration/ShiftPlanningRequest_activityLine_Wta.xml";
        public static final String DROOLS_FILE_SHIFT_PLANNING = "/home/pradeep/Downloads/kairos/kairos-user/planner/optaplanner-shiftplanning/src/main/resources/droolsFile/staffing_level_plannning/";
        public static final String USER_HOME = "user.home";
        boolean readFromFile = false;
        boolean disablePrimarySolver = false;
        boolean readSecondaryFromFile = false;
        boolean enableSecondarySolver = false;
        public static final String BENCH_MARKER_CONFIG = "com/kairos/shiftplanning/configuration/ShiftPlanningBenchmark.solver.xml";
        private static Logger log = LoggerFactory.getLogger(com.kairos.shiftplanning.executioner.ShiftPlanningSolver.class);
        Solver<StaffingLevelSolution> solver;
        SolverFactory<StaffingLevelSolution> solverFactory;
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
            constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ACTIVITY_VALID_DAYTYPE, ScoreLevel.SOFT, 4));
            constraintDTOS.add(new ConstraintDTO(ConstraintType.WTA, SHIFT_LENGTH, ScoreLevel.MEDIUM, 4));
            constraintDTOS.add(new ConstraintDTO(ConstraintType.UNIT, MINIMIZE_COST, ScoreLevel.MEDIUM, 4));
            return new SolverConfigDTO(constraintDTOS);
        }

        private SolverFactory getSolverFactory(List<File> droolsFiles, File solverConfigFile) {
            SolverConfig solverConfig = createFromXmlFile(solverConfigFile);
            solverConfig.setMoveThreadCount(String.valueOf(4));
            solverConfig.getScoreDirectorFactoryConfig().setScoreDrlFileList(droolsFiles);
            SolverFactory<Object> solverFactory = SolverFactory.create(solverConfig);
            director = solverFactory.getScoreDirectorFactory().buildScoreDirector();
            return solverFactory;
        }

        public StaffingLevelSolver(List<File> droolsFiles, File configurationFile) {
            LOGGER.info("solver file exists {}", configurationFile.exists());
            solverFactory = getSolverFactory(droolsFiles, configurationFile);
            solver = solverFactory.buildSolver();
        }

        public StaffingLevelSolution runSolver() {
            Object[] solvedSolution = getSolution(null);
            printSolvedSolution((StaffingLevelSolution) solvedSolution[0]);
            printIndictment((Map<Object, Indictment>) solvedSolution[1]);
            return (StaffingLevelSolution) solvedSolution[0];
        }

        public Object[] getSolution(StaffingLevelSolution unsolvedSolution) {
            if (!readFromFile)
                toXml(unsolvedSolution, "shift_problem");
            long start = System.currentTimeMillis();
            StaffingLevelSolution solution = disablePrimarySolver ? unsolvedSolution : solver.solve(unsolvedSolution);
            log.info("Solver took: {}", (System.currentTimeMillis() - start) / 1000);
            if (!readFromFile)
                toXml(solution, "shift_solution");
            director = solver.getScoreDirectorFactory().buildScoreDirector();
            director.setWorkingSolution(solution);
            return new Object[]{solution, director.getIndictmentMap()};//,director.getConstraintMatchTotals()
        }

        private void printIndictment(Map<Object, Indictment> indictmentMap) {
            log.info("*************Indictment**************");
            MutableInt unassignedIntervals = new MutableInt(0);
            indictmentMap.forEach((entity, indictment) -> {
                if (entity instanceof Shift && !((Shift) entity).isLocked() && ((Shift) entity).getInterval() != null) {
                    printInctmentMapOfShift((Shift) entity, indictment);
                } else if (entity instanceof ALI) {
                    printInctmentMapOfActivityLineInterval(unassignedIntervals, entity, indictment);
                } else if (entity instanceof Activity) {
                    printInctmentMapOfActivity(entity, indictment);
                } else if (entity instanceof Staff) {
                    printInctmentMapOfEmployee(entity, indictment);
                }
            });
            log.info("unassignedIntervals: {}", unassignedIntervals);
            log.info("*************Indictment End**************");
        }

        private void printInctmentMapOfShift(Shift entity, Indictment indictment) {
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
                log.info(INFO, sb);
            }
        }

        private void printInctmentMapOfActivityLineInterval(MutableInt unassignedIntervals, Object entity, Indictment indictment) {
            StringBuilder sb = new StringBuilder();
            sb.append(STR);
            if (((ALI) entity).getShift() == null) {
                unassignedIntervals.increment();
                return;
            }

            sb.append(entity.toString() + "--" + ((ALI) entity).getShift() + "\n");
            MutableBoolean any = new MutableBoolean(false);
            indictment.getConstraintMatchSet().forEach(constraintMatch -> {
                if (((HardMediumSoftLongScore) constraintMatch.getScore()).getHardScore() == 0 && ((HardMediumSoftLongScore) constraintMatch.getScore()).getMediumScore() == 0) {
                    return;
                }
                any.setTrue();
                sb.append("-------------" + constraintMatch.getConstraintName() + "----------" + constraintMatch.getScore().toString() + "\n");
            });
            if (any.isTrue()) {
                log.info(INFO, sb);
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
                log.info(INFO, sb);
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
                log.info(INFO, sb);
            }
        }

        private String getShiftPlanInfo(Shift shift) {
            return "" + shift.getStart().format(DateTimeFormatter.ofPattern("dd/MM-HH:mm")) + "--" + shift.getEnd().format(DateTimeFormatter.ofPattern("dd/MM-HH:mm"));
        }

        private void printSolvedSolution(StaffingLevelSolution solution) {
            log.info("-------Printing solution:-------");
            log.info("total intervals: {}", solution.getActivityLineIntervals().stream().count());
            log.info("total assigned intervals: {}", solution.getActivityLineIntervals().stream().filter(i -> i.getShift() != null).count());
            solution.getStaffs().forEach(emp ->
                    solution.getShifts().forEach(shift -> {
                        if (!emp.getId().equals(shift.getStaff().getId())) {
                            return;
                        }
                        log.info("Shift " + shift.getId() + "," + shift.getStaff().getId() + "," + shift.getStartDate() + ":[" + shift.getStartTime() +" : "+shift.getEndTime() + "(" + shift.getShiftActivities().size() + ")" + "]:" + shift.getShiftActivities() +
                                "[" + Optional.ofNullable(shift.getBreaks()).orElse(Collections.emptyList()).stream().collect(StringBuilder::new, (b1, b2) -> b1.append(b2.toString()), (b1, b2) -> b2.append(",").append(b1)) + "]");
                    })
            );
            log.info("-------Printing solution Finished:-------");
        }
        
        
        private List<ShiftDTO> getShift(List<Shift> shiftImp) {
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

        private List<ShiftDTO> getSubShift(Shift shift) {
            List<ShiftDTO> shiftDTOS = new ArrayList<>();
            shift.getActivityLineIntervals().sort(Comparator.comparing(ALI::getStart));
            List<ALI> alis = getMergedALIs(shift.getActivityLineIntervals());
            if (alis.size() == 1) return new ArrayList<>();
            alis.forEach(a -> {
                ShiftDTO shiftDTO = new ShiftDTO(asDate(a.getStart().minusHours(5).minusMinutes(30)), asDate(a.getEnd().minusHours(5).minusMinutes(30)), BigInteger.valueOf(375), 95l, 1005l);
                shiftDTOS.add(shiftDTO);
            });
            return shiftDTOS;
        }

        private List<ALI> getMergedALIs(List<ALI> intervals) {
            List<ALI> activityLineIntervals = new ArrayList<>();
            ALI activityLineInterval = intervals.get(0);
            for (ALI ali : intervals.subList(1, intervals.size() - 1)) {
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
                log.error(ERROR, e.getMessage());
            }
        }

        public static void writeXml(String xmlString, String fileName) {
            try (PrintWriter out = new PrintWriter(new File("" + fileName + ".xml"))) {
                out.write(xmlString);
            } catch (FileNotFoundException e) {
                log.error(ERROR, e.getMessage());
            }
        }

    public StaffingLevelSolution runSolverOnRequest(StaffingLevelSolution unSolvedsolution) throws Exception{
        //solver.addEventListener(()->{}); Todo Add Listner for BestSolution changeIndictment End
        Object[] solvedSolution = getSolution(unSolvedsolution);
        printSolvedSolution((StaffingLevelSolution) solvedSolution[0]);
        printIndictment((Map<Object, Indictment>) solvedSolution[1]);
        //sendSolutionToKairos((ShiftRequestPhasePlanningSolution) solvedSolution[0]);
        return (StaffingLevelSolution) solvedSolution[0];
    }

       // @Override
        public int run(String... args) throws Exception{
            long startTime = new Date().getTime();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(System.getProperty(USER_HOME)+"/problem.json")))){
                StringBuilder stringBuilder = new StringBuilder();
                bufferedReader.lines().forEach(s -> stringBuilder.append(s));
                ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO = ObjectMapperUtils.jsonStringToObject(stringBuilder.toString(), ShiftPlanningProblemSubmitDTO.class);
                LocalDate localDate = LocalDate.of(2020, 11, 9);
                shiftPlanningProblemSubmitDTO.getStaffingLevels().removeIf(presenceStaffingLevelDto -> !asLocalDate(presenceStaffingLevelDto.getCurrentDate()).equals(localDate));
                shiftPlanningProblemSubmitDTO.getShifts().removeIf(shiftDTO -> !asLocalDate(shiftDTO.getStartDate()).equals(localDate));
                shiftPlanningProblemSubmitDTO.setShifts(shiftPlanningProblemSubmitDTO.getShifts().subList(0,30));
                SolverConfigDTO solverConfigDTO = getSolverConfigDTO();
                List<File> droolFiles = getDroolFiles(solverConfigDTO);
                File configurationFile = new File("/home/pradeep/Downloads/kairos/kairos-user/planner/optaplanner-shiftplanning/src/main/resources/com/kairos/shiftplanning/configuration/StaffingLevelConfiguration.xml");
                StaffingLevelSolution unSolvedsolution = new StaffingLevelGenerator().initializeShiftPlanning(shiftPlanningProblemSubmitDTO);
                StaffingLevelSolver staffingLevelSolver = new StaffingLevelSolver(droolFiles, configurationFile);
                System.out.println("total starting time "+(new Date().getTime() - startTime));
                unSolvedsolution = staffingLevelSolver.runSolverOnRequest(unSolvedsolution);
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

        private void writeSolutionToFile(StaffingLevelSolution unSolvedsolution) throws IOException {
            String objectString = ObjectMapperUtils.objectToJsonString(unSolvedsolution);
            File file = new File(System.getProperty(USER_HOME) + "/" + "solution.json");
            if(!file.exists()){
                file.createNewFile();
            }
            writeStringToFile(objectString,file);
        }

        private List<File> getDroolFiles(SolverConfigDTO solverConfigDTO) throws IOException{
            List<File> files = new ArrayList<>();

            files.add(new File(DROOLS_FILE_SHIFT_PLANNING, "SHIFTPLANNING_BASE.drl"));
            //files.add(new File(System.getProperty("user.home") + "/" +"COMMON_WTA.drl"));
            for (ConstraintDTO constraint : solverConfigDTO.getConstraints()) {
                File file = new File(DROOLS_FILE_SHIFT_PLANNING, constraint.getConstraintSubType() + ".drl");
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
