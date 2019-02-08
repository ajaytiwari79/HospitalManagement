package com.kairos.service.country.default_data;

import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.access_permission.AccessPageDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.UnitTypeQueryResult;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.UnitTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.system_setting.UnitTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//  Created By vipul   On 9/8/18
@Service
@Transactional
public class UnitTypeService{
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitTypeGraphRepository unitTypeGraphRepository;
    @Inject private AccessPageRepository accessPageRepository;
    private static final Logger logger = LoggerFactory.getLogger(UnitTypeService.class);

    public UnitTypeDTO addUnitTypeInCountry(Long countryId, UnitTypeDTO unitTypeDTO) {
        Optional<Country> country = countryGraphRepository.findById(countryId, 0);
        if (!country.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
        }
        Boolean exists = unitTypeGraphRepository.checkUnitTypeExistInCountry(country.get().getId(), "(?i)" + unitTypeDTO.getName(), -1L);
        if (exists) {
            exceptionService.dataNotFoundByIdException("message.unitType.alreadyExist",unitTypeDTO.getName());
        }
        List<AccessPage> modules=accessPageRepository.findAllModulesByIds(unitTypeDTO.getModuleIds());
        if (modules.size()!=unitTypeDTO.getModuleIds().size()){
            exceptionService.invalidRequestException("message.modules.notfound",unitTypeDTO.getName());
        }

        UnitType unitType = new UnitType(unitTypeDTO.getName(), unitTypeDTO.getDescription(), country.get(),modules);
        unitTypeGraphRepository.save(unitType);
        unitTypeDTO.setId(unitType.getId());
        return unitTypeDTO;
    }

    public Map<String,Object>  getAllUnitTypeOfCountry(Long countryId) {
        List<AccessPageDTO> modules=accessPageRepository.getMainActiveTabs(countryId);
        List<UnitTypeQueryResult> unitTypes= unitTypeGraphRepository.getAllUnitTypeOfCountry(countryId);
        Map<String,Object> data= new HashMap<String, Object>(2);
        data.put("modules",modules);
        data.put("unitTypes",unitTypes);
        return data;
    }

    public UnitTypeDTO updateUnitTypeOfCountry(Long countryId, UnitTypeDTO unitTypeDTO) {
        Optional<UnitType> unitType = unitTypeGraphRepository.findById(unitTypeDTO.getId(), 1);
        if (!unitType.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitType.notFound",unitTypeDTO.getId());
        }
        Boolean exists = unitTypeGraphRepository.checkUnitTypeExistInCountry(countryId, "(?i)" + unitTypeDTO.getName(), unitTypeDTO.getId());
        if (exists) {
            exceptionService.dataNotFoundByIdException("message.unitType.alreadyExist",unitTypeDTO.getName());
        }
        List<AccessPage> modules=accessPageRepository.findAllModulesByIds(unitTypeDTO.getModuleIds());
        if (modules.size()!=unitTypeDTO.getModuleIds().size()){
            exceptionService.invalidRequestException("message.modules.notfound",unitTypeDTO.getName());
        }
        unitType.get().setAccessPage(modules);
        unitType.get().setName(unitTypeDTO.getName());
        unitType.get().setDescription(unitTypeDTO.getDescription());
        unitTypeGraphRepository.save(unitType.get());
        return unitTypeDTO;
    }

}
