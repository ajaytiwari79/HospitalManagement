package com.kairos.persistence.model.user.pay_table;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;

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
    private Date startDateMillis;
    @DateLong
    private Date endDateMillis;
    private String paymentUnit;
    @Relationship(type = HAS_PAY_GRADE)
    private List<PayGrade> payGrades;
    private String description;
    private boolean published;
    private boolean hasTempCopy;
    private boolean editable = true;

    @Relationship(type = HAS_TEMP_PAY_TABLE, direction = INCOMING)
    private PayTable payTable;

    //
    public PayTable() {
        //default constructor
    }

    public Date getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Date startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Date getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Date endDateMillis) {
        this.endDateMillis = endDateMillis;
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public PayTable getPayTable() {
        return payTable;
    }

    public void setPayTable(PayTable payTable) {
        this.payTable = payTable;
    }

    public PayTable(String name, String shortName, String description, Level level, Date startDateMillis, Date endDateMillis, String paymentUnit, boolean editable) {
        this.name = name;
        this.description = description;
        this.shortName = shortName;
        this.level = level;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.paymentUnit = paymentUnit;
        this.editable=editable;
    }

    public boolean isHasTempCopy() {
        return hasTempCopy;
    }

    public void setHasTempCopy(boolean hasTempCopy) {
        this.hasTempCopy = hasTempCopy;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(String paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
