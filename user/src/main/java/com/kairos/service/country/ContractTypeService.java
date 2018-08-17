package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.ContractType;
import com.kairos.persistence.repository.user.country.ContractTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
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

    public Map<String, Object> createContractType(long countryId, ContractType contractType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            contractType.setCountry(country);
            contractTypeGraphRepository.save(contractType);
            return contractType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getContractTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = contractTypeGraphRepository.findContractTypeByCountry(countryId);
        List<Object> response = new ArrayList<>();;
        if (data!=null){
            for (Map<String,Object> map: data) {
                Object o =  map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }

    public Map<String, Object> updateContractType(ContractType contractType){
        ContractType currentContractType = contractTypeGraphRepository.findOne(contractType.getId());
        if (currentContractType!=null){
            currentContractType.setName(contractType.getName());
            currentContractType.setDescription(contractType.getDescription());
            currentContractType.setCode(contractType.getCode());
            contractTypeGraphRepository.save(currentContractType);
            return currentContractType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteContractType(long contractTypeId){
        ContractType contractType = contractTypeGraphRepository.findOne(contractTypeId);
        if (contractType!=null){
            contractType.setEnabled(false);
            contractTypeGraphRepository.save(contractType);
            return true;
        }
        return false;
    }


    /**
     * @auther anil maurya
     * This method is used in task micro service
     *
     * @return
     */
    public List<Map<String,Object>> getAllContractType(){

        List<ContractType> contractTypes = contractTypeGraphRepository.findAll();
        List<Map<String,Object>> filterContractTypes = new ArrayList<>();
        for(ContractType contractType : contractTypes){
            Map<String,Object> map = new HashMap<>();
            map.put("name",contractType.getName());
            map.put("id",contractType.getId());
            filterContractTypes.add(map);
        }
        return filterContractTypes;
    }
}
