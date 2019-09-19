package com.kairos.persistence.model.task;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by prabjot on 16/7/17.
 */
public class UnhandledTaskCount {

    private int unhandledTasksTodayCount;
    private Set<BigInteger> unhandledTodayTasks;
    private int unhandledTasksTomorrowCount;
    private Set<BigInteger> unhandledTomorrowTasks;
    private int unhandledTasksDayAfterTomorrowCount;
    private Set<BigInteger> unhandledDayAfterTomorrowTasks;
    private int unhandledTasksOneWeekCount;
    private Set<BigInteger> unhandledOneWeekTasks;
    private int unhandledTasksTwoWeekCount;
    private Set<BigInteger> unhandledTwoWeekTasks;
    private int unhandledTasksThreeWeekCount;
    private Set<BigInteger> unhandledThreeWeekTasks;
    private int unhandledTasksFourWeekCount;
    private Set<BigInteger> unhandledFourWeekTasks;

    public int getUnhandledTasksTodayCount() {
        return unhandledTasksTodayCount;
    }

    public void setUnhandledTasksTodayCount(int unhandledTasksTodayCount) {
        this.unhandledTasksTodayCount = unhandledTasksTodayCount;
    }

    public Set<BigInteger> getUnhandledTodayTasks() {
        return Optional.ofNullable(unhandledTodayTasks).orElse(new HashSet<>());
    }

    public void setUnhandledTodayTasks(Set<BigInteger> unhandledTodayTasks) {
        this.unhandledTodayTasks = unhandledTodayTasks;
    }

    public int getUnhandledTasksTomorrowCount() {
        return unhandledTasksTomorrowCount;
    }

    public void setUnhandledTasksTomorrowCount(int unhandledTasksTomorrowCount) {
        this.unhandledTasksTomorrowCount = unhandledTasksTomorrowCount;
    }

    public Set<BigInteger> getUnhandledTomorrowTasks() {
        return Optional.ofNullable(unhandledTomorrowTasks).orElse(new HashSet<>());
    }

    public void setUnhandledTomorrowTasks(Set<BigInteger> unhandledTomorrowTasks) {
        this.unhandledTomorrowTasks = unhandledTomorrowTasks;
    }

    public int getUnhandledTasksDayAfterTomorrowCount() {
        return unhandledTasksDayAfterTomorrowCount;
    }

    public void setUnhandledTasksDayAfterTomorrowCount(int unhandledTasksDayAfterTomorrowCount) {
        this.unhandledTasksDayAfterTomorrowCount = unhandledTasksDayAfterTomorrowCount;
    }

    public Set<BigInteger> getUnhandledDayAfterTomorrowTasks() {
        return Optional.ofNullable(unhandledDayAfterTomorrowTasks).orElse(new HashSet<>());
    }

    public void setUnhandledDayAfterTomorrowTasks(Set<BigInteger> unhandledDayAfterTomorrowTasks) {
        this.unhandledDayAfterTomorrowTasks = unhandledDayAfterTomorrowTasks;
    }

    public int getUnhandledTasksOneWeekCount() {
        return unhandledTasksOneWeekCount;
    }

    public void setUnhandledTasksOneWeekCount(int unhandledTasksOneWeekCount) {
        this.unhandledTasksOneWeekCount = unhandledTasksOneWeekCount;
    }

    public Set<BigInteger> getUnhandledOneWeekTasks() {
        return Optional.ofNullable(unhandledOneWeekTasks).orElse(new HashSet<>());
    }

    public void setUnhandledOneWeekTasks(Set<BigInteger> unhandledOneWeekTasks) {
        this.unhandledOneWeekTasks = unhandledOneWeekTasks;
    }

    public int getUnhandledTasksTwoWeekCount() {
        return unhandledTasksTwoWeekCount;
    }

    public void setUnhandledTasksTwoWeekCount(int unhandledTasksTwoWeekCount) {
        this.unhandledTasksTwoWeekCount = unhandledTasksTwoWeekCount;
    }

