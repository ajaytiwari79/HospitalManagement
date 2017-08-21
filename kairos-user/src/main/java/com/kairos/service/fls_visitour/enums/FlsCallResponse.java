package com.kairos.service.fls_visitour.enums;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
public enum FlsCallResponse {
       ZERO(0, "Function successful", false),
       ONE(1, "Function successful, call geocoded only by city center", false),
       TWO(2, "Call not geocoded"),
       THREE(3, "Appointments invalid due to date or period errors, e.g. date invalid, planning in past not allowed"),
       ONEZERO(10, "No valid suggestions for appointment could be found. FunctionCode 1 only. See infotext."),
       TWOZERO(20, "No valid suggestions for appointment could be found. Confirmation of appointment failed. FixedDate empty and FunctionCode 2 only. See infotext)"),
       THREEZERO(30, "Requested call not found."),
       FOURZERO(40, "Call could not be deleted (FunctionCode 4 only) because of call status greater 3 (=fixed)"),
       TOOHIGH(11010, "EmploymentStatus of the call too high for an update."),
       NINENINE(-99, "Other error.");

    FlsCallResponse(int code , String message){
        this.code = code;
        this.message = message;
        this.isError = true;
    }
    FlsCallResponse(int code , String message, boolean isError){
        this.code = code;
        this.message = message;
        this.isError = isError;
    }
    public static FlsCallResponse getByCode(int code) {
        for(FlsCallResponse r: FlsCallResponse.values()) {
            if(r.code == code) {
                return r;
            }
        }
        return null;
    }

    private int  code;
    public String  message;
    public boolean isError;
}
