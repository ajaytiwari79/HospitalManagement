package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.activity.ActivityPriorityDTO;
import com.kairos.persistence.model.activity.ActivityPriority;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;


@Repository
public interface ActivityPriorityMongoRepository extends MongoBaseRepository<ActivityPriority, BigInteger>,CustomActivityPriorityMongoRepository {

    @Query("{sequence:{$gte:?0}, deleted:false,organizationId:?1}")
    List<ActivityPriority> findAllGreaterThenAndEqualSequenceAndOrganizationId(int sequence, Long unitId,Sort sort);

    @Query("{deleted:false,organizationId:?0}")
    List<ActivityPriorityDTO> findAllOrganizationId(Long organizationId);

    @Query(value = "{deleted:false,organizationId:?0}",count = true)
    int getActivityPriorityCountAtOrganization(Long organizationId);

    @Query(value = "{deleted:false,countryId:?0}",count = true)
    int getActivityPriorityCountAtCountry(Long organizationId);


    @Query("{sequence:{$gte:?0}, deleted:false,countryId:?1}")
    List<ActivityPriority> findAllGreaterThenAndEqualSequenceAndCountryId(int sequence,Long countryId,Sort sort);

    @Query("{deleted:false,countryId:?0}")
    List<ActivityPriorityDTO> findAllCountryId(Long countryId);


    @Query("{deleted:false,countryId:?1,sequence:?0}")
    ActivityPriority findBySequenceAndCountryId(int sequence,Long countryId);

    @Query("{deleted:false,organizationId:?1,sequence:?0}")
    ActivityPriority findBySequenceAndOrganizationId(int sequence,Long organizationId);

    @Query("{deleted:false,organizationId:?1,name:?0}")
    ActivityPriority findByNameAndOrganizationId(String name,Long organizationId);

    List<ActivityPriority> findAllByIdInAndDeletedFalse(Set<BigInteger> ids);
}
