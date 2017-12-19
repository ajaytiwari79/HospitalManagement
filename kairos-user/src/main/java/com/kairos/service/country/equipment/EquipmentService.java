package com.kairos.service.country.equipment;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.equipment.Equipment;
import com.kairos.persistence.model.user.country.equipment.EquipmentCategory;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentGraphRepository;
import com.kairos.response.dto.web.equipment.EquipmentDTO;
import com.kairos.service.UserBaseService;
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
public class EquipmentService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    EquipmentCategoryGraphRepository equipmentCategoryGraphRepository;

    @Inject
    CountryGraphRepository countryGraphRepository;

    @Inject
    EquipmentGraphRepository equipmentGraphRepository;

    public List<EquipmentCategory> getListOfEquipmentCategories(Long countryId){
        return equipmentCategoryGraphRepository.getEquipmentCategories();
    }

    public Equipment addCountryEquipment(Long countryId, EquipmentDTO equipmentDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        if( equipmentGraphRepository.isEquipmentExistsWithSameName(equipmentDTO.getName(), countryId, false) ){
            throw new DuplicateDataException("Equipment already exists with same name " +equipmentDTO.getName() );
        }
        Equipment equipment = new Equipment();
        equipment.setName(equipmentDTO.getName());
        equipment.setDescription(equipmentDTO.getDescription());
        equipment.setCategory(equipmentCategoryGraphRepository.findOne(equipmentDTO.getEquipmentCategory().getId(),0));
        save(equipment);
        equipmentGraphRepository.addEquipmentInCountry(countryId,equipment.getId());
        return equipment;
    }

    public Equipment updateEquipment(Long countryId, Long equipmentId, EquipmentDTO equipmentDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        Equipment equipment = equipmentGraphRepository.getEquipmentById(equipmentId, countryId, false);
        if( equipment == null) {
            throw new DataNotFoundByIdException("Equipment does not exist with id " +equipmentId );
        }

        if( ! ( equipment.getName().equalsIgnoreCase(equipmentDTO.getName()) ) && equipmentGraphRepository.isEquipmentExistsWithSameName(equipmentDTO.getName(), countryId, false) ){
            throw new DuplicateDataException("Equipment already exists with name " +equipmentDTO.getName() );
        }
        equipment.setName(equipmentDTO.getName());
        equipment.setDescription(equipmentDTO.getDescription());
        equipmentGraphRepository.detachEquipmentCategory(equipmentId);
        equipment.setCategory(equipmentCategoryGraphRepository.findOne(equipmentDTO.getEquipmentCategory().getId(),0));
        save(equipment);
        return equipment;
        //return featureGraphRepository.updateFeature(featureId, countryId, featureDTO.getName(), featureDTO.getDescription(), new Date().getTime());
    }

    public Boolean deleteEquipment(Long countryId, Long equipmentId){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        Equipment equipment = equipmentGraphRepository.getEquipmentById(equipmentId, countryId, false);
        if( equipment == null) {
            throw new DataNotFoundByIdException("Equipment does not exist with id " + equipmentId);
        }
        equipment.setDeleted(true);
        save(equipment);
        return true;
    }

    public HashMap<String,Object> getListOfEquipments(Long countryId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
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

}
