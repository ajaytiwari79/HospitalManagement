package com.kairos.service.country.default_data;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.UnitTypeQueryResult;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.UnitTypeGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.country.system_setting.UnitTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

//  Created By vipul   On 9/8/18
@Service
@Transactional
public class UnitTypeService extends UserBaseService {
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitTypeGraphRepository unitTypeGraphRepository;
    private static final Logger logger = LoggerFactory.getLogger(UnitTypeService.class);

    public UnitTypeDTO addUnitTypeInCountry(Long countryId, UnitTypeDTO unitTypeDTO) {
        Optional<Country> country = countryGraphRepository.findById(countryId, 0);
        if (!country.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound");
        }
        Boolean exists = unitTypeGraphRepository.checkUnitTypeExistInCountry(country.get().getId(), "(?i)" + unitTypeDTO.getName(), -1L);
        if (exists) {
            exceptionService.dataNotFoundByIdException("message.unitType.alreadyExist",unitTypeDTO.getName());
        }
        UnitType unitType = new UnitType(unitTypeDTO.getName(), unitTypeDTO.getDescription(), country.get());
        save(unitType);
        unitTypeDTO.setId(unitType.getId());
        return unitTypeDTO;
    }

    public List<UnitTypeQueryResult> getAllUnitTypeOfCountry(Long countryId) {
        return unitTypeGraphRepository.getAllUnitTypeOfCountry(countryId);
    }

    public UnitTypeDTO updateUnitTypeOfCountry(Long countryId, UnitTypeDTO unitTypeDTO) {
        Optional<UnitType> unitType = unitTypeGraphRepository.findById(unitTypeDTO.getId(), 0);
        if (!unitType.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitType.notFound",unitTypeDTO.getId());
        }

        Boolean exists = unitTypeGraphRepository.checkUnitTypeExistInCountry(countryId, "(?i)" + unitTypeDTO.getName(), unitTypeDTO.getId());
        if (exists) {
            exceptionService.dataNotFoundByIdException("message.unitType.alreadyExist",unitTypeDTO.getName());
        }
        unitType.get().setName(unitTypeDTO.getName());
        unitType.get().setDescription(unitTypeDTO.getDescription());
        save(unitType.get());
        return unitTypeDTO;
    }

}
