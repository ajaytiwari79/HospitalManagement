package com.kairos.service.region;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.repository.user.region.ProvinceGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_PROVINCESERVICE_REGION_NOTFOUND;

/**
 * Created by oodles on 7/1/17.
 */
@Service
@Transactional
public class ProvinceService {

    @Inject
    private ProvinceGraphRepository provinceGraphRepository;

    @Inject
    private RegionGraphRepository regionGraphRepository;

    @Inject
    private ExceptionService exceptionService;

    public Map<String, Object> updateProvinceById(Province province) {
        Province currentProvince = provinceGraphRepository.findOne(province.getId());
        if (currentProvince!=null){
            currentProvince.setName(province.getName());
            currentProvince.setCode(province.getCode());
            currentProvince.setGeoFence(province.getGeoFence());
            currentProvince.setLatitude(province.getLatitude());
            currentProvince.setLongitude(province.getLongitude());
            currentProvince =  provinceGraphRepository.save(currentProvince);
            return currentProvince.retrieveDetails();
        }
        return null;
    }

    public boolean deleteProvinceById(Long provinceId) {
        Province province = provinceGraphRepository.findOne(provinceId);
        if (province!=null){
            province.setEnable(false);
            provinceGraphRepository.save(province);
            return true;
        }
        return false;
    }


    public Map<String, Object> addProvinceToRegion(Province province, Long regionId) {
        Region region = regionGraphRepository.findOne(regionId);
        if(region == null){
            exceptionService.dataNotFoundByIdException(MESSAGE_PROVINCESERVICE_REGION_NOTFOUND);
            
        }
        province.setRegion(region);
        provinceGraphRepository.save(province);
        return province.retrieveDetails();
    }


    public List<Map<String,Object>> getProvinceToRegion(Long regionId) {
        return FormatUtil.formatNeoResponse(provinceGraphRepository.findAllProvinceByRegionId(regionId));

    }

    public Map<String, TranslationInfo> updateTranslationOfProvince(Long provinceId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        for(Map.Entry<String,TranslationInfo> entry :translations.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
        Province province =provinceGraphRepository.findOne(provinceId);
        province.setTranslatedNames(translatedNames);
        province.setTranslatedDescriptions(translatedDescriptios);
        provinceGraphRepository.save(province);
        return province.getTranslatedData();
    }


}
