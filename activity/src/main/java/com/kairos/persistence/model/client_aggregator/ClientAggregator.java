package com.kairos.persistence.model.client_aggregator;

import com.kairos.dto.activity.client_exception.ClientExceptionCount;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.task.UnhandledTaskCount;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
@NoArgsConstructor
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

    private List<ClientExceptionCount> clientExceptionCounts=new ArrayList<>();

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


    private UnhandledTaskCount unhandledTaskCount;

    private long visitatedHoursPerWeek;

    private long visitatedHoursPerMonth;

    private long visitatedMinutesPerWeek;

    private long visitatedMinutesPerMonth;

    private float visitatedTasksPerWeek;

    private float visitatedTasksPerMonth;


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
                '}';
    }
}
