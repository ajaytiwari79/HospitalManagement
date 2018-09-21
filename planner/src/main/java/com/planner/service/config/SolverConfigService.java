/*
package com.planner.service.config;

import com.kairos.enums.wta.WTATemplateType;
import com.kairos.dto.planner.solverconfig.ConstraintValueDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigWTADTO;
import com.kairos.planner.vrp.taskplanning.model.constraint.*;
import com.planner.commonUtil.StaticField;
import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.util.wta.FileIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolverConfigService {

    @Autowired private SolverConfigRepository solverConfigRepository;
    @Autowired private XmlConfigService xmlConfigService;
    @Autowired private PathProvider pathProvider;

    public void addSolverConfig(Long unitId, SolverConfigWTADTO solverConfigWTADTO){
        SolverConfig solverConfig= new SolverConfig(solverConfigWTADTO.getTemplateTypes(),120,unitId);
        solverConfigRepository.save(solverConfig);
    }

    */
/**
     * Creates solver config xml at {@link PathProvider} solverConfigPath property based on wta templates for this solver config
     * @param solverConfigId
     *//*

    public Document createShiftPlanningSolverConfig(BigInteger solverConfigId){
        SolverConfig solverConfig=solverConfigRepository.findByKairosId(solverConfigId).get();
        List<String> validDrls=new ArrayList<>();
        for(WTATemplateType wtaTemplateType:solverConfig.getTemplateTypes()){
            validDrls.add(getDrlPathForTemplateType(wtaTemplateType));
        }
        Document baseConfig= getBaseSolverConfig();
        xmlConfigService.putElementsInXml(baseConfig,validDrls,StaticField.SOLVER_CONFIG_DRL_PARENT_TAG);
        return baseConfig;
    }

    */
/**
     *
     * @returns a copy of base solver config file
     *//*

    public Document getBaseSolverConfig(){
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            File baseFile = new File(StaticField.BASE_SOLVER_CONFIG_FIRST_PHASE);
            File file=new File(pathProvider.getSolverConfigXmlpath());
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileIOUtil.copyFileContent(baseFile,file);
            doc = docBuilder.parse(file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private String getDrlPathForTemplateType(WTATemplateType wtaTemplateType) {
        switch (wtaTemplateType){
            case AVERAGE_SHEDULED_TIME:
                return StaticField.DRL_AVERAGE_SHEDULED_TIME;
            case CONSECUTIVE_WORKING_PARTOFDAY:
                return StaticField.DRL_CONSECUTIVE_WORKING_PARTOFDAY;
            case DAYS_OFF_IN_PERIOD:
                return StaticField.DRL_DAYS_OFF_IN_PERIOD;
            case NUMBER_OF_PARTOFDAY:
                return StaticField.DRL_NUMBER_OF_PARTOFDAY;
            case SHIFT_LENGTH:
                return StaticField.DRL_SHIFT_LENGTH;
            case NUMBER_OF_SHIFTS_IN_INTERVAL:
                return StaticField.DRL_NUMBER_OF_SHIFTS_IN_INTERVAL;
            case TIME_BANK:
                return StaticField.DRL_TIME_BANK;
            case VETO_PER_PERIOD:
                return StaticField.DRL_VETO_PER_PERIOD;
            case DAILY_RESTING_TIME:
                return StaticField.DRL_DAILY_RESTING_TIME;
            case DURATION_BETWEEN_SHIFTS:
                return StaticField.DRL_DURATION_BETWEEN_SHIFTS;
            case REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS:
                return StaticField.DRL_REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;
            case WEEKLY_REST_PERIOD:
                return StaticField.DRL_WEEKLY_REST_PERIOD;
            case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                return StaticField.DRL_NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;
            case SHORTEST_AND_AVERAGE_DAILY_REST:
                return StaticField.DRL_SHORTEST_AND_AVERAGE_DAILY_REST;
            case SENIOR_DAYS_PER_YEAR:
                return StaticField.DRL_SENIOR_DAYS_PER_YEAR;
            case CHILD_CARE_DAYS_CHECK:
                return StaticField.DRL_CHILD_CARE_DAYS_CHECK;
            case DAYS_OFF_AFTER_A_SERIES:
                return StaticField.DRL_DAYS_OFF_AFTER_A_SERIES;
            case NO_OF_SEQUENCE_SHIFT:
                return StaticField.DRL_NO_OF_SEQUENCE_SHIFT;
            case EMPLOYEES_WITH_INCREASE_RISK:
                return StaticField.DRL_EMPLOYEES_WITH_INCREASE_RISK;

            default:return null;
        }
    }

    */
