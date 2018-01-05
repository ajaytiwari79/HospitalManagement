package com.kairos.service.agreement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategoryTagDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.RuleTemplateResponseDTO;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.AddRuleTemplateCategoryDTO;
import com.kairos.response.dto.web.RuleTemplateDTO;
import com.kairos.response.dto.web.aggrements.RuleTemplateWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.CountryService;
import com.kairos.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType.CTA;

/**
 * Created by vipul on 2/8/17.
 */
@Service
public class RuleTemplateCategoryService extends UserBaseService {
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    @Inject
    private CountryService countryService;

    @Inject
    WTABaseRuleTemplateGraphRepository wtaBaseRuleTemplateGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaRuleTemplateGraphRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;
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
            throw new DuplicateDataException("Can't create duplicate rule template category in same country " + name);
        }

        Country country = countryService.getCountryById(countryId);
        country.addRuleTemplateCategory(ruleTemplateCategory);
        save(country);
        return ruleTemplateCategory;


    }

    public List<RuleTemplateCategory> getRulesTemplateCategory(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType) {
        Country country = countryService.getCountryById(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Country does not exist");
        }
        return ruleTemplateCategoryGraphRepository.getRuleTemplateCategoryByCountry(countryId, ruleTemplateCategoryType);

    }

    public boolean exists(long templateCategoryId) {
        return ruleTemplateCategoryGraphRepository.existsById(templateCategoryId);
    }


    public boolean deleteRuleTemplateCategory(long countryId, long templateCategoryId) {
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryGraphRepository.findOne(templateCategoryId);
        if (ruleTemplateCategory == null) {
            throw new DataNotFoundByIdException("RULE template ruleTemplateCategory does not exist" + templateCategoryId);
        }
        if (ruleTemplateCategory.getName() != null && ruleTemplateCategory.getName().equals("NONE")) {
            throw new ActionNotPermittedException("Can't delete none template category " + templateCategoryId);
        }
        if (ruleTemplateCategory.getRuleTemplateCategoryType().equals(CTA)) {
            List<Long> ctaRuleTemplates = ruleTemplateCategoryGraphRepository.findAllExistingCTARuleTemplateByCategory(ruleTemplateCategory.getName(), countryId);
            RuleTemplateCategory noneRuleTemplateCategory = ruleTemplateCategoryGraphRepository.findByName(countryId, "NONE", CTA);
            ruleTemplateCategoryGraphRepository.deleteRelationOfRuleTemplateCategoryAndCTA(templateCategoryId, ctaRuleTemplates);
            ruleTemplateCategoryGraphRepository.setAllCTAWithCategoryNone(noneRuleTemplateCategory.getId(), ctaRuleTemplates);
        } else {
            List<Long> wtaBaseRuleTemplateList = wtaBaseRuleTemplateGraphRepository.findAllWTABelongsByTemplateCategoryId(templateCategoryId);
            RuleTemplateCategory noneRuleTemplateCategory = ruleTemplateCategoryGraphRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
            wtaBaseRuleTemplateGraphRepository.deleteRelationOfRuleTemplateCategoryAndWTA(templateCategoryId, wtaBaseRuleTemplateList);
            wtaBaseRuleTemplateGraphRepository.setAllWTAWithCategoryNone(noneRuleTemplateCategory.getId(), wtaBaseRuleTemplateList);
        }
        return true;

    }


    public Map<String, Object> updateRuleTemplateCategory(Long countryId, Long templateCategoryId, AddRuleTemplateCategoryDTO ruleTemplateCategory) {
        if (countryService.getCountryById(countryId) == null) {
            throw new DataNotFoundByIdException("Country does not exist");
        }


        RuleTemplateCategory ruleTemplateCategoryObj = (RuleTemplateCategory) ruleTemplateCategoryGraphRepository.findOne(templateCategoryId);
        if (ruleTemplateCategoryObj.getName() == ruleTemplateCategory.getName()) {
            throw new DuplicateDataException("Can't update, rule template ruleTemplateCategory name already in country");
        }
        if (ruleTemplateCategoryObj.getName().equals("NONE")) {
            throw new ActionNotPermittedException("Can't rename NONE template category " + templateCategoryId);
        }
        ruleTemplateCategoryObj.setName(ruleTemplateCategory.getName());
        ruleTemplateCategoryObj.setDescription(ruleTemplateCategory.getDescription());
        save(ruleTemplateCategoryObj);
        return ruleTemplateCategoryObj.printRuleTemp();

    }

    public void setRuleTemplatecategoryWithRuleTemplate(Long templateCategoryId, Long ruleTemplateId) {
        ruleTemplateCategoryGraphRepository.setRuleTemplateCategoryWithRuleTemplate(templateCategoryId, ruleTemplateId);

    }

    /*
  *
  * This method will change the category of rule Template when we change the rule template all existing rule templates wil set to none
   * and new rule temp wll be setted to  this new rule template category
  * */
    public Map<String, Object> updateRuleTemplateCategory(RuleTemplateDTO ruleTemplateDTO, long countryId) {
        Map<String, Object> response = new HashMap();
        if (ruleTemplateDTO.getRuleTemplateCategoryType().equals("CTA")) {
            response = changeCTARuleTemplateCategory(countryId, ruleTemplateDTO);
        } else {
            response = changeWTARuleTemplateCategory(countryId, ruleTemplateDTO);
        }
        return response;
    }

    private Map<String, Object> changeWTARuleTemplateCategory(Long countryId, RuleTemplateDTO ruleTemplateDTO) {
        Map<String, Object> response = new HashMap();
        List<RuleTemplate> wtaBaseRuleTemplates = wtaRuleTemplateGraphRepository.getWtaBaseRuleTemplateByIds(ruleTemplateDTO.getRuleTemplateIds());
        RuleTemplateCategory previousRuleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, "(?i)" + ruleTemplateDTO.getCategoryName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(previousRuleTemplateCategory).isPresent()) {  // Rule Template Category does not exist So creating  a new one and adding in country
            previousRuleTemplateCategory = new RuleTemplateCategory(ruleTemplateDTO.getCategoryName());
            previousRuleTemplateCategory.setRuleTemplateCategoryType(RuleTemplateCategoryType.WTA);
            previousRuleTemplateCategory.setDeleted(false);
            Country country = countryGraphRepository.findOne(countryId);
            List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
            ruleTemplateCategories.add(previousRuleTemplateCategory);
            country.setRuleTemplateCategories(ruleTemplateCategories);
            countryGraphRepository.save(country);
            // Break Previous Relation
            wtaRuleTemplateGraphRepository.deleteOldCategories(ruleTemplateDTO.getRuleTemplateIds());
            previousRuleTemplateCategory.setRuleTemplates(wtaBaseRuleTemplates);
            save(previousRuleTemplateCategory);
            response.put("category", previousRuleTemplateCategory);
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));

        } else {
            List<Long> previousBaseRuleTemplates = ruleTemplateCategoryRepository.findAllExistingRuleTemplateAddedToThiscategory(ruleTemplateDTO.getCategoryName(), countryId);
            List<Long> newRuleTemplates = ruleTemplateDTO.getRuleTemplateIds();
            List<Long> ruleTemplateIdsNeedToAddInCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(previousBaseRuleTemplates, newRuleTemplates);
            List<Long> ruleTemplateIdsNeedToRemoveFromCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(newRuleTemplates, previousBaseRuleTemplates);
            ruleTemplateCategoryRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToAddInCategory, ruleTemplateDTO.getCategoryName());
            ruleTemplateCategoryRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToRemoveFromCategory, "NONE");
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));
        }
        return response;
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
        Optional<RuleTemplateCategory> countryRuleTemplateCategory = ruleTemplateCategories.parallelStream().filter(ruleTemplateCategory1 -> "CTA".equalsIgnoreCase(ruleTemplateCategory1.getRuleTemplateCategoryType().toString())
                && ruleTemplateCategory1.getName().equalsIgnoreCase(ruleTemplateDTO.getCategoryName())).findFirst();

        if (!countryRuleTemplateCategory.isPresent()) {
            ruleTemplateCategory.setName(ruleTemplateDTO.getCategoryName());
            ruleTemplateCategory.setDeleted(false);
            ruleTemplateCategory.setRuleTemplateCategoryType(CTA);
            country.addRuleTemplateCategory(ruleTemplateCategory);
            save(country);
            ruleTemplateCategoryGraphRepository.updateCategoryOfCTARuleTemplate(ruleTemplateDTO.getRuleTemplateIds(), ruleTemplateCategory.getName());
            response.put("category", ruleTemplateCategory);
        } else {
            List<Long> ctaRuleTemplates = ruleTemplateCategoryGraphRepository.findAllExistingCTARuleTemplateByCategory(ruleTemplateCategory.getName(), countryId);
            List<Long> ruleTemplateIdsNeedToAddInCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(ctaRuleTemplates, ruleTemplateDTO.getRuleTemplateIds());
            List<Long> ruleTemplateIdsNeedToRemoveFromCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(ruleTemplateDTO.getRuleTemplateIds(), ctaRuleTemplates);
            ruleTemplateCategoryGraphRepository.updateCategoryOfCTARuleTemplate(ruleTemplateIdsNeedToAddInCategory, ruleTemplateDTO.getCategoryName());
            ruleTemplateCategoryGraphRepository.updateCategoryOfCTARuleTemplate(ruleTemplateIdsNeedToRemoveFromCategory, "NONE");
        }

        return response;
    }


    public RuleTemplateWrapper getRulesTemplateCategoryByUnit(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Organization does not exist");
        }
        List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryRepository.getRuleTemplateCategoryByUnitId(unitId);
        List<RuleTemplateResponseDTO> templateList = wtaBaseRuleTemplateGraphRepository.getWTABaseRuleTemplateByUnitId(unitId);
        RuleTemplateWrapper ruleTemplateWrapper = new RuleTemplateWrapper();
        ruleTemplateWrapper.setCategoryList(categoryList);
        ruleTemplateWrapper.setTemplateList(templateList);

        return ruleTemplateWrapper;

    }

}
