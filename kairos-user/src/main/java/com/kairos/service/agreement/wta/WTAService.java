package com.kairos.service.agreement.wta;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreementQueryResult;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseDTO;
import com.kairos.persistence.model.user.expertise.ExpertiseIdListDTO;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.response.dto.web.WtaDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.expertise.ExpertiseService;
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
    @Inject
    ExpertiseService expertiseService;
    public HashMap createWta(long countryId, WtaDTO wtaDTO) {
        Country country = countryRepository.findOne(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country id " + countryId);
        }
        checkUniquenessOfData(countryId, wtaDTO);
        WorkingTimeAgreement wta = prepareWta(countryId, wtaDTO);
        wta.setCountry(country);
        save(wta);
        HashMap hs=new HashMap();
        hs.put("id",wta.getId());
        hs.put("name",wta.getName());
        return hs;
    }

    private void checkUniquenessOfData(long countryId, WtaDTO wtaDTO) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfData( wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId(), countryId);
        if (wta != null) {
            throw new InvalidRequestException("WTA combination of exp,org,level,region already exist.");

        }
        return;
    }

    private WorkingTimeAgreement prepareWta(long countryId, WtaDTO wtaDTO) {

        WorkingTimeAgreement wta = new WorkingTimeAgreement();

        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());

        Expertise expertise = expertiseRepository.findOne(((long) wtaDTO.getExpertiseId()));
        if (expertise == null) {
            throw new DataNotFoundByIdException("Invalid expertiseId " + wtaDTO.getExpertiseId());
        }
        wta.setExpertise(expertise);
        OrganizationType organizationType = organizationTypeRepository.findOne(wtaDTO.getOrganizationType());
        if (organizationType == null) {
            throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
        }
        wta.setOrganizationType(organizationType);

        OrganizationType organizationSubType = organizationTypeRepository.findOne(wtaDTO.getOrganizationSubType());
        if (organizationSubType == null) {
            throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
        }
        wta.setOrganizationSubType(organizationSubType);
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        // wtaBaseRuleTemplates = setRuleTemplates(countryId, wta, wtaDTO);
        if (wtaDTO.getRuleTemplates() != null || wtaDTO.getRuleTemplates().isEmpty()) {
            for (long ruleTemplateId : wtaDTO.getRuleTemplates()) {
                WTABaseRuleTemplate wtaBaseRuleTemplate = wtaBaseRuleTemplateGraphRepository.findOne(ruleTemplateId);
                if (wtaBaseRuleTemplate == null) {
                    throw new DataNotFoundByIdException("Invalid RuleTemplate Id " + ruleTemplateId);

                }
                wtaBaseRuleTemplates.add(wtaBaseRuleTemplate);
            }
        }
        wta.setRuleTemplates(wtaBaseRuleTemplates);
        if (wtaDTO.getStartDateMillis() == 0) {

            wta.setStartDateMillis(new Date().getTime());
        } else wta.setStartDateMillis(wtaDTO.getStartDateMillis());
        if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
            if (wtaDTO.getStartDateMillis() > wtaDTO.getEndDateMillis()) {
                throw new InvalidRequestException("End Date must not be greater than start date");

            }
            wta.setEndDateMillis(wtaDTO.getEndDateMillis());
        }

        return wta;
    }


    private WTABaseRuleTemplate getTemplateByType(Long countryId, String templateType) {
        return countryRepository.getTemplateByType(countryId, templateType);
    }

    private void checkUniquenessOfDataExcludingCurrent(long countryId, long wtaId, WtaDTO wtaDTO) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfDataExcludingCurrent( wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId(), countryId, wtaId);
        if (wta != null) {
            throw new InvalidRequestException("WTA combination of exp,org,level,region already exist.");

        }
        return;
    }

    public boolean updateWta(long countryId, long wtaId, WtaDTO wta) {


        WorkingTimeAgreement oldWta = wtaRepository.findOne(wtaId);

        if (oldWta == null) {
            return false;
        }
        checkUniquenessOfDataExcludingCurrent(countryId, wtaId, wta);

        prepareWta(countryId, oldWta, wta);

        save(oldWta);

        return true;
    }

    private void prepareWta(long countryId, WorkingTimeAgreement oldWta, WtaDTO wtaDTO) {
        oldWta.setName(wtaDTO.getName());
        oldWta.setDescription(wtaDTO.getDescription());

        if (oldWta.getExpertise().getId() != wtaDTO.getExpertiseId()) {
            Expertise expertise = expertiseRepository.findOne(wtaDTO.getExpertiseId());
            if (expertise == null) {
                throw new NullPointerException("Expertize not found by Id" + wtaDTO.getExpertiseId());
            }
            oldWta.setExpertise(expertise);
        }


        OrganizationType organizationType = organizationTypeRepository.findOne(wtaDTO.getOrganizationType());
        if (organizationType == null) {
            throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
        }
        oldWta.setOrganizationType(organizationType);

        OrganizationType organizationSubType = organizationTypeRepository.findOne(wtaDTO.getOrganizationSubType());

        if (organizationSubType == null) {
            throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
        }
        oldWta.setOrganizationSubType(organizationSubType);

        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        // wtaBaseRuleTemplates = setRuleTemplates(countryId, wta, wtaDTO);
        if (wtaDTO.getRuleTemplates() != null || wtaDTO.getRuleTemplates().isEmpty()) {
            for (long ruleTemplateId : wtaDTO.getRuleTemplates()) {
                WTABaseRuleTemplate wtaBaseRuleTemplate = wtaBaseRuleTemplateGraphRepository.findOne(ruleTemplateId);
                if (wtaBaseRuleTemplate == null) {
                    throw new DataNotFoundByIdException("Invalid RuleTemplate Id " + ruleTemplateId);

                }
                wtaBaseRuleTemplates.add(wtaBaseRuleTemplate);
            }
        }
        oldWta.setRuleTemplates(wtaBaseRuleTemplates);
        if (wtaDTO.getStartDateMillis() == 0) {
            oldWta.setStartDateMillis(new Date().getTime());
        } else oldWta.setStartDateMillis(wtaDTO.getStartDateMillis());

        if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
            if (wtaDTO.getStartDateMillis() > wtaDTO.getEndDateMillis()) {
                throw new InvalidRequestException("End Date must not be less than start date");
            }
            oldWta.setEndDateMillis(wtaDTO.getEndDateMillis());
        }
    }

    public WorkingTimeAgreement getWta(long wtaId) {
        return wtaRepository.getWta(wtaId);
    }

    public boolean removeWta(long wtaId) {
        WorkingTimeAgreement wta = wtaRepository.getWta(wtaId);
        if (wta == null) {
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }
        wta.setEnabled(false);
        save(wta);

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

    public List<Object> getAllWTAWithOrganization(long countryId) {
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithOrganization(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public List<Object> getAllWTAWithWTAId(long countryId, long wtaId) {
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithWTAId(countryId, wtaId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }
    public List<ExpertiseDTO> getAllAvailableExpertise(Long organizationSubTypeId,Long countryId){
        ExpertiseIdListDTO map = wtaRepository.getAvailableAndFreeExpertise(countryId, organizationSubTypeId);
        List<Long> linkedExpertiseIds= map.getLinkedExpertise();
        List<Long> allExpertiseIds= map.getAllExpertiseIds();
        allExpertiseIds.removeAll(linkedExpertiseIds);
        List<ExpertiseDTO> expertiseDTOS=new ArrayList<ExpertiseDTO>();
        expertiseDTOS=expertiseService.getAllFreeExpertise(allExpertiseIds);
        return expertiseDTOS;

    }
    public boolean setWtaWithOrganizationType(long wtaId, long organizationTypeId, boolean checked) {
        OrganizationType orgType = organizationTypeRepository.findOne(organizationTypeId);
        if (orgType == null) {
            throw new DataNotFoundByIdException("Invalid organisation type " + organizationTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (wta == null) {
            throw new DataNotFoundByIdException("wta not found");
        }
        if (checked) {
            // wta.getOrganizationTypes().add(orgType);
            save(wta);
        } else {
            //wta.getOrganizationTypes().remove(orgType);
            save(wta);
        }
        return checked;

    }
}