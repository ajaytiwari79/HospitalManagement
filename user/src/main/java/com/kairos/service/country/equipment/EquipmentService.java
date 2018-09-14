package com.kairos.service.country.equipment;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.equipment.Equipment;
import com.kairos.persistence.model.country.equipment.EquipmentCategory;
import com.kairos.persistence.model.country.equipment.EquipmentQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.dto.user.equipment.EquipmentDTO;
import com.kairos.dto.user.equipment.VehicleEquipmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 12/12/17.
 */
@Service
@Transactional
public class EquipmentService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private EquipmentCategoryGraphRepository equipmentCategoryGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private EquipmentGraphRepository equipmentGraphRepository;

    @Inject
    private ResourceGraphRepository resourceGraphRepository;

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private ExceptionService exceptionService;

    public List<EquipmentCategory> getListOfEquipmentCategories(Long countryId){
        return equipmentCategoryGraphRepository.getEquipmentCategories();
    }

    public Equipment addCountryEquipment(Long countryId, EquipmentDTO equipmentDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
        }
        if( equipmentGraphRepository.isEquipmentExistsWithSameName(equipmentDTO.getName(), countryId, false) ){
            exceptionService.duplicateDataException("message.equipment.name.alreadyExist",equipmentDTO.getName());

        }
        Equipment equipment = new Equipment();
        equipment.setName(equipmentDTO.getName());
        equipment.setDescription(equipmentDTO.getDescription());
        equipment.setCategory(equipmentCategoryGraphRepository.findOne(equipmentDTO.getEquipmentCategory().getId(),0));
        equipmentGraphRepository.save(equipment);
        equipmentGraphRepository.addEquipmentInCountry(countryId,equipment.getId());
        return equipment;
    }

    public Equipment updateEquipment(Long countryId, Long equipmentId, EquipmentDTO equipmentDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        Equipment equipment = equipmentGraphRepository.getEquipmentById(equipmentId, countryId, false);
        if( equipment == null) {
            exceptionService.dataNotFoundByIdException("message.equipment.id.notExist",equipmentId);

        }

        if( ! ( equipment.getName().equalsIgnoreCase(equipmentDTO.getName()) ) && equipmentGraphRepository.isEquipmentExistsWithSameName(equipmentDTO.getName(), countryId, false) ){
            exceptionService.duplicateDataException("message.equipment.name.alreadyExist",equipmentDTO.getName());

        }
        equipment.setName(equipmentDTO.getName());
        equipment.setDescription(equipmentDTO.getDescription());
        equipmentGraphRepository.detachEquipmentCategory(equipmentId);
        equipment.setCategory(equipmentCategoryGraphRepository.findOne(equipmentDTO.getEquipmentCategory().getId(),0));
        equipmentGraphRepository.save(equipment);
        return equipment;
        //return featureGraphRepository.updateFeature(featureId, countryId, featureDTO.getName(), featureDTO.getDescription(), new Date().getTime());
    }

    public Boolean deleteEquipment(Long countryId, Long equipmentId){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        Equipment equipment = equipmentGraphRepository.getEquipmentById(equipmentId, countryId, false);
        if( equipment == null) {
            exceptionService.dataNotFoundByIdException("message.equipment.id.notExist", equipmentId);

        }
        equipment.setDeleted(true);
        equipmentGraphRepository.save(equipment);
        return true;
    }

    public HashMap<String,Object> getListOfEquipments(Long countryId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }

        if(filterText == null){
            filterText = "";
        }

        HashMap<String,Object> equipmentsData = new HashMap<>();
        equipmentsData.put("equipments",equipmentGraphRepository.getListOfEquipment(countryId, false, filterText));

        return equipmentsData;
    }

    public HashMap<String,Object> getListOfEquipmentsByUnitId(Long unitId, String filterText){
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }

        if(filterText == null){
            filterText = "";
        }

        HashMap<String,Object> equipmentsData = new HashMap<>();
        equipmentsData.put("equipments",equipmentGraphRepository.getListOfEquipment(countryId, false, filterText));

        return equipmentsData;
    }

    public EquipmentCategory getEquipmentCategoryByName(String name){
        return equipmentCategoryGraphRepository.getEquipmentCategoryByName(name);
    }

    public Equipment getEquipmentByName(long countryId, String name){
        return equipmentGraphRepository.getEquipmentByName(countryId, name, false);
    }



    public List<EquipmentQueryResult> fetchSelectedEquipmentsOfResources(Long organizationId, Long resourceId){
        return equipmentGraphRepository.getResourcesSelectedEquipments(organizationId, resourceId, false);
    }

    public HashMap<String,List<EquipmentQueryResult>> getEquipmentsForResource(Long organizationId, Long resourceId){
        Organization organization = organizationGraphRepository.findOne(organizationId,1);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound",organizationId);

        }
        HashMap<String, List<EquipmentQueryResult>> featuresData = new HashMap<>();
        featuresData.put("availableEquipments",equipmentGraphRepository.getListOfEquipment(organization.getCountry().getId(), false, ""));
        featuresData.put("selectedEquipments",fetchSelectedEquipmentsOfResources(organizationId,resourceId));
        return featuresData;
    }

    public Resource updateEquipmentsOfResource(Long organizationId, Long resourceId, VehicleEquipmentDTO vehicleEquipmentDTO){
        Resource resource = resourceGraphRepository.getResourceOfOrganizationById(organizationId, resourceId, false);
        if (resource == null) {
            exceptionService.dataNotFoundByIdException("message.equipment.resource.id.notFound",resourceId);

        }
        Organization organization = organizationGraphRepository.findOne(organizationId,1);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound",organizationId);

        }
        List<Equipment> equipments = equipmentGraphRepository.getListOfEquipmentByIds(organization.getCountry().getId(), false, vehicleEquipmentDTO.getEquipments());
        equipmentGraphRepository.detachResourceEquipments(resourceId);
        resource.setEquipments(equipments);
        resourceGraphRepository.save(resource);
        return resource;
    }
}
