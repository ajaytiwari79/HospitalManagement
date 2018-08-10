package com.kairos.scheduler.persistence.model.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by pankaj on 4/3/17.
 */
@Document(collection = "kairos_sequence")
public class MongoSequence implements Serializable {

    @Id
    private String id;
    private int sequenceNumber;
    @Indexed
    private String sequenceName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    @Override
    public String toString() {
        return "MongoSequence{" +
                "id='" + id + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", sequenceName=" + sequenceName +
                '}';
    }
}
