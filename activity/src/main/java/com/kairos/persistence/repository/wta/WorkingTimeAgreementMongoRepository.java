package com.kairos.persistence.repository.wta;


import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@Repository
public interface WorkingTimeAgreementMongoRepository extends MongoBaseRepository<WorkingTimeAgreement, BigInteger>, CustomWorkingTimeAgreementMongoRepostory {

    @Query("{name:?0,countryId:?1,deleted:false}")
    WorkingTimeAgreement getWtaByName(String wtaName, Long countryId);


    @Query("{countryId:?0,id:?1,deleted:false}")
    WorkingTimeAgreement getWTAByCountryId(long countryId, BigInteger wtaId);

    @Query("{id:?0,deleted:false}")
    WorkingTimeAgreement removeOldParentWTAMapping(BigInteger wtaId);


    List<WorkingTimeAgreement> findAllByUnitPositionIdInAndDeletedFalse(Set<Long> unitPositionId);


    @Query("{_id:{$in:?0}, deleted:false}")
    List<WorkingTimeAgreement> findAllByIdsInAndDeletedFalse(Set<BigInteger> ids);
    

}