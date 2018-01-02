package com.kairos.service.agreement.wta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.tag.Tag;
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
import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.RuleTemplateCategoryService;
import com.kairos.service.agreement.RuleTemplateService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.util.DateUtil;
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
    private ExpertiseService expertiseService;
    @Inject
    private RuleTemplateService ruleTemplateService;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private TagService tagService;
    @Inject
    private WTAOrganizationService wtaOrganizationService;


    private final Logger logger = LoggerFactory.getLogger(WTAService.class);

    /**
     * @param countryId
     * @param wtaDTO
     * @return
     * @Author Vipul
     */
    public WorkingTimeAgreement createWta(long countryId, WTADTO wtaDTO) {
        Country country = countryRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country id " + countryId);
        }
        //TODO  API functionality has been changed for now KP-958
        //  checkUniquenessOfData(countryId, wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId());
        WorkingTimeAgreement wta = wtaRepository.getWtaByName("(?i)" + wtaDTO.getName(), countryId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new DuplicateDataException("Duplicate WTA name" + wtaDTO.getName());
        }
        // Link tags to WTA
        System.out.println(wtaDTO.getTags().size());
        if (wtaDTO.getTags().size() > 0) {
            List<Tag> tags = tagService.getCountryTagsByIdsAndMasterDataType(wtaDTO.getTags(), MasterDataTypeEnum.WTA);
            wta.setTags(tags);
        }


        wta = prepareWta(countryId, wtaDTO);


        wta.setCountry(country);
        save(wta);
        // Adding this wta to all organization type
        wtaRepository.linkWTAWithAllOrganizationOfThisSubType(wta.getId(), wta.getOrganizationSubType().getId());
        wta.setOrganizationType(wta.getOrganizationType().basicDetails());
        wta.setOrganizationSubType(wta.getOrganizationSubType().basicDetails());
        wta.getExpertise().setCountry(null);
        return wta;
    }

    private void checkUniquenessOfData(long countryId, long organizationSubTypeId, long organizationTypeId, long expertiseId) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfData(organizationSubTypeId, organizationTypeId, expertiseId, countryId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of expertise ,organization type and Sub type already exist.");

        }
        return;
    }

    private WorkingTimeAgreement prepareWta(long countryId, WTADTO wtaDTO) {

        WorkingTimeAgreement wta = new WorkingTimeAgreement();
        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());

        Expertise expertise = expertiseRepository.findOne(((long) wtaDTO.getExpertiseId()));
        if (!Optional.ofNullable(expertise).isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertiseId " + wtaDTO.getExpertiseId());
        }
        wta.setExpertise(expertise);

        OrganizationType organizationType = organizationTypeRepository.findOne(wtaDTO.getOrganizationType());
        if (!Optional.ofNullable(organizationType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
        }
        wta.setOrganizationType(organizationType);

        OrganizationType organizationSubType = organizationTypeRepository.findOne(wtaDTO.getOrganizationSubType());
        if (!Optional.ofNullable(organizationSubType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
        }

        wta.setOrganizationSubType(organizationSubType);

        List<RuleTemplate> ruleTemplates = new ArrayList<>();
        if (wtaDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaOrganizationService.copyRuleTemplates(null, wtaDTO.getRuleTemplates(), "COUNTRY", countryId);
            wta.setRuleTemplates(ruleTemplates);
        }
        wta.setRuleTemplates(ruleTemplates);
        Long dateInMillies = (wtaDTO.getStartDateMillis() == 0) ? DateUtil.getCurrentDate().getTime() : wtaDTO.getStartDateMillis();
        wta.setStartDateMillis(dateInMillies);

        wta.setStartDateMillis(dateInMillies);
        if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
            if (dateInMillies > wtaDTO.getEndDateMillis()) {
                throw new InvalidRequestException("End Date must not be greater than start date");

            }
            wta.setEndDateMillis(wtaDTO.getEndDateMillis());
        }

        return wta;
    }
