package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.OrganizationalSecurityMeasure;
import com.kairos.persistance.repository.master_data_management.asset_management.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class OrganizationalSecurityMeasureService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalSecurityMeasureService.class);

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;


    public Map<String, List<OrganizationalSecurityMeasure>> createOrganizationalSecurityMeasure(Long countryId, List<OrganizationalSecurityMeasure> orgSecurityMeasures) {
        Map<String, List<OrganizationalSecurityMeasure>> result = new HashMap<>();
        List<OrganizationalSecurityMeasure> existing = new ArrayList<>();
        List<OrganizationalSecurityMeasure> newOrgSecurityMeasures = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (orgSecurityMeasures.size() != 0) {
            for (OrganizationalSecurityMeasure securityMeasure : orgSecurityMeasures) {
                if (!StringUtils.isBlank(securityMeasure.getName())) {
                    names.add(securityMeasure.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            existing = organizationalSecurityMeasureMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));
            if (names.size() != 0) {
                for (String name : names) {

                    OrganizationalSecurityMeasure newOrganizationalSecurityMeasure = new OrganizationalSecurityMeasure();
                    newOrganizationalSecurityMeasure.setName(name);
                    newOrganizationalSecurityMeasure.setCountryId(countryId);
                    newOrgSecurityMeasures.add(newOrganizationalSecurityMeasure);

                }
                newOrgSecurityMeasures = save(newOrgSecurityMeasures);
            }
            result.put("existing", existing);
            result.put("new", newOrgSecurityMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<OrganizationalSecurityMeasure> getAllOrganizationalSecurityMeasure() {
        return organizationalSecurityMeasureMongoRepository.findAllOrganizationalSecurityMeasures(UserContext.getCountryId());
    }


    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasure(Long countryId, BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteOrganizationalSecurityMeasure(BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public OrganizationalSecurityMeasure updateOrganizationalSecurityMeasure(BigInteger id, OrganizationalSecurityMeasure orgSecurityMeasure) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByName(UserContext.getCountryId(),orgSecurityMeasure.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            throw new InvalidRequestException("data exist of "+orgSecurityMeasure.getName());
        } else {
            exist=organizationalSecurityMeasureMongoRepository.findByid(id);
            exist.setName(orgSecurityMeasure.getName());
            return save(exist);

        }
    }


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


}
