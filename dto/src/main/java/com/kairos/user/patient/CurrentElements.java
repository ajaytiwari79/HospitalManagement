package com.kairos.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.user.client.CitizenSupplier;
import com.kairos.user.visitation.RepetitionNext;

import java.util.Date;

/**
 * Created by oodles on 26/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentElements {

    private Paragraph paragraph;

    private String elementGrouping;

    private Integer priority;

    private String type;

    @JsonIgnoreProperties
    private String pattern;

    @JsonIgnoreProperties
    private String count;

    @JsonIgnoreProperties
    private RepetitionNext next;

    @JsonIgnoreProperties
    private Date date;

    @JsonIgnoreProperties
    private Integer number;

    @JsonIgnoreProperties
    private CitizenSupplier supplier;

    @JsonIgnoreProperties
    private String text;

    public RepetitionNext getNext() {
        return next;
    }

    public void setNext(RepetitionNext next) {
        this.next = next;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Paragraph getParagraph ()
    {
        return paragraph;
    }

    public void setParagraph (Paragraph paragraph)
    {
        this.paragraph = paragraph;
    }

    public String getElementGrouping ()
    {
        return elementGrouping;
    }

    public void setElementGrouping (String elementGrouping)
    {
        this.elementGrouping = elementGrouping;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public CitizenSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(CitizenSupplier supplier) {
        this.supplier = supplier;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [paragraph = "+paragraph+", elementGrouping = "+elementGrouping+", priority = "+priority+", type = "+type+"]";
    }
}