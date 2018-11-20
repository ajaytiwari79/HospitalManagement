package com.kairos.service.country;

import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
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
   private OrganizationGraphRepository  organizationGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    public ReasonCodeDTO createReasonCodeForCountry(long countryId,ReasonCodeDTO reasonCodeDTO){
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

    public ReasonCodeDTO createReasonCodeForUnit(long unitId,ReasonCodeDTO reasonCodeDTO){
        Organization organization = organizationGraphRepository.findOne(unitId);
        if(!Optional.ofNullable(organization).isPresent()){
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",unitId);
        }
        boolean isAlreadyExists=reasonCodeGraphRepository.findByUnitIdAndNameExcludingCurrent(unitId,-1L,"(?i)"+reasonCodeDTO.getName().trim(),reasonCodeDTO.getReasonCodeType());
        if(isAlreadyExists){
            exceptionService.duplicateDataException("message.reasonCode.name.alreadyExist",reasonCodeDTO.getName());

        }
        ReasonCode reasonCode=new ReasonCode(reasonCodeDTO.getName(),reasonCodeDTO.getCode(),reasonCodeDTO.getDescription(),reasonCodeDTO.getReasonCodeType(),organization);
        reasonCodeGraphRepository.save(reasonCode);

        return new ReasonCodeDTO(reasonCode.getId(),reasonCode.getName(),reasonCode.getCode(),reasonCode.getDescription(),reasonCode.getReasonCodeType());
    }


    public List<ReasonCodeResponseDTO> getReasonCodesForCountry(long countryId, ReasonCodeType reasonCodeType){
        return reasonCodeGraphRepository.findReasonCodesByCountry(countryId,reasonCodeType);
    }


    public List<ReasonCodeResponseDTO> getReasonCodesByUnitId(long unitId, ReasonCodeType reasonCodeType){
        return reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(unitId,reasonCodeType);
    }

    public ReasonCodeResponseDTO updateReasonCodeForCountry(long countryId, ReasonCodeDTO reasonCodeDTO){
        boolean isNameAlreadyExists=reasonCodeGraphRepository.findByNameExcludingCurrent(countryId,reasonCodeDTO.getId(),"(?i)"+reasonCodeDTO.getName(),reasonCodeDTO.getReasonCodeType());
        if(isNameAlreadyExists){
            exceptionService.duplicateDataException("message.reasonCode.name.alreadyExist",reasonCodeDTO.getName());

        }
        ReasonCode reasonCode=reasonCodeGraphRepository.findByCountryAndReasonCode(countryId,reasonCodeDTO.getId());
        if(!Optional.ofNullable(reasonCode).isPresent()){
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound",reasonCodeDTO.getId());

        }
        ReasonCodeResponseDTO reasonCodeResponseDTO=updateReasonCode(reasonCode,reasonCodeDTO);
        return reasonCodeResponseDTO;
    }

    public ReasonCodeResponseDTO updateReasonCodeForUnit(long unitId, ReasonCodeDTO reasonCodeDTO){
        boolean isNameAlreadyExists=reasonCodeGraphRepository.findByUnitIdAndNameExcludingCurrent(unitId,reasonCodeDTO.getId(),"(?i)"+reasonCodeDTO.getName(),reasonCodeDTO.getReasonCodeType());
        if(isNameAlreadyExists){
            exceptionService.duplicateDataException("message.reasonCode.name.alreadyExist",reasonCodeDTO.getName());

        }
        ReasonCode reasonCode=reasonCodeGraphRepository.findByUnitidAndReasonCode(unitId,reasonCodeDTO.getId());
        if(!Optional.ofNullable(reasonCode).isPresent()){
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound",reasonCodeDTO.getId());

        }
        ReasonCodeResponseDTO reasonCodeResponseDTO=updateReasonCode(reasonCode,reasonCodeDTO);
        return reasonCodeResponseDTO;
    }

    public ReasonCodeResponseDTO updateReasonCode(ReasonCode reasonCode,ReasonCodeDTO reasonCodeDTO){
        reasonCode.setName(reasonCodeDTO.getName());
        reasonCode.setCode(reasonCodeDTO.getCode());
        reasonCode.setDescription(reasonCodeDTO.getDescription());
        reasonCodeGraphRepository.save(reasonCode);
        return new ReasonCodeResponseDTO(reasonCode.getId(),reasonCode.getName(),reasonCode.getCode(),reasonCode.getDescription(),reasonCode.getReasonCodeType());
    }

    public boolean deleteReasonCodeForCountry(long countryId, long reasonCodeId){
        ReasonCode reasonCode=reasonCodeGraphRepository.findByCountryAndReasonCode(countryId,reasonCodeId);
        if(!Optional.ofNullable(reasonCode).isPresent()){
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound",reasonCodeId);

        }
        reasonCode.setDeleted(true);
        reasonCodeGraphRepository.save(reasonCode);
        return true;
    }

    public boolean deleteReasonCodeForUnit(long unitId, long reasonCodeId){
        ReasonCode reasonCode=reasonCodeGraphRepository.findByUnitidAndReasonCode(unitId,reasonCodeId);
        if(!Optional.ofNullable(reasonCode).isPresent()){
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound",reasonCodeId);

        }
        reasonCode.setDeleted(true);
        reasonCodeGraphRepository.save(reasonCode);
        return true;
    }

   public void createDefalutDataForUnit(Organization organization, long countryId){
        List<ReasonCodeResponseDTO> reasonCodeResponseDTO=reasonCodeGraphRepository.findReasonCodeByCountryId(countryId);
       createDefaultData(reasonCodeResponseDTO,organization);
   }

    public void createDefalutDataForSubUnit(Organization organization, long parentId){
        List<ReasonCodeResponseDTO> reasonCodeResponseDTO=reasonCodeGraphRepository.findReasonCodeByUnitId(parentId);
        createDefaultData(reasonCodeResponseDTO,organization);
    }

   public void createDefaultData(List<ReasonCodeResponseDTO> reasonCodeResponseDTO, Organization organization){
       if(!reasonCodeResponseDTO.isEmpty()){
       List<ReasonCode> reasonCodes=new ArrayList<>();
       reasonCodeResponseDTO.forEach(reasonCode->{
           reasonCodes.add(new ReasonCode(reasonCode.getName(),reasonCode.getCode(),reasonCode.getDescription(),reasonCode.getReasonCodeType(),organization));
       });
       reasonCodeGraphRepository.saveAll(reasonCodes);
   }
    }
}
