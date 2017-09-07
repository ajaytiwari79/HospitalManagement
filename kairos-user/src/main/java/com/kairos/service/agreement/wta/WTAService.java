package com.kairos.service.agreement.wta;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreementQueryResult;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.response.dto.web.WtaDTO;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;


/**
 * Created by pawanmandhan on 2/8/17.
 */


@Transactional
@Service
public class WTAService extends UserBaseService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private WorkingTimeAgreementGraphRepository wtaRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeRepository;
    @Inject
    private CountryGraphRepository countryRepository;
    @Inject
    private ExpertiseGraphRepository expertiseRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;
    @Inject
    private RegionGraphRepository regionRepository;
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaBaseRuleTemplateGraphRepository;

    public WorkingTimeAgreement createWta(long countryId, WtaDTO wtaDTO) {

        WorkingTimeAgreement wta = prepareWta(countryId, wtaDTO);

        logger.info("prepared wta object : ",wta);

        Country country = countryRepository.findOne(countryId);

        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country id");
        }
        wta.setCountry(country);

        save(wta);
        wta.getExpertise().setCountry(null);

       for( OrganizationType orgType:wta.getOrganizationTypes()){
           orgType.setCountry(null);
           orgType.setOrganizationTypeList(null);
           orgType.setOrganizationServiceList(null);
       }

        logger.info("response wta object : ",wta);

        return wta;
    }


    private WorkingTimeAgreement prepareWta(long countryId, WtaDTO wtaDTO) {

        WorkingTimeAgreement wta = new WorkingTimeAgreement();

        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());

        Expertise expertise = expertiseRepository.findOne(((long) wtaDTO.getExpertiseId()));
        if (expertise == null) {
            throw new DataNotFoundByIdException("Invalid expertiseId "+wtaDTO.getExpertiseId());
        }
        wta.setExpertise(expertise);
        List<OrganizationType> organizationTypes = new ArrayList<OrganizationType>();

        for (long orgTypeId : wtaDTO.getOrganizationTypes()) {
            OrganizationType orgType = organizationTypeRepository.findOne(orgTypeId);
            if (orgType == null) {
                throw new DataNotFoundByIdException("Invalid organization type Id "+orgTypeId);
            }
            organizationTypes.add(orgType);
        }
        wta.setOrganizationTypes(organizationTypes);
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

       // wtaBaseRuleTemplates = setRuleTemplates(countryId, wta, wtaDTO);
        for (long ruleTemplateId : wtaDTO.getRuleTemplates()) {
            WTABaseRuleTemplate wtaBaseRuleTemplate = wtaBaseRuleTemplateGraphRepository.findOne(ruleTemplateId);
            if (wtaBaseRuleTemplate == null) {
                throw new DataNotFoundByIdException("Invalid RuleTemplate Id");

            }
            wtaBaseRuleTemplates.add(wtaBaseRuleTemplate);
        }
        wta.setRuleTemplates(wtaBaseRuleTemplates);
        Region region = regionRepository.findOne(wtaDTO.getRegionId());
        if (region == null) {
            throw new DataNotFoundByIdException("Invalid Region");
        }
        wta.setRegion(region);
        if (wtaDTO.getStartDate() == 0) {

            wta.setStartDate(new Date().getTime());
        }

        else wta.setStartDate(wtaDTO.getStartDate());

        if (wtaDTO.getEndDate() != null && wtaDTO.getEndDate() > 0 ) {
            wta.setEndDate(wtaDTO.getEndDate());
        }

      return wta;
    }


    private WTABaseRuleTemplate getTemplateByType(Long countryId, String templateType) {
        return countryRepository.getTemplateByType(countryId, templateType);
    }

    public Map<String, Object> updateWta(long wtaId, WtaDTO wta) {
        WorkingTimeAgreement oldWta = wtaRepository.findOne(wtaId);

        if (oldWta == null) {
            return null;
        }

        prepareWta(oldWta, wta);

        save(oldWta);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("id", oldWta.getId());
        response.put("name", oldWta.getName());

        return response;
    }

    private void prepareWta(WorkingTimeAgreement oldWta, WtaDTO wta) {

        if (oldWta.getExpertise().getId() != wta.getExpertiseId()) {
            Expertise expertise = expertiseRepository.findOne(wta.getExpertiseId());
            if (expertise == null) {
                throw new NullPointerException("Expertize Cannot be null");
            }
            oldWta.setExpertise(expertise);

        }


        //Todo On WTA update use Javers to maintain its versions

        oldWta.setName(wta.getName());
        oldWta.setStartDate(wta.getStartDate());
        oldWta.setEndDate(wta.getEndDate());
        oldWta.setDescription(wta.getDescription());

    }

    public WorkingTimeAgreement getWta(long wtaId) {
        return wtaRepository.findOne(wtaId);
    }

    public boolean removeWta(long wtaId) {
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (wta == null) {
            return false;
        }
        wta.setEnabled(false);
        save(wta);
        if (wtaRepository.findOne(wtaId).isEnabled()) {
            return false;
        }
        return true;
    }

    public List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationId(long organizationId) {
        return wtaRepository.getAllWTAByOrganizationId(organizationId);
    }

    public List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByCountryId(long countryId) {
        return wtaRepository.getAllWTAByCountryId(countryId);
    }

    public List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
        return wtaRepository.getAllWTAByOrganizationSubType(organizationSubTypeId);
    }
    public List<Object> getAllWTAWithOrganization(long countryId){
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithOrganization(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public List<Object>getAllWTAWithWTAId(long countryId,long wtaId){
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithWTAId(countryId,wtaId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public  boolean setWtaWithOrganizationType(long wtaId,long organizationTypeId,boolean checked){
        OrganizationType orgType =organizationTypeRepository.findOne(organizationTypeId);
        if (orgType == null) {
            throw new DataNotFoundByIdException("Invalid organisation type "+organizationTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (wta==null){
            throw new DataNotFoundByIdException("wta not found");
        }
        if(checked){
            wta.getOrganizationTypes().add(orgType);
            save(wta);
        }else {
            wta.getOrganizationTypes().remove(orgType);
            save(wta);
        }
        return checked;

    }
}