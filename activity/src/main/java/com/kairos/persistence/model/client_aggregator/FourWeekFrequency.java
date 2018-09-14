package com.kairos.persistence.model.client_aggregator;
import com.kairos.commons.utils.DateUtils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static com.kairos.persistence.model.constants.TaskConstants.*;
import static com.kairos.persistence.model.constants.TaskConstants.DAY_END_MINUTE;
import static com.kairos.persistence.model.constants.TaskConstants.DAY_END_SECOND;

/**
 * Created by prabjot on 26/7/17.
 */
public final class FourWeekFrequency {

    private LocalDateTime startOfDay;
    private LocalDateTime endOfDay;
    private LocalDateTime startOfTomorrow;
    private LocalDateTime endOfTomorrow;
    private LocalDateTime startOfDayAfterTomorrow;
    private LocalDateTime endOfDayAfterTomorrow;
    private LocalDateTime startOfWeek;
    private LocalDateTime endOfWeek;
    private LocalDateTime endOfSecondWeek;
    private LocalDateTime endOfThirdWeek;
    private LocalDateTime endOfFourWeek;

    private FourWeekFrequency(){};

    private static LocalDate getMondayOfThisWeek(){
        return (LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) ? LocalDate.now() : DateUtils.getDateForPreviousDay(LocalDate.now(), DayOfWeek.MONDAY);
    }

    private static LocalDate getSundayOfFourthWeek(LocalDate startDate){
        return startDate.plusDays(27);
    }

    public LocalDateTime getStartOfDay() {
        return startOfDay;
    }

    public FourWeekFrequency setStartOfDay(LocalDateTime startOfDay) {
        this.startOfDay = startOfDay;
        return this;
    }

    public LocalDateTime getEndOfDay() {
        return endOfDay;
    }

    public FourWeekFrequency setEndOfDay(LocalDateTime endOfDay) {
        this.endOfDay = endOfDay;
        return this;
    }

    public LocalDateTime getStartOfTomorrow() {
        return startOfTomorrow;
    }

    public FourWeekFrequency setStartOfTomorrow(LocalDateTime startOfTomorrow) {
        this.startOfTomorrow = startOfTomorrow;
        return this;
    }

    public LocalDateTime getEndOfTomorrow() {
        return endOfTomorrow;
    }

    public FourWeekFrequency setEndOfTomorrow(LocalDateTime endOfTomorrow) {
        this.endOfTomorrow = endOfTomorrow;
        return this;
    }

    public LocalDateTime getStartOfDayAfterTomorrow() {
        return startOfDayAfterTomorrow;
    }

    public FourWeekFrequency setStartOfDayAfterTomorrow(LocalDateTime startOfDayAfterTomorrow) {
        this.startOfDayAfterTomorrow = startOfDayAfterTomorrow;
        return this;
    }

    public LocalDateTime getEndOfDayAfterTomorrow() {
        return endOfDayAfterTomorrow;
    }

    public FourWeekFrequency setEndOfDayAfterTomorrow(LocalDateTime endOfDayAfterTomorrow) {
        this.endOfDayAfterTomorrow = endOfDayAfterTomorrow;
        return this;
    }

    public LocalDateTime getStartOfWeek() {
        return startOfWeek;
    }

    public FourWeekFrequency setStartOfWeek(LocalDateTime startOfWeek) {
        this.startOfWeek = startOfWeek;
        return this;
    }

    public LocalDateTime getEndOfWeek() {
        return endOfWeek;
    }

    public FourWeekFrequency setEndOfWeek(LocalDateTime endOfWeek) {
        this.endOfWeek = endOfWeek;
        return this;
    }

    public LocalDateTime getEndOfSecondWeek() {
        return endOfSecondWeek;
    }

    public FourWeekFrequency setEndOfSecondWeek(LocalDateTime endOfSecondWeek) {
        this.endOfSecondWeek = endOfSecondWeek;
        return this;
    }

    public LocalDateTime getEndOfThirdWeek() {
        return endOfThirdWeek;
    }

    public FourWeekFrequency setEndOfThirdWeek(LocalDateTime endOfThirdWeek) {
        this.endOfThirdWeek = endOfThirdWeek;
        return this;
    }

    public LocalDateTime getEndOfFourWeek() {
        return endOfFourWeek;
    }

    public FourWeekFrequency setEndOfFourWeek(LocalDateTime endOfFourWeek) {
        this.endOfFourWeek = endOfFourWeek;
        return this;
    }

    public static FourWeekFrequency getInstance(){

        LocalDate mondayOfFirstWeek = getMondayOfThisWeek();
        LocalDate sundayOfFourthWeek = getSundayOfFourthWeek(mondayOfFirstWeek);

        LocalDateTime startOfDay = LocalDateTime.now().withHour(DAY_START_HOUR).withMinute(DAY_START_MINUTE).withSecond(DAY_START_SECOND).withNano(DAY_START_NANO);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND).withNano(DAY_END_NANO);
        LocalDateTime startOfTomorrow = startOfDay.plusDays(1);
        LocalDateTime endOfTomorrow = endOfDay.plusDays(1);
        LocalDateTime startOfDayAfterTomorrow = startOfTomorrow.plusDays(1);
        LocalDateTime endOfDayAfterTomorrow = endOfTomorrow.plusDays(1);
        LocalDateTime startOfWeek = mondayOfFirstWeek.atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND).withNano(DAY_END_NANO);
        LocalDateTime endOfSecondWeek = startOfWeek.plusDays(13).withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND).withNano(DAY_END_NANO);
        LocalDateTime endOfThirdWeek = startOfWeek.plusDays(20).withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND).withNano(DAY_END_NANO);
        LocalDateTime endOfFourWeek = sundayOfFourthWeek.atStartOfDay().withHour(DAY_END_HOUR).withMinute(DAY_END_MINUTE).withSecond(DAY_END_SECOND).withNano(DAY_END_NANO);
        return new FourWeekFrequency().setStartOfDay(startOfDay).setEndOfDay(endOfDay).setStartOfTomorrow(startOfTomorrow).setEndOfTomorrow(endOfTomorrow)
                .setStartOfDayAfterTomorrow(startOfDayAfterTomorrow).setEndOfDayAfterTomorrow(endOfDayAfterTomorrow).setStartOfWeek(startOfWeek).setEndOfWeek(endOfWeek)
                .setEndOfSecondWeek(endOfSecondWeek).setEndOfThirdWeek(endOfThirdWeek).setEndOfFourWeek(endOfFourWeek);

    }

    @Override
    public String toString() {
        return "FourWeekFrequency{" +
                "startOfDay=" + startOfDay +
                ", endOfDay=" + endOfDay +
                ", startOfTomorrow=" + startOfTomorrow +
                ", endOfTomorrow=" + endOfTomorrow +
                ", startOfDayAfterTomorrow=" + startOfDayAfterTomorrow +
                ", endOfDayAfterTomorrow=" + endOfDayAfterTomorrow +
                ", startOfWeek=" + startOfWeek +
                ", endOfWeek=" + endOfWeek +
                ", endOfSecondWeek=" + endOfSecondWeek +
                ", endOfThirdWeek=" + endOfThirdWeek +
                ", endOfFourWeek=" + endOfFourWeek +
                '}';
    }
}
