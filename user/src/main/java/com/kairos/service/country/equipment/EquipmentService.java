package com.kairos.service.country.equipment;

import com.kairos.dto.user.equipment.EquipmentDTO;
import com.kairos.dto.user.equipment.VehicleEquipmentDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.equipment.Equipment;
import com.kairos.persistence.model.country.equipment.EquipmentCategory;
import com.kairos.persistence.model.country.equipment.EquipmentQueryResult;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.google_calender.CountryCalenderService;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.*;

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
    private CountryService countryService;

    @Inject
    private EquipmentGraphRepository equipmentGraphRepository;

    @Inject
    private ResourceGraphRepository resourceGraphRepository;

    @Inject
    private UnitGraphRepository unitGraphRepository;

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
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);
        }
        if( equipmentGraphRepository.isEquipmentExistsWithSameName(equipmentDTO.getName(), countryId, false) ){
            exceptionService.duplicateDataException(MESSAGE_EQUIPMENT_NAME_ALREADYEXIST,equipmentDTO.getName());

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
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);

        }
        Equipment equipment = equipmentGraphRepository.getEquipmentById(equipmentId, countryId, false);
        if( equipment == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EQUIPMENT_ID_NOTEXIST,equipmentId);

        }

        if( ! ( equipment.getName().equalsIgnoreCase(equipmentDTO.getName()) ) && equipmentGraphRepository.isEquipmentExistsWithSameName(equipmentDTO.getName(), countryId, false) ){
            exceptionService.duplicateDataException(MESSAGE_EQUIPMENT_NAME_ALREADYEXIST,equipmentDTO.getName());

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
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);

        }
        Equipment equipment = equipmentGraphRepository.getEquipmentById(equipmentId, countryId, false);
        if( equipment == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EQUIPMENT_ID_NOTEXIST, equipmentId);

        }
        equipment.setDeleted(true);
        equipmentGraphRepository.save(equipment);
        return true;
    }

    public HashMap<String,Object> getListOfEquipments(Long countryId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);

        }

        if(filterText == null){
            filterText = "";
        }

        HashMap<String,Object> equipmentsData = new HashMap<>();
        equipmentsData.put("equipments",equipmentGraphRepository.getListOfEquipment(countryId, false, filterText));

        return equipmentsData;
    }

    public Map<String,Object> getListOfEquipmentsByUnitId(Long unitId, String filterText){
        Long countryId = countryService.getCountryIdByUnitId(unitId);
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND,countryId);

        }

        if(filterText == null){
            filterText = "";
        }

        HashMap<String,Object> equipmentsData = new HashMap<>();
        equipmentsData.put("equipments",equipmentGraphRepository.getListOfEquipment(countryId, false, filterText));

        return equipmentsData;
    }



    public List<EquipmentQueryResult> fetchSelectedEquipmentsOfResources(Long organizationId, Long resourceId){
        return equipmentGraphRepository.getResourcesSelectedEquipments(organizationId, resourceId, false);
    }

    public Map<String,List<EquipmentQueryResult>> getEquipmentsForResource(Long organizationId, Long resourceId){
        Unit unit = unitGraphRepository.findOne(organizationId,1);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND,organizationId);

        }
        HashMap<String, List<EquipmentQueryResult>> featuresData = new HashMap<>();
        featuresData.put("availableEquipments",equipmentGraphRepository.getListOfEquipment(unit.getCountry().getId(), false, ""));
        featuresData.put("selectedEquipments",fetchSelectedEquipmentsOfResources(organizationId,resourceId));
        return featuresData;
    }

    public Resource updateEquipmentsOfResource(Long organizationId, Long resourceId, VehicleEquipmentDTO vehicleEquipmentDTO){
        Resource resource = resourceGraphRepository.getResourceOfOrganizationById(organizationId, resourceId, false);
        if (resource == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EQUIPMENT_RESOURCE_ID_NOTFOUND,resourceId);

        }
        Unit unit = unitGraphRepository.findOne(organizationId,1);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND,organizationId);

        }
        List<Equipment> equipments = equipmentGraphRepository.getListOfEquipmentByIds(unit.getCountry().getId(), false, vehicleEquipmentDTO.getEquipments());
        equipmentGraphRepository.detachResourceEquipments(resourceId);
        resource.setEquipments(equipments);
        resourceGraphRepository.save(resource);
        return resource;
    }
}
