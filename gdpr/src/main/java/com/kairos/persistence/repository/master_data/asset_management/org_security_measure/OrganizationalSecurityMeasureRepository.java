package com.kairos.persistence.repository.master_data.asset_management.org_security_measure;


import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationalSecurityMeasureRepository extends CustomGenericRepository<OrganizationalSecurityMeasure> {

    @Query(value = "SELECT new com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO(osm.id, osm.name, osm.organizationId, osm.suggestedDataStatus, osm.suggestedDate) FROM OrganizationalSecurityMeasure osm WHERE osm.countryId = ?1 and osm.deleted = false order by createdAt desc")
    List<OrganizationalSecurityMeasureResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO(osm.id, osm.name, osm.organizationId, osm.suggestedDataStatus, osm.suggestedDate) FROM OrganizationalSecurityMeasure osm WHERE osm.organizationId = ?1 and osm.deleted = false order by createdAt desc")
    List<OrganizationalSecurityMeasureResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
