package com.kairos.persistence.model.client_aggregator;
import com.kairos.dto.activity.client_exception.ClientExceptionCount;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.task.UnhandledTaskCount;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 12/7/17.
 */
@Document(collection = "clientAggregator")
public class ClientAggregator extends MongoBaseEntity {
    @Indexed
    @NotNull(message = "error.TaskDemand.unitId.notnull")
    protected long unitId;

    @Indexed
    @NotNull(message = "error.TaskDemand.citizenId.notnull")
    protected long citizenId;

    private int longDrivingFourWeekCount = 0;

    private int longDrivingThreeWeekCount = 0;

    private int longDrivingTwoWeekCount = 0;

    private int longDrivingOneWeekCount = 0;

    private int mostDrivenFourWeekCount = 0;

    private int mostDrivenThreeWeekCount = 0;

    private int mostDrivenTwoWeekCount = 0;

    private int mostDrivenOneWeekCount = 0;

    private int escalationOneWeekCount = 0;

    private int escalationTwoWeekCount = 0;

    private int escalationThreeWeekCount = 0;

    private int escalationFourWeekCount = 0;

    private int waitingFourWeekCount = 0;

    private int waitingThreeWeekCount = 0;

    private int waitingTwoWeekCount = 0;

    private int waitingOneWeekCount = 0;

    private int totalPlannedProblemsFourWeekCount = 0;

    private int totalPlannedProblemsThreeWeekCount = 0;

    private int totalPlannedProblemsTwoWeekCount = 0;

    private int totalPlannedProblemsOneWeekCount = 0;

    private List<BigInteger> longDrivingFourWeekTasks ;

    private List<BigInteger> longDrivingThreeWeekTasks;

    private List<BigInteger> longDrivingTwoWeekTasks;

    private List<BigInteger> longDrivingOneWeekTasks;

    private List<BigInteger> mostDrivenFourWeekTasks;

    private List<BigInteger> mostDrivenThreeWeekTasks;

    private List<BigInteger> mostDrivenTwoWeekTasks;

    private List<BigInteger> mostDrivenOneWeekTasks;

    private List<BigInteger> escalationOneWeekTasks;

    private List<BigInteger> escalationTwoWeekTasks;

    private List<BigInteger> escalationThreeWeekTasks;

    private List<BigInteger> escalationFourWeekTasks;

    private List<BigInteger> waitingFourWeekTasks;

    private List<BigInteger> waitingThreeWeekTasks;

    private List<BigInteger> waitingTwoWeekTasks;

    private List<BigInteger> waitingOneWeekTasks;

    private List<BigInteger> totalPlannedProblemsFourWeekTasks;

    private List<BigInteger> totalPlannedProblemsThreeWeekTasks;

    private List<BigInteger> totalPlannedProblemsTwoWeekTasks;

    private List<BigInteger> totalPlannedProblemsOneWeekTasks;

    private List<ClientExceptionCount> clientExceptionCounts;

    private int plannedStatusFourWeekCount = 0;

    private int plannedStatusThreeWeekCount = 0;

    private int plannedStatusTwoWeekCount = 0;

    private int plannedStatusOneWeekCount = 0;

    private List<BigInteger> plannedStatusFourWeekTasks;

    private List<BigInteger> plannedStatusThreeWeekTasks;

    private List<BigInteger> plannedStatusTwoWeekTasks;

    private List<BigInteger> plannedStatusOneWeekTasks;

    public ClientAggregator(long unitId, long citizenId) {
        this.unitId = unitId;
        this.citizenId = citizenId;
    }

    public List<ClientExceptionCount> getClientExceptionCounts() {
        return Optional.ofNullable(clientExceptionCounts).orElse(new ArrayList<>());
    }

    public void setClientExceptionCounts(List<ClientExceptionCount> clientExceptionCounts) {
        this.clientExceptionCounts = clientExceptionCounts;
    }

    public UnhandledTaskCount getUnhandledTaskCount() {
        return unhandledTaskCount;
    }

    public void setUnhandledTaskCount(UnhandledTaskCount unhandledTaskCount) {
        this.unhandledTaskCount = unhandledTaskCount;
    }

    private UnhandledTaskCount unhandledTaskCount;

    private long visitatedHoursPerWeek;

    private long visitatedHoursPerMonth;

    private long visitatedMinutesPerWeek;

