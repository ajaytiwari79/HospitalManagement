package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.OrganizationSetting;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 16/11/16.
 */
@Repository
public interface OpeningSettingsGraphRepository extends Neo4jBaseRepository<OrganizationSetting,Long> {


}
