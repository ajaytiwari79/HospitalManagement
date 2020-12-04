package com.kairos.service.reason_code;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.reason_code.ReasonCode;
import com.kairos.persistence.repository.reason_code.ReasonCodeRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.reason_code.ReasonCodeType.TIME_TYPE;

@Service
public class ReasonCodeService {

    @Inject
    private ReasonCodeRepository reasonCodeRepository;
    @Inject
    private ExceptionService exceptionService;

    public ReasonCodeDTO createReasonCodeForCountry(long countryId, ReasonCodeDTO reasonCodeDTO) {

        boolean isAlreadyExists = reasonCodeRepository.existsByCountryIdAndIdNotInAndNameOrReasonCodeTypeOrCode(countryId, new BigInteger("-1"), "(?i)" + reasonCodeDTO.getName().trim(), reasonCodeDTO.getReasonCodeType(),reasonCodeDTO.getCode());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException("MESSAGE_REASONCODE_NAME_ALREADYEXIST", reasonCodeDTO.getName());
        }
        validateReasonCode(reasonCodeDTO);
        ReasonCode reasonCode = new ReasonCode(reasonCodeDTO.getName(), reasonCodeDTO.getCode(), reasonCodeDTO.getDescription(), reasonCodeDTO.getReasonCodeType(), countryId, reasonCodeDTO.getTimeTypeId());
        reasonCodeRepository.save(reasonCode);

