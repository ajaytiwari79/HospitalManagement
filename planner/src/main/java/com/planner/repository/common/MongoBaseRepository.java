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
     * @param checkForCountry can be either country  or unit
     * @return
     */
    List<T> findAllObjectsNotDeletedById(boolean checkForCountry,Long countryOrUnitId);

    T findByIdNotDeleted(BigInteger objectId);

    <T extends MongoBaseEntity> T saveObject(T entity);

    <T extends MongoBaseEntity> List<T> saveObjectList(List<T> entity);

    boolean safeDeleteById(BigInteger id);

    boolean isNameExistsById(String name,BigInteger idNotApplicableForCheck,boolean checkForCountry,Long countryOrUnitId);

    <T extends MongoBaseEntity> boolean safeDeleteByObject(T o);
}
