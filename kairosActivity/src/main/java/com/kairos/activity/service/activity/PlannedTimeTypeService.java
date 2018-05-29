package com.kairos.activity.service.activity;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.persistence.model.activity.PlannedTimeType;
import com.kairos.activity.persistence.repository.activity.PlannedTimeTypeRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.response.dto.web.CountryDTO;
import com.kairos.response.dto.web.wta.PresenceTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/*
 * @author: Mohit Shakya
 * @usage: Service operations for planned time type.
 */

@Service
public class PlannedTimeTypeService extends MongoBaseService {

    private Logger logger = LoggerFactory.getLogger(PlannedTimeTypeService.class);

    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PlannedTimeTypeRepository plannedTimeTypeRepository;

    public void verifyCountry(Long countryId){
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while creating Planned Time type in country" + countryId);
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
    }

    public PresenceTypeDTO addPresenceType(PresenceTypeDTO presenceTypeDTO, Long countryId) {
        verifyCountry(countryId);
        PlannedTimeType plannedTimeType = plannedTimeTypeRepository.findByNameAndDeletedAndCountryId("(?i)" + presenceTypeDTO.getName(), false, countryId);
        if (Optional.ofNullable(plannedTimeType).isPresent()) {
            logger.error("Planned Time type already exist in country By Name " + presenceTypeDTO.getName());
            exceptionService.duplicateDataException("message.presenceType.name.alreadyExist",presenceTypeDTO.getName());
        }
        plannedTimeType = new PlannedTimeType();
        plannedTimeType.setName(presenceTypeDTO.getName());
        plannedTimeType.setCountryId(countryId);
        save(plannedTimeType);
        presenceTypeDTO.setId(plannedTimeType.getId().longValue());
        logger.info(plannedTimeType.toString());
        return presenceTypeDTO;
    }

    public List<PresenceTypeDTO> getAllPresenceTypeByCountry(Long countryId) {
        verifyCountry(countryId);
        List<PresenceTypeDTO> presenceTypeDTOList =
                plannedTimeTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        return presenceTypeDTOList;
    }

    public boolean deletePresenceTypeById(Long presenceTypeId) {
        Optional<PlannedTimeType> presenceTypeOptional = plannedTimeTypeRepository.findById(BigInteger.valueOf(presenceTypeId));
        if (!presenceTypeOptional.isPresent()) {
            logger.error("Presence type not found by Id removing" + presenceTypeId);
            exceptionService.dataNotFoundByIdException("message.presenceType.id.notFound");

        }
        PlannedTimeType plannedTimeType = presenceTypeOptional.get();
        plannedTimeType.setDeleted(true);
        save(plannedTimeType);
        return true;
    }

    public PresenceTypeDTO updatePresenceType(Long countryId, Long presenceTypeId, PresenceTypeDTO presenceTypeDTO) {
        List<PlannedTimeType> plannedTimeTypes = plannedTimeTypeRepository.findByNameAndDeletedAndCountryIdExcludingCurrent(countryId, presenceTypeDTO.getName(), false);
        if (plannedTimeTypes.size()>1) {
            exceptionService.duplicateDataException("message.presenceType.name.alreadyExist",presenceTypeDTO.getName());
        }
        Optional<PlannedTimeType> presenceTypeOptional = plannedTimeTypeRepository.findById(BigInteger.valueOf(presenceTypeId));
        if (!presenceTypeOptional.isPresent()) {
            logger.error("Planned Time type not found by Id removing" + presenceTypeId);
            exceptionService.dataNotFoundByIdException("message.presenceType.id.notFound");
        }
        PlannedTimeType plannedTimeType = presenceTypeOptional.get();
        plannedTimeType.setName(presenceTypeDTO.getName());
        save(plannedTimeType);
        return presenceTypeDTO;
    }
}
