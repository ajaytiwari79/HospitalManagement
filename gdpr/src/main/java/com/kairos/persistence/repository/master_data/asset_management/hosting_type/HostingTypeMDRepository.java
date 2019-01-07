package com.kairos.persistence.repository.master_data.asset_management.hosting_type;


import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingTypeMD;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface HostingTypeMDRepository extends JpaRepository<HostingTypeMD, Long> {

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.countryId = ?1 and ht.deleted = ?2 and lower(ht.name) IN ?3")
    List<HostingTypeMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set name = ?1 where id= ?2")
    Integer updateHostingTypeName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.id = ?1 and ht.countryId = ?2 and ht.deleted = ?3")
    HostingTypeMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.HostingTypeResponseDTO(ht.id, ht.name, ht.organizationId, ht.suggestedDataStatus, ht.suggestedDate )  FROM HostingTypeMD ht WHERE ht.countryId = ?1 and ht.deleted = false order by createdAt desc")
    List<HostingTypeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

}