        return new ReasonCodeDTO(reasonCode.getId(), reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType(), reasonCode.getTimeTypeId());
    }

    public ReasonCodeDTO createReasonCodeForUnit(long unitId, ReasonCodeDTO reasonCodeDTO) {
        boolean isAlreadyExists = reasonCodeRepository.existsByUnitIdAndNameOrReasonCodeTypeOrCodeAndIdNotIn(unitId,  reasonCodeDTO.getName().trim(), reasonCodeDTO.getReasonCodeType(),reasonCodeDTO.getCode(),new BigInteger("-1"));
        if (isAlreadyExists) {
            exceptionService.duplicateDataException("MESSAGE_REASONCODE_NAME_ALREADYEXIST", reasonCodeDTO.getName());
        }
        ReasonCode reasonCode = new ReasonCode(reasonCodeDTO.getName(), reasonCodeDTO.getCode(), reasonCodeDTO.getDescription(), reasonCodeDTO.getReasonCodeType(), reasonCodeDTO.getTimeTypeId(),unitId);
        reasonCodeRepository.save(reasonCode);
        return new ReasonCodeDTO(reasonCode.getId(), reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType(), reasonCode.getTimeTypeId());
    }


    public List<ReasonCodeDTO> getReasonCodesForCountry(long countryId, ReasonCodeType reasonCodeType) {
        return  reasonCodeRepository.findReasonCodesByCountryIdAndReasonCodeTypeAndDeletedFalse(countryId, reasonCodeType);
    }


    public List<ReasonCodeDTO> getReasonCodesByUnitId(long unitId, ReasonCodeType reasonCodeType) {
         return reasonCodeRepository.findByUnitIdAndReasonCodeTypeAndDeletedFalse(unitId, reasonCodeType);
    }

    public List<ReasonCodeDTO> getReasonCodesByUnitIds(List<Long> unitIds, ReasonCodeType reasonCodeType) {
        return reasonCodeRepository.findByUnitIdInAndReasonCodeType(unitIds, reasonCodeType);
    }

    public ReasonCodeDTO updateReasonCodeForCountry(long countryId, ReasonCodeDTO reasonCodeDTO) {
        boolean isNameAlreadyExists = reasonCodeRepository.existsByCountryIdAndIdNotInAndNameOrReasonCodeTypeOrCode(countryId, reasonCodeDTO.getId(), "(?i)" + reasonCodeDTO.getName(), reasonCodeDTO.getReasonCodeType(),reasonCodeDTO.getCode());
        if (isNameAlreadyExists) {
            exceptionService.duplicateDataException("MESSAGE_REASONCODE_NAME_ALREADYEXIST", reasonCodeDTO.getName());
        }
        validateReasonCode(reasonCodeDTO);
        ReasonCode reasonCode = reasonCodeRepository.findOne(reasonCodeDTO.getId());
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("MESSAGE_REASONCODE_ID_NOTFOUND", reasonCodeDTO.getId());

        }
        return updateReasonCode(reasonCode, reasonCodeDTO);
    }

    public ReasonCodeDTO updateReasonCodeForUnit(long unitId, ReasonCodeDTO reasonCodeDTO) {
        boolean isNameAlreadyExists = reasonCodeRepository.existsByUnitIdAndNameOrReasonCodeTypeOrCodeAndIdNotIn(unitId,  "(?i)" + reasonCodeDTO.getName(), reasonCodeDTO.getReasonCodeType(),reasonCodeDTO.getCode(),reasonCodeDTO.getId());
        if (isNameAlreadyExists) {
            exceptionService.duplicateDataException("MESSAGE_REASONCODE_NAME_ALREADYEXIST", reasonCodeDTO.getName());
        }
        ReasonCode reasonCode = reasonCodeRepository.findOne(reasonCodeDTO.getId());
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("MESSAGE_REASONCODE_ID_NOTFOUND", reasonCodeDTO.getId());

        }
        return updateReasonCode(reasonCode, reasonCodeDTO);
    }

    private ReasonCodeDTO updateReasonCode(ReasonCode reasonCode, ReasonCodeDTO reasonCodeDTO) {
        reasonCode.setName(reasonCodeDTO.getName());
        reasonCode.setCode(reasonCodeDTO.getCode());
        reasonCode.setDescription(reasonCodeDTO.getDescription());
        reasonCode.setTimeTypeId(reasonCodeDTO.getTimeTypeId());
        reasonCodeRepository.save(reasonCode);
        return new ReasonCodeDTO(reasonCode.getId(), reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType());
    }

    public boolean deleteReasonCode(BigInteger reasonCodeId) {
        ReasonCode reasonCode = reasonCodeRepository.findOne(reasonCodeId);
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("MESSAGE_REASONCODE_ID_NOTFOUND", reasonCodeId);

        }
        reasonCode.setDeleted(true);
        reasonCodeRepository.save(reasonCode);
        return true;
    }


    public void createReasonCodeForUnit(Long unitId, long countryId) {
        List<ReasonCodeDTO> reasonCodeDTO = reasonCodeRepository.findReasonCodeByCountryIdAndDeletedFalseOrderByCreatedAt(countryId);
        createDefaultData(reasonCodeDTO, unitId);
    }

    private void createDefaultData(List<ReasonCodeDTO> reasonCodeResponseDTO, Long unitId) {
        if (!reasonCodeResponseDTO.isEmpty()) {
            List<ReasonCode> reasonCodes=reasonCodeResponseDTO.stream().map(reasonCode->new ReasonCode(reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType(),  reasonCode.getTimeTypeId(),unitId)).collect(Collectors.toList());
            reasonCodeRepository.saveAll(reasonCodes);
        }
    }

    private void validateReasonCode(ReasonCodeDTO reasonCodeDTO){
        if(TIME_TYPE.equals(reasonCodeDTO.getReasonCodeType()) && isNull(reasonCodeDTO.getTimeTypeId())){
                exceptionService.actionNotPermittedException("ERROR_TIMETYPE_UNSELECTED");
        }
    }

    public boolean anyReasonCodeLinkedWithTimeType(BigInteger timeTypeId){
        return reasonCodeRepository.existsByTimeTypeIdAndDeletedFalse(timeTypeId);
    }

    public Map<String, TranslationInfo> updateTranslation(BigInteger reasonCodeId, Map<String,TranslationInfo> translations) {
        ReasonCode reasonCode =reasonCodeRepository.findOne(reasonCodeId);
        reasonCode.setTranslations(translations);
        reasonCodeRepository.save(reasonCode);
        return reasonCode.getTranslations();
    }


    public List<ReasonCodeDTO> findAllByIds(Set<BigInteger> absenceReasonCodeIds) {
        return reasonCodeRepository.findAllByIdAndDeletedFalse(absenceReasonCodeIds);
    }

    public void transferReasonCode(List<ReasonCodeDTO> reasonCodeDTOS) {
        List<ReasonCode> reasonCodes= ObjectMapperUtils.copyCollectionPropertiesByMapper(reasonCodeDTOS,ReasonCode.class);
        reasonCodeRepository.saveEntities(reasonCodes);
    }


}
