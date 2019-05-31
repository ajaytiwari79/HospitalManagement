package com.kairos.service.country;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.enums.reason_code.ReasonCodeType.TIME_TYPE;

/**
 * Created by pavan on 23/3/18.
 */
@Service
@Transactional
public class ReasonCodeService {

    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityIntegrationService activityIntegrationService;

    public ReasonCodeDTO createReasonCodeForCountry(long countryId, ReasonCodeDTO reasonCodeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }
        boolean isAlreadyExists = reasonCodeGraphRepository.findByNameExcludingCurrent(countryId, -1L, "(?i)" + reasonCodeDTO.getName().trim(), reasonCodeDTO.getReasonCodeType());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException(MESSAGE_REASONCODE_NAME_ALREADYEXIST, reasonCodeDTO.getName());
        }
        validateReasonCode(reasonCodeDTO,country.getId());
        ReasonCode reasonCode = new ReasonCode(reasonCodeDTO.getName(), reasonCodeDTO.getCode(), reasonCodeDTO.getDescription(), reasonCodeDTO.getReasonCodeType(), country, reasonCodeDTO.getTimeTypeId());
        reasonCodeGraphRepository.save(reasonCode);

        return new ReasonCodeDTO(reasonCode.getId(), reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType(), reasonCode.getTimeTypeId());
    }

    public ReasonCodeDTO createReasonCodeForUnit(long unitId, ReasonCodeDTO reasonCodeDTO) {
        Unit unit = unitGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        boolean isAlreadyExists = reasonCodeGraphRepository.findByUnitIdAndNameExcludingCurrent(unitId, -1L, "(?i)" + reasonCodeDTO.getName().trim(), reasonCodeDTO.getReasonCodeType());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException(MESSAGE_REASONCODE_NAME_ALREADYEXIST, reasonCodeDTO.getName());
        }
        ReasonCode reasonCode = new ReasonCode(reasonCodeDTO.getName(), reasonCodeDTO.getCode(), reasonCodeDTO.getDescription(), reasonCodeDTO.getReasonCodeType(), unit, reasonCodeDTO.getTimeTypeId());
        reasonCodeGraphRepository.save(reasonCode);

        return new ReasonCodeDTO(reasonCode.getId(), reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType(), reasonCode.getTimeTypeId());
    }


    public List<ReasonCodeResponseDTO> getReasonCodesForCountry(long countryId, ReasonCodeType reasonCodeType) {
        return reasonCodeGraphRepository.findReasonCodesByCountry(countryId, reasonCodeType);
    }


    public List<ReasonCodeResponseDTO> getReasonCodesByUnitId(long unitId, ReasonCodeType reasonCodeType) {
        return reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(unitId, reasonCodeType);
    }

    public ReasonCodeResponseDTO updateReasonCodeForCountry(long countryId, ReasonCodeDTO reasonCodeDTO) {
        boolean isNameAlreadyExists = reasonCodeGraphRepository.findByNameExcludingCurrent(countryId, reasonCodeDTO.getId(), "(?i)" + reasonCodeDTO.getName(), reasonCodeDTO.getReasonCodeType());
        if (isNameAlreadyExists) {
            exceptionService.duplicateDataException(MESSAGE_REASONCODE_NAME_ALREADYEXIST, reasonCodeDTO.getName());
        }
        validateReasonCode(reasonCodeDTO,countryId);
        ReasonCode reasonCode = reasonCodeGraphRepository.findByCountryAndReasonCode(countryId, reasonCodeDTO.getId());
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_REASONCODE_ID_NOTFOUND, reasonCodeDTO.getId());

        }
        return updateReasonCode(reasonCode, reasonCodeDTO);
    }

    public ReasonCodeResponseDTO updateReasonCodeForUnit(long unitId, ReasonCodeDTO reasonCodeDTO) {
        boolean isNameAlreadyExists = reasonCodeGraphRepository.findByUnitIdAndNameExcludingCurrent(unitId, reasonCodeDTO.getId(), "(?i)" + reasonCodeDTO.getName(), reasonCodeDTO.getReasonCodeType());
        if (isNameAlreadyExists) {
            exceptionService.duplicateDataException(MESSAGE_REASONCODE_NAME_ALREADYEXIST, reasonCodeDTO.getName());
        }
        ReasonCode reasonCode = reasonCodeGraphRepository.findByUnitIdAndReasonCode(unitId, reasonCodeDTO.getId());
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_REASONCODE_ID_NOTFOUND, reasonCodeDTO.getId());

        }
        return updateReasonCode(reasonCode, reasonCodeDTO);
    }

    private ReasonCodeResponseDTO updateReasonCode(ReasonCode reasonCode, ReasonCodeDTO reasonCodeDTO) {
        reasonCode.setName(reasonCodeDTO.getName());
        reasonCode.setCode(reasonCodeDTO.getCode());
        reasonCode.setDescription(reasonCodeDTO.getDescription());
        reasonCode.setTimeTypeId(reasonCodeDTO.getTimeTypeId());
        reasonCodeGraphRepository.save(reasonCode);
        return new ReasonCodeResponseDTO(reasonCode.getId(), reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType());
    }

    public boolean deleteReasonCodeForCountry(long countryId, long reasonCodeId) {
        ReasonCode reasonCode = reasonCodeGraphRepository.findByCountryAndReasonCode(countryId, reasonCodeId);
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_REASONCODE_ID_NOTFOUND, reasonCodeId);

        }
        reasonCode.setDeleted(true);
        reasonCodeGraphRepository.save(reasonCode);
        return true;
    }

    public ReasonCode deleteReasonCodeForUnit(long unitId, long reasonCodeId) {
        ReasonCode reasonCode = reasonCodeGraphRepository.findByUnitIdAndReasonCode(unitId, reasonCodeId);
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_REASONCODE_ID_NOTFOUND, reasonCodeId);
        }
        reasonCode.setDeleted(true);
        return reasonCodeGraphRepository.save(reasonCode);
    }

    public void createReasonCodeForUnit(Unit unit, long parentId) {
        List<ReasonCodeResponseDTO> reasonCodeResponseDTO = reasonCodeGraphRepository.findReasonCodeByUnitId(parentId);
        createDefaultData(reasonCodeResponseDTO, unit);
    }

    private void createDefaultData(List<ReasonCodeResponseDTO> reasonCodeResponseDTO, Unit unit) {
        if (!reasonCodeResponseDTO.isEmpty()) {
            List<ReasonCode> reasonCodes=reasonCodeResponseDTO.stream().map(reasonCode->new ReasonCode(reasonCode.getName(), reasonCode.getCode(), reasonCode.getDescription(), reasonCode.getReasonCodeType(), unit, reasonCode.getTimeTypeId())).collect(Collectors.toList());
            reasonCodeGraphRepository.saveAll(reasonCodes);
        }
    }

    private boolean validateReasonCode(ReasonCodeDTO reasonCodeDTO,Long countryId){
        if(TIME_TYPE.equals(reasonCodeDTO.getReasonCodeType())){
            if (reasonCodeDTO.getTimeTypeId()==null){
                exceptionService.actionNotPermittedException(ERROR_TIMETYPE_UNSELECTED);
            }
            boolean timeTypeExists=activityIntegrationService.verifyTimeType(reasonCodeDTO.getTimeTypeId(),countryId);
            if(!timeTypeExists){
                exceptionService.actionNotPermittedException(MESSAGE_DATANOTFOUND,"Time type",reasonCodeDTO.getTimeTypeId());
            }
        }
        return true;
    }

    public boolean anyReasonCodeLinkedWithTimeType(BigInteger timeTypeId){
        return reasonCodeGraphRepository.existsByTimeTypeIdAndDeletedFalse(timeTypeId);
    }
}
