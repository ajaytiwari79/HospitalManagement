package com.kairos.persistence.repository.user.agreement.cta;

import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectiveTimeAgreementGraphRepository extends Neo4jBaseRepository<CostTimeAgreement,Long> {





}