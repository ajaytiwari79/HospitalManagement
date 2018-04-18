package com.planning.service.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.planning.domain.config.Category;
import com.planning.domain.config.Constraint;
import com.planning.domain.config.Rule;
import com.planning.domain.config.SolverConfig;
import com.planning.enums.SolverConfigPhase;
import com.planning.repository.config.SolverConfigRepository;
import com.planning.responseDto.config.CategoryDTO;
import com.planning.responseDto.config.ConstraintDTO;
import com.planning.responseDto.config.RuleDTO;
import com.planning.responseDto.config.SolverConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SolverConfigService {

    @Autowired private SolverConfigRepository solverConfigRepository;

    public List<SolverConfigDTO> getAllSolverConfig(Long unitId){
        List<SolverConfigDTO> solverConfigDTOS = null;
        if(unitId!=null){
            List<SolverConfig> solverConfigs = solverConfigRepository.getAllByUnitId(unitId,SolverConfig.class);
            for (SolverConfig solverConfig:solverConfigs) {
                SolverConfigDTO solverConfigDTO = getSolverConfigDTO(solverConfig);
                solverConfigDTO.setConstraintDTOList(getContraintDTOs(solverConfig.getId()));
                solverConfigDTOS.add(solverConfigDTO);
            }
        }
        return solverConfigDTOS;
    }

    public SolverConfigDTO getOne(String solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findById(solverConfigId,SolverConfig.class);
        SolverConfigDTO solverConfigDTO = getSolverConfigDTO(solverConfig);
        List<CategoryDTO> categoryDTOS = solverConfigRepository.findAll(Category.class);
        //solverConfigDTO.setConstraintDTOList(getContraintDTOs(constraints));
        solverConfigDTO.setCategoryDTOS(categoryDTOS);
        return solverConfigDTO;
    }

    private SolverConfigDTO getSolverConfigDTO(SolverConfig solverConfig){
        SolverConfigDTO solverConfigDTO = new SolverConfigDTO();
        solverConfigDTO.setOptaPlannerId(solverConfig.getId());
        solverConfigDTO.setTemplate(solverConfig.isTemplate());
        solverConfigDTO.setHardLevel(solverConfig.getHard());
        solverConfigDTO.setMediumLevel(solverConfig.getMedium());
        solverConfigDTO.setPhase(solverConfig.getPhase().toValue());
        solverConfigDTO.setSoftLevel(solverConfig.getSoft());
        solverConfigDTO.setTerminationTime(solverConfig.getTerminationTime());
        return solverConfigDTO;
    }

    public SolverConfigDTO getOneForPlanning(String solverConfigId){
        SolverConfigDTO solverConfigDTO = getOne(solverConfigId);
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

    public boolean saveSolverConfig(SolverConfigDTO solverConfigDTO){
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setHard(solverConfigDTO.getHardLevel());
        solverConfig.setMedium(solverConfigDTO.getMediumLevel());
        solverConfig.setSoft(solverConfigDTO.getSoftLevel());
        solverConfig.setUnitId(solverConfigDTO.getUnitId());
        solverConfig.setPhase(SolverConfigPhase.valueOf(solverConfigDTO.getPhase()));
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

    public boolean saveDefaultSolverConfig(SolverConfigDTO solverConfigDTO){
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setTemplate(solverConfigDTO.isTemplate());
        solverConfig.setUnitId(solverConfigDTO.getUnitId());
        solverConfig.setPhase(SolverConfigPhase.getEnumByString(solverConfigDTO.getPhase()));
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
            /*rule.setPattern(contraintDto.getRuleDTO().getPattern());
            rule.setRuleCondition(contraintDto.getRuleDTO().getRuleCondition());
            rule.setNoOfruleValues(contraintDto.getRuleDTO().getNoOfruleValues());
            rule.setDisabled(contraintDto.getRuleDTO().isDisabled());
            rule.setRuleName(contraintDto.getRuleDTO().getRuleName());
            rule.setSalience(contraintDto.getRuleDTO().getSalience());
            rule.setOutputValues(contraintDto.getRuleDTO().getOutputValues());*/
            rule = solverConfigRepository.findById(contraintDto.getRuleId(),Rule.class);
            if(rule!=null){
                constraint.setRuleId(rule.getId());
                solverConfigRepository.save(constraint);
            }else {
                solverConfigRepository.deleteById(solverConfig.getId(),SolverConfig.class);
            }
        }
        return true;
        /*ObjectMapper mapper = new ObjectMapper();
        List<Rule> rules = new ArrayList<>();
        try {
            TypeFactory typeFactory = mapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(
                    List.class, SolverConfigDTO.class);
            rules = mapper.readValue( mapper.writeValueAsString(map), collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //solverConfigRepository.saveList(rules);
        return true;*/
    }

}