    private long visitatedMinutesPerMonth;

    private float visitatedTasksPerWeek;

    private float visitatedTasksPerMonth;

    public ClientAggregator() {
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(long citizenId) {
        this.citizenId = citizenId;
    }

    public int getLongDrivingFourWeekCount() {
        return longDrivingFourWeekCount;
    }

    public void setLongDrivingFourWeekCount(int longDrivingFourWeekCount) {
        this.longDrivingFourWeekCount = longDrivingFourWeekCount;
    }

    public int getLongDrivingThreeWeekCount() {
        return longDrivingThreeWeekCount;
    }

    public void setLongDrivingThreeWeekCount(int longDrivingThreeWeekCount) {
        this.longDrivingThreeWeekCount = longDrivingThreeWeekCount;
    }

    public int getLongDrivingTwoWeekCount() {
        return longDrivingTwoWeekCount;
    }

    public void setLongDrivingTwoWeekCount(int longDrivingTwoWeekCount) {
        this.longDrivingTwoWeekCount = longDrivingTwoWeekCount;
    }

    public int getLongDrivingOneWeekCount() {
        return longDrivingOneWeekCount;
    }

    public void setLongDrivingOneWeekCount(int longDrivingOneWeekCount) {
        this.longDrivingOneWeekCount = longDrivingOneWeekCount;
    }

    public int getMostDrivenFourWeekCount() {
        return mostDrivenFourWeekCount;
    }

    public void setMostDrivenFourWeekCount(int mostDrivenFourWeekCount) {
        this.mostDrivenFourWeekCount = mostDrivenFourWeekCount;
    }

    public int getMostDrivenThreeWeekCount() {
        return mostDrivenThreeWeekCount;
    }

    public void setMostDrivenThreeWeekCount(int mostDrivenThreeWeekCount) {
        this.mostDrivenThreeWeekCount = mostDrivenThreeWeekCount;
    }

    public int getMostDrivenTwoWeekCount() {
        return mostDrivenTwoWeekCount;
    }

    public void setMostDrivenTwoWeekCount(int mostDrivenTwoWeekCount) {
        this.mostDrivenTwoWeekCount = mostDrivenTwoWeekCount;
    }

    public int getMostDrivenOneWeekCount() {
        return mostDrivenOneWeekCount;
    }

    public void setMostDrivenOneWeekCount(int mostDrivenOneWeekCount) {
        this.mostDrivenOneWeekCount = mostDrivenOneWeekCount;
    }

    public int getEscalationOneWeekCount() {
        return escalationOneWeekCount;
    }

    public void setEscalationOneWeekCount(int escalationOneWeekCount) {
        this.escalationOneWeekCount = escalationOneWeekCount;
    }

    public int getEscalationTwoWeekCount() {
        return escalationTwoWeekCount;
    }

    public void setEscalationTwoWeekCount(int escalationTwoWeekCount) {
        this.escalationTwoWeekCount = escalationTwoWeekCount;
    }

    public int getEscalationThreeWeekCount() {
        return escalationThreeWeekCount;
    }

    public void setEscalationThreeWeekCount(int escalationThreeWeekCount) {
        this.escalationThreeWeekCount = escalationThreeWeekCount;
    }

    public int getEscalationFourWeekCount() {
        return escalationFourWeekCount;
    }

    public void setEscalationFourWeekCount(int escalationFourWeekCount) {
        this.escalationFourWeekCount = escalationFourWeekCount;
    }

    public int getWaitingFourWeekCount() {
        return waitingFourWeekCount;
    }

    public void setWaitingFourWeekCount(int waitingFourWeekCount) {
        this.waitingFourWeekCount = waitingFourWeekCount;
    }

    public int getWaitingThreeWeekCount() {
        return waitingThreeWeekCount;
    }

    public void setWaitingThreeWeekCount(int waitingThreeWeekCount) {
        this.waitingThreeWeekCount = waitingThreeWeekCount;
    }

    public int getWaitingTwoWeekCount() {
        return waitingTwoWeekCount;
    }

    public void setWaitingTwoWeekCount(int waitingTwoWeekCount) {
        this.waitingTwoWeekCount = waitingTwoWeekCount;
    }

    public int getWaitingOneWeekCount() {
        return waitingOneWeekCount;
    }

    public void setWaitingOneWeekCount(int waitingOneWeekCount) {
        this.waitingOneWeekCount = waitingOneWeekCount;
    }

    public int getTotalPlannedProblemsFourWeekCount() {
        return totalPlannedProblemsFourWeekCount;
    }

    public void setTotalPlannedProblemsFourWeekCount(int totalPlannedProblemsFourWeekCount) {
        this.totalPlannedProblemsFourWeekCount = totalPlannedProblemsFourWeekCount;
    }

    public int getTotalPlannedProblemsThreeWeekCount() {
        return totalPlannedProblemsThreeWeekCount;
    }

    public void setTotalPlannedProblemsThreeWeekCount(int totalPlannedProblemsThreeWeekCount) {
        this.totalPlannedProblemsThreeWeekCount = totalPlannedProblemsThreeWeekCount;
    }

    public int getTotalPlannedProblemsTwoWeekCount() {
        return totalPlannedProblemsTwoWeekCount;
    }

    public void setTotalPlannedProblemsTwoWeekCount(int totalPlannedProblemsTwoWeekCount) {
        this.totalPlannedProblemsTwoWeekCount = totalPlannedProblemsTwoWeekCount;
    }

    public int getTotalPlannedProblemsOneWeekCount() {
        return totalPlannedProblemsOneWeekCount;
    }

    public void setTotalPlannedProblemsOneWeekCount(int totalPlannedProblemsOneWeekCount) {
        this.totalPlannedProblemsOneWeekCount = totalPlannedProblemsOneWeekCount;
    }

    public List<BigInteger> getLongDrivingFourWeekTasks() {
        return longDrivingFourWeekTasks;
    }

    public void setLongDrivingFourWeekTasks(List<BigInteger> longDrivingFourWeekTasks) {
        this.longDrivingFourWeekTasks = longDrivingFourWeekTasks;
    }

    public List<BigInteger> getLongDrivingThreeWeekTasks() {
        return longDrivingThreeWeekTasks;
    }

    public void setLongDrivingThreeWeekTasks(List<BigInteger> longDrivingThreeWeekTasks) {
        this.longDrivingThreeWeekTasks = longDrivingThreeWeekTasks;
    }

    public List<BigInteger> getLongDrivingTwoWeekTasks() {
        return longDrivingTwoWeekTasks;
    }

    public void setLongDrivingTwoWeekTasks(List<BigInteger> longDrivingTwoWeekTasks) {
        this.longDrivingTwoWeekTasks = longDrivingTwoWeekTasks;
    }

    public List<BigInteger> getLongDrivingOneWeekTasks() {
        return longDrivingOneWeekTasks;
    }

    public void setLongDrivingOneWeekTasks(List<BigInteger> longDrivingOneWeekTasks) {
        this.longDrivingOneWeekTasks = longDrivingOneWeekTasks;
    }

    public List<BigInteger> getMostDrivenFourWeekTasks() {
        return mostDrivenFourWeekTasks;
    }

    public void setMostDrivenFourWeekTasks(List<BigInteger> mostDrivenFourWeekTasks) {
        this.mostDrivenFourWeekTasks = mostDrivenFourWeekTasks;
    }

    public List<BigInteger> getMostDrivenThreeWeekTasks() {
        return mostDrivenThreeWeekTasks;
    }

    public void setMostDrivenThreeWeekTasks(List<BigInteger> mostDrivenThreeWeekTasks) {
        this.mostDrivenThreeWeekTasks = mostDrivenThreeWeekTasks;
    }

    public List<BigInteger> getMostDrivenTwoWeekTasks() {
        return mostDrivenTwoWeekTasks;
    }

    public void setMostDrivenTwoWeekTasks(List<BigInteger> mostDrivenTwoWeekTasks) {
        this.mostDrivenTwoWeekTasks = mostDrivenTwoWeekTasks;
    }

    public List<BigInteger> getMostDrivenOneWeekTasks() {
        return mostDrivenOneWeekTasks;
    }

    public void setMostDrivenOneWeekTasks(List<BigInteger> mostDrivenOneWeekTasks) {
        this.mostDrivenOneWeekTasks = mostDrivenOneWeekTasks;
    }

    public List<BigInteger> getEscalationOneWeekTasks() {
        return escalationOneWeekTasks;
    }

    public void setEscalationOneWeekTasks(List<BigInteger> escalationOneWeekTasks) {
        this.escalationOneWeekTasks = escalationOneWeekTasks;
    }

    public List<BigInteger> getEscalationTwoWeekTasks() {
        return escalationTwoWeekTasks;
    }

    public void setEscalationTwoWeekTasks(List<BigInteger> escalationTwoWeekTasks) {
        this.escalationTwoWeekTasks = escalationTwoWeekTasks;
    }

    public List<BigInteger> getEscalationThreeWeekTasks() {
        return escalationThreeWeekTasks;
    }

    public void setEscalationThreeWeekTasks(List<BigInteger> escalationThreeWeekTasks) {
        this.escalationThreeWeekTasks = escalationThreeWeekTasks;
    }

    public List<BigInteger> getEscalationFourWeekTasks() {
        return escalationFourWeekTasks;
    }

    public void setEscalationFourWeekTasks(List<BigInteger> escalationFourWeekTasks) {
        this.escalationFourWeekTasks = escalationFourWeekTasks;
    }

    public List<BigInteger> getWaitingFourWeekTasks() {
        return waitingFourWeekTasks;
    }

    public void setWaitingFourWeekTasks(List<BigInteger> waitingFourWeekTasks) {
        this.waitingFourWeekTasks = waitingFourWeekTasks;
    }

    public List<BigInteger> getWaitingThreeWeekTasks() {
        return waitingThreeWeekTasks;
    }

    public void setWaitingThreeWeekTasks(List<BigInteger> waitingThreeWeekTasks) {
        this.waitingThreeWeekTasks = waitingThreeWeekTasks;
    }

    public List<BigInteger> getWaitingTwoWeekTasks() {
        return waitingTwoWeekTasks;
    }

    public void setWaitingTwoWeekTasks(List<BigInteger> waitingTwoWeekTasks) {
        this.waitingTwoWeekTasks = waitingTwoWeekTasks;
    }

    public List<BigInteger> getWaitingOneWeekTasks() {
        return waitingOneWeekTasks;
    }

    public void setWaitingOneWeekTasks(List<BigInteger> waitingOneWeekTasks) {
        this.waitingOneWeekTasks = waitingOneWeekTasks;
    }

    public List<BigInteger> getTotalPlannedProblemsFourWeekTasks() {
        return totalPlannedProblemsFourWeekTasks;
    }

    public void setTotalPlannedProblemsFourWeekTasks(List<BigInteger> totalPlannedProblemsFourWeekTasks) {
        this.totalPlannedProblemsFourWeekTasks = totalPlannedProblemsFourWeekTasks;
    }

    public List<BigInteger> getTotalPlannedProblemsThreeWeekTasks() {
        return totalPlannedProblemsThreeWeekTasks;
    }

    public void setTotalPlannedProblemsThreeWeekTasks(List<BigInteger> totalPlannedProblemsThreeWeekTasks) {
        this.totalPlannedProblemsThreeWeekTasks = totalPlannedProblemsThreeWeekTasks;
    }

    public List<BigInteger> getTotalPlannedProblemsTwoWeekTasks() {
        return totalPlannedProblemsTwoWeekTasks;
    }

    public void setTotalPlannedProblemsTwoWeekTasks(List<BigInteger> totalPlannedProblemsTwoWeekTasks) {
        this.totalPlannedProblemsTwoWeekTasks = totalPlannedProblemsTwoWeekTasks;
    }

    public List<BigInteger> getTotalPlannedProblemsOneWeekTasks() {
        return totalPlannedProblemsOneWeekTasks;
    }

    public void setTotalPlannedProblemsOneWeekTasks(List<BigInteger> totalPlannedProblemsOneWeekTasks) {
        this.totalPlannedProblemsOneWeekTasks = totalPlannedProblemsOneWeekTasks;
    }


    public int getPlannedStatusFourWeekCount() {
        return plannedStatusFourWeekCount;
    }

    public void setPlannedStatusFourWeekCount(int plannedStatusFourWeekCount) {
        this.plannedStatusFourWeekCount = plannedStatusFourWeekCount;
    }

    public int getPlannedStatusThreeWeekCount() {
        return plannedStatusThreeWeekCount;
    }

    public void setPlannedStatusThreeWeekCount(int plannedStatusThreeWeekCount) {
        this.plannedStatusThreeWeekCount = plannedStatusThreeWeekCount;
    }

    public int getPlannedStatusTwoWeekCount() {
        return plannedStatusTwoWeekCount;
    }

    public void setPlannedStatusTwoWeekCount(int plannedStatusTwoWeekCount) {
        this.plannedStatusTwoWeekCount = plannedStatusTwoWeekCount;
    }

    public int getPlannedStatusOneWeekCount() {
        return plannedStatusOneWeekCount;
    }

    public void setPlannedStatusOneWeekCount(int plannedStatusOneWeekCount) {
        this.plannedStatusOneWeekCount = plannedStatusOneWeekCount;
    }

    public List<BigInteger> getPlannedStatusFourWeekTasks() {
        return plannedStatusFourWeekTasks;
    }

    public void setPlannedStatusFourWeekTasks(List<BigInteger> plannedStatusFourWeekTasks) {
        this.plannedStatusFourWeekTasks = plannedStatusFourWeekTasks;
    }

    public List<BigInteger> getPlannedStatusThreeWeekTasks() {
        return plannedStatusThreeWeekTasks;
    }

    public void setPlannedStatusThreeWeekTasks(List<BigInteger> plannedStatusThreeWeekTasks) {
        this.plannedStatusThreeWeekTasks = plannedStatusThreeWeekTasks;
    }

    public List<BigInteger> getPlannedStatusTwoWeekTasks() {
        return plannedStatusTwoWeekTasks;
    }

    public void setPlannedStatusTwoWeekTasks(List<BigInteger> plannedStatusTwoWeekTasks) {
        this.plannedStatusTwoWeekTasks = plannedStatusTwoWeekTasks;
    }

    public List<BigInteger> getPlannedStatusOneWeekTasks() {
        return plannedStatusOneWeekTasks;
    }

    public void setPlannedStatusOneWeekTasks(List<BigInteger> plannedStatusOneWeekTasks) {
        this.plannedStatusOneWeekTasks = plannedStatusOneWeekTasks;
    }

    public long getVisitatedHoursPerWeek() {
        return visitatedHoursPerWeek;
    }

    public void setVisitatedHoursPerWeek(long visitatedHoursPerWeek) {
        this.visitatedHoursPerWeek = visitatedHoursPerWeek;
    }

    public long getVisitatedHoursPerMonth() {
        return visitatedHoursPerMonth;
    }

    public void setVisitatedHoursPerMonth(long visitatedHoursPerMonth) {
        this.visitatedHoursPerMonth = visitatedHoursPerMonth;
    }

    public long getVisitatedMinutesPerWeek() {
        return visitatedMinutesPerWeek;
    }

    public void setVisitatedMinutesPerWeek(long visitatedMinutesPerWeek) {
        this.visitatedMinutesPerWeek = visitatedMinutesPerWeek;
    }

    public long getVisitatedMinutesPerMonth() {
        return visitatedMinutesPerMonth;
    }

    public void setVisitatedMinutesPerMonth(long visitatedMinutesPerMonth) {
        this.visitatedMinutesPerMonth = visitatedMinutesPerMonth;
    }

    public float getVisitatedTasksPerWeek() {
        return visitatedTasksPerWeek;
    }

    public void setVisitatedTasksPerWeek(float visitatedTasksPerWeek) {
        this.visitatedTasksPerWeek = visitatedTasksPerWeek;
    }

    public float getVisitatedTasksPerMonth() {
        return visitatedTasksPerMonth;
    }

    public void setVisitatedTasksPerMonth(float visitatedTasksPerMonth) {
        this.visitatedTasksPerMonth = visitatedTasksPerMonth;
    }

    @Override
    public String toString() {
        return "ClientAggregator{" +
                "unitId=" + unitId +
                ", citizenId=" + citizenId +
                ", longDrivingFourWeekCount=" + longDrivingFourWeekCount +
                ", longDrivingThreeWeekCount=" + longDrivingThreeWeekCount +
                ", longDrivingTwoWeekCount=" + longDrivingTwoWeekCount +
                ", longDrivingOneWeekCount=" + longDrivingOneWeekCount +
                ", mostDrivenFourWeekCount=" + mostDrivenFourWeekCount +
                ", mostDrivenThreeWeekCount=" + mostDrivenThreeWeekCount +
                ", mostDrivenTwoWeekCount=" + mostDrivenTwoWeekCount +
                ", mostDrivenOneWeekCount=" + mostDrivenOneWeekCount +
                ", escalationOneWeekCount=" + escalationOneWeekCount +
                ", escalationTwoWeekCount=" + escalationTwoWeekCount +
                ", escalationThreeWeekCount=" + escalationThreeWeekCount +
                ", escalationFourWeekCount=" + escalationFourWeekCount +
                ", waitingFourWeekCount=" + waitingFourWeekCount +
                ", waitingThreeWeekCount=" + waitingThreeWeekCount +
                ", waitingTwoWeekCount=" + waitingTwoWeekCount +
                ", waitingOneWeekCount=" + waitingOneWeekCount +
                ", totalPlannedProblemsFourWeekCount=" + totalPlannedProblemsFourWeekCount +
                ", totalPlannedProblemsThreeWeekCount=" + totalPlannedProblemsThreeWeekCount +
                ", totalPlannedProblemsTwoWeekCount=" + totalPlannedProblemsTwoWeekCount +
                ", totalPlannedProblemsOneWeekCount=" + totalPlannedProblemsOneWeekCount +
                ", longDrivingFourWeekTasks=" + longDrivingFourWeekTasks +
                ", longDrivingThreeWeekTasks=" + longDrivingThreeWeekTasks +
                ", longDrivingTwoWeekTasks=" + longDrivingTwoWeekTasks +
                ", longDrivingOneWeekTasks=" + longDrivingOneWeekTasks +
                ", mostDrivenFourWeekTasks=" + mostDrivenFourWeekTasks +
                ", mostDrivenThreeWeekTasks=" + mostDrivenThreeWeekTasks +
                ", mostDrivenTwoWeekTasks=" + mostDrivenTwoWeekTasks +
                ", mostDrivenOneWeekTasks=" + mostDrivenOneWeekTasks +
                ", escalationOneWeekTasks=" + escalationOneWeekTasks +
                ", escalationTwoWeekTasks=" + escalationTwoWeekTasks +
                ", escalationThreeWeekTasks=" + escalationThreeWeekTasks +
                ", escalationFourWeekTasks=" + escalationFourWeekTasks +
                ", waitingFourWeekTasks=" + waitingFourWeekTasks +
                ", waitingThreeWeekTasks=" + waitingThreeWeekTasks +
                ", waitingTwoWeekTasks=" + waitingTwoWeekTasks +
                ", waitingOneWeekTasks=" + waitingOneWeekTasks +
                ", totalPlannedProblemsFourWeekTasks=" + totalPlannedProblemsFourWeekTasks +
                ", totalPlannedProblemsThreeWeekTasks=" + totalPlannedProblemsThreeWeekTasks +
                ", totalPlannedProblemsTwoWeekTasks=" + totalPlannedProblemsTwoWeekTasks +
                ", totalPlannedProblemsOneWeekTasks=" + totalPlannedProblemsOneWeekTasks +
                ", clientExceptionCounts=" + clientExceptionCounts +
                ", plannedStatusFourWeekCount=" + plannedStatusFourWeekCount +
                ", plannedStatusThreeWeekCount=" + plannedStatusThreeWeekCount +
                ", plannedStatusTwoWeekCount=" + plannedStatusTwoWeekCount +
                ", plannedStatusOneWeekCount=" + plannedStatusOneWeekCount +
                ", plannedStatusFourWeekTasks=" + plannedStatusFourWeekTasks +
                ", plannedStatusThreeWeekTasks=" + plannedStatusThreeWeekTasks +
                ", plannedStatusTwoWeekTasks=" + plannedStatusTwoWeekTasks +
                ", plannedStatusOneWeekTasks=" + plannedStatusOneWeekTasks +
                ", unhandledTaskCount=" + unhandledTaskCount +
                ", visitatedHoursPerWeek=" + visitatedHoursPerWeek +
                ", visitatedHoursPerMonth=" + visitatedHoursPerMonth +
                ", visitatedMinutesPerWeek=" + visitatedMinutesPerWeek +
                ", visitatedMinutesPerMonth=" + visitatedMinutesPerMonth +
                ", visitatedTasksPerWeek=" + visitatedTasksPerWeek +
                ", visitatedTasksPerMonth=" + visitatedTasksPerMonth +
                '}';
    }
}
