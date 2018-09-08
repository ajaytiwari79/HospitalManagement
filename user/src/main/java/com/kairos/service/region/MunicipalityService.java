package com.kairos.service.region;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ProvinceGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 22/12/16.
 */
@Service
@Transactional
public class MunicipalityService {
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private ProvinceGraphRepository provinceGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public Municipality createMunicipality(Municipality municipality){
        return municipalityGraphRepository.save(municipality);
    }


    public Municipality findMunicipalityById(Long municipalityId){
        return municipalityGraphRepository.findOne(municipalityId);
    }


    public List<Municipality> findAllMunicipality(){
        return   municipalityGraphRepository.findAll();
    }


    public Map<String, Object> updateMunicipalityById(Municipality municipality){
        Municipality currentMunicipality = municipalityGraphRepository.findOne(municipality.getId());
        if (currentMunicipality!=null){
            currentMunicipality.setName(municipality.getName());
            currentMunicipality.setGeoFence(municipality.getGeoFence());
            currentMunicipality.setLatitude(municipality.getLatitude());
            currentMunicipality.setLongitude(municipality.getLongitude());
            currentMunicipality.setCode(municipality.getCode());

            municipalityGraphRepository.save(currentMunicipality);
            return  currentMunicipality.retrieveDetails();


        }
        return  null;
    }

    public boolean deleteMunicipalityById(Long municipalityId){
        Municipality municipality = municipalityGraphRepository.findOne(municipalityId);
        if (municipality!=null){
            municipality.setEnable(false);
            municipalityGraphRepository.save(municipality);
            return true;
        }
        return false;
    }



    public Map<String, Object> addZipCodeToMunicipality(Long municipalityId, ZipCode zipCode) {
        Municipality municipality = municipalityGraphRepository.findOne(municipalityId);
        if(municipality == null){
            exceptionService.dataNotFoundByIdException("message.municipality.notFound");

        }
        List<Municipality> municipalities = zipCode.getMunicipalities();
        municipalities.add(municipality);
        zipCode.setMunicipalities(municipalities);
        zipCodeGraphRepository.save(zipCode);
        return  zipCode.retrieveDetails();
    }


    public Map<String, Object> updateZipCodeToMunicipality(ZipCode zipCode) {
        ZipCode currentZip = zipCodeGraphRepository.findOne(zipCode.getId());
        if (currentZip!=null){
            currentZip.setName(zipCode.getName());
            currentZip.setGeoFence(zipCode.getGeoFence());
            currentZip.setZipCode(zipCode.getZipCode());
            zipCodeGraphRepository.save(currentZip);
            return currentZip.retrieveDetails();
        }
        return null;
    }

    public boolean deleteZipCodeToMunicipality(Long zipCodeId) {
        ZipCode currentZip = zipCodeGraphRepository.findOne(zipCodeId);
        if (currentZip!=null){
            currentZip.setEnable(false);

            zipCodeGraphRepository.save(currentZip);
            return true;
        }
        return false;
    }

    public List<Map<String,Object>> getAllZipCodeToMunicipality(Long municipalityId) {
        return FormatUtil.formatNeoResponse( municipalityGraphRepository.getAllZipCodes(municipalityId));
    }



    public Map<String, Object> addMunicipalityToProvince(Municipality municipality, Long provinceId) {
        Province province = provinceGraphRepository.findOne(provinceId);
        if (province!=null){
            municipality.setProvince(province);
             municipality = municipalityGraphRepository.save(municipality);
            return municipality.retrieveDetails();
        }
        return null;
    }

    public List<Map<String,Object>> getMunicipalityToProvince(Long provinceId) {
        return FormatUtil.formatNeoResponse(municipalityGraphRepository.getAllMunicipalitiesOfProvince(provinceId));

    }

    public List<Municipality> getMunicipalitiesByZipCode(int zipcode){
        return municipalityGraphRepository.getMuncipalityByZipcode(zipcode);
    }
}
