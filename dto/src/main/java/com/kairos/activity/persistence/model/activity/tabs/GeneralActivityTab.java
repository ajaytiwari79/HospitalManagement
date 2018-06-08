package com.kairos.activity.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.response.dto.tag.TagDTO;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 22/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralActivityTab implements Serializable {

    private String name;
    private String code;
    private String printoutSymbol;
    private BigInteger categoryId;
    private Boolean colorPresent;
    private String backgroundColor;
    private String textColor;
    private String description;
    private boolean isActive =true;
    private  String shortName;
    private boolean eligibleForUse=true;
    private String originalIconName;
    private String modifiedIconName;
    private String ultraShortName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean overRuleCtaWta;

    private List<TagDTO> tags = new ArrayList<>();
    private String payrollType;
    private String payrollSystem;

    public GeneralActivityTab() {
    }

    public GeneralActivityTab(String name, String description,String ultraShortName) {
        this.name = name;
        this.description = description;
        this.ultraShortName=ultraShortName;
    }

    public GeneralActivityTab(String name, String code, String printoutSymbol, BigInteger categoryId, Boolean colorPresent, String backgroundColor, String textColor, String description) {
        this.name = name;
        this.code = code;
        this.printoutSymbol = printoutSymbol;
        this.categoryId = categoryId;
        this.colorPresent = colorPresent;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.description = description;
    }

    public GeneralActivityTab(String name, String code, String printoutSymbol, BigInteger categoryId, Boolean colorPresent, String backgroundColor,
                              String textColor, String description, boolean isActive, String shortName, boolean eligibleForUse) {
        this.name = name;
        this.code = code;
        this.printoutSymbol = printoutSymbol;
        this.categoryId = categoryId;
        this.colorPresent = colorPresent;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.description = description;
        this.isActive = isActive;
        this.shortName = shortName;
        this.eligibleForUse=eligibleForUse;
    }


    public GeneralActivityTab(String name, String code, String printoutSymbol, BigInteger categoryId, Boolean colorPresent, String backgroundColor, String textColor, String description,
                              boolean isActive, String shortName, boolean eligibleForUse, String originalIconName, String modifiedIconName,String ultraShortName,boolean overRuleCtaWta, String payrollSystem, String payrollType,LocalDate startDate,LocalDate endDate) {
        this.name = name;
        this.code = code;
        this.printoutSymbol = printoutSymbol;
        this.categoryId = categoryId;
        this.colorPresent = colorPresent;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.description = description;
        this.isActive = isActive;
        this.shortName = shortName;
        this.eligibleForUse = eligibleForUse;
        this.originalIconName = originalIconName;
        this.modifiedIconName = modifiedIconName;
        this.ultraShortName=ultraShortName;

        this.overRuleCtaWta=overRuleCtaWta;
        this.payrollSystem = payrollSystem;
        this.payrollType = payrollType;
        this.startDate=startDate;
        this.endDate=endDate;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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

    public String getUltraShortName() {
        return ultraShortName;
    }

    public void setUltraShortName(String ultraShortName) {
        this.ultraShortName = ultraShortName;
    }

    public boolean isOverRuleCtaWta() {
        return overRuleCtaWta;
    }

    public void setOverRuleCtaWta(boolean overRuleCtaWta) {
        this.overRuleCtaWta = overRuleCtaWta;
    }

    public String getPayrollType() {
        return payrollType;
    }

    public void setPayrollType(String payrollType) {
        this.payrollType = payrollType;
    }

    public String getPayrollSystem() {
        return payrollSystem;
    }

    public void setPayrollSystem(String payrollSystem) {
        this.payrollSystem = payrollSystem;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
