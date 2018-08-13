package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.gdpr.metadata.OrganizationalSecurityMeasureDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.persistance.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationalSecurityMeasureService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalSecurityMeasureService.class);

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param
     * @param securityMeasureDTOS
     * @return return map which contain list of new OrganizationalSecurityMeasure and list of existing OrganizationalSecurityMeasure if OrganizationalSecurityMeasure already exist
     * @description this method create new OrganizationalSecurityMeasure if OrganizationalSecurityMeasure not exist with same name ,
     * and if exist then simply add  OrganizationalSecurityMeasure to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing OrganizationalSecurityMeasure using collation ,used for case insensitive result
     */
    public Map<String, List<OrganizationalSecurityMeasure>> createOrganizationalSecurityMeasure(Long countryId, List<OrganizationalSecurityMeasureDTO> securityMeasureDTOS) {

        Map<String, List<OrganizationalSecurityMeasure>> result = new HashMap<>();
        Set<String> orgSecurityMeasureNames = new HashSet<>();
        if (!securityMeasureDTOS.isEmpty()) {
            for (OrganizationalSecurityMeasureDTO securityMeasure : securityMeasureDTOS) {
                orgSecurityMeasureNames.add(securityMeasure.getName());
            }

            List<OrganizationalSecurityMeasure> existing = findByNamesAndCountryId(countryId, orgSecurityMeasureNames, OrganizationalSecurityMeasure.class);
            orgSecurityMeasureNames = ComparisonUtils.getNameListForMetadata(existing, orgSecurityMeasureNames);
            List<OrganizationalSecurityMeasure> newOrgSecurityMeasures = new ArrayList<>();
            if (!orgSecurityMeasureNames.isEmpty()) {
                for (String name : orgSecurityMeasureNames) {

                    OrganizationalSecurityMeasure newOrganizationalSecurityMeasure = new OrganizationalSecurityMeasure(name);
                    newOrganizationalSecurityMeasure.setCountryId(countryId);
                    newOrgSecurityMeasures.add(newOrganizationalSecurityMeasure);

                }
                newOrgSecurityMeasures = organizationalSecurityMeasureMongoRepository.saveAll(getNextSequence(newOrgSecurityMeasures));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newOrgSecurityMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     * @param countryId
     * @param
     * @return list of OrganizationalSecurityMeasure
     */
    public List<OrganizationalSecurityMeasure> getAllOrganizationalSecurityMeasure(Long countryId) {
        return organizationalSecurityMeasureMongoRepository.findAllOrganizationalSecurityMeasures(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id        id of OrganizationalSecurityMeasure
     * @return OrganizationalSecurityMeasure object fetch via id
     * @throws DataNotFoundByIdException throw exception if OrganizationalSecurityMeasure not exist for given id
     */
    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasure(Long countryId, BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteOrganizationalSecurityMeasure(Long countryId, BigInteger id) {

        OrganizationalSecurityMeasure orgSecurityMeasure = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(orgSecurityMeasure).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        delete(orgSecurityMeasure);
        return true;

    }

    /**
     * @param countryId
     * @param
     * @param id                 id of OrganizationalSecurityMeasure
     * @param securityMeasureDTO
     * @return return updated OrganizationalSecurityMeasure object
     * @throws DuplicateDataException if OrganizationalSecurityMeasure not exist for given id
     */
    public OrganizationalSecurityMeasureDTO updateOrganizationalSecurityMeasure(Long countryId, BigInteger id, OrganizationalSecurityMeasureDTO securityMeasureDTO) {

        OrganizationalSecurityMeasure orgSecurityMeasure = organizationalSecurityMeasureMongoRepository.findByName(countryId, securityMeasureDTO.getName());
        if (Optional.ofNullable(orgSecurityMeasure).isPresent()) {
            if (id.equals(orgSecurityMeasure.getId())) {
                return securityMeasureDTO;
            }
            throw new DuplicateDataException("data exist of " + securityMeasureDTO.getName());
        }
        orgSecurityMeasure = organizationalSecurityMeasureMongoRepository.findByid(id);

        if (!Optional.ofNullable(orgSecurityMeasure).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Organization Security Measure", id);
        }
        orgSecurityMeasure.setName(securityMeasureDTO.getName());
        organizationalSecurityMeasureMongoRepository.save(orgSecurityMeasure);
        return securityMeasureDTO;

    }

    /**
     * @param countryId
     * @param
     * @param name      OrganizationalSecurityMeasure name
     * @return OrganizationalSecurityMeasure fetch via name
     * @throws DataNotExists throw exception if OrganizationalSecurityMeasure not exist for given name
     */
    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasureByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    public List<OrganizationalSecurityMeasureResponseDTO> getAllNotInheritedFromParentOrgAndUnitOrgSecurityMeasure(Long countryId, Long parentOrganizationId, Long unitId) {

        return organizationalSecurityMeasureMongoRepository.getAllNotInheritedFromParentOrgAndUnitOrgSecurityMeasure(countryId, parentOrganizationId, unitId);
    }


}
