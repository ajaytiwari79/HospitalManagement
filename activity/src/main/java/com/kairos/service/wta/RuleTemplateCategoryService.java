package com.kairos.service.wta;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.wta.templates.RuleTemplateCategory;
import com.kairos.activity.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.wta.templates.WTABuilderService;
import com.kairos.persistence.repository.wta.RuleTemplateCategoryMongoRepository;
import com.kairos.persistence.repository.wta.WTABaseRuleTemplateMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.persistence.model.country.CountryDTO;

import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.activity.wta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.enums.RuleTemplateCategoryType.CTA;
import static com.kairos.enums.RuleTemplateCategoryType.WTA;


/**
 * Created by vipul on 2/8/17.
 */
@Transactional
@Service
public class RuleTemplateCategoryService extends MongoBaseService {
    @Inject
    private RuleTemplateCategoryMongoRepository ruleTemplateCategoryMongoRepository;
    @Inject private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Autowired
    private ExceptionService excpExceptionService;

    private final Logger logger = LoggerFactory.getLogger(RuleTemplateCategoryService.class);

    /**
     * used to save a new Rule template in a country
     * Created by vipul on 2/8/17.
     * params countryId and rule template category via name and desc
     */
    //TODO need to modified this method
    public RuleTemplateAndCategoryDTO createRuleTemplateCategory(long countryId, RuleTemplateCategoryDTO ruleTemplateCategoryDTO) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            excpExceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }

        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, ruleTemplateCategoryDTO.getName(), ruleTemplateCategoryDTO.getRuleTemplateCategoryType());
        RuleTemplateAndCategoryDTO ruleTemplateAndCategoryDTO = null;
        if(ruleTemplateCategory==null){
            ruleTemplateCategory = new RuleTemplateCategory();
            ObjectMapperUtils.copyProperties(ruleTemplateCategoryDTO, ruleTemplateCategory);
            ruleTemplateCategory.setCountryId(country.getId());
            save(ruleTemplateCategory);
            List<WTABaseRuleTemplate> wtaBaseRuleTemplates = (List<WTABaseRuleTemplate>)wtaBaseRuleTemplateMongoRepository.findAllById(ruleTemplateCategoryDTO.getRuleTemplateIds());
            for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                wtaBaseRuleTemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
            }
            save(wtaBaseRuleTemplates);
            ruleTemplateCategoryDTO.setId(ruleTemplateCategory.getId());
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = WTABuilderService.copyRuleTemplatesToDTO(wtaBaseRuleTemplates);
            wtaBaseRuleTemplateDTOS.forEach(wtaBaseRuleTemplateDTO -> {wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategoryDTO);});
            ruleTemplateAndCategoryDTO = new RuleTemplateAndCategoryDTO(ruleTemplateCategoryDTO,wtaBaseRuleTemplateDTOS );
        }else{
            ruleTemplateAndCategoryDTO = updateRuleTemplateCategory(countryId,null,ruleTemplateCategoryDTO,ruleTemplateCategory);
        }
        return ruleTemplateAndCategoryDTO;

    }

    public List<RuleTemplateCategory> getRulesTemplateCategory(long countryId, RuleTemplateCategoryType ruleTemplateCategoryType) {
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (country == null) {
            excpExceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }
        return ruleTemplateCategoryMongoRepository.getRuleTemplateCategoryByCountry(countryId, ruleTemplateCategoryType);

    }

    public boolean exists(BigInteger templateCategoryId) {
        return ruleTemplateCategoryMongoRepository.existsById(templateCategoryId);
    }


    public boolean deleteRuleTemplateCategory(long countryId, BigInteger templateCategoryId) {
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryMongoRepository.findOne(templateCategoryId);
        if (ruleTemplateCategory == null) {
            excpExceptionService.dataNotFoundByIdException("message.ruletemplatecategory.id",templateCategoryId);
        }

        if (ruleTemplateCategory.getName() != null && ruleTemplateCategory.getName().equals("NONE")) {
            excpExceptionService.actionNotPermittedException("message.ruletemplatecategory.delete",templateCategoryId);
        }
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.findAllByCategoryId(templateCategoryId);
        RuleTemplateCategory noneRuleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "NONE", RuleTemplateCategoryType.WTA);
        wtaBaseRuleTemplates.forEach(rt->{
            rt.setRuleTemplateCategoryId(noneRuleTemplateCategory.getId());
        });
        if (!wtaBaseRuleTemplates.isEmpty()){
            save(wtaBaseRuleTemplates);
        }
        /*if(ruleTemplateCategory.getRuleTemplateIds()!=null && !ruleTemplateCategory.getRuleTemplateIds().isEmpty()) {
            Set<BigInteger> ruleTemplateIds = new HashSet<>(noneRuleTemplateCategory.getRuleTemplateIds());
            ruleTemplateIds.addAll(ruleTemplateCategory.getRuleTemplateIds());
            noneRuleTemplateCategory.setRuleTemplateIds(new ArrayList<>(ruleTemplateIds));
            save(noneRuleTemplateCategory);
        }*/
        ruleTemplateCategory.setDeleted(true);
        save(ruleTemplateCategory);
        return true;

    }


    //Create and Update method should be different
    public RuleTemplateAndCategoryDTO updateRuleTemplateCategory(Long countryId, BigInteger templateCategoryId, RuleTemplateCategoryDTO ruleTemplateCategoryDTO,RuleTemplateCategory ruleTemplateCategoryObj) {
        if (ruleTemplateCategoryObj==null){
            ruleTemplateCategoryObj = (RuleTemplateCategory) ruleTemplateCategoryMongoRepository.findById(templateCategoryId).get();
        }
        if (!Optional.ofNullable(ruleTemplateCategoryObj).isPresent()) {
            excpExceptionService.dataNotFoundByIdException("message.ruletemplatecategory.name.notfound",ruleTemplateCategoryDTO.getName());
        }

        if (!ruleTemplateCategoryDTO.getName().trim().equalsIgnoreCase(ruleTemplateCategoryObj.getName())) {
            RuleTemplateCategory templateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, ruleTemplateCategoryDTO.getName(), RuleTemplateCategoryType.WTA);
            if (Optional.ofNullable(templateCategory).isPresent()) {
                excpExceptionService.duplicateDataException("message.ruletemplatecategory.name.alreadyexist",ruleTemplateCategoryDTO.getName());
            }
        }
        //ObjectMapperUtils.copyProperties(ruleTemplateCategoryDTO,ruleTemplateCategoryObj);
        if (ruleTemplateCategoryObj.getName().equals("NONE") || ruleTemplateCategoryDTO.getName().equals("NONE")) {
            excpExceptionService.actionNotPermittedException("message.ruletemplatecategory.name.update");
        }
        ruleTemplateCategoryObj.setName(ruleTemplateCategoryDTO.getName());
        ruleTemplateCategoryObj.setDescription(ruleTemplateCategoryDTO.getDescription());
        ruleTemplateCategoryObj.setCountryId(countryId);
        RuleTemplateAndCategoryDTO ruleTemplateAndCategoryDTO = new RuleTemplateAndCategoryDTO();
        save(ruleTemplateCategoryObj);
        if(ruleTemplateCategoryDTO.getRuleTemplateIds()!=null && !ruleTemplateCategoryDTO.getRuleTemplateIds().isEmpty()){
            RuleTemplateCategory defaultCategory = ruleTemplateCategoryMongoRepository
                    .findByName(countryId, "NONE", WTA);
            List<WTABaseRuleTemplate> wtaBaseRuleTemplates = (List<WTABaseRuleTemplate>)wtaBaseRuleTemplateMongoRepository.findAllByCategoryId(ruleTemplateCategoryObj.getId());
            wtaBaseRuleTemplates.forEach(wtaBaseRuleTemplate -> {
                if(!ruleTemplateCategoryDTO.getRuleTemplateIds().contains(wtaBaseRuleTemplate.getId())){
                    wtaBaseRuleTemplate.setRuleTemplateCategoryId(defaultCategory.getId());
                }
            });
            if (!wtaBaseRuleTemplates.isEmpty()){
                save(wtaBaseRuleTemplates);
            }
            wtaBaseRuleTemplates = (List<WTABaseRuleTemplate>)wtaBaseRuleTemplateMongoRepository.findAllById(ruleTemplateCategoryDTO.getRuleTemplateIds());
            for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                wtaBaseRuleTemplate.setRuleTemplateCategoryId(ruleTemplateCategoryObj.getId());
            }
            save(wtaBaseRuleTemplates);
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = WTABuilderService.copyRuleTemplatesToDTO(wtaBaseRuleTemplates);
            wtaBaseRuleTemplateDTOS.forEach(wtaBaseRuleTemplateDTO -> {wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategoryDTO);});
            ruleTemplateAndCategoryDTO.setTemplateList(wtaBaseRuleTemplateDTOS);
        }
        ruleTemplateCategoryDTO.setId(ruleTemplateCategoryObj.getId());
        //ruleTemplateAndCategoryDTO.setCategory(ruleTemplateCategoryDTO);
        return ruleTemplateAndCategoryDTO;

    }

    public RuleTemplateCategoryDTO updateRuleTemplateCategory(Long countryId, BigInteger templateCategoryId,RuleTemplateCategoryDTO ruleTemplateCategory){
        CountryDTO country = countryRestClient.getCountryById(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            excpExceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }
        RuleTemplateCategory ruleTemplateCategoryObj = (RuleTemplateCategory) ruleTemplateCategoryMongoRepository.findById(templateCategoryId).get();
        ruleTemplateCategoryObj.setName(ruleTemplateCategory.getName());
        ruleTemplateCategoryObj.setDescription(ruleTemplateCategory.getDescription());
        save(ruleTemplateCategoryObj);
        ruleTemplateCategory.setId(ruleTemplateCategoryObj.getId());
        return ruleTemplateCategory;
    }



    public void assignCategoryToRuleTemplate(List<RuleTemplateCategoryTagDTO> categoryList,List<WTABaseRuleTemplateDTO> templateList){
        for (RuleTemplateCategoryTagDTO ruleTemplateCategoryTagDTO : categoryList) {
            for (WTABaseRuleTemplateDTO ruleTemplateResponseDTO : templateList) {
                if(ruleTemplateCategoryTagDTO.getId().equals(ruleTemplateResponseDTO.getRuleTemplateCategoryId())){
                    RuleTemplateCategoryDTO ruleTemplateCategoryDTO = new RuleTemplateCategoryDTO();
                    BeanUtils.copyProperties(ruleTemplateCategoryTagDTO,ruleTemplateCategoryDTO);
                    ruleTemplateResponseDTO.setRuleTemplateCategory(ruleTemplateCategoryDTO);
                    //ruleTemplateResponseDTO.setTemplateType("MAXIMUM_SHIFT_LENGTH");
                }
            }
        }
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
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.getWtaBaseRuleTemplateByIds(ruleTemplateDTO.getRuleTemplateIds());
        RuleTemplateCategory previousRuleTemplateCategory = ruleTemplateCategoryMongoRepository.findByName(countryId, "(?i)" + ruleTemplateDTO.getCategoryName(), RuleTemplateCategoryType.WTA);
        if (!Optional.ofNullable(previousRuleTemplateCategory).isPresent()) {  // Rule Template Category does not exist So creating  a new one and adding in country
            previousRuleTemplateCategory = new RuleTemplateCategory(ruleTemplateDTO.getCategoryName());
            previousRuleTemplateCategory.setRuleTemplateCategoryType(RuleTemplateCategoryType.WTA);
            previousRuleTemplateCategory.setDeleted(false);
            CountryDTO country = countryRestClient.getCountryById(countryId);
            previousRuleTemplateCategory.setCountryId(country.getId());
            // Break Previous Relation
            wtaBaseRuleTemplateMongoRepository.deleteOldCategories(ruleTemplateDTO.getRuleTemplateIds());
            for (WTABaseRuleTemplateDTO wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                wtaBaseRuleTemplate.setRuleTemplateCategoryId(previousRuleTemplateCategory.getId());
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

    /*private List<WTARuleTemplateDTO> getJsonOfUpdatedTemplates(List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates, RuleTemplateCategory ruleTemplateCategory) {

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
