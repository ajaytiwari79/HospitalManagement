package com.kairos.activity.shift;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;


public class ShiftResponse {
    private BigInteger shiftId;
    private String shiftName;
    private List<String> messages;
    private boolean success = false;
    private LocalDate shiftCreationDate;


    public ShiftResponse() {
        //Default Constructor
    }

    public ShiftResponse(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDate getShiftCreationDate() {
        return shiftCreationDate;
    }

    public void setShiftCreationDate(LocalDate shiftCreationDate) {
        this.shiftCreationDate = shiftCreationDate;
    }

    public ShiftResponse(BigInteger shiftId, String shiftName, List<String> messages, Boolean success, LocalDate shiftCreationDate) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.messages = messages;
        this.success = success;
        this.shiftCreationDate = shiftCreationDate;
    }

    public ShiftResponse(BigInteger shiftId, String shiftName, List<String> messages, boolean success) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.messages = messages;
        this.success = success;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ShiftResponse{");
        sb.append("shiftId=").append(shiftId);
        sb.append(", shiftName='").append(shiftName).append('\'');
        sb.append(", messages=").append(messages);
        sb.append(", success=").append(success);
        sb.append(", shiftCreationDate=").append(shiftCreationDate);
        sb.append('}');
        return sb.toString();
    }
}
