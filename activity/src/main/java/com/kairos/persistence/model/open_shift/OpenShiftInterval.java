package com.kairos.persistence.model.open_shift;

import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.enums.DurationType;
import org.springframework.data.mongodb.core.mapping.Document;

//Domain name can be changed
@Document
public class OpenShiftInterval extends MongoBaseEntity implements Comparable<OpenShiftInterval> {
    private int from;
    private int to;
    private Long countryId;
    private DurationType type;

    public OpenShiftInterval() {
        //Default Constructor
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public DurationType getType() {
        return type;
    }

    public void setType(DurationType type) {
        this.type = type;
    }

    @Override
    public int compareTo(OpenShiftInterval o) {
        return this.from-o.from;
    }
}
