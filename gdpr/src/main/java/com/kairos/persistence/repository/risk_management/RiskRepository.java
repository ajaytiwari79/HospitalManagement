package com.kairos.persistence.repository.risk_management;

import com.kairos.persistence.model.risk_management.RiskMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
////@JaversSpringDataAuditable

public interface RiskRepository extends CustomGenericRepository<RiskMD> {

    /*@Query(value = "Select risk from RiskMD risk where risk.id = ?1 and risk.organizationId = ?2 and risk.assetType.id = ?3 and risk.deleted = false")
    RiskMD findByIdAndOrganizationIdAndAssetTypeId(Long id, Long orgId, Long assetTypeId);*/


   /* @Modifying
    @Transactional
    @Query(value = "update RiskMD set deleted = true, assetType = null where id = ?1 and assetType.id = ?2 and deleted = false")
    Integer unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(Long riskId, Long assetTypeId, Long orgId);*/

    @Query(value = "Select risk from RiskMD risk where risk.organizationId = ?1 and risk.deleted = false")
    List<RiskMD> getAllRiskByOrganizationId(Long orgId);

    /*@Query(value = "select PA.name as processingActivityName, PA.id as processingActivityId, risk.name, risk.id ,\n" +
            "risk.risk_level, risk.description, risk.risk_recommendation from riskmd risk inner join processing_activitymd_risks PAR ON PAR.risks_id = risk.id\n" +
            "left join processing_activitymd PA ON PAR.processing_activitymd_id = PA.id where risk.organization_id = 958 and risk.deleted = false", nativeQuery = true)
    List<RiskResponseDTO> getAllRiskOfOrganizationId(Long orgId);
*/

   // List<RiskResponseDTO> getAllRiskOfOrganizationId(Long orgId);
    


}
