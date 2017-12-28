package com.kairos.service.agreement.wta;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreementQueryResult;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.WTARuleTemplateQueryResponse;
import com.kairos.persistence.model.user.agreement.wta.templates.WTAWithCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.*;
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
import com.kairos.response.dto.web.WTADTO;
import com.kairos.response.dto.web.WtaDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.RuleTemplateCategoryService;
import com.kairos.service.agreement.RuleTemplateService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.*;


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
    ExpertiseService expertiseService;
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
        wta.setTags(tagService.getCountryTagsByIdsAndMasterDataType(wtaDTO.getTags(), MasterDataTypeEnum.WTA));

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
            ruleTemplates = wtaOrganizationService.copyRuleTemplatesWithNew(null, wtaDTO.getRuleTemplates(), "COUNTRY", countryId);
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

    public void copyRuleTemplates(WtaDTO wtaDTO, List<RuleTemplate> wtaBaseRuleTemplates, List<WTAWithCategoryDTO> wtaRuleTemplateQueryResponseArrayList) {

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
                WTAWithCategoryDTO responseWtaWithCategory = new WTAWithCategoryDTO();
                responseWtaWithCategory.setRuleTemplateCategory(wtaBaseRuleTemplate.getRuleTemplateCategory());
                BeanUtils.copyProperties(wtaBaseRuleTemplateCopy, responseWtaWithCategory);
                wtaRuleTemplateQueryResponseArrayList.add(responseWtaWithCategory);
            }
        }

    }


    private void checkUniquenessOfDataExcludingCurrent(long countryId, long wtaId, WtaDTO wtaDTO) {
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

        oldWta = prepareWta(oldWta, updateDTO);
        List<RuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaOrganizationService.copyRuleTemplatesWithNew(oldWta.getRuleTemplates(), updateDTO.getRuleTemplates(), "COUNTRY", countryId);
            oldWta.setRuleTemplates(ruleTemplates);
        }
        save(oldWta);
        //
        oldWta.setOrganizationType(oldWta.getOrganizationType().basicDetails());
        oldWta.setOrganizationSubType(oldWta.getOrganizationSubType().basicDetails());
        oldWta.getExpertise().setCountry(null);
        return oldWta;
    }

    private WorkingTimeAgreement prepareWta(WorkingTimeAgreement oldWta, WTADTO updateDTO) {
        oldWta.setName(updateDTO.getName());
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setStartDateMillis(updateDTO.getStartDateMillis());
        oldWta.setEndDateMillis(updateDTO.getEndDateMillis());
        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            Expertise expertise = expertiseRepository.findOne(updateDTO.getExpertiseId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Expertize not found by Id" + updateDTO.getExpertiseId());
            }
            oldWta.setExpertise(expertise);
        }

        if (oldWta.getOrganizationType().getId() != updateDTO.getOrganizationType()) {
            OrganizationType organizationType = organizationTypeRepository.findOne(updateDTO.getOrganizationType());
            if (!Optional.ofNullable(organizationType).isPresent()) {
                throw new DataNotFoundByIdException("Invalid organization type " + updateDTO.getOrganizationType());
            }
            oldWta.setOrganizationType(organizationType);
        }

        if (oldWta.getOrganizationSubType().getId() != updateDTO.getOrganizationSubType()) {
            OrganizationType organizationSubType = organizationTypeRepository.findOne(updateDTO.getOrganizationSubType());

            if (!Optional.ofNullable(organizationSubType).isPresent()) {
                throw new DataNotFoundByIdException("Invalid organization sub type " + updateDTO.getOrganizationSubType());
            }
            oldWta.setOrganizationSubType(organizationSubType);
        }

        return oldWta;

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

            prepareWta(countryId, oldWta, wta, wtaRuleTemplateQueryResponseArrayList);

            HashMap response = new HashMap();
            response.put("id", oldWta.getId());
            response.put("name", oldWta.getName());
            response.put("ruleTemplates", wtaRuleTemplateQueryResponseArrayList);
            return response;
        }

        private void prepareWta(long countryId, WorkingTimeAgreement oldWta, WtaDTO wtaDTO, List<WTAWithCategoryDTO> wtaRuleTemplateQueryResponseArrayList) {
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

    public List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationId(long organizationId) {
        return wtaRepository.getAllWTAByOrganizationId(organizationId);
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
        List<WTAWithCategoryDTO> wtaRuleTemplateQueryResponseArrayList = new ArrayList<WTAWithCategoryDTO>();

        OrganizationType orgType = organizationTypeRepository.findOne(organizationSubTypeId);
        if (!Optional.ofNullable(orgType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organisation Sub type Id " + organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        //TODO need to again activate check
        //checkUniquenessOfData(countryId, organizationSubTypeId, wta.getOrganizationType().getId(), wta.getExpertise().getId());
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("wta not found " + wtaId);
        }
        if (checked) {
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            WorkingTimeAgreement.copyProperties(wta, newWtaObject);
            if (Optional.ofNullable(wta.getRuleTemplates()).isPresent() && wta.getRuleTemplates().size() > 0) {
                WtaDTO wtaDTO = wta.buildwtaDTO();
                List<Long> ruleTemplateIds = new ArrayList<>();
                for (RuleTemplate wtaBRT : wta.getRuleTemplates()) {
                    ruleTemplateIds.add(wtaBRT.getId());
                }
                wtaDTO.setRuleTemplates(ruleTemplateIds);
                copyRuleTemplates(wtaDTO, wta.getRuleTemplates(), wtaRuleTemplateQueryResponseArrayList);
            }
            newWtaObject.setId(null);
            newWtaObject.setOrganizationSubType(orgType);
            save(newWtaObject);
            map.put("wta", newWtaObject);
            map.put("ruleTemplate", wtaRuleTemplateQueryResponseArrayList);
        } else {
            wta.setDeleted(true);
            save(wta);
        }
        return map;

    }

    protected WTABaseRuleTemplate createCopyOfPrevious(WTARuleTemplateQueryResponse wtaBaseRuleTemplate) {
        WTABaseRuleTemplate wtaBaseRuleTemplateCopy = null;
        switch (wtaBaseRuleTemplate.getTemplateType()) {
            case TEMPLATE1:
                MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                maximumShiftLengthWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumShiftLengthWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumShiftLengthWTATemplate.setTimeLimit(wtaBaseRuleTemplate.getTimeLimit());
                maximumShiftLengthWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                maximumShiftLengthWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumShiftLengthWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(maximumShiftLengthWTATemplate);
                break;

            case TEMPLATE2:
                MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                minimumShiftLengthWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumShiftLengthWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumShiftLengthWTATemplate.setTimeLimit(wtaBaseRuleTemplate.getTimeLimit());
                minimumShiftLengthWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                minimumShiftLengthWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumShiftLengthWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(minimumShiftLengthWTATemplate);
                break;

            case TEMPLATE3:
                MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                maximumConsecutiveWorkingDaysWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumConsecutiveWorkingDaysWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumConsecutiveWorkingDaysWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(maximumConsecutiveWorkingDaysWTATemplate);

                break;

            case TEMPLATE4:
                MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate();
                minimumRestInConsecutiveDaysWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumRestInConsecutiveDaysWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(wtaBaseRuleTemplate.getMinimumRest());
                minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(wtaBaseRuleTemplate.getDaysWorked());
                minimumRestInConsecutiveDaysWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumRestInConsecutiveDaysWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(minimumRestInConsecutiveDaysWTATemplate);
                break;

            case TEMPLATE5:
                MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                maximumNightShiftLengthWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumNightShiftLengthWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                maximumNightShiftLengthWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumNightShiftLengthWTATemplate.setTimeLimit(wtaBaseRuleTemplate.getTimeLimit());
                maximumNightShiftLengthWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                maximumNightShiftLengthWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                wtaBaseRuleTemplateCopy = save(maximumNightShiftLengthWTATemplate);
                break;

            case TEMPLATE6:
                MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                minimumConsecutiveNightsWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumConsecutiveNightsWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumConsecutiveNightsWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                minimumConsecutiveNightsWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumConsecutiveNightsWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());

                wtaBaseRuleTemplateCopy = save(minimumConsecutiveNightsWTATemplate);
                break;

            case TEMPLATE7:
                MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                maximumConsecutiveWorkingNights.setName(wtaBaseRuleTemplate.getName());
                maximumConsecutiveWorkingNights.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumConsecutiveWorkingNights.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumConsecutiveWorkingNights.setNightsWorked(wtaBaseRuleTemplate.getNightsWorked());
                maximumConsecutiveWorkingNights.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                maximumConsecutiveWorkingNights.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(maximumConsecutiveWorkingNights);
                break;
            case TEMPLATE8:
                MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate();
                minimumRestConsecutiveNightsWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumRestConsecutiveNightsWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumRestConsecutiveNightsWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumRestConsecutiveNightsWTATemplate.setNightsWorked(wtaBaseRuleTemplate.getNightsWorked());
                minimumRestConsecutiveNightsWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                minimumRestConsecutiveNightsWTATemplate.setMinimumRest(wtaBaseRuleTemplate.getMinimumRest());
                minimumRestConsecutiveNightsWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(minimumRestConsecutiveNightsWTATemplate);
                break;
            case TEMPLATE9:
                MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = new MaximumNumberOfNightsWTATemplate();
                maximumNumberOfNightsWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumNumberOfNightsWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumNumberOfNightsWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumNumberOfNightsWTATemplate.setNightsWorked(wtaBaseRuleTemplate.getNightsWorked());
                maximumNumberOfNightsWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumNumberOfNightsWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumNumberOfNightsWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumNumberOfNightsWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());

                wtaBaseRuleTemplateCopy = save(maximumNumberOfNightsWTATemplate);
                break;
            case TEMPLATE10:
                MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = new MaximumDaysOffInPeriodWTATemplate();
                maximumDaysOffInPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumDaysOffInPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumDaysOffInPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                maximumDaysOffInPeriodWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(maximumDaysOffInPeriodWTATemplate);
                break;
            case TEMPLATE11:
                MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = new MaximumAverageScheduledTimeWTATemplate();
                maximumAverageScheduledTimeWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumAverageScheduledTimeWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(wtaBaseRuleTemplate.getUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(wtaBaseRuleTemplate.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(wtaBaseRuleTemplate.getBalanceAdjustment());
                maximumAverageScheduledTimeWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumAverageScheduledTimeWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(maximumAverageScheduledTimeWTATemplate);
                break;
            case TEMPLATE12:
                MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                maximumVetoPerPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumVetoPerPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumVetoPerPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(wtaBaseRuleTemplate.getMaximumVetoPercentage());
                maximumVetoPerPeriodWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());

                wtaBaseRuleTemplateCopy = save(maximumVetoPerPeriodWTATemplate);
                break;
            case TEMPLATE13:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate();
                numberOfWeekendShiftInPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                numberOfWeekendShiftInPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                numberOfWeekendShiftInPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberShiftsPerPeriod(wtaBaseRuleTemplate.getNumberShiftsPerPeriod());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberOfWeeks(wtaBaseRuleTemplate.getNumberOfWeeks());
                numberOfWeekendShiftInPeriodWTATemplate.setFromDayOfWeek(wtaBaseRuleTemplate.getFromDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setFromTime(wtaBaseRuleTemplate.getFromTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToTime(wtaBaseRuleTemplate.getToTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToDayOfWeek(wtaBaseRuleTemplate.getToDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setProportional(wtaBaseRuleTemplate.getProportional());
                numberOfWeekendShiftInPeriodWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());

                wtaBaseRuleTemplateCopy = save(numberOfWeekendShiftInPeriodWTATemplate);
                break;
            case TEMPLATE14:
                CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                careDayCheckWTATemplate.setName(wtaBaseRuleTemplate.getName());
                careDayCheckWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                careDayCheckWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                careDayCheckWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                careDayCheckWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());

                wtaBaseRuleTemplateCopy = save(careDayCheckWTATemplate);
                break;
            case TEMPLATE15:
                MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                minimumDailyRestingTimeWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumDailyRestingTimeWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumDailyRestingTimeWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(wtaBaseRuleTemplate.getContinuousDayRestHours());
                minimumDailyRestingTimeWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());
                wtaBaseRuleTemplateCopy = save(minimumDailyRestingTimeWTATemplate);
                break;
            case TEMPLATE16:
                MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate();
                minimumDurationBetweenShiftWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumDurationBetweenShiftWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumDurationBetweenShiftWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumDurationBetweenShiftWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(wtaBaseRuleTemplate.getMinimumDurationBetweenShifts());
                minimumDurationBetweenShiftWTATemplate.setDisabled(wtaBaseRuleTemplate.getActive());

                wtaBaseRuleTemplateCopy = save(minimumDurationBetweenShiftWTATemplate);
                break;
            case TEMPLATE17:
                MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                minimumWeeklyRestPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumWeeklyRestPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumWeeklyRestPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(wtaBaseRuleTemplate.getContinuousWeekRest());
                wtaBaseRuleTemplateCopy = save(minimumWeeklyRestPeriodWTATemplate);
                break;
            case TEMPLATE18:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                shortestAndAverageDailyRestWTATemplate.setName(wtaBaseRuleTemplate.getName());
                shortestAndAverageDailyRestWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                shortestAndAverageDailyRestWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                shortestAndAverageDailyRestWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                shortestAndAverageDailyRestWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                shortestAndAverageDailyRestWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(wtaBaseRuleTemplate.getContinuousDayRestHours());
                shortestAndAverageDailyRestWTATemplate.setAverageRest(wtaBaseRuleTemplate.getAverageRest());
                shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(wtaBaseRuleTemplate.getShiftAffiliation());
                wtaBaseRuleTemplateCopy = save(shortestAndAverageDailyRestWTATemplate);
                break;
            case TEMPLATE19:
                MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new MaximumShiftsInIntervalWTATemplate();
                maximumShiftsInIntervalWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumShiftsInIntervalWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumShiftsInIntervalWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumShiftsInIntervalWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumShiftsInIntervalWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumShiftsInIntervalWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumShiftsInIntervalWTATemplate.setShiftsLimit(wtaBaseRuleTemplate.getShiftsLimit());
                maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(wtaBaseRuleTemplate.getOnlyCompositeShifts());
                wtaBaseRuleTemplateCopy = save(maximumShiftsInIntervalWTATemplate);
                break;
            case TEMPLATE20:
                MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = new MaximumSeniorDaysInYearWTATemplate();
                maximumSeniorDaysInYearWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumSeniorDaysInYearWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumSeniorDaysInYearWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumSeniorDaysInYearWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumSeniorDaysInYearWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumSeniorDaysInYearWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumSeniorDaysInYearWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                maximumSeniorDaysInYearWTATemplate.setActivityCode(wtaBaseRuleTemplate.getActivityCode());

                wtaBaseRuleTemplateCopy = save(maximumSeniorDaysInYearWTATemplate);
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");


        }
        return wtaBaseRuleTemplateCopy;
    }
}