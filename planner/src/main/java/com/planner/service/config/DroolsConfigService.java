package com.planner.service.config;


//import org.junit.Test;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.commonUtil.StaticField;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.descr.PackageDescr;


@Service
public class DroolsConfigService {


    private static Logger logger = LoggerFactory.getLogger(DroolsConfigService.class);
//TODO pradeep need Refactor
/*

    public String getDroolFilePath(SolverConfigDTO solverConfigDTO) {
        int totalLevel = solverConfigDTO.getHardLevel()+solverConfigDTO.getMediumLevel()+solverConfigDTO.getSoftLevel();
        PackageDescrBuilder packageDescrBuilder = getDefaultDroolPackage(totalLevel);
        for (ConstraintDTO constraintDto : solverConfigDTO.getConstraintDTOList()) {
            if (!constraintDto.getRuleDTO().isDisabled()) {
                packageDescrBuilder = getPackageDescrBuilder(packageDescrBuilder, constraintDto, solverConfigDTO.getMediumLevel(),totalLevel);
            }
        }
        PackageDescr packageDescr = packageDescrBuilder.getDescr();
        DrlDumper drlDumper = new DrlDumper();
        String drlString = drlDumper.dump(packageDescr);
        return getDroolFilePath(drlString, solverConfigDTO.getOptaPlannerId());
    }

    private String getDroolFilePath(String drlString, String fileName) {
        File file = null;
        drlString = drlString.replaceAll("null\\( ","");
        drlString = drlString.replaceAll("null \\)","");
        try {
            file = new File(StaticField.DROOLSFILEPATH + fileName + StaticField.DROOLFILEEXTENSION);
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(drlString);
            bw.close();
            logger.info("File Created Successfully");
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return file.getPath();
    }


    private PackageDescrBuilder getDefaultDroolPackage(int totalLevel) {
        PackageDescrBuilder packageDescrBuilder = null;
        if(totalLevel==3){
            packageDescrBuilder = DescrFactory.newPackage()
                    .name(StaticField.COM_KAIROS_SHIFT_PLANNING_RULES).end().attribute(StaticField.DIALECT, StaticField.JAVA).end().newImport().target(StaticField.HARDMEDIUMSOFTIMPORT).end().newImport().target(StaticField.COM_KAIROS_SHIFTPLANNING_DOMAIN).end().newImport().target(StaticField.UTIL_ARRAYLIST).end().newImport().target(StaticField.SHIFT_PLANNING_UTILITY).end().newGlobal().type(StaticField.HARD_MEDIUM_SOFT_SCORE_HOLDER).identifier(StaticField.SCORE_HOLDER).end();
        }else{
            packageDescrBuilder = DescrFactory.newPackage()
                    .name(StaticField.COM_KAIROS_PLANNING_RULES).end().attribute(StaticField.DIALECT, StaticField.JAVA).end().newImport().target(StaticField.BENDABLEIMPORT).end().newImport().target(StaticField.COM_KAIROS_PLANNING_DOMAIN).end().newGlobal().type(StaticField.BENDABLE_LONG_SCORE_HOLDER).identifier(StaticField.SCORE_HOLDER).end();
        }
        return packageDescrBuilder;
    }

    private PackageDescrBuilder getPackageDescrBuilder(PackageDescrBuilder packageDescrBuilder, ConstraintDTO constraintDto, int mediumLevel,int totalLevel) {
        packageDescrBuilder = packageDescrBuilder.newRule().name(constraintDto.getRuleDTO().getRuleName())
                .attribute(StaticField.SALIENCE, Integer.toString(constraintDto.getRuleDTO().getSalience()))
                .lhs()
                .pattern().constraint(getRuleCondition(constraintDto)+"null").end().end()
                .rhs(getContraintWithLevel(constraintDto, mediumLevel,totalLevel)).end();
        return packageDescrBuilder;
    }


    private String getRuleCondition(ConstraintDTO constraintDTO) {
        String ruleCondition = "";
        if (constraintDTO.getDynamicRuleValues()!=null && constraintDTO.getDynamicRuleValues().size() > 0) {
            ruleCondition = getCondition(constraintDTO.getDynamicRuleValues(), constraintDTO.getRuleDTO().getRuleCondition());
            return ruleCondition;
        } else if (constraintDTO.getStaticRuleValues()!=null && constraintDTO.getStaticRuleValues().size() > 0) {
            ruleCondition = getCondition(constraintDTO.getStaticRuleValues(), constraintDTO.getRuleDTO().getRuleCondition());
            return ruleCondition;
        }
        ruleCondition = constraintDTO.getRuleDTO().getRuleCondition();
        return ruleCondition;
    }

    private String getCondition(List<Integer> values, String condition) {
        for (int i = 0; i < values.size(); i++) {
            condition = new StringBuffer(condition.replace("[" + i + "]", Integer.toString(values.get(i)))).toString();
        }
        return condition;
    }

    */
/*because of we use hard and soft constraint so thats why
    we add use upper level of soft constraint as a medium
    constraint because of this we use medium level for it*//*

    private String getContraintWithLevel(ConstraintDTO constraintDTO, int mediumLevel, int totalLevel) {
        if (totalLevel == 3) {
            if(constraintDTO.getLevel().equals(StaticField.HARD)){
                return StaticField.HARDCONSTRAINT+"-"+getContraintValue(constraintDTO) + ");";
            }else if(constraintDTO.getLevel().equals(StaticField.MEDIUM)){
                return StaticField.MEDIUMCONSTRAINT+"-"+getContraintValue(constraintDTO) + ");";
            }else {
                return StaticField.SOFTCONSTRAINT +"-"+ getContraintValue(constraintDTO) + ");";
            }
        } else {
            if (constraintDTO.getLevel().equals(StaticField.HARD)) {
                return StaticField.HARDCONSTRAINT + constraintDTO.getLevelNo() + ",-" + getContraintValue(constraintDTO) + ");";
            } else if (constraintDTO.getLevel().equals(StaticField.MEDIUM)) {
                return StaticField.SOFTCONSTRAINT + constraintDTO.getLevelNo() + ",-" + getContraintValue(constraintDTO) + ");";
            } else {
                return StaticField.SOFTCONSTRAINT + (mediumLevel + constraintDTO.getLevelNo()) + ",-"+getContraintValue(constraintDTO) + ");";
            }
        }
    }


    private String getContraintValue(ConstraintDTO constraintDTO) {
        if (constraintDTO.getLevelValue() != null && constraintDTO.getLevelValue() != 0) {
            return Integer.toString(constraintDTO.getLevelValue());
        } else if (constraintDTO.getRuleDTO().getOutputValues()!=null && constraintDTO.getRuleDTO().getOutputValues().size() > 0) {
            return constraintDTO.getRuleDTO().getOutputValues().get(0);
        } else {
            return Integer.toString(constraintDTO.getRuleDTO().getNoOfruleValues());
        }
    }
*/


/*
    public void droolsConfig() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name("com.kairos.planner.rules").newImport().target("").end().newGlobal().type("BendableLongScoreHolder").identifier("scoreHolder;").end()
                .newRule().name("Skill requirements")
                .attribute("salience","10")
                .lhs()
                .and()
                .pattern("task").constraint("missingSkillCount > 0 && $missingSkillCount : missingSkillCount").end()
                .end()
                .end()
                .rhs("scoreHolder.addHardConstraintMatch(kcontext,0,  - 10);").end()
                .getDescr();
        DrlDumper dumper = new DrlDumper();
        String drl = dumper.dump(pkg);
        System.out.print(drl);
        try {
            File file = new File("/media/pradeep/bak/test.drl");
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(drl);
            bw.close();
            System.out.println("File Created Successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }*/


}
