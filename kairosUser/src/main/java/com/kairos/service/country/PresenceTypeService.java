package com.kairos.service.country;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.timetype.PresenceTypeDTO;
import com.kairos.persistence.model.timetype.PresenceTypeWithTimeTypeDTO;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.PresenceType;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.PresenceTypeRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
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
    @Inject
    private TimeTypeRestClient timeTypeRestClient;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject private OrganizationService organizationService;
    @Inject
    private ExceptionService exceptionService;

    public PresenceTypeDTO addPresenceType(PresenceTypeDTO presenceTypeDTO, Long countryId) {

        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while creating Presence type in country" + countryId);
            exceptionService.dataNotFoundByIdException("exception.country.id.notFound",countryId);

        }
        PresenceType presenceType = presenceTypeRepository.findByNameAndDeletedAndCountryId("(?i)" + presenceTypeDTO.getName(), false, countryId);
        if (Optional.ofNullable(presenceType).isPresent()) {
            logger.error("Presence type already exist in country By Name " + presenceTypeDTO.getName());
            exceptionService.duplicateDataException("exception.presenceType.name.alreadyExist",presenceTypeDTO.getName());

        }
        presenceType = new PresenceType();
        presenceType.setName(presenceTypeDTO.getName());
        presenceType.setCountry(country);
        save(presenceType);
        presenceTypeDTO.setId(presenceType.getId());
        logger.info(presenceType.toString());
        return presenceTypeDTO;
    }

    public List<PresenceTypeDTO> getAllPresenceTypeByCountry(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while creating Presence type in country" + countryId);
            exceptionService.dataNotFoundByIdException("exception.country.id.notFound",countryId);

        }
        List<PresenceTypeDTO> presenceTypeDTOList =
                presenceTypeRepository.getAllPresenceTypeByCountryId(countryId, false);
        return presenceTypeDTOList;
    }

    public boolean deletePresenceTypeById(Long presenceTypeId) {
        PresenceType presenceType = presenceTypeRepository.findOne(presenceTypeId);
        if (!Optional.ofNullable(presenceType).isPresent()) {
            logger.error("Presence type not found by Id removing" + presenceTypeId);
            exceptionService.dataNotFoundByIdException("exception.presenceType.id.notFound");

        }
        presenceType.setDeleted(true);
        save(presenceType);
        return true;
    }

    public PresenceTypeDTO updatePresenceType(Long countryId, Long presenceTypeId, PresenceTypeDTO presenceTypeDTO) {
        boolean unique = presenceTypeRepository.findByNameAndDeletedAndCountryIdExcludingCurrent(countryId, presenceTypeId, "(?i)" + presenceTypeDTO.getName(), false);
        if (unique) {
            exceptionService.duplicateDataException("exception.presenceType.name.alreadyExist",presenceTypeDTO.getName());

        }
        PresenceType presenceType = presenceTypeRepository.findOne(presenceTypeId);
        if (!Optional.ofNullable(presenceType).isPresent()) {
            logger.error("Presence type not found by Id removing" + presenceTypeId);
            exceptionService.dataNotFoundByIdException("exception.presenceType.id.notFound");
        }

        presenceType.setName(presenceTypeDTO.getName());
        save(presenceType);
        return presenceTypeDTO;
    }

    public PresenceTypeWithTimeTypeDTO getAllPresenceTypeAndTimeTypesByCountry(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while getting Presence type  and Time type in country" + countryId);
            exceptionService.dataNotFoundByIdException("exception.country.id.notFound",countryId);

        }
        PresenceTypeWithTimeTypeDTO presenceTypeWithTimeTypes = new PresenceTypeWithTimeTypeDTO();
        presenceTypeWithTimeTypes.setPresenceTypes(presenceTypeRepository.getAllPresenceTypeByCountryId(countryId, false));
      //  presenceTypeWithTimeTypes.setTimeTypes(timeTypeRestClient.getAllTimeTypes(countryId));
        return presenceTypeWithTimeTypes;
    }


    public Organization fetchParentOrganization(Long unitId) {
        Organization parent = null;
        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        } else {
            parent = unit;
        }
        return parent;
    }


    public PresenceTypeWithTimeTypeDTO getAllPresenceTypeAndTimeTypesByUnitId(Long unitId) {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        Country country= organizationGraphRepository.getCountry(organization.getId());
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while getting Presence type  and Time type " + unitId);
            exceptionService.dataNotFoundByIdException("exception.unit.id.notFound",unitId);

        }
        PresenceTypeWithTimeTypeDTO presenceTypeWithTimeTypes = new PresenceTypeWithTimeTypeDTO();
        presenceTypeWithTimeTypes.setPresenceTypes(presenceTypeRepository.getAllPresenceTypeByCountryId(country.getId(), false));
        //presenceTypeWithTimeTypes.setTimeTypes(timeTypeRestClient.getAllTimeTypes(country.getId()));
        presenceTypeWithTimeTypes.setCountryId(country.getId());
        return presenceTypeWithTimeTypes;
    }

}
