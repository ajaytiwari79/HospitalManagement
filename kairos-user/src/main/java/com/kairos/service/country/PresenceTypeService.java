package com.kairos.service.country;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.dto.timeType.PresenceTypeDTO;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.PresenceType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.PresenceTypeRepository;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by vipul on 10/11/17.
 */
@Service
@Transactional
public class PresenceTypeService extends UserBaseService {
    private Logger logger = LoggerFactory.getLogger(PresenceTypeService.class);
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private PresenceTypeRepository presenceTypeRepository;

    public PresenceTypeDTO addPresenceType(PresenceTypeDTO presenceTypeDTO, Long countryId) {
        logger.info(presenceTypeDTO.toString());
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while creating Presence type in country" + countryId);
            throw new DataNotFoundByIdException("Invalid Country");
        }
        PresenceType presenceType = presenceTypeRepository.findByNameAndDeletedAndCountryId("(?i)" + presenceTypeDTO.getName(), false, countryId);
        if (Optional.ofNullable(presenceType).isPresent()) {
            logger.error("Presence type already exist in country By Name " + presenceTypeDTO.getName());
            throw new DuplicateDataException("Presence type already exist in country By Name " + presenceTypeDTO.getName());
        }
        presenceType = new PresenceType();
        presenceType.setName(presenceTypeDTO.getName());
        presenceType.setCountry(country);
        save(presenceType);
        presenceTypeDTO.setId(presenceType.getId());
        logger.info(presenceType.toString());
        return presenceTypeDTO;
    }


}
