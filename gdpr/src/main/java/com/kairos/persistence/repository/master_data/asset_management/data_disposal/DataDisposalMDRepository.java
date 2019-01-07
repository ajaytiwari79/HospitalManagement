package com.kairos.persistence.repository.master_data.asset_management.data_disposal;


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
    List<DataDisposalMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set name = ?1 where id= ?2")
    Integer updateDataDisposalName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.id = ?1 and d.countryId = ?2 and d.deleted = ?3")
    DataDisposalMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.DataDisposalResponseDTO(d.id, d.name, d.organizationId, d.suggestedDataStatus, d.suggestedDate )  FROM DataDisposalMD d WHERE d.countryId = ?1 and d.deleted = false order by createdAt desc")
    List<DataDisposalResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

}