    public Set<BigInteger> getUnhandledTwoWeekTasks() {
        return Optional.ofNullable(unhandledTwoWeekTasks).orElse(new HashSet<>());
    }

    public void setUnhandledTwoWeekTasks(Set<BigInteger> unhandledTwoWeekTasks) {
        this.unhandledTwoWeekTasks = unhandledTwoWeekTasks;
    }

    public int getUnhandledTasksThreeWeekCount() {
        return unhandledTasksThreeWeekCount;
    }

    public void setUnhandledTasksThreeWeekCount(int unhandledTasksThreeWeekCount) {
        this.unhandledTasksThreeWeekCount = unhandledTasksThreeWeekCount;
    }

    public Set<BigInteger> getUnhandledThreeWeekTasks() {
        return Optional.ofNullable(unhandledThreeWeekTasks).orElse(new HashSet<>());
    }

    public void setUnhandledThreeWeekTasks(Set<BigInteger> unhandledThreeWeekTasks) {
        this.unhandledThreeWeekTasks = unhandledThreeWeekTasks;
    }

    public int getUnhandledTasksFourWeekCount() {
        return unhandledTasksFourWeekCount;
    }

    public void setUnhandledTasksFourWeekCount(int unhandledTasksFourWeekCount) {
        this.unhandledTasksFourWeekCount = unhandledTasksFourWeekCount;
    }

    public Set<BigInteger> getUnhandledFourWeekTasks() {
        return Optional.ofNullable(unhandledFourWeekTasks).orElse(new HashSet<>());
    }

    public void setUnhandledFourWeekTasks(Set<BigInteger> unhandledFourWeekTasks) {
        this.unhandledFourWeekTasks = unhandledFourWeekTasks;
    }

    public UnhandledTaskCount resetValues(){
        this.unhandledTasksTodayCount = 0;
        this.unhandledTodayTasks = null;
        this.unhandledTasksTomorrowCount = 0;
        this.unhandledTomorrowTasks = null;
        this.unhandledTasksDayAfterTomorrowCount = 0;
        this.unhandledDayAfterTomorrowTasks = null;
        this.unhandledTasksOneWeekCount = 0;
        this.unhandledOneWeekTasks = null;
        this.unhandledTasksTwoWeekCount = 0;
        this.unhandledTwoWeekTasks = null;
        this.unhandledTasksThreeWeekCount = 0;
        this.unhandledThreeWeekTasks = null;
        this.unhandledTasksFourWeekCount = 0;
        this.unhandledFourWeekTasks = null;
        return this;
    }

    @Override
    public String toString() {
        return "UnhandledTaskCount{" +
                "unhandledTasksTodayCount=" + unhandledTasksTodayCount +
                ", unhandledTodayTasks=" + unhandledTodayTasks +
                ", unhandledTasksTomorrowCount=" + unhandledTasksTomorrowCount +
                ", unhandledTomorrowTasks=" + unhandledTomorrowTasks +
                ", unhandledTasksDayAfterTomorrowCount=" + unhandledTasksDayAfterTomorrowCount +
                ", unhandledDayAfterTomorrowTasks=" + unhandledDayAfterTomorrowTasks +
                ", unhandledTasksOneWeekCount=" + unhandledTasksOneWeekCount +
                ", unhandledOneWeekTasks=" + unhandledOneWeekTasks +
                ", unhandledTasksTwoWeekCount=" + unhandledTasksTwoWeekCount +
                ", unhandledTwoWeekTasks=" + unhandledTwoWeekTasks +
                ", unhandledTasksThreeWeekCount=" + unhandledTasksThreeWeekCount +
                ", unhandledThreeWeekTasks=" + unhandledThreeWeekTasks +
                ", unhandledTasksFourWeekCount=" + unhandledTasksFourWeekCount +
                ", unhandledFourWeekTasks=" + unhandledFourWeekTasks +
                '}';
    }
}
