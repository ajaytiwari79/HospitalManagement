package com.kairos.activity.service.wta;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.activity.persistence.repository.wta.RuleTemplateCategoryMongoRepository;
import com.kairos.activity.persistence.repository.wta.WTABaseRuleTemplateMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.response.dto.web.CountryDTO;
import com.kairos.response.dto.web.UpdateRuleTemplateCategoryDTO;

import com.kairos.response.dto.web.enums.RuleTemplateCategoryType;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.response.dto.web.enums.RuleTemplateCategoryType.CTA;


/**
 * Created by vipul on 2/8/17.
 */
@Transactional
@Service
public class RuleTemplateCategoryService extends MongoBaseService {
    @Inject
    private RuleTemplateCategoryMongoRepository ruleTemplateCategoryMongoRepository;

    @Inject
    WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private OrganizationRestClient organizationRestClient;

    private final Logger logger = LoggerFactory.getLogger(RuleTemplateCategoryService.class);

    /**
     * used to save a new Rule template in a country
     * Created by vipul on 2/8/17.
     * params countryId and rule template category via name and desc
     */
    //TODO need to modified this method
    public RuleTemplateCategoryDTO createRuleTemplateCategory(long countryId, RuleTemplateCategoryDTO ruleTemplateCategoryDTO) {

        RuleTemplateCategory templateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, ruleTemplateCategoryDTO.getName(), ruleTemplateCategoryDTO.getRuleTemplateCategoryType());