/*public List<SolverConfigWTADTO> getAllSolverConfig(Long unitId){
        List<SolverConfigWTADTO> solverConfigDTOS = null;
        if(unitId!=null){
            List<SolverConfig> solverConfigs = solverConfigRepository.getAllByUnitId(unitId,SolverConfig.class);
            for (SolverConfig solverConfig:solverConfigs) {
                SolverConfigWTADTO solverConfigDTO = getSolverConfigDTO(solverConfig);
                solverConfigDTO.setConstraintDTOList(getContraintDTOs(solverConfig.getId()));
                solverConfigDTOS.add(solverConfigDTO);
            }
        }
        return solverConfigDTOS;
    }

    public SolverConfigWTADTO getOne(String solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findById(solverConfigId,SolverConfig.class);
        SolverConfigWTADTO solverConfigDTO = getSolverConfigDTO(solverConfig);
        List<CategoryDTO> categoryDTOS = solverConfigRepository.findAll(Category.class);
        //solverConfigDTO.setConstraintDTOList(getContraintDTOs(constraints));
        solverConfigDTO.setCategoryDTOS(categoryDTOS);
        return solverConfigDTO;
    }

    private SolverConfigWTADTO getSolverConfigDTO(SolverConfig solverConfig){
        SolverConfigWTADTO solverConfigDTO = new SolverConfigWTADTO();
        solverConfigDTO.setOptaPlannerId(solverConfig.getId());
        solverConfigDTO.setTemplate(solverConfig.isTemplate());
        solverConfigDTO.setHardLevel(solverConfig.getHard());
        solverConfigDTO.setMediumLevel(solverConfig.getMedium());
        solverConfigDTO.setPhase(solverConfig.getPhase().toValue());
        solverConfigDTO.setSoftLevel(solverConfig.getSoft());
        solverConfigDTO.setTerminationTime(solverConfig.getTerminationTime());
        return solverConfigDTO;
    }

    public SolverConfigWTADTO getOneForPlanning(String solverConfigId){
        SolverConfigWTADTO solverConfigDTO = getOne(solverConfigId);
        if(solverConfigDTO!=null){
            solverConfigDTO.setOptaPlannerId(solverConfigId);
            solverConfigDTO.setConstraintDTOList(getContraintDTOs(solverConfigId));
        }
        return solverConfigDTO;
    }

    public List<ConstraintDTO> getContraintDTOs(String solverConfigId){
        List<Constraint> constraints = solverConfigRepository.getAllContraintsBySolverConfigId(solverConfigId);
        Map<String,RuleDTO> ruleDTOMap = getRulesMap();
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        for (Constraint constraint :constraints) {
            ConstraintDTO constraintDTO = new ConstraintDTO();
            constraintDTO.setLevel(constraint.getLevel());
            constraintDTO.setLevelValue(constraint.getLevelValue());
            constraintDTO.setLevelNo(constraint.getLevelNo());
            constraintDTO.setDynamicRuleValues(constraint.getDynamicRuleValue());
            constraintDTO.setStaticRuleValues(constraint.getStaticRuleValues());
            constraintDTO.setRuleDTO(ruleDTOMap.get(constraint.getRuleId()));
            constraintDTOS.add(constraintDTO);
        }
        return constraintDTOS;
    }

    public boolean saveSolverConfig(SolverConfigWTADTO solverConfigDTO){
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setHard(solverConfigDTO.getHardLevel());
        solverConfig.setMedium(solverConfigDTO.getMediumLevel());
        solverConfig.setSoft(solverConfigDTO.getSoftLevel());
        solverConfig.setUnitId(solverConfigDTO.getUnitId());
        solverConfig.setPhase(PlanningType.valueOf(solverConfigDTO.getPhase()));
        solverConfig.setParentSolverConfigId(solverConfigDTO.getOptaPlannerId());
        solverConfig.setTemplate(solverConfigDTO.isTemplate());
        solverConfig = solverConfigRepository.save(solverConfig);
        List<Constraint> constraints = new ArrayList<>();
        for (ConstraintDTO constrainDto:solverConfigDTO.getConstraintDTOList()) {
            Constraint constraint = new Constraint();
            constraint.setStaticRuleValues(constrainDto.getStaticRuleValues());
            constraint.setLevel(constrainDto.getLevel());
            constraint.setLevelNo(constrainDto.getLevelNo());
            constraint.setLevelValue(constrainDto.getLevelValue());
            constraint.setSolverConfigId(solverConfig.getId());
            constraint.setRuleId(constrainDto.getRuleId());
            constraint.setDynamicRuleValue(constrainDto.getDynamicRuleValues());
            constraints.add(constraint);
        }
        solverConfigRepository.saveList(constraints);
        return true;
    }

    public Map<String,RuleDTO> getRulesMap(){
        List<Rule> rules = solverConfigRepository.findAll(Rule.class);
        Map<String,RuleDTO> ruleMap = new HashMap<>(rules.size());
        for (Rule rule:rules) {
            RuleDTO ruleDTO = new RuleDTO();
            ruleDTO.setPattern(rule.getPattern());
            ruleDTO.setOptaPlannerId(rule.getId());
            ruleDTO.setRuleCondition(rule.getRuleCondition());
            ruleDTO.setRuleName(rule.getRuleName());
            ruleDTO.setDisabled(rule.isDisabled());
            ruleDTO.setSalience(rule.getSalience());
            ruleDTO.setOutputValues(rule.getOutputValues());
            ruleDTO.setNoOfruleValues(rule.getNoOfruleValues());
            ruleMap.put(rule.getId(),ruleDTO);
        }
        return ruleMap;
    }

    public boolean saveRulesByJson(Map data){
        ObjectMapper mapper = new ObjectMapper();
        List<Rule> rules = new ArrayList<>();
        try {
            TypeFactory typeFactory = mapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(
                    List.class, Rule.class);
            rules = mapper.readValue(new File("files/Rule.json"), collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        solverConfigRepository.saveList(rules);
        return true;
    }

    public boolean updateRulesByJson(Map requestMap){
        ObjectMapper mapper = new ObjectMapper();
        List<RuleDTO> ruleDTOS = new ArrayList<>();
        try {
            TypeFactory typeFactory = mapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(
                    List.class, RuleDTO.class);
            ruleDTOS = mapper.readValue(new File("files/updateRules.json"), collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveRules(ruleDTOS);
        return true;
    }

    private void saveRules(List<RuleDTO> ruleDTOS){
        List<Rule> rules = new ArrayList<>();
        for (RuleDTO ruleDto:ruleDTOS) {
            Rule rule = solverConfigRepository.findById(ruleDto.getOptaPlannerId(),Rule.class);
            rule.setPattern(ruleDto.getPattern());
            rule.setSalience(ruleDto.getSalience());
            rule.setDisabled(ruleDto.isDisabled());
            rule.setNoOfruleValues(ruleDto.getNoOfruleValues());
            rule.setRuleCondition(ruleDto.getRuleCondition());
            rule.setOutputValues(ruleDto.getOutputValues());
            rule.setRuleName(ruleDto.getRuleName());
            rule = solverConfigRepository.save(rule);
            rules.add(rule);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File("files/rule.json"), rules);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveDefaultSolverConfig(SolverConfigWTADTO solverConfigDTO){
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setTemplate(solverConfigDTO.isTemplate());
        solverConfig.setUnitId(solverConfigDTO.getUnitId());
        solverConfig.setPhase(PlanningType.getEnumByString(solverConfigDTO.getPhase()));
        solverConfig.setHard(solverConfigDTO.getHardLevel());
        solverConfig.setMedium(solverConfigDTO.getMediumLevel());
        solverConfig.setSoft(solverConfigDTO.getSoftLevel());
        solverConfig = solverConfigRepository.save(solverConfig);
        for (ConstraintDTO contraintDto:solverConfigDTO.getConstraintDTOList()) {
            Constraint constraint = new Constraint();
            constraint.setDynamicRuleValue(contraintDto.getDynamicRuleValues());
            constraint.setSolverConfigId(solverConfig.getId());
            constraint.setLevel(contraintDto.getLevel());
            constraint.setLevelNo(contraintDto.getLevelNo());
            constraint.setSolverConfigId(solverConfig.getId());
            constraint.setStaticRuleValues(contraintDto.getStaticRuleValues());
            Rule rule = null;
            *//*
*/
/*rule.setPattern(contraintDto.getRuleDTO().getPattern());
            rule.setRuleCondition(contraintDto.getRuleDTO().getRuleCondition());
            rule.setNoOfruleValues(contraintDto.getRuleDTO().getNoOfruleValues());
            rule.setDisabled(contraintDto.getRuleDTO().isDisabled());
            rule.setRuleName(contraintDto.getRuleDTO().getRuleName());
            rule.setSalience(contraintDto.getRuleDTO().getSalience());
            rule.setOutputValues(contraintDto.getRuleDTO().getOutputValues());*//*
*/
/*
            rule = solverConfigRepository.findById(contraintDto.getRuleId(),Rule.class);
            if(rule!=null){
                constraint.setRuleId(rule.getId());
                solverConfigRepository.save(constraint);
            }else {
                solverConfigRepository.deleteById(solverConfig.getId(),SolverConfig.class);
            }
        }
        return true;
        *//*
*/
/*ObjectMapper mapper = new ObjectMapper();
        List<Rule> rules = new ArrayList<>();
        try {
            TypeFactory typeFactory = mapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(
                    List.class, SolverConfigWTADTO.class);
            rules = mapper.readValue( mapper.writeValueAsString(map), collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //solverConfigRepository.saveList(rules);
        return true;*//*
*/
/*
    }*//*




    */
