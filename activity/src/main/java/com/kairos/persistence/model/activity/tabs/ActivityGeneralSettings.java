package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.annotations.KPermissionField;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGeneralSettings implements Serializable {
    @KPermissionField
    private String name;
    @KPermissionField
    private String code;
    @KPermissionField
    private String printoutSymbol;
    @KPermissionField
    private BigInteger categoryId;
    @KPermissionField
    private Boolean colorPresent;
    private String backgroundColor;
    @KPermissionField
    private String description;
    @KPermissionField
    private boolean active = true;
    @KPermissionField
    private String shortName;
    private boolean eligibleForUse = true;
    private String originalIconName;
    private String modifiedIconName;
    @KPermissionField
    private String ultraShortName;
    @KPermissionField
    private LocalDate startDate;
    @KPermissionField
    private LocalDate endDate;

    @KPermissionField
    private List<BigInteger> tags = new ArrayList<>();
    private Map<String, TranslationInfo> translations;

    public ActivityGeneralSettings(String name, String description, String ultraShortName) {
        this.name = name;
        this.description = description;
        this.ultraShortName = ultraShortName;
    }

    public String getName() {
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),description);
    }

}

