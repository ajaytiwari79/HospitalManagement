package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.ContractType;
import com.kairos.persistence.model.country.default_data.ContractTypeDTO;
import com.kairos.persistence.model.country.default_data.VatType;
import com.kairos.persistence.repository.user.country.ContractTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class ContractTypeService {

    @Inject
    private ContractTypeGraphRepository contractTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public ContractTypeDTO createContractType(long countryId, ContractTypeDTO contractTypeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        ContractType contractType = null;
        if ( country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        } else {
            Boolean contractTypeExistInCountryByNameOrCode = contractTypeGraphRepository.contractTypeExistInCountryByNameOrCode(countryId, "(?i)" + contractTypeDTO.getName(), contractTypeDTO.getCode(), -1L);
            if (contractTypeExistInCountryByNameOrCode) {
                exceptionService.duplicateDataException("error.ContractType.name.code.exist");
            }
            contractType = new ContractType(contractTypeDTO.getName(), contractTypeDTO.getCode(), contractTypeDTO.getDescription());
            contractType.setCountry(country);
            contractTypeGraphRepository.save(contractType);
        }
        contractTypeDTO.setId(contractType.getId());
        return contractTypeDTO;
    }

    public List<ContractTypeDTO> getContractTypeByCountryId(long countryId){
        return contractTypeGraphRepository.findContractTypeByCountry(countryId);
    }

    public ContractTypeDTO updateContractType(long countryId, ContractTypeDTO contractTypeDTO){
        Boolean vatTypeExistInCountryByNameOrCode = contractTypeGraphRepository.contractTypeExistInCountryByNameOrCode(countryId, "(?i)" + contractTypeDTO.getName(), contractTypeDTO.getCode(), contractTypeDTO.getId());
        if (vatTypeExistInCountryByNameOrCode) {
            exceptionService.duplicateDataException("error.ContractType.name.code.exist");
        }
        ContractType currentContractType = contractTypeGraphRepository.findOne(contractTypeDTO.getId());
        if (currentContractType != null){
            currentContractType.setName(contractTypeDTO.getName());
            currentContractType.setDescription(contractTypeDTO.getDescription());
            currentContractType.setCode(contractTypeDTO.getCode());
            contractTypeGraphRepository.save(currentContractType);
        }
        return contractTypeDTO;
    }

    public boolean deleteContractType(long contractTypeId){
        ContractType contractType = contractTypeGraphRepository.findOne(contractTypeId);
        if (contractType!=null){
            contractType.setEnabled(false);
            contractTypeGraphRepository.save(contractType);
        }else {
            exceptionService.duplicateDataException("error.VatType.notfound");
        }
        return true;
    }

}
