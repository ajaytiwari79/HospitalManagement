package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShiftResponse {
    private BigInteger shiftId;
    private String shiftName;
    private List<String> messages;
    private boolean success = false;
    private LocalDate shiftCreationDate;


    public ShiftResponse(BigInteger shiftId, String shiftName, List<String> messages, Boolean success, LocalDate shiftCreationDate) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.messages = messages;
        this.success = success;
        this.shiftCreationDate = shiftCreationDate;
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
