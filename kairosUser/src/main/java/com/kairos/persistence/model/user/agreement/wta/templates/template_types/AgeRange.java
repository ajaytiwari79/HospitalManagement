package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pavan on 24/4/18.
 */
@NodeEntity
public class AgeRange extends UserBaseEntity{
    private int from;
    private int to;
    private int numberOfLeaves;

    public AgeRange() {
        //Default Constructor
    }

    public AgeRange(int from, int to, int numberOfLeaves) {
        this.from = from;
        this.to = to;
        this.numberOfLeaves = numberOfLeaves;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getNumberOfLeaves() {
        return numberOfLeaves;
    }

    public void setNumberOfLeaves(int numberOfLeaves) {
        this.numberOfLeaves = numberOfLeaves;
    }
}
