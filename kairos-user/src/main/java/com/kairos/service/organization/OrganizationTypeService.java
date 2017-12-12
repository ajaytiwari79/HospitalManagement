package com.kairos.service.organization;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.OrganizationTypeDTO;
import com.kairos.response.dto.web.UpdateOrganizationTypeDTO;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by oodles on 18/10/16.
 */
@Transactional
@Service
public class OrganizationTypeService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    CountryGraphRepository countryGraphRepository;

    public List<OrgTypeLevelWrapper> getOrgTypesByCountryId(Long countryId) {

        return organizationTypeGraphRepository.getOrganizationTypeByCountryId(countryId);
    }

    public OrganizationType createOrganizationTypeForCountry(Long countryId, OrganizationTypeDTO organizationTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if(!Optional.ofNullable(country).isPresent()){
            throw new DataNotFoundByIdException("Invalid country id " + countryId);
        }
        List<Level> levels = organizationTypeDTO.getLevels().stream().map(level->new Level(level)).collect(Collectors.toList());
        OrganizationType organizationType = new OrganizationType(organizationTypeDTO.getName(),country,levels);
        return prepareResponse(save(organizationType));
    }



    public OrganizationType getOrganizationTypeById(Long organizationTypeId) {
        return organizationTypeGraphRepository.findOne(organizationTypeId);

    }

    public OrganizationType createOrganizationType(OrganizationType organizationType) {
        return organizationTypeGraphRepository.save(organizationType);
    }


    public List<OrganizationType> getAllOrganizationTypes() {
        return organizationTypeGraphRepository.findAll();
    }

    public boolean deleteOrganizationType(Long organizationTypeId) {
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(organizationTypeId);
        if (organizationType!=null){
            organizationType.setEnable(false);
            save(organizationType);
            return true;
        }
        return false;

    }

    public OrganizationType updateOrganizationType(Long organizationTypeId,UpdateOrganizationTypeDTO updateOrganizationTypeDTO) {
        OrganizationType orgTypeToUpdate = organizationTypeGraphRepository.findOne(organizationTypeId);
        if(!Optional.ofNullable(orgTypeToUpdate).isPresent()){
            throw new DataNotFoundByIdException("Invalid organization type id " + organizationTypeId);
        }

        List<Level> levels = orgTypeToUpdate.getLevels();
        updateOrganizationTypeDTO.getLevelsToUpdate().forEach(levelToUpdate->{
            if(!Optional.ofNullable(levelToUpdate.getId()).isPresent()){
                levels.add(new Level(levelToUpdate.getName()));
            } else {
                Optional<Level> result = levels.stream().filter(levelInList->levelInList.getId().equals(levelToUpdate.getId())).findFirst();
                if(result.isPresent()){
                    Level level = result.get();
                    level.setName(result.get().getName());
                }
            }
        });
        updateOrganizationTypeDTO.getLevelsToDelete().forEach(levelToDelete ->{
            Optional<Level> result = levels.stream().filter(levelInList -> levelInList.getId().equals(levelToDelete)).findFirst();
            if(result.isPresent()){
                Level level = result.get();
                level.setDeleted(true);
            }
        });
        orgTypeToUpdate.setLevels(levels);
        orgTypeToUpdate.setName(updateOrganizationTypeDTO.getName());
        return prepareResponse(save(orgTypeToUpdate));
    }

    private OrganizationType prepareResponse(OrganizationType organizationType){
        OrganizationType response = new OrganizationType();
        response.setId(organizationType.getId());
        response.setName(organizationType.getName());
        List<Level> activeLevels = organizationType.getLevels().parallelStream().filter(level -> !level.isDeleted()).collect(Collectors.toList());
        response.setLevels(activeLevels);
        return response;
    }

    public Map<String, Object> addOrganizationTypeSubType(OrganizationType organizationType, Long organizationTypeId) {
        OrganizationType type = organizationTypeGraphRepository.findOne(organizationTypeId);
        if (type != null) {
            organizationType = organizationTypeGraphRepository.save(organizationType);
            organizationTypeGraphRepository.createSubTypeRelation(organizationType.getId(),organizationTypeId);
                return organizationType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getOrgSubTypesByTypeId(Long organizationTypeId) {
        List<Map<String, Object>> queryResponse = organizationTypeGraphRepository.getOrganizationSubTypeByTypeId(organizationTypeId);
        if (!queryResponse.isEmpty()){
            List<Object> response = new ArrayList<>();
            for (Map<String,Object> map: queryResponse) {
                Object o = map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }

    /**
     * @author prabjot
     * this method will update the relationship of expertise and organization Type based on parameter {isSelected},if parameter value is true
     * new relationship b/w expertise and organization type will be created or updated(if relationship already exist) if parameter value is false
     * then relationship will be inactive (isEnabled param of relationship will set to false)
     * @param expertiseId
     * @param orgTypeId
     * @param isSelected
     */
    public void addExpertiseInOrgType(long orgTypeId,long expertiseId,boolean isSelected){
        if(isSelected){
                if(organizationTypeGraphRepository.orgTypeHasAlreadySkill(orgTypeId, expertiseId) == 0){
                    organizationTypeGraphRepository.addExpertiseInOrgType(orgTypeId,expertiseId,DateUtil.getCurrentDate().getTime(),DateUtil.getCurrentDate().getTime());
                } else {
                    organizationTypeGraphRepository.updateOrgTypeExpertise(orgTypeId,expertiseId,DateUtil.getCurrentDate().getTime());
                }
        } else {
            organizationTypeGraphRepository.deleteOrgTypeExpertise(orgTypeId,expertiseId,DateUtil.getCurrentDate().getTime());
        }
    }

    /**
     * to get expertise for particular organization type
     * @param orgTypeId
     * @return
     */
    public List<Map<String,Object>> getExpertise(long countryId,long orgTypeId){
        OrgTypeExpertiseQueryResult orgTypeExpertiseQueryResult = organizationTypeGraphRepository.getExpertiseOfOrganizationType(countryId,orgTypeId);
        return orgTypeExpertiseQueryResult.getExpertise();
    }

    public OrganizationTypeHierarchyQueryResult getOrganizationTypeHierarchy(long countryId, Set<Long> orgSubServiceId){
        return organizationTypeGraphRepository.getOrganizationTypeHierarchy(countryId,orgSubServiceId);
    }

    public List<Organization> getOrganizationByOrganizationTypeId(long organizationTypeId){
        return organizationTypeGraphRepository.getOrganizationsByOrganizationType(organizationTypeId);
    }

    public void linkOrganizationTypeWithService(Set<Long> orgTypeId, long serviceId){
        organizationTypeGraphRepository.linkOrganizationTypeWithService(orgTypeId,serviceId);
    }

    public void deleteLinkingOfOrganizationTypeAndService(Set<Long> orgTypeId,long serviceId){
        organizationTypeGraphRepository.deleteRelOrganizationTypeWithService(orgTypeId,serviceId);
    }


}
