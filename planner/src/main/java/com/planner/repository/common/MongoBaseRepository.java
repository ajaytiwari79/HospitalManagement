package com.planner.repository.common;

import com.planner.domain.MongoBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface MongoBaseRepository<T, ID> extends MongoRepository<T, ID> {
    Optional<T> findByKairosId(BigInteger kairosId);

    List<T> findAllNotDeleted();//For all entries in any collection

    //Currently Applicable for only either Country or Unit
    List<T> findAllSolverConfigNotDeletedByType(String solverConfigType);

    <T extends MongoBaseEntity> T saveObject(T entity);

    boolean safeDeleteById(BigInteger id);

    boolean isNameExists(String name,BigInteger idNotApplicableForCheck,boolean checkForCountry);

    <T extends MongoBaseEntity> boolean safeDeleteByObject(T o);
}
