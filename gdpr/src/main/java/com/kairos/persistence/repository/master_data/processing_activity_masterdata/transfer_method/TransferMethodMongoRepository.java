package com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface TransferMethodMongoRepository extends MongoBaseRepository<TransferMethod, BigInteger>, CustomTransferMethodRepository {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    TransferMethod findByIdAndNonDeleted(Long countryId, BigInteger id);

    TransferMethod findByid(BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    TransferMethod findByName(Long countryId, String name);

    @Query("{deleted:false,countryId:?0}")
    List<TransferMethodResponseDTO> findAllByCountryIdSortByCreatedDate(Long countryId, Sort sort);

    @Query("{deleted:false,countryId:?0}")
    List<TransferMethodResponseDTO> findAllByCountryId(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<TransferMethod> getTransferMethodListByIds(Long countryId, Set<BigInteger> transferMethodIds);

    @Query("{_id:{$in:?0},deleted:false}")
    List<TransferMethodResponseDTO> findTransferMethodByIds(List<BigInteger> transferMethodIds);


    @Query("{organizationId:?0,deleted:false}")
    List<TransferMethodResponseDTO> findAllByUnitIdSortByCreatedDate(Long unitId, Sort  sort);

    @Query("{organizationId:?0,deleted:false}")
    List<TransferMethodResponseDTO> findAllByUnitId(Long unitId);


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    TransferMethod findByUnitIdAndId(Long unitId, BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    TransferMethod findByUnitIdAndName(Long unitId, String name);

}
