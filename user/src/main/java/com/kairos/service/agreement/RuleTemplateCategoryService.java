package com.kairos.service.agreement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.user.country.agreement.RuleTemplateCategoryCTADTO;
import com.kairos.user.country.agreement.UpdateRuleTemplateCategoryDTO;
import com.kairos.persistence.model.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ArrayUtil;
import com.kairos.wrapper.RuleTemplateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.persistence.model.agreement.cta.RuleTemplateCategoryType.CTA;

/**
 * Created by vipul on 2/8/17.
 */
@Service
public class RuleTemplateCategoryService {
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    @Inject
    private CountryService countryService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;
    @Inject
    private ExceptionService exceptionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * used to save a new Rule template in a country
     * Created by vipul on 2/8/17.
     * params countryId and rule template category via name and desc
     */
    //TODO need to modified this method
    public RuleTemplateCategory createRuleTemplateCategory(long countryId, RuleTemplateCategory ruleTemplateCategory) {

        String name = "(?i)" + ruleTemplateCategory.getName();
        int ruleFound = countryGraphRepository.checkDuplicateRuleTemplateCategory(countryId, ruleTemplateCategory.getRuleTemplateCategoryType(), name);

        if (ruleFound != 0) {
            exceptionService.duplicateDataException("message.ruleTemplate.category.duplicate", name);
        }

        Country country = countryService.getCountryById(countryId);
        country.addRuleTemplateCategory(ruleTemplateCategory);
        countryGraphRepository.save(country);
        return ruleTemplateCategory;

    }

    public List<RuleTemplateCategory> getRulesTemplateCategory(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType) {
        Country country = countryService.getCountryById(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notExist");

        }
        return ruleTemplateCategoryGraphRepository.getRuleTemplateCategoryByCountry(countryId, ruleTemplateCategoryType);

    }

    public boolean exists(long templateCategoryId) {
        return ruleTemplateCategoryGraphRepository.existsById(templateCategoryId);
    }


    public boolean deleteRuleTemplateCategory(long countryId, long templateCategoryId) {
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryGraphRepository.findOne(templateCategoryId);
        if (ruleTemplateCategory == null) {
            exceptionService.dataNotFoundByIdException("message.ruleTemplate.category.notExist", templateCategoryId);

        }
        if (ruleTemplateCategory.getName() != null && ruleTemplateCategory.getName().equals("NONE")) {
            exceptionService.actionNotPermittedException("message.ruleTemplate.category.delete", templateCategoryId);

        }
        if (ruleTemplateCategory.getRuleTemplateCategoryType().equals(CTA)) {
            List<Long> ctaRuleTemplates = ruleTemplateCategoryGraphRepository.findAllExistingCTARuleTemplateByCategory(ruleTemplateCategory.getName(), countryId);
            RuleTemplateCategory noneRuleTemplateCategory = ruleTemplateCategoryGraphRepository.findByName(countryId, "NONE", CTA);
            ruleTemplateCategoryGraphRepository.deleteRelationOfRuleTemplateCategoryAndCTA(templateCategoryId, ctaRuleTemplates);
            ruleTemplateCategoryGraphRepository.setAllCTAWithCategoryNone(noneRuleTemplateCategory.getId(), ctaRuleTemplates);
        }/* else {
            List<Long> wtaBaseRuleTemplateList = wtaBaseRuleTemplateGraphRepository.findAllWTABelongsByTemplateCategoryId(templateCategoryId);
            RuleTemplateCategory noneRuleTemplateCategory = ruleTemplateCategoryGraphRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
            wtaBaseRuleTemplateGraphRepository.deleteRelationOfRuleTemplateCategoryAndWTA(templateCategoryId, wtaBaseRuleTemplateList);
            wtaBaseRuleTemplateGraphRepository.setAllWTAWithCategoryNone(noneRuleTemplateCategory.getId(), wtaBaseRuleTemplateList);
        }*/
        return true;

    }


    public Map<String, Object> updateRuleTemplateCategory(Long countryId, Long templateCategoryId, UpdateRuleTemplateCategoryDTO ruleTemplateCategory) {
        if (countryService.getCountryById(countryId) == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notExist");

        }
        RuleTemplateCategory ruleTemplateCategoryObj = (RuleTemplateCategory) ruleTemplateCategoryGraphRepository.findOne(templateCategoryId);
        if (!Optional.ofNullable(ruleTemplateCategoryObj).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.ruleTemplate.category.notfound", templateCategoryId);

        }
        if (ruleTemplateCategoryObj.getName().equals("NONE") || ruleTemplateCategory.getName().equals("NONE")) {
            exceptionService.actionNotPermittedException("message.ruleTemplate.category.rename", templateCategoryId);

        }
        if (!ruleTemplateCategory.getName().trim().equalsIgnoreCase(ruleTemplateCategoryObj.getName())) {
            boolean isAlreadyExists = ruleTemplateCategoryGraphRepository.findByNameExcludingCurrent(countryId, CTA, "(?i)" + ruleTemplateCategory.getName().trim(), templateCategoryId);
            if (isAlreadyExists) {
                exceptionService.duplicateDataException("message.ruleTemplate.category.duplicate", ruleTemplateCategory.getName());

            }
        }
        ruleTemplateCategoryObj.setName(ruleTemplateCategory.getName());
        ruleTemplateCategoryObj.setDescription(ruleTemplateCategory.getDescription());
        ruleTemplateCategoryGraphRepository.save(ruleTemplateCategoryObj);
        return ruleTemplateCategoryObj.printRuleTemp();

    }


