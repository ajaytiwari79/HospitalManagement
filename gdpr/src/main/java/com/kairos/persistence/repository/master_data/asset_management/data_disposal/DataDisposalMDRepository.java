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
public interface DataDisposalMDRepository extends JpaRepository<DataDisposalMD, Integer> {

    @Query(value = "SELECT d FROM DataDisposalMD d WHERE d.countryId = ?1 and d.deleted = ?2 and lower(d.name) IN ?3")
    List<DataDisposalMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set name = ?1 where id= ?2")
    Integer updateDataDisposalName(String name, Integer id);


    @Transactional
    @Modifying
    @Query(value = "delete from DataDisposalMD d where d.id = ?1 and d.countryId = ?2")
    Integer deleteByIdAndCountryId(Integer id, Long countryId);




}
