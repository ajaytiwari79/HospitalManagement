package com.kairos.service.country;

import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by pavan on 23/3/18.
 */
@Service
@Transactional
public class ReasonCodeService {

   @Inject
   ReasonCodeGraphRepository reasonCodeGraphRepository;
   @Inject
   CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    public ReasonCodeDTO createReasonCode(long countryId,ReasonCodeDTO reasonCodeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        if(!Optional.ofNullable(country).isPresent()){
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }

        boolean isAlreadyExists=reasonCodeGraphRepository.findByNameExcludingCurrent(countryId,-1L,"(?i)"+reasonCodeDTO.getName().trim(),reasonCodeDTO.getReasonCodeType());
        if(isAlreadyExists){
            exceptionService.duplicateDataException("message.reasonCode.name.alreadyExist",reasonCodeDTO.getName());

        }
        ReasonCode reasonCode=new ReasonCode(reasonCodeDTO.getName(),reasonCodeDTO.getCode(),reasonCodeDTO.getDescription(),reasonCodeDTO.getReasonCodeType(),country);
        reasonCodeGraphRepository.save(reasonCode);

        return new ReasonCodeDTO(reasonCode.getId(),reasonCode.getName(),reasonCode.getCode(),reasonCode.getDescription(),reasonCode.getReasonCodeType());
    }

    public List<ReasonCodeResponseDTO> getReasonCodes(long countryId, ReasonCodeType reasonCodeType){
        return reasonCodeGraphRepository.findReasonCodesByCountry(countryId,reasonCodeType);
    }

    public List<ReasonCodeResponseDTO> getReasonCodesByUnitId(long unitId, ReasonCodeType reasonCodeType){
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        return reasonCodeGraphRepository.findReasonCodesByCountry(countryId,reasonCodeType);
    }

    public ReasonCodeResponseDTO updateReasonCode(long countryId,ReasonCodeDTO reasonCodeDTO){
        boolean isNameAlreadyExists=reasonCodeGraphRepository.findByNameExcludingCurrent(countryId,reasonCodeDTO.getId(),"(?i)"+reasonCodeDTO.getName(),reasonCodeDTO.getReasonCodeType());
        if(isNameAlreadyExists){
            exceptionService.duplicateDataException("message.reasonCode.name.alreadyExist",reasonCodeDTO.getName());

        }
        ReasonCode reasonCode=reasonCodeGraphRepository.findByCountryAndReasonCode(countryId,reasonCodeDTO.getId());
        if(!Optional.ofNullable(reasonCode).isPresent()){
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound",reasonCodeDTO.getId());

        }
        reasonCode.setName(reasonCodeDTO.getName());
        reasonCode.setCode(reasonCodeDTO.getCode());
        reasonCode.setDescription(reasonCodeDTO.getDescription());
        reasonCodeGraphRepository.save(reasonCode);
        ReasonCodeResponseDTO reasonCodeResponseDTO=new ReasonCodeResponseDTO(reasonCode.getId(),reasonCode.getName(),reasonCode.getCode(),reasonCode.getDescription(),reasonCode.getReasonCodeType());
        return reasonCodeResponseDTO;
    }
    public boolean deleteReasonCode(long countryId,long reasonCodeId){
        ReasonCode reasonCode=reasonCodeGraphRepository.findByCountryAndReasonCode(countryId,reasonCodeId);
        if(!Optional.ofNullable(reasonCode).isPresent()){
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound",reasonCodeId);

        }
        reasonCode.setDeleted(true);
        reasonCodeGraphRepository.save(reasonCode);
        return true;
    }
}
