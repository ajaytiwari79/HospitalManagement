package com.kairos.persistence.repository.task_type;

import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by prabjot on 4/10/16.
 */
@Repository
public interface TaskTypeMongoRepository extends MongoBaseRepository<TaskType,BigInteger> {

    /**
     * it will return all country task types only
     * @return
     */
    @Override
    @Query(value="{'isEnabled':true,'organizationId':0}")
    List<TaskType> findAll();


    List <TaskType> findBySubServiceIdAndOrganizationIdAndIsEnabled(long subServiceId, long organizationId, boolean isEnabled);

    List <TaskType> findBySubServiceIdAndOrganizationId(long subServiceId, long organizationId);

    @Query(fields="{ 'title' : 1, 'description' : 1,'expiresOn':1,'taskTypeStatus':1, 'colorForGantt':1}")
    List<TaskType> findByOrganizationIdAndIsEnabled(long organizationId, boolean isEnabled);
    TaskType findByExternalId(String externalId);
    TaskType findByTitleAndSubServiceId(String title, long subServiceId);

    List<TaskType> findByTeamIdAndSubServiceIdAndIsEnabled(long teamId, long subServiceId, boolean isEnabled);

    TaskType findByRootIdAndOrganizationIdAndSubServiceIdAndIsEnabled(String rootTaskTypeId, long organizationId, long subServiceId, boolean isEnabled);

    List<TaskType> findBySubServiceIdInAndOrganizationIdAndIsEnabled(List<Long> subServiceIds, long organizationId, boolean isEnabled);
    List<TaskType> findAllByIdInAndIsEnabled(Set<String> taskTypeIds, boolean isEnabled);

    @Query("{'organizationId':0,_id:{$ne:?0},isEnabled:true}")
    List<TaskType> getTaskTypesForCopySettings(String id);

    TaskType findByOrganizationIdAndRootIdAndSubServiceId(long organizationId, BigInteger rootId, long subServiceId);

    @Query(value = "{organizationId:?0,deleted:false,'subServiceId':{$in:?1}}",fields = "{'title' : 1,'description':1,'duration':1}")
    List<TaskTypeDTO> getTaskTypesOfOrganisation(Long organizationId, List<Long> serviceIds);

    @Query("{organizationId : ?0,deleted : false,title:{$in:?1}}")
    List<TaskType> findByName(Long unitId,List<String> name);


}
