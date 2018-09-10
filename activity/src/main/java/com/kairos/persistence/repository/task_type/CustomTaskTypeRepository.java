package com.kairos.persistence.repository.task_type;

import com.kairos.dto.activity.task_type.TaskTypeResponseDTO;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.wrapper.OrgTaskTypeAggregateResult;
import com.kairos.wrapper.TaskTypeAggregateResult;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 16/5/17.
 */
public interface CustomTaskTypeRepository {


    List<TaskTypeAggregateResult> getTaskTypesOfCitizens(List<Long> citizenIds);

    List<OrgTaskTypeAggregateResult> getTaskTypesOfUnit(long unitId);

    void updateUnitTaskTypesStatus(BigInteger taskTypeId, boolean status);

    List<TaskTypeAggregateResult> getCitizenTaskTypesOfUnit(Long unitId, ClientFilterDTO clientFilterDTO, List<String> taskTypeIdsByServiceIds);

    public List<TaskTypeResponseDTO> getAllTaskType();

    List <TaskTypeResponseDTO> findAllBySubServiceIdAndOrganizationId(long subServiceId, long organizationId);

    public List<TaskTypeResponseDTO> getAllTaskTypeBySubServiceAndOrganizationAndIsEnabled(long subServiceId, long organizationId, boolean isEnabled);

    public List<TaskTypeResponseDTO> getAllTaskTypeByTeamIdAndSubServiceAndIsEnabled(long teamId, long subServiceId, boolean isEnabled);
}