/*
    public void copyRuleTemplates(WtaDTO wtaDTO, List<RuleTemplate> wtaBaseRuleTemplates, List<RuleTemplateCategoryDTO> wtaRuleTemplateQueryResponseArrayList) {

        if (wtaDTO.getRuleTemplates() != null || !wtaDTO.getRuleTemplates().isEmpty()) {
            for (long ruleTemplateId : wtaDTO.getRuleTemplates()) {
                WTARuleTemplateQueryResponse wtaBaseRuleTemplate = ruleTemplateService.getRuleTemplateById(ruleTemplateId);
                WTABaseRuleTemplate wtaBaseRuleTemplateCopy = new WTABaseRuleTemplate();
                if (!Optional.ofNullable(wtaBaseRuleTemplate).isPresent()) {
                    throw new DataNotFoundByIdException("Invalid RuleTemplate Id " + ruleTemplateId);

                }

                wtaBaseRuleTemplateCopy = createCopyOfPrevious(wtaBaseRuleTemplate);
                ruleTemplateCategoryService.setRuleTemplatecategoryWithRuleTemplate(wtaBaseRuleTemplate.getRuleTemplateCategory().getId(), wtaBaseRuleTemplateCopy.getId());
                wtaBaseRuleTemplates.add(wtaBaseRuleTemplateCopy);
                RuleTemplateCategoryDTO responseWtaWithCategory = new RuleTemplateCategoryDTO();
                responseWtaWithCategory.setRuleTemplateCategory(wtaBaseRuleTemplate.getRuleTemplateCategory());
                BeanUtils.copyProperties(wtaBaseRuleTemplateCopy, responseWtaWithCategory);
                wtaRuleTemplateQueryResponseArrayList.add(responseWtaWithCategory);
            }
        }

    }
*/

    private void checkUniquenessOfDataExcludingCurrent(long countryId, long wtaId, WTADTO wtaDTO) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfDataExcludingCurrent(wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId(), countryId, wtaId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of exp,org,level,region already exist.");

        }
        return;
    }

    // FOR COUNTRY
    public WorkingTimeAgreement updateWtaOfCountry(Long countryId, Long wtaId, WTADTO updateDTO) {
        WorkingTimeAgreement oldWta = wtaRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }
        // TODO may be again changed in future.

        WorkingTimeAgreement workingTimeAgreement = wtaRepository.getWtaByNameExcludingCurrent("(?i)" + updateDTO.getName(), countryId, wtaId);
        if (Optional.ofNullable(workingTimeAgreement).isPresent()) {
            throw new DuplicateDataException("Duplicate WTA name " + updateDTO.getName());
        }
        WorkingTimeAgreement newWta = new WorkingTimeAgreement();
        newWta = prepareWta(oldWta, updateDTO);
        List<RuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaOrganizationService.copyRuleTemplates(oldWta.getRuleTemplates(), updateDTO.getRuleTemplates(), "COUNTRY", countryId);
            newWta.setRuleTemplates(ruleTemplates);
        }
        newWta.setParentWTA(oldWta);
        save(newWta);
        newWta.setOrganizationType(oldWta.getOrganizationType().basicDetails());
        newWta.setOrganizationSubType(oldWta.getOrganizationSubType().basicDetails());
        newWta.getExpertise().setCountry(null);

        oldWta.setCountry(null);
        oldWta.setOrganizationType(null);
        oldWta.setOrganizationSubType(null);
        save(oldWta);

        return newWta;
    }

    private WorkingTimeAgreement prepareWta(WorkingTimeAgreement oldWta, WTADTO updateDTO) {
        WorkingTimeAgreement newWta = new WorkingTimeAgreement();
        newWta.setName(updateDTO.getName());
        newWta.setDescription(updateDTO.getDescription());
        newWta.setStartDateMillis(updateDTO.getStartDateMillis());
        newWta.setEndDateMillis(updateDTO.getEndDateMillis());
        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            Expertise expertise = expertiseRepository.findOne(updateDTO.getExpertiseId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Expertize not found by Id" + updateDTO.getExpertiseId());
            }
            newWta.setExpertise(expertise);
        } else {
            newWta.setExpertise(oldWta.getExpertise());
        }

        if (oldWta.getOrganizationType().getId() != updateDTO.getOrganizationType()) {
            OrganizationType organizationType = organizationTypeRepository.findOne(updateDTO.getOrganizationType());
            if (!Optional.ofNullable(organizationType).isPresent()) {
                throw new DataNotFoundByIdException("Invalid organization type " + updateDTO.getOrganizationType());
            }
            newWta.setOrganizationType(organizationType);
        } else {
            newWta.setOrganizationType(oldWta.getOrganizationType());
        }

        if (oldWta.getOrganizationSubType().getId() != updateDTO.getOrganizationSubType()) {
            OrganizationType organizationSubType = organizationTypeRepository.findOne(updateDTO.getOrganizationSubType());

            if (!Optional.ofNullable(organizationSubType).isPresent()) {
                throw new DataNotFoundByIdException("Invalid organization sub type " + updateDTO.getOrganizationSubType());
            }
            newWta.setOrganizationSubType(organizationSubType);
        } else {
            newWta.setOrganizationSubType(oldWta.getOrganizationSubType());
        }
        return newWta;

    }

    /*
        public HashMap updateWta(long countryId, long wtaId, WtaDTO wta) {
            WorkingTimeAgreement oldWta = wtaRepository.findOne(wtaId);
            if (!Optional.ofNullable(oldWta).isPresent()) {
                throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
            }
            // TODO may be again changed in future.
            //checkUniquenessOfDataExcludingCurrent(countryId, wtaId, wta);
            WorkingTimeAgreement workingTimeAgreement = wtaRepository.getWtaByNameExcludingCurrent("(?i)" + wta.getName(), countryId, wtaId);
            if (Optional.ofNullable(workingTimeAgreement).isPresent()) {
                throw new DuplicateDataException("Duplicate WTA name " + wta.getName());
            }

            List<WTAWithCategoryDTO> wtaRuleTemplateQueryResponseArrayList = new ArrayList<WTAWithCategoryDTO>();

            copyWta(countryId, oldWta, wta, wtaRuleTemplateQueryResponseArrayList);

            HashMap response = new HashMap();
            response.put("id", oldWta.getId());
            response.put("name", oldWta.getName());
            response.put("ruleTemplates", wtaRuleTemplateQueryResponseArrayList);
            return response;
        }

        private void copyWta(long countryId, WorkingTimeAgreement oldWta, WtaDTO wtaDTO, List<WTAWithCategoryDTO> wtaRuleTemplateQueryResponseArrayList) {
            oldWta.setName(wtaDTO.getName());
            oldWta.setDescription(wtaDTO.getDescription());

            if (oldWta.getExpertise().getId() != wtaDTO.getExpertiseId()) {
                Expertise expertise = expertiseRepository.findOne(wtaDTO.getExpertiseId());
                if (!Optional.ofNullable(expertise).isPresent()) {
                    throw new DataNotFoundByIdException("Expertize not found by Id" + wtaDTO.getExpertiseId());
                }
                oldWta.setExpertise(expertise);
            }


<<<<<<< HEAD
        if (wtaDTO.getStartDateMillis() == 0) {
            oldWta.setStartDateMillis(DateUtil.getCurrentDate().getTime());
        } else oldWta.setStartDateMillis(wtaDTO.getStartDateMillis());
=======
            OrganizationType organizationType = organizationTypeRepository.findOne(wtaDTO.getOrganizationType());
            if (!Optional.ofNullable(organizationType).isPresent()) {
                throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
            }
            oldWta.setOrganizationType(organizationType);

            OrganizationType organizationSubType = organizationTypeRepository.findOne(wtaDTO.getOrganizationSubType());
>>>>>>> KP-1654

            if (!Optional.ofNullable(organizationSubType).isPresent()) {
                throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
            }
<<<<<<< HEAD
            oldWta.setEndDateMillis(wtaDTO.getEndDateMillis());
        }
        oldWta.setTags(tagService.getCountryTagsByIdsAndMasterDataType(wtaDTO.getTags(), MasterDataTypeEnum.WTA));
        save(oldWta);
    }
=======
            oldWta.setOrganizationSubType(organizationSubType);

            List<RuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();

            copyRuleTemplates(wtaDTO, wtaBaseRuleTemplates, wtaRuleTemplateQueryResponseArrayList);
>>>>>>> KP-1654

            oldWta.setRuleTemplates(wtaBaseRuleTemplates);


            if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
                if (wtaDTO.getStartDateMillis() > wtaDTO.getEndDateMillis()) {
                    throw new InvalidRequestException("End Date must not be less than start date");
                }
                oldWta.setEndDateMillis(wtaDTO.getEndDateMillis());
            }
            save(oldWta);
        }
    */
    public WorkingTimeAgreement getWta(long wtaId) {
        return wtaRepository.getWta(wtaId);
    }

    public boolean removeWta(long wtaId) {
        WorkingTimeAgreement wta = wtaRepository.getWta(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }
        wta.setDeleted(true);
        save(wta);
        return true;
    }

    public List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByOrganizationId(long organizationId) {
        return wtaRepository.getAllWTAByOrganizationTypeId(organizationId);
    }

    public List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByCountryId(long countryId) {
        return wtaRepository.getAllWTAByCountryId(countryId);
    }

    public List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
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

    public List<ExpertiseDTO> getAllAvailableExpertise(Long organizationSubTypeId, Long countryId) {
        ExpertiseIdListDTO map = wtaRepository.getAvailableAndFreeExpertise(countryId, organizationSubTypeId);
        List<Long> linkedExpertiseIds = map.getLinkedExpertise();
        List<Long> allExpertiseIds = map.getAllExpertiseIds();
        allExpertiseIds.removeAll(linkedExpertiseIds);
        List<ExpertiseDTO> expertiseDTOS = new ArrayList<ExpertiseDTO>();
        expertiseDTOS = expertiseService.getAllFreeExpertise(allExpertiseIds);
        return expertiseDTOS;

    }

    public Map<String, Object> setWtaWithOrganizationType(Long countryId, long wtaId, long organizationSubTypeId, boolean checked) {
        Map<String, Object> map = new HashMap<>();
        List<RuleTemplateCategoryDTO> wtaRuleTemplateQueryResponseArrayList = new ArrayList<RuleTemplateCategoryDTO>();

        OrganizationType organizationSubType = organizationTypeRepository.findOne(organizationSubTypeId);
        if (!Optional.ofNullable(organizationSubType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organisation Sub type Id " + organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId, 2);
        //TODO need to again activate check
        //checkUniquenessOfData(countryId, organizationSubTypeId, wta.getOrganizationType().getId(), wta.getExpertise().getId());
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("wta not found " + wtaId);
        }
        if (checked) {
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            copyWta(wta, newWtaObject);
            newWtaObject.setOrganizationSubType(organizationSubType);
            List<RuleTemplate> ruleTemplateWithCategory = copyRuleTemplate(wta.getRuleTemplates());
            newWtaObject.setRuleTemplates(ruleTemplateWithCategory);
            newWtaObject.setOrganizationSubType(organizationSubType);
            save(newWtaObject);
        // setting basic propery for response
            newWtaObject.setOrganizationType(newWtaObject.getOrganizationType().basicDetails());
            newWtaObject.setOrganizationSubType(newWtaObject.getOrganizationSubType().basicDetails());
            newWtaObject.setExpertise(newWtaObject.getExpertise().retrieveBasicDetails());
            newWtaObject.setCountry(null);
            map.put("wta", newWtaObject);
            map.put("ruleTemplate", ruleTemplateWithCategory);
        } else {
            wta.setDeleted(true);
            save(wta);
        }
        return map;

    }

    private List<RuleTemplate> copyRuleTemplate(List<RuleTemplate> ruleTemplates) {
        List<RuleTemplate> ruleTemplateCategory = new ArrayList<>(ruleTemplates.size());
        ObjectMapper objectMapper = new ObjectMapper();
        ruleTemplates.forEach(ruleTemplate -> {
            RuleTemplate wtaBaseRuleTemplateDTO = objectMapper.convertValue(ruleTemplate, RuleTemplate.class);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplate.getRuleTemplateCategory());
            wtaBaseRuleTemplateDTO.setId(null);
            ruleTemplateCategory.add(wtaBaseRuleTemplateDTO);
            ruleTemplateCategory.toString();
        });
        return ruleTemplateCategory;
    }

    private WorkingTimeAgreement copyWta(WorkingTimeAgreement oldWta, WorkingTimeAgreement newWta) {
        newWta.setName(oldWta.getName());
        newWta.setDescription(oldWta.getDescription());
        newWta.setStartDateMillis(oldWta.getStartDateMillis());
        newWta.setEndDateMillis(oldWta.getEndDateMillis());
        newWta.setExpertise(oldWta.getExpertise());
        newWta.setOrganizationType(oldWta.getOrganizationType());
        newWta.setCountry(oldWta.getCountry());
        newWta.setId(null);
        return newWta;

    }

}