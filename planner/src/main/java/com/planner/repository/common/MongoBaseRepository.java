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


    <T extends MongoBaseEntity> T saveEntity(T entity);

    List<T> findAllObjectsNotDeletedById(boolean checkForCountry,Long countryOrUnitId);

    T findByIdNotDeleted(BigInteger objectId);

    <T extends MongoBaseEntity> List<T> saveList(List<T> entity);

    boolean safeDeleteById(BigInteger id);

    boolean isNameExistsById(String name,BigInteger idNotApplicableForCheck,boolean checkForCountry,Long countryOrUnitId);

    <T extends MongoBaseEntity> boolean safeDeleteByObject(T o);
}
