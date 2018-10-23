package com.planner.domain.wta.templates;

import com.planner.domain.MongoBaseEntity;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * This is to be moved to submodule thats shared between activity and planner
 */
//@Document
public class WorkingTimeAgreement extends MongoBaseEntity {
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<WTABaseRuleTemplate> templates;

    public WorkingTimeAgreement(String name, String description, Date startDate, Date endDate, List<WTABaseRuleTemplate> templates, BigInteger kairosId) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.templates = templates;
        //this.kairosId=kairosId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<WTABaseRuleTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<WTABaseRuleTemplate> templates) {
        this.templates = templates;
    }
}
