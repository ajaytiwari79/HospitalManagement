package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class GeneralActivityTab implements Serializable {

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

        private List<BigInteger> tags = new ArrayList<>();

        public GeneralActivityTab() {
        }

        public GeneralActivityTab(String name, String description,String ultraShortName) {
            this.name = name;
            this.description = description;
            this.ultraShortName=ultraShortName;
        }

        public GeneralActivityTab(String name, String code, String printoutSymbol, BigInteger categoryId, Boolean colorPresent, String backgroundColor, String description) {
            this.name = name;
            this.code = code;
            this.printoutSymbol = printoutSymbol;
            this.categoryId = categoryId;
            this.colorPresent = colorPresent;
            this.backgroundColor = backgroundColor;
            this.description = description;
        }

        public GeneralActivityTab(String name, String code, String printoutSymbol, BigInteger categoryId, Boolean colorPresent, String backgroundColor,
                                  String description, boolean isActive, String shortName, boolean eligibleForUse) {
            this.name = name;
            this.code = code;
            this.printoutSymbol = printoutSymbol;
            this.categoryId = categoryId;
            this.colorPresent = colorPresent;
            this.backgroundColor = backgroundColor;
            this.description = description;
            this.isActive = isActive;
            this.shortName = shortName;
            this.eligibleForUse=eligibleForUse;
        }


        public GeneralActivityTab(String name, String code, String printoutSymbol, BigInteger categoryId, Boolean colorPresent, String backgroundColor, String description,
                                  boolean isActive, String shortName, boolean eligibleForUse, String originalIconName, String modifiedIconName,String ultraShortName, LocalDate startDate,LocalDate endDate) {
            this.name = name;
            this.code = code;
            this.printoutSymbol = printoutSymbol;
            this.categoryId = categoryId;
            this.colorPresent = colorPresent;
            this.backgroundColor = backgroundColor;
            this.description = description;
            this.isActive = isActive;
            this.shortName = shortName;
            this.eligibleForUse = eligibleForUse;
            this.originalIconName = originalIconName;
            this.modifiedIconName = modifiedIconName;
            this.ultraShortName=ultraShortName;
            this.startDate=startDate;
            this.endDate=endDate;
        }

        public List<BigInteger> getTags() {
            return tags;
        }

        public void setTags(List<BigInteger> tags) {
            this.tags = tags;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
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

