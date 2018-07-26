package com.kairos.service.wta;

import com.kairos.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.activity.wta.rule_template_category.RuleTemplateAndCategoryDTO;
import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryDTO;
import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryTagDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.WTABuilderService;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.CountryRestClient;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.country.basic_details.CountryDTO;
import com.kairos.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.enums.RuleTemplateCategoryType.CTA;
import static com.kairos.enums.RuleTemplateCategoryType.WTA;


/**
 * Created by vipul on 2/8/17.
 */
@Transactional
@Service
public class RuleTemplateCategoryService extends MongoBaseService {
    @Inject
    private RuleTemplateCategoryRepository ruleTemplateCategoryMongoRepository;
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
    public RuleTemplateAndCategoryDTO updateRuleTemplateCategory(Long countryId, BigInteger templateCategoryId, RuleTemplateCategoryDTO ruleTemplateCategoryDTO, RuleTemplateCategory ruleTemplateCategoryObj) {
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



    public void assignCategoryToRuleTemplate(List<RuleTemplateCategoryTagDTO> categoryList, List<WTABaseRuleTemplateDTO> templateList){
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
