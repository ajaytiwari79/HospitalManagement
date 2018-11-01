package com.planner.domain.common;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "planner_sequence")
public class MongoSequence {

    @Id
    private String id;
    @Indexed
    private String sequenceName;//Class name for which sequence will be generate
    private int sequenceNumber;

    //Setters and Getters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSequenceName() {
        return sequenceName;
    }
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }
    public int getSequenceNumber() {
        return sequenceNumber;
    }
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
