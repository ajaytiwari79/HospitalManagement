package com.kairos.persistence.model.task_demand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by oodles on 12/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class MonthlyFrequency {

    protected int     monthFrequency;
    protected DayOfWeek dayOfWeek;
    protected WeekOfMonth weekOfMonth;
    protected int      weekdayCount;

    public MonthlyFrequency(){
        //Default Constructor
    }

    public int getMonthFrequency() {
        return monthFrequency;
    }

    public void setMonthFrequency(int monthFrequency) {
        this.monthFrequency = monthFrequency;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public WeekOfMonth getWeekOfMonth() {
        return weekOfMonth;
    }

    public void setWeekOfMonth(WeekOfMonth weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    public enum DayOfWeek{

        MONDAY("Monday"),
        TUESDAY("Tuesday"),
        WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"),
        FRIDAY("Friday");

        public String value;

        DayOfWeek(String value) {
            this.value = value;
        }

        public static MonthlyFrequency.DayOfWeek getByValue(String value){
            for(MonthlyFrequency.DayOfWeek dayOfWeek : MonthlyFrequency.DayOfWeek.values()){
                if(dayOfWeek.value.equals(value)){
                    return dayOfWeek;
                }
            }
            return null;
        }
    }

    public enum WeekOfMonth{

        FIRST("First"),
        SECOND("Second"),
        THIRD("Third"),
        FOURTH("Fourth");

        public String value;

        WeekOfMonth(String value) {
            this.value = value;
        }

        public static MonthlyFrequency.WeekOfMonth getByValue(String value){
            for(MonthlyFrequency.WeekOfMonth weekOfMonth : MonthlyFrequency.WeekOfMonth.values()){
                if(weekOfMonth.value.equals(value)){
                    return weekOfMonth;
                }
            }
            return null;
        }
    }

    public int getWeekdayCount() {
        return weekdayCount;
    }

    public void setWeekdayCount(int weekdayCount) {
        this.weekdayCount = weekdayCount;
    }

   /* @Override
    public String toString() {
        return "MonthlyFrequency{" +
                "monthFrequency=" + monthFrequency +
                ", dayOfWeek=" + dayOfWeek.value +
                ", weekOfMonth=" + weekOfMonth.value +
                *//*", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", nextVisit=" + nextVisit +
                ", weekdayFrequency=" + weekdayFrequency +
                ", weekdayVisits=" + weekdayVisits +
                ", weekdaySupplierId=" + weekdaySupplierId +
                ", weekendFrequency=" + weekendFrequency +
                ", weekendVisits=" + weekendVisits +
                ", weekendSupplierId=" + weekendSupplierId +
                ", lastModifiedByStaffId=" + lastModifiedByStaffId +
                ", demandImages=" + demandImages +
                ", isShift=" + isShift +
                ", unitId=" + unitId +
                ", dayName='" + dayName + '\'' +
                ", citizenId=" + citizenId +
                ", createdByStaffId=" + createdByStaffId +
                ", priority=" + priority +
                ", remarks='" + remarks + '\'' +
                ", isDeleted=" + isDeleted +
                ", visitourTeamId='" + visitourTeamId + '\'' +*//*
                '}';
    }*/

}
