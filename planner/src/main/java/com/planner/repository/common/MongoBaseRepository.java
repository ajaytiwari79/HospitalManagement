package com.planner.repository.common;

import com.planner.domain.common.MongoBaseEntity;
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
    @Deprecated
    List<T> findAllSolverConfigNotDeletedByType(String solverConfigType);

    /**
     *
     * @param type can be either country  or unit
     * @return
     */
    List<T> findAllObjectsNotDeletedByType(String type);

    <T extends MongoBaseEntity> T saveObject(T entity);

    boolean safeDeleteById(BigInteger id);

    boolean isNameExists(String name,BigInteger idNotApplicableForCheck,boolean checkForCountry);

    <T extends MongoBaseEntity> boolean safeDeleteByObject(T o);
}
