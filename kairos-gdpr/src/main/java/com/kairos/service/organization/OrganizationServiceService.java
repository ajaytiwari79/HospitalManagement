package com.kairos.service.organization;


import com.kairos.ExceptionHandler.DuplicateDataException;
import com.kairos.ExceptionHandler.NotExists;
import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.repository.organization.OrganizationServiceMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class OrganizationServiceService  extends MongoBaseService {
@Inject
    private OrganizationServiceMongoRepository organizationServicerepository;


public OrganizationService createOrganizationservice(OrganizationService organizationService)
{

OrganizationService exists=organizationServicerepository.findByName(organizationService.getName());
if (!Optional.ofNullable(exists).isPresent())
{
    OrganizationService organizationService1=new OrganizationService();
    organizationService1.setId(organizationService.getId());
    organizationService1.setDescription(organizationService.getDescription());
    organizationService1.setName(organizationService.getName());
    organizationService1.setOrganizationSubService(organizationService.getOrganizationSubService());
    organizationService1.setKmdExternalId(organizationService.getKmdExternalId());
    return save(organizationService1);


}else
    throw new DuplicateDataException("organization service with name"+organizationService.getName()+" already Exist");

}


    public List<OrganizationService> getOrganizationServices(List<Long> orgTypeListId) {

        OrganizationService organizationService;
        List<OrganizationService> organizationServiceList = new ArrayList<>();
        for (Long id : orgTypeListId) {
             organizationService = (OrganizationService) organizationServicerepository.findById(id.toString());
            if (Optional.ofNullable(organizationService).isPresent()) {
                organizationServiceList.add(organizationService);
            } else {
                throw new NotExists("organization service for id  ->" + id + "not exist");
            }
        }
        return organizationServiceList;


    }



/*

    public List<OrganizationService> getSubServicesByIds(List<Long> orgSubServiceId)
    {

        List<OrganizationService> subServiceList=new ArrayList<>();
if (orgSubServiceId.size()!=0)
{
    for (Long id:orgSubServiceId)
    {
subServiceList.add(organizationServicerepository.findByOrganizationSubService(id));
}
return subServiceList;
}
else
    return null;

    }

*/



    }