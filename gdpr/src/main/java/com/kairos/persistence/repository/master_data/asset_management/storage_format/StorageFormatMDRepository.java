package com.kairos.persistence.repository.master_data.asset_management.storage_format;


import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.persistence.model.master_data.default_asset_setting.StorageFormatMD;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface StorageFormatMDRepository extends JpaRepository<StorageFormatMD, Integer> {

    @Query(value = "SELECT sf FROM StorageFormatMD sf WHERE sf.countryId = ?1 and sf.deleted = ?2 and lower(sf.name) IN ?3")
    List<StorageFormatMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set name = ?1 where id= ?2")
    Integer updateStorageFormatName(String name, Integer id);


    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Integer id, Long countryId);

    @Query(value = "SELECT sf FROM StorageFormatMD sf WHERE sf.id = ?1 and sf.countryId = ?2 and sf.deleted = ?3")
    StorageFormatMD findByIdAndCountryIdAndDeleted(Integer id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.StorageFormatResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM StorageFormatMD sf WHERE sf.countryId = ?1 and sf.deleted = false order by createdAt desc")
    List<StorageFormatResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

}
