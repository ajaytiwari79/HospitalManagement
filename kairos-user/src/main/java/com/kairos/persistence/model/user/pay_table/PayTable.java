package com.kairos.persistence.model.user.pay_table;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 21/12/17.
 */
@NodeEntity
public class PayTable extends UserBaseEntity {
    private String name;
    private String shortName;
    @Relationship(type = IN_ORGANIZATION_LEVEL)
    private Level level;
    @DateLong
    private Date startDate;
    @DateLong
    private Date endDate;
    @Relationship(type = HAS_PAY_GRADE)
    private List<PayGrade> payGrades;
    private String description;
    public PayTable() {
        //default constructor
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<PayGrade> getPayGrades() {
        return payGrades;
    }

    public void setPayGrades(List<PayGrade> payGrades) {
        this.payGrades = payGrades;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PayTable(String name, String shortName,String description, Level level, Date startDate, Date endDate) {
        this.name = name;
        this.description=description;
        this.shortName = shortName;
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
