package com.kairos.persistence.repository.risk_management;

import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.model.risk_management.RiskMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface RiskRepository extends CustomGenericRepository<RiskMD> {

    @Query(value = "Select risk from RiskMD risk where risk.id = ?1 and risk.organizationId = ?2 and risk.assetType.id = ?3 and risk.deleted = false")
    RiskMD findByIdAndOrganizationIdAndAssetTypeId(Long id, Long orgId, Long assetTypeId);


    @Modifying
    @Transactional
    @Query(value = "update RiskMD set deleted = true, assetType = null where id = ?1 and assetType.id = ?2 and deleted = false")
    Integer unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(Long riskId, Long assetTypeId, Long orgId);

    /*@Query("{deleted:false,countryId:?0,_id:?1}")
    Risk findByIdAndCountryId(Long countryId, BigInteger riskId);


    @Query("{deleted:false,_id:?0}")
    Risk findByIdAndNonDeleted(BigInteger riskId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<Risk> findRiskByCountryIdAndIds(Long countryId, List<BigInteger> riskIds);

    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<Risk> findRiskByUnitIdAndIds(Long unitId, List<BigInteger> riskIds);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    Risk findUnitIdAndId(Long unitId, BigInteger riskId);*/


}
