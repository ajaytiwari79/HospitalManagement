package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.master_data.OrganizationalSecurityMeasure;
import com.kairos.persistance.repository.master_data.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationalSecurityMeasureService extends MongoBaseService {

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;


    public OrganizationalSecurityMeasure createOrganizationalSecurityMeasure(String orgSecurityMeasure) {
        if (StringUtils.isEmpty(orgSecurityMeasure)) {
            throw new RequestDataNull("requested orgSecurityMeasure name is null");

        }
        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByName(orgSecurityMeasure);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for " + orgSecurityMeasure);
        } else {
            OrganizationalSecurityMeasure newOrganizationalSecurityMeasure = new OrganizationalSecurityMeasure();
            newOrganizationalSecurityMeasure.setName(orgSecurityMeasure);
            return save(newOrganizationalSecurityMeasure);
        }
    }


    public List<OrganizationalSecurityMeasure> getAllOrganizationalSecurityMeasure() {
        List<OrganizationalSecurityMeasure> result = organizationalSecurityMeasureMongoRepository.findAll();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("OrganizationalSecurityMeasure not exist please create purpose ");
    }


    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasureById(BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteOrganizationalSecurityMeasureById(BigInteger id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            organizationalSecurityMeasureMongoRepository.delete(exist);
            return true;

        }
    }


    public OrganizationalSecurityMeasure updateOrganizationalSecurityMeasure(BigInteger id, String orgSecurityMeasure) {

        if (StringUtils.isEmpty(orgSecurityMeasure)) {
            throw new RequestDataNull("requested orgSecurityMeasure name is null");

        }
        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(orgSecurityMeasure);
            return save(exist);

        }
    }


}
