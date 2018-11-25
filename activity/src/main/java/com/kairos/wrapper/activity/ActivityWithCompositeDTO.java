package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.activity.activity_tabs.TimeCalculationActivityDTO;
import com.kairos.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.persistence.model.activity.tabs.GeneralActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.activity.tabs.SkillActivityTab;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 8/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityWithCompositeDTO {

    private BigInteger id;
    private String name;
    private GeneralActivityTab generalActivityTab;
    private TimeCalculationActivityDTO timeCalculationActivityTab;
    private List<CompositeActivityDTO> compositeActivities= new ArrayList<>();
    private List<Long> expertises= new ArrayList<>();
    private List<Long> employmentTypes= new ArrayList<>();
    private RulesActivityTab rulesActivityTab;
    private SkillActivityTab skillActivityTab;
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private BalanceSettingsActivityTab balanceSettingsActivityTab;


    public ActivityWithCompositeDTO() {
        //Default Constructor
    }

    public TimeCalculationActivityDTO getTimeCalculationActivityTab() {
        return timeCalculationActivityTab;
    }

    public void setTimeCalculationActivityTab(TimeCalculationActivityDTO timeCalculationActivityTab) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<CompositeActivityDTO> getCompositeActivities() {
        return compositeActivities;
    }

    public void setCompositeActivities(List<CompositeActivityDTO> compositeActivities) {
        this.compositeActivities = compositeActivities;
    }


    public GeneralActivityTab getGeneralActivityTab() {
        return generalActivityTab;
    }

    public void setGeneralActivityTab(GeneralActivityTab generalActivityTab) {
        this.generalActivityTab = generalActivityTab;
    }

    public List<Long> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<Long> expertises) {
        this.expertises = expertises;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public RulesActivityTab getRulesActivityTab() {
        return rulesActivityTab;
    }

    public void setRulesActivityTab(RulesActivityTab rulesActivityTab) {
        this.rulesActivityTab = rulesActivityTab;
    }

    public SkillActivityTab getSkillActivityTab() {
        return skillActivityTab;
    }

    public void setSkillActivityTab(SkillActivityTab skillActivityTab) {
        this.skillActivityTab = skillActivityTab;
    }

    public PhaseSettingsActivityTab getPhaseSettingsActivityTab() {
        return phaseSettingsActivityTab;
    }

    public void setPhaseSettingsActivityTab(PhaseSettingsActivityTab phaseSettingsActivityTab) {
        this.phaseSettingsActivityTab = phaseSettingsActivityTab;
    }

    public BalanceSettingsActivityTab getBalanceSettingsActivityTab() {
        return balanceSettingsActivityTab;
    }

    public void setBalanceSettingsActivityTab(BalanceSettingsActivityTab balanceSettingsActivityTab) {
        this.balanceSettingsActivityTab = balanceSettingsActivityTab;
    }
}
