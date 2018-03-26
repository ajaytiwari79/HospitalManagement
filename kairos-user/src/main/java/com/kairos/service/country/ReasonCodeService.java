package com.kairos.service.country;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.ReasonCode;
import com.kairos.persistence.model.user.country.ReasonCodeResponseDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.response.dto.web.ReasonCodeDTO;
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
public class ReasonCodeService extends UserBaseEntity {

   @Inject
   ReasonCodeGraphRepository reasonCodeGraphRepository;
   @Inject
   CountryGraphRepository countryGraphRepository;

    public ReasonCodeDTO createReasonCode(long countryId,ReasonCodeDTO reasonCodeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        if(!Optional.ofNullable(country).isPresent()){
            throw new DataNotFoundByIdException("Country not found: "+countryId);
        }

        boolean isAlreadyExists=reasonCodeGraphRepository.findByNameExcludingCurrent(countryId,-1L,"(?i)"+reasonCodeDTO.getName().trim(),reasonCodeDTO.getReasonCodeType());
        if(isAlreadyExists){
            throw new DuplicateDataException("ReasonCode already exists: "+reasonCodeDTO.getName());
        }
        ReasonCode reasonCode=new ReasonCode(reasonCodeDTO.getName().trim(),reasonCodeDTO.getCode(),reasonCodeDTO.getDescription(),reasonCodeDTO.getReasonCodeType(),country);
        reasonCodeGraphRepository.save(reasonCode);

        return new ReasonCodeDTO(reasonCode.getId(),reasonCode.getName(),reasonCode.getCode(),reasonCode.getDescription(),reasonCode.getReasonCodeType());
    }

    public List<ReasonCodeResponseDTO> getReasonCodes(long countryId){
       return reasonCodeGraphRepository.findReasonCodesByCountry(countryId);
    }

    public ReasonCodeResponseDTO updateReasonCode(long countryId,ReasonCodeDTO reasonCodeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        if(!Optional.ofNullable(country).isPresent()){
            throw new DataNotFoundByIdException("Country not found: "+countryId);
        }
        ReasonCode reasonCode=reasonCodeGraphRepository.findOne(reasonCodeDTO.getId());
        if(!Optional.ofNullable(reasonCode).isPresent() || reasonCode.isDeleted() == true){
            throw new DataNotFoundByIdException("Invalid ReasonCode: "+reasonCodeDTO.getId());
        }
        boolean isNameAlreadyExists=reasonCodeGraphRepository.findByNameExcludingCurrent(countryId,reasonCodeDTO.getId(),"(?i)"+reasonCodeDTO.getName().trim(),reasonCodeDTO.getReasonCodeType());
        if(isNameAlreadyExists){
            throw new DuplicateDataException("ReasonCode already exists: "+reasonCodeDTO.getName());
        }
        reasonCode.setName(reasonCodeDTO.getName().trim());
        reasonCode.setCode(reasonCodeDTO.getCode());
        reasonCode.setDescription(reasonCodeDTO.getDescription());
        reasonCodeGraphRepository.save(reasonCode);
        ReasonCodeResponseDTO reasonCodeResponseDTO=new ReasonCodeResponseDTO(reasonCode.getId(),reasonCode.getName(),reasonCode.getCode(),reasonCode.getDescription(),reasonCode.getReasonCodeType());
        return reasonCodeResponseDTO;
    }
    public boolean deleteReasonCode(long reasonCodeId){
        ReasonCode reasonCode=reasonCodeGraphRepository.findOne(reasonCodeId);
        if(!Optional.ofNullable(reasonCode).isPresent() || reasonCode.isDeleted() == true){
            throw new DataNotFoundByIdException("Invalid ReasonCode: "+reasonCodeId);
        }
        reasonCode.setDeleted(true);
        reasonCodeGraphRepository.save(reasonCode);
        return true;
    }
}
