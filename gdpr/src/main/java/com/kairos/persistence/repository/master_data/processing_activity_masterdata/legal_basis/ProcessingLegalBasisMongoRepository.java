package com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis;

import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JaversSpringDataAuditable
public interface ProcessingLegalBasisMongoRepository extends MongoBaseRepository<ProcessingLegalBasis, BigInteger>,CustomProcessingLegalBasisRepository {


    @Query("{'countryId':?0,_id:?1,deleted:false}")
    ProcessingLegalBasis findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    ProcessingLegalBasis findByName(Long countryId,String name);

    ProcessingLegalBasis findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<ProcessingLegalBasis> getProcessingLegalBasisListByIds(Long countryId, Set<BigInteger> processingLegalBasisIds);

    @Query("{_id:{$in:?0},deleted:false}")
    List<ProcessingLegalBasis> findProcessingLegalBasisByIds(List<BigInteger> legalBasisIds);

    @Query("{deleted:false,countryId:?0}")
    List<ProcessingLegalBasisResponseDTO> findAllByCountryId(Long countryId);

    @Query("{deleted:false,countryId:?0}")
    List<ProcessingLegalBasisResponseDTO> findAllByCountryIdSortByCreatedDate(Long countryId, Sort sort);

    @Query("{organizationId:?0,deleted:false}")
    List<ProcessingLegalBasisResponseDTO> findAllByUnitIdSortByCreatedDate(Long unitId, Sort sort);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingLegalBasis findByUnitIdAndId(Long unitId, BigInteger id);


    @Query("{organizationId:?0,name:?1,deleted:false}")
    ProcessingLegalBasis findByNameAndUnitId(Long unitId, String name);

    @Query("{organizationId:?0,deleted:false}")
    List<ProcessingLegalBasisResponseDTO> findAllByUnitId(Long unitId);






}
