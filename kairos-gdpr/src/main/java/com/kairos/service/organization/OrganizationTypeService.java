package com.kairos.service.organization;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.persistance.model.organization.OrganizationType;
import com.kairos.persistance.repository.organization.OrganizationTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationTypeService extends MongoBaseService {

@Inject
private OrganizationTypeMongoRepository organizationTypeRepository;


    public OrganizationType createOrganizationType(OrganizationType organizationType)
    {
return save(organizationType);
    }




    public List<OrganizationType> getOrganizationTypes(List<Long> orgTypeListId)
    {

       List<OrganizationType> organizationTypeList=new ArrayList<>();
       for (Long id:orgTypeListId)
       {
OrganizationType organizationType=organizationTypeRepository.findById(id.toString());
           if (Optional.ofNullable(organizationType).isPresent())
           {
               organizationTypeList.add(organizationType);
           }
           else
               throw new DataNotExists("organization type for id  ->"+id+ "not exist");

       }
return organizationTypeList;

    }
}

