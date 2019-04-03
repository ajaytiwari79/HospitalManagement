package com.kairos.persistence.repository.risk_management;

import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.RiskResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface RiskRepository extends CustomGenericRepository<Risk> {


    @Query(nativeQuery = true, name = "getAllProcessingActivityRiskData")
    List<RiskResponseDTO> getAllProcessingActivityRiskByOrganizationId(Long orgId);

    @Query(nativeQuery = true, name = "getAllAssetTypeRiskData")
    List<RiskResponseDTO> getAllAssetTypeRiskByOrganizationId(Long orgId);


}
