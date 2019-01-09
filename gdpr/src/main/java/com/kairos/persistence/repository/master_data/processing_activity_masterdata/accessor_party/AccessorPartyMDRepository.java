package com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorPartyMD;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface AccessorPartyMDRepository extends JpaRepository<AccessorPartyMD, Long> {

    @Query(value = "SELECT AP FROM AccessorPartyMD AP WHERE AP.countryId = ?1 and AP.deleted = ?2 and lower(AP.name) IN ?3")
    List<AccessorPartyMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update AccessorPartyMD set name = ?1 where id= ?2")
    Integer updateAccessorPartyName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update AccessorPartyMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT AP FROM AccessorPartyMD AP WHERE AP.id = ?1 and AP.countryId = ?2 and AP.deleted = ?3")
    AccessorPartyMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.AccessorPartyResponseDTO(AP.id, AP.name, AP.organizationId, AP.suggestedDataStatus, AP.suggestedDate )  FROM AccessorPartyMD AP WHERE AP.countryId = ?1 and AP.deleted = false order by createdAt desc")
    List<AccessorPartyResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update AccessorPartyMD set deleted = true where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateAccessorPartiesStatus(Long countryId, Set<Long> ids);

    @Query(value = "SELECT AP FROM AccessorPartyMD AP WHERE AP.id IN (?1) and AP.deleted = false")
    List<AccessorPartyMD> findAllByIds( Set<Long> ids);

    @Query(value = "Select AP from AccessorPartyMD AP where AP.countryId = ?2 and lower(AP.name) = lower(?1) and AP.deleted = false")
    AccessorPartyMD findByNameAndCountryId(String name, Long countryId);

}
