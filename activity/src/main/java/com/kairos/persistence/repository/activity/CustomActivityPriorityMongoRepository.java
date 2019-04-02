package com.kairos.persistence.repository.activity;

import com.kairos.persistence.model.activity.ActivityPriority;

import java.math.BigInteger;
import java.util.List;

public interface CustomActivityPriorityMongoRepository {

    List<ActivityPriority> findLastSeqenceByOrganizationIds(List<Long> organizationIds);
    boolean existsByNameAndOrganizationIdAndNotEqualToId(String name, BigInteger id, Long organizationId);
    boolean existsByNameAndCountryIdAndNotEqualToId(String name, BigInteger id,Long countryId);
}
