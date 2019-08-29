package com.kairos.persistence.repository.data_inventory.asset;

import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class AssetRepositoryImpl implements CustomAssetRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<AssetBasicResponseDTO> getAllAssetRelatedProcessingActivityByOrgId(Long orgId) {
        Query query = entityManager.createNamedQuery("getAllAssetRelatedProcessingActivityData").setParameter(1, orgId);
        return (List<AssetBasicResponseDTO>) query.getResultList();
    }
}
