package com.kairos.persistence.model.activity.tabs.rules_activity_tab;

import com.kairos.enums.sickness.ReplaceSkillActivityEnum;
import com.kairos.persistence.model.tag.Tag;

import java.util.List;

public class SicknessSetting {
    private boolean layerForPublishedShift;
    private boolean layerForUnPublishedShift;
    private ReplaceSkillActivityEnum replaceSkillActivityEnum;
    private boolean usedOnMainEmployments;
    private boolean allowedAutoAbsence;
    private byte recurrenceDays;// if a staff fall sick and select this activity then for recurrence days and times --   -128 to 127
    private byte recurrenceTimes;// -- the  shift of that staff will be entered.     -128 to 127
    private boolean childCare;
    private List<Tag> staffTags;
    private boolean topOnApprovedAbsences;
    private boolean usedOnFreeDays;
    private boolean usedOnProtecedDaysOff;




}
