package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.asset_management.OrganizationalSecurityMeasure;
import com.kairos.persistance.repository.master_data.asset_management.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.service.common.MongoBaseService;
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
    private ComparisonUtils comparisonUtils;



    /**
     * @description this method create new OrganizationalSecurityMeasure if OrganizationalSecurityMeasure not exist with same name ,
     * and if exist then simply add  OrganizationalSecurityMeasure to existing list and return list ;
     * findByNamesList()  return list of existing OrganizationalSecurityMeasure using collation ,used for case insensitive result
     * @param countryId
     * @param organizationId
     * @param orgSecurityMeasures
     * @return return map which contain list of new OrganizationalSecurityMeasure and list of existing OrganizationalSecurityMeasure if OrganizationalSecurityMeasure already exist
     *
     */
    public Map<String, List<OrganizationalSecurityMeasure>> createOrganizationalSecurityMeasure(Long countryId, Long organizationId, List<OrganizationalSecurityMeasure> orgSecurityMeasures) {

        Map<String, List<OrganizationalSecurityMeasure>> result = new HashMap<>();
        Set<String> orgSecurityMeasureNames = new HashSet<>();
        if (orgSecurityMeasures.size() != 0) {
            for (OrganizationalSecurityMeasure securityMeasure : orgSecurityMeasures) {
                if (!StringUtils.isBlank(securityMeasure.getName())) {
                    orgSecurityMeasureNames.add(securityMeasure.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            List<OrganizationalSecurityMeasure> existing = findByNamesList(countryId,organizationId,orgSecurityMeasureNames,OrganizationalSecurityMeasure.class);
            orgSecurityMeasureNames = comparisonUtils.getNameListForMetadata(existing, orgSecurityMeasureNames);
            List<OrganizationalSecurityMeasure> newOrgSecurityMeasures = new ArrayList<>();
            if (orgSecurityMeasureNames.size() != 0) {
                for (String name : orgSecurityMeasureNames) {

                    OrganizationalSecurityMeasure newOrganizationalSecurityMeasure = new OrganizationalSecurityMeasure();
                    newOrganizationalSecurityMeasure.setName(name);
                    newOrganizationalSecurityMeasure.setCountryId(countryId);
                    newOrganizationalSecurityMeasure.setOrganizationId(organizationId);
                    newOrgSecurityMeasures.add(newOrganizationalSecurityMeasure);

                }
                newOrgSecurityMeasures = organizationalSecurityMeasureMongoRepository.saveAll(sequenceGenerator(newOrgSecurityMeasures));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newOrgSecurityMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     *
     * @param countryId
     * @param organizationId
     * @return list of OrganizationalSecurityMeasure
     */
    public List<OrganizationalSecurityMeasure> getAllOrganizationalSecurityMeasure(Long countryId, Long organizationId) {
        return organizationalSecurityMeasureMongoRepository.findAllOrganizationalSecurityMeasures(countryId, organizationId);
    }


    /**
     * @throws DataNotFoundByIdException throw exception if OrganizationalSecurityMeasure not exist for given id
     * @param countryId
     * @param organizationId
     * @param id id of OrganizationalSecurityMeasure
     * @return OrganizationalSecurityMeasure object fetch via id
     */
    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasure(Long countryId, Long organizationId, BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteOrganizationalSecurityMeasure(Long countryId, Long organizationId, BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /**
     * @throws DuplicateDataException if OrganizationalSecurityMeasure not exist for given id
     * @param countryId
     * @param organizationId
     * @param id  id of OrganizationalSecurityMeasure
     * @param orgSecurityMeasure
     * @return return updated OrganizationalSecurityMeasure object
     */
    public OrganizationalSecurityMeasure updateOrganizationalSecurityMeasure(Long countryId, Long organizationId, BigInteger id, OrganizationalSecurityMeasure orgSecurityMeasure) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByName(countryId, organizationId, orgSecurityMeasure.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data exist of " + orgSecurityMeasure.getName());
        } else {
            exist = organizationalSecurityMeasureMongoRepository.findByid(id);
            exist.setName(orgSecurityMeasure.getName());
            return organizationalSecurityMeasureMongoRepository.save(sequenceGenerator(exist));

        }
    }

    /**
     * @throws DataNotExists throw exception if OrganizationalSecurityMeasure not exist for given name
     * @param countryId
     * @param organizationId
     * @param name OrganizationalSecurityMeasure name
     * @return OrganizationalSecurityMeasure fetch via name
     */
    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasureByName(Long countryId, Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByName(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