    private List<RuleTemplateCategoryDTO> getJsonOfUpdatedTemplates(List<RuleTemplate> wtaBaseRuleTemplates, RuleTemplateCategory ruleTemplateCategory) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<RuleTemplateCategoryDTO> wtaBaseRuleTemplateDTOS = new ArrayList<>(wtaBaseRuleTemplates.size());
        wtaBaseRuleTemplates.forEach(wtaBaseRuleTemplate -> {
            RuleTemplateCategoryDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(wtaBaseRuleTemplate, RuleTemplateCategoryDTO.class);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategory);
            wtaBaseRuleTemplateDTOS.add(wtaBaseRuleTemplateDTO);
        });

        return wtaBaseRuleTemplateDTOS;
    }

    public Map<String, Object> changeCTARuleTemplateCategory(Long countryId, RuleTemplateDTO ruleTemplateDTO) {
        Map<String, Object> response = new HashMap();
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory();
        Country country = countryGraphRepository.findOne(countryId);
        List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();

        Optional<RuleTemplateCategory> countryRuleTemplateCategory = ruleTemplateCategories.parallelStream().filter(ruleTemplateCategory1 -> "CTA".equalsIgnoreCase(ruleTemplateCategory1.getRuleTemplateCategoryType() != null ? ruleTemplateCategory1.getRuleTemplateCategoryType().toString() : "")
                && ruleTemplateCategory1.getName().equalsIgnoreCase(ruleTemplateDTO.getCategoryName())).findFirst();

        if (!countryRuleTemplateCategory.isPresent() || (countryRuleTemplateCategory.isPresent() && countryRuleTemplateCategory.get().isDeleted() == true)) {
            ruleTemplateCategory.setName(ruleTemplateDTO.getCategoryName());
            ruleTemplateCategory.setDeleted(false);
            ruleTemplateCategory.setRuleTemplateCategoryType(CTA);
            country.addRuleTemplateCategory(ruleTemplateCategory);
            countryGraphRepository.save(country);
            ruleTemplateCategoryGraphRepository.updateCategoryOfCTARuleTemplate(ruleTemplateDTO.getRuleTemplateIds(), ruleTemplateCategory.getName());
            response.put("category", ruleTemplateCategory);
        } else {
            List<Long> ctaRuleTemplates = ruleTemplateCategoryGraphRepository.findAllExistingCTARuleTemplateByCategory(ruleTemplateDTO.getCategoryName(), countryId);
            List<Long> ruleTemplateIdsNeedToAddInCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(ctaRuleTemplates, ruleTemplateDTO.getRuleTemplateIds());
            List<Long> ruleTemplateIdsNeedToRemoveFromCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(ruleTemplateDTO.getRuleTemplateIds(), ctaRuleTemplates);
            ruleTemplateCategoryGraphRepository.updateCategoryOfCTARuleTemplate(ruleTemplateIdsNeedToAddInCategory, ruleTemplateDTO.getCategoryName());
            ruleTemplateCategoryGraphRepository.updateCategoryOfCTARuleTemplate(ruleTemplateIdsNeedToRemoveFromCategory, "NONE");
        }

        return response;
    }

    // creating default rule template category NONE
    public void createDefaultRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        ruleTemplateCategoryGraphRepository.save(ruleTemplateCategory);

    }

    public RuleTemplateCategory getCTARuleTemplateCategoryOfCountryByName(Long countryId, String name) {
        RuleTemplateCategory category = ruleTemplateCategoryGraphRepository
                .findByName(countryId, "NONE", RuleTemplateCategoryType.CTA);
        return category;
    }

    public Map<String, Object> createRuleTemplateCategory(long countryId, RuleTemplateCategoryCTADTO ruleTemplateCategoryDTO) {

        String name = "(?i)" + ruleTemplateCategoryDTO.getName();
        RuleTemplateCategory ruleTemplateCategory;
        ruleTemplateCategory = ruleTemplateCategoryGraphRepository.findByName(name, RuleTemplateCategoryType.CTA);
        Country country = countryService.getCountryById(countryId);
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = new RuleTemplateCategory(ruleTemplateCategoryDTO.getName(), RuleTemplateCategoryType.CTA);
            country.addRuleTemplateCategory(ruleTemplateCategory);
        }

        countryGraphRepository.save(country);
        ruleTemplateCategoryGraphRepository.updateCategoryOfCTARuleTemplate(ruleTemplateCategoryDTO.getRuleTemplateIds(), ruleTemplateCategory.getName());
        // TODO fix need with Front end harish Modi
        Map<String, Object> response = new HashMap();
        response.put("category", ruleTemplateCategory);
        return response;

    }

}
