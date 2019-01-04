package com.kairos.persistence.repository.master_data.asset_management.org_security_measure;


import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasureMD;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface OrganizationalSecurityMeasureMDRepository extends JpaRepository<OrganizationalSecurityMeasureMD, Integer> {

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.countryId = ?1 and osm.deleted = ?2 and lower(osm.name) IN ?3")
    List<OrganizationalSecurityMeasureMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set name = ?1 where id= ?2")
    Integer updateOrganizationalSecurityMeasureName(String name, Integer id);


    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Integer id, Long countryId);

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.id = ?1 and osm.countryId = ?2 and osm.deleted = ?3")
    OrganizationalSecurityMeasureMD findByIdAndCountryIdAndDeleted(Integer id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO(osm.id, osm.name, osm.organizationId, osm.suggestedDataStatus, osm.suggestedDate) FROM OrganizationalSecurityMeasureMD osm WHERE osm.countryId = ?1 and osm.deleted = false order by createdAt desc")
    List<OrganizationalSecurityMeasureResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

}