/*public SolverConfig getSolverConfigByDTO(SolverConfigDTO solverConfigDTO){
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setTerminationSeconds(solverConfigDTO.getTerminationTime());
        solverConfig.setUnitId(solverConfigDTO.getUnitId());
        Constraint constraint = new Constraint();
        for (ConstraintValueDTO constraintValue : solverConfigDTO.getConstraints()) {
            switch (constraintValue.getName()){
                case "Maximize flexitime utilization":constraint.setMaximizeFlexitimeUtilization(new MaximizeFlexitimeUtilization(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Minimize driving time":constraint.setMinimizeDrivingTime(new MinimizeDrivingTime(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Must Be Planned":constraint.setMustBePlanned(new MustBePlanned(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Number of Task Per shift":constraint.setNumberOfTaskPerShift(new NumberOfTaskPerShift(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Optimize plan based on Skill":constraint.setOptimizePlanBasedOnSkill(new OptimizePlanBasedOnSkill(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Plan inside Time Window":constraint.setPlanInsideTimeWindow(new PlanInsideTimeWindow(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Plan task from same installation number together":constraint.setPlanTaskFromSameInstallationNumber(new PlanTaskFromSameInstallationNumber(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Starts as First task":constraint.setStartsAsFirstTask(new StartsAsFirstTask(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
                case "Task from same installation number":constraint.setTaskFromSameInstallationNumber(new TaskFromSameInstallationNumber(constraintValue.getPenalityValue(),constraintValue.getConstraintValue()));
                    break;
            }
        }
        return solverConfig;
    }*//*


}
*/
