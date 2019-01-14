package com.kairos.persistence.repository.master_data.asset_management.data_disposal;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface DataDisposalMDRepository extends JpaRepository<DataDisposalMD, Long> {

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.countryId = ?1 and d.deleted = ?2 and lower(d.name) IN ?3")
    List<DataDisposalMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> names);

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.countryId = ?1 and d.deleted = ?2 and lower(d.name) = lower(?3)")
    DataDisposalMD findByCountryIdAndDeletedAndName(Long countryId, boolean deleted, String name);


    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set name = ?1 where id= ?2 and countryId = ?3")
    Integer updateMasterDataDisposalName(String name, Long id, Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateDataDisposalStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.id IN (?1) and d.deleted = false")
    List<DataDisposalMD> findAllByIds( Set<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.id = ?1 and d.countryId = ?2 and d.deleted = ?3")
    DataDisposalMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.DataDisposalResponseDTO(d.id, d.name, d.organizationId, d.suggestedDataStatus, d.suggestedDate )  FROM DataDisposalMD d WHERE d.countryId = ?1 and d.deleted = false order by createdAt desc")
    List<DataDisposalResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.organizationId = ?1 and d.deleted = ?2 and lower(d.name) IN ?3")
    List<DataDisposalMD> findByUnitIdAndDeletedAndNameIn(Long orgId, boolean deleted, List<String> names);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateDataDisposalName(String name, Long id, Long orgId);


    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set deleted = true where id = ?1 and organizationId = ?2")
    Integer deleteByIdAndUnitId(Long id, Long unitId);

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.id = ?1 and d.organizationId = ?2 and d.deleted = ?3")
    DataDisposalMD findByIdAndUnitIdAndDeleted(Long id, Long orgId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.DataDisposalResponseDTO(d.id, d.name, d.organizationId, d.suggestedDataStatus, d.suggestedDate )  FROM DataDisposalMD d WHERE d.organizationId = ?1 and d.deleted = false order by createdAt desc")
    List<DataDisposalResponseDTO> findAllByUnitIdAndSortByCreatedDate(Long orgId);

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.organizationId = ?1 and d.deleted = ?2 and lower(d.name) = lower(?3)")
    DataDisposalMD findByUnitIdAndDeletedAndName(Long orgId, boolean deleted, String name);


}
