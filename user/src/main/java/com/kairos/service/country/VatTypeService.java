package com.kairos.service.country;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.VatType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.VatTypeGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class VatTypeService {

    @Inject
    private VatTypeGraphRepository vatTypeGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createVatType(long countryId, VatType vatType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            vatType.setCountry(country);
             vatTypeGraphRepository.save(vatType);
            return vatType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getVatTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = vatTypeGraphRepository.findVATtypeByCountry(countryId);
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

    public Map<String, Object> updateVatType(VatType vatType){
        VatType currentVatType = vatTypeGraphRepository.findOne(vatType.getId());
        if (currentVatType!=null){
            currentVatType.setName(vatType.getName());
            currentVatType.setDescription(vatType.getDescription());
            currentVatType.setPercentage(vatType.getPercentage());
            currentVatType.setCode(vatType.getCode());
            vatTypeGraphRepository.save(currentVatType);

            return currentVatType.retrieveDetails();

        }
        return null;
    }

    public boolean deleteVatType(long contractTypeId){
        VatType vatType = vatTypeGraphRepository.findOne(contractTypeId);
        if (vatType !=null){
            vatType.setEnabled(false);
            vatTypeGraphRepository.save(vatType);
            return true;
        }
        return false;
    }
}
