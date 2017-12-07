package com.kairos.service.country.feature;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.feature.Feature;
import com.kairos.persistence.model.user.country.feature.FeatureQueryResult;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.FeatureGraphRepository;
import com.kairos.persistence.repository.user.resources.VehicleGraphRepository;
import com.kairos.response.dto.web.feature.FeatureDTO;
import com.kairos.response.dto.web.feature.VehicleFeaturesDTO;
import com.kairos.response.dto.web.tag.TagDTO;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 4/12/17.
 */
@Service
@Transactional
public class FeatureService extends UserBaseService{

    @Autowired
    CountryGraphRepository countryGraphRepository;

    @Autowired
    FeatureGraphRepository featureGraphRepository;

    @Autowired
    VehicleGraphRepository vehicleGraphRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Feature addCountryFeature(Long countryId, FeatureDTO featureDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        logger.info("featureDTO : "+featureDTO.getName());
        if( featureGraphRepository.isFeatureExistsWithSameName(featureDTO.getName(), countryId, false) ){
            throw new DuplicateDataException("Feature already exists with same name " +featureDTO.getName() );
        }
        return featureGraphRepository.createFeature(countryId,featureDTO.getName(), featureDTO.getDescription(), new Date().getTime());
    }

    public FeatureQueryResult updateFeature(Long countryId, Long featureId, FeatureDTO featureDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        Feature feature = featureGraphRepository.getFeatureById(featureId, countryId, false);
        if( feature == null) {
            throw new DataNotFoundByIdException("Feature does not exist with id " +featureId );
        }

        if( ! ( feature.getName().equalsIgnoreCase(featureDTO.getName()) ) && featureGraphRepository.isFeatureExistsWithSameName(featureDTO.getName(), countryId, false) ){
            throw new DuplicateDataException("Feature already exists with name " +featureDTO.getName() );
        }
        return featureGraphRepository.updateFeature(featureId, countryId, featureDTO.getName(), featureDTO.getDescription(), new Date().getTime());
    }


    public Boolean deleteFeature(Long countryId, Long featureId){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        Feature feature = featureGraphRepository.getFeatureById(featureId, countryId, false);
        if( feature == null) {
            throw new DataNotFoundByIdException("Incorrect feature id " + featureId);
        }
        feature.setDeleted(true);
        save(feature);
        return true;
    }

    public HashMap<String,Object> getListOfFeatures(Long countryId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }

        if(filterText == null){
            filterText = "";
        }

        HashMap<String,Object> featuresData = new HashMap<>();
        featuresData.put("features",featureGraphRepository.getListOfFeatures(countryId, false, filterText));

        return featuresData;
    }

    public Vehicle updateFeaturesOfVehicle(Long countryId, Long vehicleId, VehicleFeaturesDTO vehicleFeaturesDTO){
        Vehicle vehicle = vehicleGraphRepository.findOne(vehicleId,0);
        if (vehicle == null) {
            throw new DataNotFoundByIdException("Incorrect vehicle id " + vehicleId);
        }
        List<Feature> features = featureGraphRepository.getListOfFeaturesByIds(countryId,false, vehicleFeaturesDTO.getFeatures());
        vehicle.setFeatures(features);
        vehicleGraphRepository.save(vehicle);
        return vehicle;
    }

    public Feature getFeatureByName(long countryId, String name){
        return featureGraphRepository.getFeatureByName(countryId, name, false);
    }
}
