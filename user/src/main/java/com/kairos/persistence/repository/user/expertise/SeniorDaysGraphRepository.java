package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.SeniorDays;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeniorDaysGraphRepository  extends Neo4jBaseRepository<SeniorDays, Long> {
}
