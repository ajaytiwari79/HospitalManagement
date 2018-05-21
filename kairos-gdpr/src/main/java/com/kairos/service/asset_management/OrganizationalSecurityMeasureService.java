package com.kairos.service.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.asset_management.OrganizationalSecurityMeasure;
import com.kairos.persistance.repository.asset_management.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class OrganizationalSecurityMeasureService extends MongoBaseService {

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;


    public Map<String, List<OrganizationalSecurityMeasure>> createOrganizationalSecurityMeasure(List<OrganizationalSecurityMeasure> orgSecurityMeasures) {
        Map<String, List<OrganizationalSecurityMeasure>> result = new HashMap<>();
        List<OrganizationalSecurityMeasure> existing= new ArrayList<>();
        List<OrganizationalSecurityMeasure> newOrgSecurityMeasures = new ArrayList<>();
        if (orgSecurityMeasures.size() != 0) {
            for (OrganizationalSecurityMeasure securityMeasure : orgSecurityMeasures) {

                OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByName(securityMeasure.getName());
                if (Optional.ofNullable(exist).isPresent()) {
                    existing.add(exist);

                } else {
                    OrganizationalSecurityMeasure newSecurityMeasure = new OrganizationalSecurityMeasure();
                    newSecurityMeasure.setName(securityMeasure.getName());
                    newOrgSecurityMeasures.add(save(newSecurityMeasure));
                }
            }

            result.put("existing", existing);
            result.put("new", newOrgSecurityMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<OrganizationalSecurityMeasure> getAllOrganizationalSecurityMeasure() {
        List<OrganizationalSecurityMeasure> result = organizationalSecurityMeasureMongoRepository.findAllHostingProviders();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("OrganizationalSecurityMeasure not exist please create purpose ");
    }


    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasureById(BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteOrganizationalSecurityMeasureById(BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
           exist.setDeleted(true);
           save(exist);
            return true;

        }
    }


    public OrganizationalSecurityMeasure updateOrganizationalSecurityMeasure(BigInteger id, OrganizationalSecurityMeasure orgSecurityMeasure) {

        if (StringUtils.isEmpty(orgSecurityMeasure)) {
            throw new InvalidRequestException("requested orgSecurityMeasure name is null");

        }
        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(orgSecurityMeasure.getName());

            return save(exist);

        }
    }


}
