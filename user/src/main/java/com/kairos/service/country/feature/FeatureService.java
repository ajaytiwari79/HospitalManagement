package com.kairos.service.country.feature;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.feature.Feature;
import com.kairos.persistence.model.country.feature.FeatureQueryResult;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.FeatureGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.persistence.repository.user.resources.VehicleGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.feature.FeatureDTO;
import com.kairos.dto.user.country.feature.VehicleFeaturesDTO;
import com.kairos.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 4/12/17.
 */
@Service
@Transactional
public class FeatureService{

    @Autowired
    CountryGraphRepository countryGraphRepository;

    @Autowired
    FeatureGraphRepository featureGraphRepository;

    @Autowired
    VehicleGraphRepository vehicleGraphRepository;

    @Autowired
    ResourceGraphRepository resourceGraphRepository;

    @Inject
    private ExceptionService exceptionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Feature addCountryFeature(Long countryId, FeatureDTO featureDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        logger.info("featureDTO : "+featureDTO.getName());
        if( featureGraphRepository.isFeatureExistsWithSameName(featureDTO.getName(), countryId, false) ){
            exceptionService.duplicateDataException("message.feature.name.alreadyExist",featureDTO.getName());

        }
        return featureGraphRepository.createFeature(countryId,featureDTO.getName(), featureDTO.getDescription(), LocalDateTime.now().toString());
    }

    public FeatureQueryResult updateFeature(Long countryId, Long featureId, FeatureDTO featureDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        Feature feature = featureGraphRepository.getFeatureById(featureId, countryId, false);
        if( feature == null) {
            exceptionService.dataNotFoundByIdException("message.feature.id.notFound",featureId);

        }

        if( ! ( feature.getName().equalsIgnoreCase(featureDTO.getName()) ) && featureGraphRepository.isFeatureExistsWithSameName(featureDTO.getName(), countryId, false) ){
            exceptionService.duplicateDataException("message.feature.name.alreadyExist",featureDTO.getName() );

        }
        return featureGraphRepository.updateFeature(featureId, countryId, featureDTO.getName(), featureDTO.getDescription(), LocalDateTime.now().toString());
    }


    public Boolean deleteFeature(Long countryId, Long featureId){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        Feature feature = featureGraphRepository.getFeatureById(featureId, countryId, false);
        if( feature == null) {
            exceptionService.dataNotFoundByIdException("message.feature.id.notFound",featureId);

        }
        feature.setDeleted(true);
        featureGraphRepository.save(feature);
        return true;
    }

    public HashMap<String,Object> getListOfFeatures(Long countryId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

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
            exceptionService.dataNotFoundByIdException("message.feature.vehicle.id.notFound",vehicleId);

        }
        List<Feature> features = featureGraphRepository.getListOfFeaturesByCountryAndIds(countryId,false, vehicleFeaturesDTO.getFeatures());
        vehicle.setFeatures(features);
        vehicleGraphRepository.save(vehicle);
        return vehicle;
    }

    public List<FeatureQueryResult> fetchAvailableFeaturesOfResources(Long organizationId, Long resourceId){
        return featureGraphRepository.getResourcesAvailableFeatures(organizationId, resourceId, false);
    }

    public List<FeatureQueryResult> fetchSelectedFeaturesOfResources(Long organizationId, Long resourceId){
        return featureGraphRepository.getResourcesSelectedFeatures(organizationId, resourceId, false);
    }

    public HashMap<String,List<FeatureQueryResult>> getFeaturesForResource(Long organizationId, Long resourceId){
        HashMap<String, List<FeatureQueryResult>> featuresData = new HashMap<>();
        featuresData.put("availableFeatures",fetchAvailableFeaturesOfResources(organizationId,resourceId));
        featuresData.put("selectedFeatures",fetchSelectedFeaturesOfResources(organizationId,resourceId));
        return featuresData;
    }

    public Resource updateFeaturesOfResource(Long organizationId, Long resourceId, VehicleFeaturesDTO vehicleFeaturesDTO){
        Resource resource = resourceGraphRepository.getResourceOfOrganizationById(organizationId, resourceId, false);
        if (resource == null) {
            exceptionService.dataNotFoundByIdException("message.feature.resource.id.notFound",resourceId);

        }
        List<Feature> features = featureGraphRepository.getAvailableFeaturesOfResourceByOrganizationAndIds(organizationId, resourceId, false, vehicleFeaturesDTO.getFeatures());
        featureGraphRepository.detachResourceFeatures(resourceId);
        resource.setFeatures(features);
        resourceGraphRepository.save(resource);
        return resource;
    }

    public Feature getFeatureByName( Long countryId, String name){
        return featureGraphRepository.getFeatureByName(countryId, name, false);
    }


}
