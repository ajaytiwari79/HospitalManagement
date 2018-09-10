package com.kairos.persistence.repository.task_type;

import com.kairos.persistence.model.task.Task;
import com.kairos.wrapper.EscalatedTasksWrapper;
import com.kairos.wrapper.task.StaffAssignedTasksWrapper;
import com.kairos.wrapper.TaskCountWithAssignedUnit;
import com.kairos.wrapper.TaskWrapper;
import com.kairos.dto.planner.vrp.task.VRPTaskDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 1/6/17.
 */
public interface CustomTaskMongoRepository {

    List<Task> getActualPlanningTask(long citizenId, Date fromDate, Date toDate);

    List<StaffAssignedTasksWrapper> getStaffAssignedTasks(long unitId, long staffId, Date dateFrom, Date dateTo);

    List<BigInteger> updateTasksActiveStatusInBulk(List<BigInteger> taskIds, boolean makeActive);

    List<Task> getTasksBetweenExceptionDates(long unitId, long citizenId, Date timeFrom, Date timeTo);

    List<Task> getTasksBetweenExceptionDates(long unitId, List<Long> citizenId, Date timeFrom, Date timeTo);

    List<EscalatedTasksWrapper> getStaffNotAssignedTasksGroupByCitizen(Long unitId);

    List<TaskWrapper> getUnhandledTaskForMobileView(long citizenId, long unitId, Date dateFrom, Date dateTo, Sort  sort);

    List<Task> getCitizenTasksGroupByUnitIds(Long citizenId, Date date,Pageable pageable);

    TaskCountWithAssignedUnit countOfTasksAfterDateAndAssignedUnits(Long citizenId, Date date);

    void deleteTasksAfterDate(Long citizenId,Date date);

    void inactiveTasksAfterDate(Long citizenId,Date date);

    List<Task> getTasksByException(long citizenId, long unitId, List<BigInteger> exceptionIds);

    int deleteExceptionsFromTasks(long clientId, long unitId, List<BigInteger> exceptionIds);

    public List<Task> getTaskByException(long citizenId, long unitId, BigInteger exceptionId);
    public List<VRPTaskDTO> getAllTasksByUnitId(Long unitId);

    public Map<Long,BigInteger> getAllTasksInstallationNoAndTaskTypeId(Long unitId);

}
