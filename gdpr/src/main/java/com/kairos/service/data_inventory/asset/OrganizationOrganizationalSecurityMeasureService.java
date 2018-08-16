package com.kairos.service.data_inventory.asset;

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
import com.kairos.service.master_data.asset_management.OrganizationalSecurityMeasureService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationOrganizationalSecurityMeasureService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalSecurityMeasureService.class);

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param
     * @param organizationId
     * @param orgSecurityMeasureDTOs
     * @return return map which contain list of new OrganizationalSecurityMeasure and list of existing OrganizationalSecurityMeasure if OrganizationalSecurityMeasure already exist
     * @description this method create new OrganizationalSecurityMeasure if OrganizationalSecurityMeasure not exist with same name ,
     * and if exist then simply add  OrganizationalSecurityMeasure to existing list and return list ;
     * findByOrganizationIdAndNamesList()  return list of existing OrganizationalSecurityMeasure using collation ,used for case insensitive result
     */
    public Map<String, List<OrganizationalSecurityMeasure>> createOrganizationalSecurityMeasure(Long organizationId, List<OrganizationalSecurityMeasureDTO> orgSecurityMeasureDTOs) {

        Map<String, List<OrganizationalSecurityMeasure>> result = new HashMap<>();
        Set<String> orgSecurityMeasureNames = new HashSet<>();
        if (!orgSecurityMeasureDTOs.isEmpty()) {
            for (OrganizationalSecurityMeasureDTO securityMeasure : orgSecurityMeasureDTOs) {
                orgSecurityMeasureNames.add(securityMeasure.getName());
            }

            List<OrganizationalSecurityMeasure> existing = findAllByNameAndOrganizationId(organizationId, orgSecurityMeasureNames, OrganizationalSecurityMeasure.class);
            orgSecurityMeasureNames = ComparisonUtils.getNameListForMetadata(existing, orgSecurityMeasureNames);
            List<OrganizationalSecurityMeasure> newOrgSecurityMeasures = new ArrayList<>();
            if (!orgSecurityMeasureNames.isEmpty()) {
                for (String name : orgSecurityMeasureNames) {

                    OrganizationalSecurityMeasure newOrganizationalSecurityMeasure = new OrganizationalSecurityMeasure(name);
                    newOrganizationalSecurityMeasure.setOrganizationId(organizationId);
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
     * @param
     * @param organizationId
     * @return list of OrganizationalSecurityMeasure
     */
    public List<OrganizationalSecurityMeasureResponseDTO> getAllOrganizationalSecurityMeasure(Long organizationId) {
        return organizationalSecurityMeasureMongoRepository.findAllOrgOrganizationalSecurityMeasures(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @param id             id of OrganizationalSecurityMeasure
     * @return OrganizationalSecurityMeasure object fetch via id
     * @throws DataNotFoundByIdException throw exception if OrganizationalSecurityMeasure not exist for given id
     */
    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasure(Long organizationId, BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteOrganizationalSecurityMeasure(Long organizationId, BigInteger id) {

        OrganizationalSecurityMeasure organizationalSecurityMeasure = organizationalSecurityMeasureMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(organizationalSecurityMeasure).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(organizationalSecurityMeasure);
            return true;

        }
    }

    /**
     * @param
     * @param organizationId
     * @param id                    id of OrganizationalSecurityMeasure
     * @param orgSecurityMeasureDTO
     * @return return updated OrganizationalSecurityMeasure object
     * @throws DuplicateDataException if OrganizationalSecurityMeasure not exist for given id
     */
    public OrganizationalSecurityMeasureDTO updateOrganizationalSecurityMeasure(Long organizationId, BigInteger id, OrganizationalSecurityMeasureDTO orgSecurityMeasureDTO) {

        OrganizationalSecurityMeasure organizationalSecurityMeasure = organizationalSecurityMeasureMongoRepository.findByOrganizationIdAndName(organizationId, orgSecurityMeasureDTO.getName());
        if (Optional.ofNullable(organizationalSecurityMeasure).isPresent()) {
            if (id.equals(organizationalSecurityMeasure.getId())) {
                return orgSecurityMeasureDTO;
            }
            throw new DuplicateDataException("data exist of " + orgSecurityMeasureDTO.getName());
        }
        organizationalSecurityMeasure = organizationalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(organizationalSecurityMeasure).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Security Measure", id);
        }
        organizationalSecurityMeasure.setName(orgSecurityMeasureDTO.getName());
        organizationalSecurityMeasureMongoRepository.save(organizationalSecurityMeasure);
        return orgSecurityMeasureDTO;


    }

    /**
     * @param
     * @param organizationId
     * @param name           OrganizationalSecurityMeasure name
     * @return OrganizationalSecurityMeasure fetch via name
     * @throws DataNotExists throw exception if OrganizationalSecurityMeasure not exist for given name
     */
    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasureByName(Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByOrganizationIdAndName(organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
