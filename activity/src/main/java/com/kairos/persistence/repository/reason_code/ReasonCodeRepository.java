package com.kairos.persistence.repository.reason_code;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.reason_code.ReasonCode;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface ReasonCodeRepository extends MongoBaseRepository<ReasonCode,BigInteger> {

    List<ReasonCodeDTO> findReasonCodesByCountryIdAndReasonCodeTypeAndDeletedFalse(long countryId, ReasonCodeType reasonCodeType);

    boolean existsByCountryIdAndIdNotInAndNameOrReasonCodeTypeOrCode(Long countryId, BigInteger reasonCodeId, String name, ReasonCodeType reasonCodeType, String code);

    List<ReasonCodeDTO> findByUnitIdAndReasonCodeTypeAndDeletedFalse(long unitId, ReasonCodeType reasonCodeType);

    List<ReasonCodeDTO> findByUnitIdInAndReasonCodeType(List<Long> unitId, ReasonCodeType reasonCodeType);

    List<ReasonCodeDTO> findAllByUnitIdAndDeletedFalseOrderByCreationDate(long orgId);

    boolean existsByUnitIdAndNameOrReasonCodeTypeOrCodeAndIdNotIn(Long unitId,  String name, ReasonCodeType reasonCodeType,String code,BigInteger reasonCodeId);

    ReasonCode findByUnitIdAndReasonCodeAndDeletedFalse(long unitId, long reasonCodeId);

    List<ReasonCode> findByIdInAndDeletedFalse(Set<Long> reasonCodeIds);

    boolean existsByTimeTypeIdAndDeletedFalse(BigInteger timeTypeId);

    List<ReasonCodeDTO> findReasonCodeByCountryIdAndDeletedFalseOrderByCreationDateASC(Long countryId);

    List<ReasonCodeDTO> findAllByIdAndDeletedFalse(Set<BigInteger> ids);

    List<ReasonCodeDTO> findByReasonCodeTypeAndUnitIdNotNull(ReasonCodeType reasonCodeType);
}
