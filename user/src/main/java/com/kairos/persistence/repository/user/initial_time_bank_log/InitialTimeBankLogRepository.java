package com.kairos.persistence.repository.user.initial_time_bank_log;

import com.kairos.persistence.model.user.initial_time_bank_log.InitialTimeBankLog;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created By G.P.Ranjan on 25/6/19
 **/
@Repository
public interface InitialTimeBankLogRepository extends Neo4jBaseRepository<InitialTimeBankLog,Long> {
    @Query("MATCH (InitialTimeBankLog) where InitialTimeBankLog.employmentId={0} and InitialTimeBankLog.deleted=false RETURN InitialTimeBankLog order by InitialTimeBankLog.creationDate desc")
    List<InitialTimeBankLog> getInitialTimeBankLogByEmployment(Long employmentId);
}