        if (templateCategory != null) {
            throw new DuplicateDataException("Can't create duplicate rule template category in same country " + ruleTemplateCategoryDTO.getName());
        }
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new ActionNotPermittedException("Country not exists " + countryId);
        }
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory();
        BeanUtils.copyProperties(ruleTemplateCategoryDTO, ruleTemplateCategory);
        ruleTemplateCategory.setCountryId(country.getId());
        save(ruleTemplateCategory);
        ruleTemplateCategoryDTO.setId(ruleTemplateCategory.getId());
        return ruleTemplateCategoryDTO;

    }

    public List<RuleTemplateCategory> getRulesTemplateCategory(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Country does not exist");
        }
        return ruleTemplateCategoryMongoRepository.getRuleTemplateCategoryByCountry(countryId, ruleTemplateCategoryType);

    }

    public boolean exists(BigInteger templateCategoryId) {
        return ruleTemplateCategoryMongoRepository.existsById(templateCategoryId);
    }


    public boolean deleteRuleTemplateCategory(long countryId, BigInteger templateCategoryId) {
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findOne(templateCategoryId);
        if (ruleTemplateCategory == null) {
            throw new DataNotFoundByIdException("RULE template ruleTemplateCategory does not exist" + templateCategoryId);
        }
        if (ruleTemplateCategory.getName() != null && ruleTemplateCategory.getName().equals("NONE")) {
            throw new ActionNotPermittedException("Can't delete none template category " + templateCategoryId);
        }
        RuleTemplateCategory noneRuleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        if(ruleTemplateCategory.getRuleTemplateIds()!=null && !ruleTemplateCategory.getRuleTemplateIds().isEmpty()) {
            Set<BigInteger> ruleTemplateIds = new HashSet<>(noneRuleTemplateCategory.getRuleTemplateIds());
            ruleTemplateIds.addAll(ruleTemplateCategory.getRuleTemplateIds());
            noneRuleTemplateCategory.setRuleTemplateIds(new ArrayList<>(ruleTemplateIds));
            save(noneRuleTemplateCategory);
        }
        ruleTemplateCategory.setDeleted(true);
        save(ruleTemplateCategory);
        return true;

    }

    public UpdateRuleTemplateCategoryDTO updateRuleTemplateCategory(Long countryId, BigInteger templateCategoryId, UpdateRuleTemplateCategoryDTO ruleTemplateCategory) {
        if (countryRestClient.getCountryById(countryId) == null) {
            throw new DataNotFoundByIdException("Country does not exist");
        }
        RuleTemplateCategory ruleTemplateCategoryObj = (RuleTemplateCategory) ruleTemplateCategoryMongoRepository.findOne(templateCategoryId);
        if (!Optional.ofNullable(ruleTemplateCategoryObj).isPresent()) {
            throw new DataNotFoundByIdException("Invalid category " + templateCategoryId);
        }
        if (ruleTemplateCategoryObj.getName().equals("NONE") || ruleTemplateCategory.getName().equals("NONE")) {
            throw new ActionNotPermittedException("Can't rename NONE template category " + templateCategoryId);
        }
        if (!ruleTemplateCategory.getName().trim().equalsIgnoreCase(ruleTemplateCategoryObj.getName())) {
            RuleTemplateCategory templateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, ruleTemplateCategory.getName(), RuleTemplateCategoryType.WTA);
            if (Optional.ofNullable(templateCategory).isPresent()) {
                throw new DuplicateDataException("ruleTemplateCategory name already  exists " + ruleTemplateCategory.getName());
            }
        }
        ruleTemplateCategoryObj.setName(ruleTemplateCategory.getName());
        ruleTemplateCategoryObj.setDescription(ruleTemplateCategory.getDescription());
        save(ruleTemplateCategoryObj);
        ruleTemplateCategory.setId(ruleTemplateCategoryObj.getId());
        return ruleTemplateCategory;

    }


    /*
  *
  * This method will change the category of rule Template when we change the rule template all existing rule templates wil set to none
   * and new rule temp wll be setted to  this new rule template category
  * */
    /*public Map<String, Object> updateRuleTemplateCategory(RuleTemplateDTO ruleTemplateDTO, long countryId) {
        Map<String, Object> response = new HashMap();
        if (ruleTemplateDTO.getRuleTemplateCategoryType().equals(CTA.name())) {
            response = changeCTARuleTemplateCategory(countryId, ruleTemplateDTO);
        } else {
            response = changeWTARuleTemplateCategory(countryId, ruleTemplateDTO);
        }
        return response;
    }*/

   /* private Map<String, Object> changeWTARuleTemplateCategory(Long countryId, RuleTemplateDTO ruleTemplateDTO) {
        Map<String, Object> response = new HashMap();
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWtaBaseRuleTemplateByIds(ruleTemplateDTO.getRuleTemplateIds());
        RuleTemplateCategory previousRuleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "(?i)" + ruleTemplateDTO.getCategoryName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(previousRuleTemplateCategory).isPresent()) {  // Rule Template Category does not exist So creating  a new one and adding in country
            previousRuleTemplateCategory = new RuleTemplateCategory(ruleTemplateDTO.getCategoryName());
            previousRuleTemplateCategory.setRuleTemplateCategoryType(RuleTemplateCategoryType.WTA);
            previousRuleTemplateCategory.setDeleted(false);
            CountryDTO country = countryRestClient.getCountryById(countryId);
            previousRuleTemplateCategory.setCountryId(country.getId());
            // Break Previous Relation
            wtaBaseRuleTemplateMongoRepository.deleteOldCategories(ruleTemplateDTO.getRuleTemplateIds());
            for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                wtaBaseRuleTemplate.setWTARuleTemplateCategory(previousRuleTemplateCategory.getId());
            }
            save(wtaBaseRuleTemplates);
            save(previousRuleTemplateCategory);
            response.put("category", previousRuleTemplateCategory);
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));

        } else {
            List<Long> previousBaseRuleTemplates = ruleTemplateCategoryMongoRepository.findAllExistingRuleTemplateAddedToThiscategory(ruleTemplateDTO.getCategoryName(), countryId);
            List<Long> newRuleTemplates = ruleTemplateDTO.getRuleTemplateIds();
            List<Long> ruleTemplateIdsNeedToAddInCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(previousBaseRuleTemplates, newRuleTemplates);
            List<Long> ruleTemplateIdsNeedToRemoveFromCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(newRuleTemplates, previousBaseRuleTemplates);
            ruleTemplateCategoryMongoRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToAddInCategory, ruleTemplateDTO.getCategoryName());
            ruleTemplateCategoryMongoRepository.updateCategoryOfRuleTemplate(ruleTemplateIdsNeedToRemoveFromCategory, "NONE");
            response.put("templateList", getJsonOfUpdatedTemplates(wtaBaseRuleTemplates, previousRuleTemplateCategory));
        }
        return response;
    }*/

    /*private List<WTARuleTemplateDTO> getJsonOfUpdatedTemplates(List<WTABaseRuleTemplate> wtaBaseRuleTemplates, RuleTemplateCategory ruleTemplateCategory) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<WTARuleTemplateDTO> wtaBaseRuleTemplateDTOS = new ArrayList<>(wtaBaseRuleTemplates.size());
        wtaBaseRuleTemplates.forEach(wtaBaseRuleTemplate -> {
            WTARuleTemplateDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(wtaBaseRuleTemplate, WTARuleTemplateDTO.class);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategory);
            wtaBaseRuleTemplateDTOS.add(wtaBaseRuleTemplateDTO);
        });

        return wtaBaseRuleTemplateDTOS;
    }*/

    /*public Map<String, Object> changeCTARuleTemplateCategory(Long countryId, RuleTemplateDTO ruleTemplateDTO) {
        Map<String, Object> response = new HashMap();
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory();
        CountryDTO country = countryRestClient.getCountryById(countryId);
        List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();

        Optional<RuleTemplateCategory> countryRuleTemplateCategory = ruleTemplateCategories.parallelStream().filter(ruleTemplateCategory1 -> "CTA".equalsIgnoreCase(ruleTemplateCategory1.getRuleTemplateCategoryType() !=null ? ruleTemplateCategory1.getRuleTemplateCategoryType().toString() : "")
                && ruleTemplateCategory1.getName().equalsIgnoreCase(ruleTemplateDTO.getCategoryName())).findFirst();

        if (!countryRuleTemplateCategory.isPresent() || (countryRuleTemplateCategory.isPresent() && countryRuleTemplateCategory.get().isDeleted()==true)) {
            ruleTemplateCategory.setName(ruleTemplateDTO.getCategoryName());
            ruleTemplateCategory.setDeleted(false);
            ruleTemplateCategory.setRuleTemplateCategoryType(CTA);
            country.addRuleTemplateCategory(ruleTemplateCategory);
            save(country);
            ruleTemplateCategoryMongoRepository.updateCategoryOfCTARuleTemplate(ruleTemplateDTO.getRuleTemplateIds(), ruleTemplateCategory.getName());
            response.put("category", ruleTemplateCategory);
        } else {
            List<Long> ctaRuleTemplates = ruleTemplateCategoryMongoRepository.findAllExistingCTARuleTemplateByCategory(ruleTemplateDTO.getCategoryName(), countryId);
            List<Long> ruleTemplateIdsNeedToAddInCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(ctaRuleTemplates, ruleTemplateDTO.getRuleTemplateIds());
            List<Long> ruleTemplateIdsNeedToRemoveFromCategory = ArrayUtil.getUniqueElementWhichIsNotInFirst(ruleTemplateDTO.getRuleTemplateIds(), ctaRuleTemplates);
            ruleTemplateCategoryMongoRepository.updateCategoryOfCTARuleTemplate(ruleTemplateIdsNeedToAddInCategory, ruleTemplateDTO.getCategoryName());
            ruleTemplateCategoryMongoRepository.updateCategoryOfCTARuleTemplate(ruleTemplateIdsNeedToRemoveFromCategory, "NONE");
        }

        return response;
    }*/


    /* public RuleTemplateWrapper getRulesTemplateCategoryByUnit(Long unitId) {
         OrganizationDTO organization = organizationRestClient.getOrganization(unitId);
         if (!Optional.ofNullable(organization).isPresent()) {
             throw new DataNotFoundByIdException("Organization does not exist");
         }
         List<RuleTemplateCategoryTagDTO> categoryList = ruleTemplateCategoryMongoRepository.getRuleTemplateCategoryByUnitId(unitId);
         List<RuleTemplateResponseDTO> templateList = wtaBaseRuleTemplateMongoRepository.getWTABaseRuleTemplateByUnitId(unitId);
         RuleTemplateWrapper ruleTemplateWrapper = new RuleTemplateWrapper();
         ruleTemplateWrapper.setCategoryList(categoryList);
         ruleTemplateWrapper.setTemplateList(templateList);

         return ruleTemplateWrapper;

     }*/
    // creating default rule template category NONE
    public void createDefaultRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        save(ruleTemplateCategory);

    }

    public RuleTemplateCategory getCTARuleTemplateCategoryOfCountryByName(Long countryId, String name) {
        RuleTemplateCategory category = ruleTemplateCategoryMongoRepository
                .findByName(countryId, "NONE", CTA);
        return category;
    }

}
