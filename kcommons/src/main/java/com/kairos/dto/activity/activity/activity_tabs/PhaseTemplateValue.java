package com.kairos.dto.activity.activity.activity_tabs;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by pavan on 7/2/18.
 */

//This for Activity
@NoArgsConstructor
@Getter
@Setter
public class PhaseTemplateValue implements Comparable<PhaseTemplateValue>{
    private BigInteger phaseId;
    private String name;
    private String description;
    private List<Long> eligibleEmploymentTypes;
    private boolean eligibleForManagement;
    private boolean staffCanDelete;
    private boolean managementCanDelete;
    private boolean staffCanSell;
    private boolean managementCanSell;
    private int sequence;
    private AllowedSettings allowedSettings;
    private List<ActivityShiftStatusSettings> activityShiftStatusSettings;
    private Map<String, TranslationInfo> translations;

    public PhaseTemplateValue(BigInteger phaseId, String name, String description, List<Long> eligibleEmploymentTypes, boolean eligibleForManagement,
                              boolean staffCanDelete, boolean managementCanDelete, boolean staffCanSell, boolean managementCanSell,AllowedSettings allowedSettings) {
        this.phaseId = phaseId;
        this.name = name;
        this.description = description;
        this.eligibleEmploymentTypes = eligibleEmploymentTypes;
        this.eligibleForManagement = eligibleForManagement;
        this.staffCanDelete = staffCanDelete;
        this.managementCanDelete = managementCanDelete;
        this.staffCanSell = staffCanSell;
        this.managementCanSell = managementCanSell;
        this.allowedSettings=allowedSettings;
    }
    public List<ActivityShiftStatusSettings> getActivityShiftStatusSettings() {
        return Optional.ofNullable(activityShiftStatusSettings).orElse(new ArrayList<>());
    }

    public String getName(){
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }

    public String getDescription(){
        return TranslationUtil.getDescription(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),description);
    }

    @Override
    public int compareTo(PhaseTemplateValue phaseTemplateValue) {
        return Integer.compare(this.sequence,phaseTemplateValue.sequence);
    }
}
