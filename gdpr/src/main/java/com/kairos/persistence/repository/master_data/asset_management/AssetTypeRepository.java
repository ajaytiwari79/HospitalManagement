package com.kairos.persistence.repository.master_data.asset_management;


import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface AssetTypeRepository extends JpaRepository<AssetTypeMD,BigInteger> {

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.countryId = ?1 and ht.deleted = false and lower(at.name) = lower(?2) and subAssetType = ?3")
    AssetTypeMD findByNameAndCountryIdAndSubAssetType(String name,Long countryId, boolean hasSubAssetType);


}
