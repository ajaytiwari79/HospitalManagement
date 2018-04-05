package com.kairos.activity.response.dto.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.activity.tabs.GeneralActivityTab;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 22/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralActivityTabDTO {

    private BigInteger activityId;
    private String name;
    private String code;
    private String printoutSymbol;
    private String categoryName;
    private BigInteger categoryId;
    private Boolean colorPresent;
    private String backgroundColor;
    private String textColor;
    private String description;
    private boolean isActive=true;
    private  String shortName;
    private boolean eligibleForUse=true;
    private String ultraShortName;
    private String bonusHoursType;
    private boolean overRuleCtaWta;

    public String getUltraShortName() {
        return ultraShortName;
    }

    public void setUltraShortName(String ultraShortName) {
        this.ultraShortName = ultraShortName;
    }

    @Override
    public String toString() {
        return "GeneralActivityTabDTO{" +
                "activityId=" + activityId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", printoutSymbol='" + printoutSymbol + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryId=" + categoryId +
                ", colorPresent=" + colorPresent +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", textColor='" + textColor + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", shortName='" + shortName + '\'' +
                ", eligibleForUse=" + eligibleForUse +
                ", originalIconName='" + originalIconName + '\'' +
                ", modifiedIconName='" + modifiedIconName + '\'' +
                ", tags=" + tags +
                '}';
    }

    private String originalIconName;
    private String modifiedIconName;
    private List<BigInteger> tags = new ArrayList<>();

    public GeneralActivityTab buildGeneralActivityTab() {
        GeneralActivityTab generalActivityTab =
                new GeneralActivityTab(name, code, printoutSymbol,categoryId, colorPresent, backgroundColor, textColor, description,this.isActive,
                        this.shortName,this.eligibleForUse,this.originalIconName,this.modifiedIconName,ultraShortName,bonusHoursType,overRuleCtaWta);
        return generalActivityTab;
    }
    public GeneralActivityTab buildGeneralTabWithNameAndDesc() {
        GeneralActivityTab general = new GeneralActivityTab(name, description,ultraShortName);

        return general;
    }

    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrintoutSymbol() {
        return printoutSymbol;
    }

    public void setPrintoutSymbol(String printoutSymbol) {
        this.printoutSymbol = printoutSymbol;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }



    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

     public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getColorPresent() {
        return colorPresent;
    }

    public void setColorPresent(Boolean colorPresent) {
        this.colorPresent = colorPresent;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isEligibleForUse() {
        return eligibleForUse;
    }

    public void setEligibleForUse(boolean eligibleForUse) {
        this.eligibleForUse = eligibleForUse;
    }

    public String getOriginalIconName() {
        return originalIconName;
    }

    public void setOriginalIconName(String originalIconName) {
        this.originalIconName = originalIconName;
    }

    public String getModifiedIconName() {
        return modifiedIconName;
    }


    public void setModifiedIconName(String modifiedIconName) {
        this.modifiedIconName = modifiedIconName;
    }

    public String getBonusHoursType() {
        return bonusHoursType;
    }

    public void setBonusHoursType(String bonusHoursType) {
        this.bonusHoursType = bonusHoursType;
    }

    public boolean isOverRuleCtaWta() {
        return overRuleCtaWta;
    }

    public void setOverRuleCtaWta(boolean overRuleCtaWta) {
        this.overRuleCtaWta = overRuleCtaWta;
    }
}
