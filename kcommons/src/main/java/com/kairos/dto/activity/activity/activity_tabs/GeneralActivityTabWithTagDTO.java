package com.kairos.dto.activity.activity.activity_tabs;

import com.kairos.dto.user.country.tag.TagDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This DTO is made just to customize the default view at frontend
 * For example:-
 * Default domain have
 * List<BigInteger> tags  but required List<Tag> tags
 * @Note:- please update the comment list whenever any more changes done
 */
public class GeneralActivityTabWithTagDTO {

    private String name;
    private String code;
    private String printoutSymbol;
    private BigInteger categoryId;
    private Boolean colorPresent;
    private String backgroundColor;
    private String description;
    private boolean isActive =true;
    private  String shortName;
    private boolean eligibleForUse=true;
    private String originalIconName;
    private String modifiedIconName;
    private String ultraShortName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TagDTO> tags = new ArrayList<>();

   //=======================================================================

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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
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

    public String getUltraShortName() {
        return ultraShortName;
    }

    public void setUltraShortName(String ultraShortName) {
        this.ultraShortName = ultraShortName;
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

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public String getPrintoutSymbol() {
        return printoutSymbol;
    }

    public void setPrintoutSymbol(String printoutSymbol) {
        this.printoutSymbol = printoutSymbol;
    }
}
