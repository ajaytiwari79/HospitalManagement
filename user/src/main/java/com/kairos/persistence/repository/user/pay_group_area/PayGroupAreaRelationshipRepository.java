package com.kairos.persistence.repository.user.pay_group_area;

import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaMunicipalityRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepositoryImpl;
import org.springframework.stereotype.Repository;

/**
 * Created by vipul on 12/3/18.
 */
@Repository
public interface PayGroupAreaRelationshipRepository extends Neo4jBaseRepository<PayGroupAreaMunicipalityRelationship,Long> {



}
