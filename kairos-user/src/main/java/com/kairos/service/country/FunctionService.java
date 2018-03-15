package com.kairos.service.country;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.Function;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.response.dto.web.FunctionDTO;
import com.kairos.persistence.model.user.country.FunctionResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pavan on 13/3/18.
 */
@Service
@Transactional
public class FunctionService extends UserBaseEntity{

    @Inject CountryGraphRepository countryGraphRepository;

    @Inject FunctionGraphRepository functionGraphRepository;

    @Inject OrganizationGraphRepository organizationGraphRepository;

    public Function createFunction(Long countryId, FunctionDTO functionDTO){
        Country country = countryGraphRepository.findOne(countryId);
        if(!Optional.ofNullable(country).isPresent()){
            throw new DataNotFoundByIdException("Country not found: "+countryId);
        }
        Function isAlreadyExists=functionGraphRepository.findByNameIgnoreCase(countryId,functionDTO.getName().trim());
        if(Optional.ofNullable(isAlreadyExists).isPresent()){
            throw new DuplicateDataException("Function already exists: "+functionDTO.getName());
        }
        List<Level> levels=new ArrayList<>();
        if(!functionDTO.getOrganizationLevelIds().isEmpty()){
            levels=countryGraphRepository.getAllLevelById(countryId,functionDTO.getOrganizationLevelIds());
        }
        List<Organization> unions=new ArrayList<>();
        if(!functionDTO.getUnionIds().isEmpty()){
            unions= organizationGraphRepository.findAllUnionById(functionDTO.getUnionIds());
        }
        Function function=new Function(functionDTO.getName(),functionDTO.getDescription(),functionDTO.getStartDate(),functionDTO.getEndDate(),unions,levels,country);
        functionGraphRepository.save(function);
        function.setCountry(null);
        return function;
    }

    public List<FunctionResponseDTO> getFunctions(long countryId){
        return functionGraphRepository.findFunctionsByCountry(countryId);

    }
    public Function updateFunction(Long countryId,FunctionDTO functionDTO){
        Country country = countryGraphRepository.findOne(countryId);
        if(!Optional.ofNullable(country).isPresent()){
            throw new DataNotFoundByIdException("Country not found: "+countryId);
        }
        Function function=functionGraphRepository.findOne(functionDTO.getId());
        if(!Optional.ofNullable(function).isPresent() || function.isDeleted() == true){
            throw new DataNotFoundByIdException("Invalid function: "+functionDTO.getId());
        }
        Function isNameAlreadyExists=functionGraphRepository.findByNameExcludingCurrent(countryId,functionDTO.getId(),functionDTO.getName().trim());
        if(Optional.ofNullable(isNameAlreadyExists).isPresent()){
            throw new DuplicateDataException("Function already exists: "+functionDTO.getName());
        }
        List<Level> levels=new ArrayList<>();
        if(!functionDTO.getOrganizationLevelIds().isEmpty()){
            levels=countryGraphRepository.getAllLevelById(countryId,functionDTO.getOrganizationLevelIds());
        }
        List<Organization> unions=new ArrayList<>();
        if(!functionDTO.getUnionIds().isEmpty()){
            unions= organizationGraphRepository.findAllUnionById(functionDTO.getUnionIds());
        }

        function.setName(functionDTO.getName());
        function.setDescription(functionDTO.getDescription());
        function.setStartDate(functionDTO.getStartDate());
        function.setEndDate(functionDTO.getEndDate());
        function.setUnions(unions);
        function.setOrganizationLevels(levels);
        functionGraphRepository.save(function);
        function.setCountry(null);
        return function;
    }
    public boolean deleteFunction(long functionId){
        Function function=functionGraphRepository.findOne(functionId);
        if(!Optional.ofNullable(function).isPresent() || function.isDeleted() == true){
            throw new DataNotFoundByIdException("Invalid function: "+functionId);
        }
        function.setDeleted(true);
        functionGraphRepository.save(function);
        return true;
    }
}
