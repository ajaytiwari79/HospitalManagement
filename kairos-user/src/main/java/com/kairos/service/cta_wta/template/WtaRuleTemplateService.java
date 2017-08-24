package com.kairos.service.cta_wta.template;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.user.agreement.wta.templates.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.cta_wta.template.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.cta_wta.template.TemplateCategoryRelationGraphRepository;
import com.kairos.persistence.repository.user.cta_wta.template.WTABaseRuleTemplateGraphRepository;
import com.kairos.response.dto.web.WTARuleTemplateDTO;
import com.kairos.response.dto.web.WtaRuleTemplateDTO;
import com.kairos.service.UserBaseService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.AppConstants.*;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@Service
public class WtaRuleTemplateService extends UserBaseService {


    List<String> ruleTemplate = new ArrayList<String>();
    /* Stream.of(RuleTemplate.values())
            .map(RuleTemplate::name)
            .collect(Collectors.toList());*/
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaRuleTemplateGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;
    @Inject
    TemplateCategoryRelationGraphRepository templateRelationShipGraphRepository;

    public boolean createRuleTemplate(long countryId) {

        List<WTABaseRuleTemplate> baseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        Country country = countryGraphRepository.findOne(countryId);

        if (country == null) {
            throw new DataNotFoundByIdException("Invalid organisation");
        }


        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory("NONE");
        ruleTemplateCategoryService.createRuleTemplate(countryId, ruleTemplateCategory);
        ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId, "NONE");


        WtaTemplate1 wta1 = new WtaTemplate1(TEMPLATE1_NAME, TEMPLATE1, true, TEMPLATE1_DESCRIPTION, "", ruleTemplate, true);
        baseRuleTemplates.add(wta1);

        WtaTemplate2 wta2 = new WtaTemplate2(TEMPLATE2_NAME, TEMPLATE2, true, TEMPLATE2_DESCRIPTION, "", ruleTemplate, true);
        baseRuleTemplates.add(wta2);

        WtaTemplate3 wta3 = new WtaTemplate3(TEMPLATE3_NAME, TEMPLATE3, true, TEMPLATE3_DESCRIPTION, ruleTemplate, true, 6);
        baseRuleTemplates.add(wta3);

        WtaTemplate4 wta4 = new WtaTemplate4(TEMPLATE4_NAME, TEMPLATE4, true, TEMPLATE4_DESCRIPTION, "40:00", 7);
        baseRuleTemplates.add(wta4);

        WtaTemplate5 wta5 = new WtaTemplate5(TEMPLATE5_NAME, TEMPLATE5, true, TEMPLATE5_DESCRIPTION, "40:00", ruleTemplate, true);
        baseRuleTemplates.add(wta5);


        WtaTemplate6 wta6 = new WtaTemplate6(TEMPLATE16_NAME, TEMPLATE6, true, TEMPLATE6_DESCRIPTION, 1);
        baseRuleTemplates.add(wta6);

        WtaTemplate7 wta7 = new WtaTemplate7(TEMPLATE7_NAME, TEMPLATE7, true, TEMPLATE7_DESCRIPTION, ruleTemplate, true, 4);
        baseRuleTemplates.add(wta7);

        WtaTemplate8 wta8 = new WtaTemplate8(TEMPLATE8_NAME, TEMPLATE8, true, TEMPLATE7_DESCRIPTION, ruleTemplate, "7", 4);
        baseRuleTemplates.add(wta8);

        WtaTemplate9 wta9 = new WtaTemplate9(TEMPLATE9_NAME, TEMPLATE9, true, TEMPLATE9_DESCRIPTION, ruleTemplate, 12, 12, 12);
        baseRuleTemplates.add(wta9);

        WtaTemplate10 wta10 = new WtaTemplate10(TEMPLATE10_NAME, TEMPLATE10, true, TEMPLATE10_DESCRIPTION, ruleTemplate, 12, 12, 12);
        baseRuleTemplates.add(wta10);

        WtaTemplate11 wta11 = new WtaTemplate11(TEMPLATE11_NAME, TEMPLATE11, true, TEMPLATE11_DESCRIPTION, ruleTemplate, 12, 12, 12, true, true, "");
        baseRuleTemplates.add(wta11);

        WtaTemplate12 wta12 = new WtaTemplate12(TEMPLATE12_NAME, TEMPLATE12, true, TEMPLATE12_DESCRIPTION, 2);
        baseRuleTemplates.add(wta12);

        WtaTemplate13 wta13 = new WtaTemplate13(TEMPLATE13_NAME, TEMPLATE13, true, TEMPLATE13_DESCRIPTION, 12, 12, "NA", 2, 2);
        baseRuleTemplates.add(wta13);

        WtaTemplate14 wta14 = new WtaTemplate14(TEMPLATE14_NAME, TEMPLATE14, true, TEMPLATE14_DESCRIPTION, 2, 2);
        baseRuleTemplates.add(wta14);

        WtaTemplate15 wta15 = new WtaTemplate15(TEMPLATE15_NAME, TEMPLATE15, true, TEMPLATE15_DESCRIPTION, "");
        baseRuleTemplates.add(wta15);

        WtaTemplate16 wta16 = new WtaTemplate16(TEMPLATE16_NAME, TEMPLATE16, true, TEMPLATE16_DESCRIPTION, ruleTemplate, "");
        baseRuleTemplates.add(wta16);

        WtaTemplate17 wta17 = new WtaTemplate17(TEMPLATE17_NAME, TEMPLATE17, true, TEMPLATE17_DESCRIPTION, "");
        baseRuleTemplates.add(wta17);

        WtaTemplate18 wta18 = new WtaTemplate18(TEMPLATE18_NAME, TEMPLATE18, true, TEMPLATE18_DESCRIPTION, ruleTemplate, 1, "NA", 1, "", "", null);
        baseRuleTemplates.add(wta18);

        WtaTemplate19 wta19 = new WtaTemplate19(TEMPLATE19_NAME, TEMPLATE19, true, TEMPLATE19_DESCRIPTION, ruleTemplate, 1, "NA", 1, 1, true);
        baseRuleTemplates.add(wta19);

        WtaTemplate20 wta20 = new WtaTemplate20(TEMPLATE20_NAME, TEMPLATE20, true, TEMPLATE20_DESCRIPTION, 1, "NA", 1, 1, null);
        baseRuleTemplates.add(wta20);
        country.setWTABaseRuleTemplate(baseRuleTemplates);
        save(country);

        ruleTemplateCategory.setWtaBaseRuleTemplates(baseRuleTemplates);
        ruleTemplateCategoryRepository.save(ruleTemplateCategory);
        /*for (WTABaseRuleTemplate template : country.getWTABaseRuleTemplate()) {
            template.setRuleTemplateCategory(ruleTemplateCategory);
            //wtaRuleTemplateGraphRepository.findOne(template.getId());
            //TemplateCategoryRelation rel = new TemplateCategoryRelation(template, ruleTemplateCategory);
            //save(rel);
        }*/

        //wtaRuleTemplateGraphRepository.addCategoryInAllTemplate(ruleTemplateIdList, ruleTemplateCategory.getId());

        return true;
    }

    public Map getRuleTemplate(long countryId) {

        List<WTABaseRuleTemplate> baseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        List<WTABaseRuleTemplateDTO> wtaResponse
                = countryGraphRepository.getRuleTemplatesAndCategories(countryId);

        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        List<RuleTemplateCategory> categoryList = country.getRuleTemplateCategories();

        if (categoryList == null) {
            throw new DataNotFoundByIdException("Category List is null");
        }

        List<WTABaseRuleTemplate> templateList = country.getWTABaseRuleTemplate();
        if (templateList == null) {
            throw new DataNotFoundByIdException("Template List is null");
        }


        Map response = new HashMap();
        response.put("categoryList", categoryList);
        response.put("templateList", wtaResponse);

        return response;
    }

    public WTABaseRuleTemplateDTO updateRuleTemplate(long countryId, String templateType, WTARuleTemplateDTO templateDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country");
        }

        WTABaseRuleTemplate oldTemplate = getTemplateByType(countryId, templateType);
        if (oldTemplate == null) {
            throw new DataNotFoundByIdException("Invalid TemplateType");
        }

        wtaRuleTemplateGraphRepository.deleteCategoryFromTemplate(oldTemplate.getId());

        RuleTemplateCategory templateCategory = ruleTemplateCategoryRepository.findByName(countryId, templateDTO.getCategory());

        if (templateCategory == null) {
            templateCategory = new RuleTemplateCategory(templateDTO.getCategory(), "", false);
            country.getRuleTemplateCategories().add(templateCategory);
            save(country);
            ruleTemplateCategoryRepository.findByName(countryId, templateDTO.getCategory());
        }

        switch (templateType) {

            case TEMPLATE1:

                WtaTemplate1 template1 = (WtaTemplate1) getTemplateByType(countryId, templateType);
                template1.setDescription(templateDTO.getDescription());
                //template1.setRuleTemplateCategory(templateCategory);

                template1.setTime(templateDTO.getTime());
                template1.setBalanceType(templateDTO.getBalanceType());
                template1.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(template1);

                break;

            case TEMPLATE2:
                WtaTemplate2 template2 = (WtaTemplate2) getTemplateByType(countryId, templateType);

                template2.setDescription(templateDTO.getDescription());
                //template2.setRuleTemplateCategory(templateCategory);
                template2.setTime(templateDTO.getTime());
                template2.setBalanceType(templateDTO.getBalanceType());
                template2.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(template2);
                break;

            case TEMPLATE3:
                WtaTemplate3 template3 = (WtaTemplate3) getTemplateByType(countryId, templateType);

                template3.setDescription(templateDTO.getDescription());
                //template3.setRuleTemplateCategory(templateCategory);
                template3.setDays(templateDTO.getDays());
                template3.setBalanceType(templateDTO.getBalanceType());
                template3.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(template3);
                break;

            case TEMPLATE4:
                WtaTemplate4 template4 = (WtaTemplate4) getTemplateByType(countryId, templateType);

                template4.setDescription(templateDTO.getDescription());
                //template4.setRuleTemplateCategory(templateCategory);
                template4.setMinimumRest(templateDTO.getMinimumRest());
                template4.setDaysWorked(templateDTO.getDaysWorked());
                oldTemplate = save(template4);
                break;

            case TEMPLATE5:
                WtaTemplate5 template5 = (WtaTemplate5) getTemplateByType(countryId, templateType);

                template5.setDescription(templateDTO.getDescription());
                //template5.setRuleTemplateCategory(templateCategory);
                template5.setTime(templateDTO.getTime());
                template5.setBalanceType(templateDTO.getBalanceType());
                template5.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(template5);
                break;

            case TEMPLATE6:
                WtaTemplate6 template6 = (WtaTemplate6) getTemplateByType(countryId, templateType);

                template6.setDescription(templateDTO.getDescription());
                //template6.setRuleTemplateCategory(templateCategory);
                template6.setNumberOfDays(templateDTO.getNumber());
                break;

            case TEMPLATE7:
                WtaTemplate7 template7 = (WtaTemplate7) getTemplateByType(countryId, templateType);

                template7.setDescription(templateDTO.getDescription());
                //template7.setRuleTemplateCategory(templateCategory);
                template7.setNightsWorked(templateDTO.getNightsWorked());
                template7.setBalanceType(templateDTO.getBalanceType());
                template7.setCheckAgainstTimeRules(templateDTO.isCheckAgainstTimeRules());
                oldTemplate = save(template7);
                break;
            case TEMPLATE8:
                WtaTemplate8 template8 = (WtaTemplate8) getTemplateByType(countryId, templateType);

                template8.setDescription(templateDTO.getDescription());
                //template8.setRuleTemplateCategory(templateCategory);
                template8.setNightsWorked(templateDTO.getNightsWorked());
                template8.setBalanceType(templateDTO.getBalanceType());
                template8.setMinimumRest(templateDTO.getMinimumRest());
                oldTemplate = save(template8);
                break;
            case TEMPLATE9:
                WtaTemplate9 template9 = (WtaTemplate9) getTemplateByType(countryId, templateType);

                template9.setDescription(templateDTO.getDescription());
                //template9.setRuleTemplateCategory(templateCategory);
                template9.setNightsWorked(templateDTO.getNightsWorked());
                template9.setBalanceType(templateDTO.getBalanceType());
                template9.setInterval(templateDTO.getInterval());
                template9.setValidationStartDate(templateDTO.getValidationStartDate());
                oldTemplate = save(template9);
                break;
            case TEMPLATE10:
                WtaTemplate10 template10 = (WtaTemplate10) getTemplateByType(countryId, templateType);

                template10.setDescription(templateDTO.getDescription());
                //template10.setRuleTemplateCategory(templateCategory);
                template10.setInterval(templateDTO.getInterval());
                template10.setValidationStartDate(templateDTO.getValidationStartDate());
                template10.setBalanceType(templateDTO.getBalanceType());
                template10.setMinimumDaysOff(templateDTO.getMinimumDaysOff());
                oldTemplate = save(template10);
                break;
            case TEMPLATE11:
                WtaTemplate11 template11 = (WtaTemplate11) getTemplateByType(countryId, templateType);


                template11.setDescription(templateDTO.getDescription());
                template11.setMaximumAvgTime(templateDTO.getMaximumAvgTime());
                // template11.setRuleTemplateCategory(templateCategory);
                template11.setInterval(templateDTO.getInterval());
                template11.setBalanceType(templateDTO.getBalanceType());
                template11.setValidationStartDate(templateDTO.getValidationStartDate());
                template11.setMinimumDaysOff(templateDTO.getMinimumDaysOff());
                oldTemplate = save(template11);
                break;
            case TEMPLATE12:
                WtaTemplate12 template12 = (WtaTemplate12) getTemplateByType(countryId, templateType);

                template12.setDescription(templateDTO.getDescription());
                // template12.setRuleTemplateCategory(templateCategory);
                template12.setMaximumVeto(templateDTO.getMaximumVeto());
                oldTemplate = save(template12);
                break;
            case TEMPLATE13:
                WtaTemplate13 template13 = (WtaTemplate13) getTemplateByType(countryId, templateType);

                template13.setDescription(templateDTO.getDescription());
                // template13.setRuleTemplateCategory(templateCategory);
                template13.setNumberShiftsPerPeriod(templateDTO.getNumberShiftsPerPeriod());
                template13.setNumberOfWeeks(templateDTO.getNumberOfWeeks());
                template13.setFromDayOfWeek(templateDTO.getFromDayOfWeek());
                template13.setFromTime(templateDTO.getFromTime());
                template13.setProportional(templateDTO.getProportional());
                oldTemplate = save(template13);
                break;
            case TEMPLATE14:
                WtaTemplate14 template14 = (WtaTemplate14) getTemplateByType(countryId, templateType);

                template14.setDescription(templateDTO.getDescription());
                // template14.setRuleTemplateCategory(templateCategory);
                template14.setInterval(templateDTO.getInterval());
                template14.setValidationStartDate(templateDTO.getValidationStartDate());
                oldTemplate = save(template14);
                break;
            case TEMPLATE15:
                // WTABaseRuleTemplate templateToUpdate =
                WtaTemplate15 template15 = (WtaTemplate15) getTemplateByType(countryId, templateType);

                template15.setDescription(templateDTO.getDescription());
                //  template15.setRuleTemplateCategory(templateCategory);
                template15.setContinuousDayRestHours(templateDTO.getContinousDayRestHours());
                oldTemplate = save(template15);
                break;
            case TEMPLATE16:
                WtaTemplate16 template16 = (WtaTemplate16) getTemplateByType(countryId, templateType);

                template16.setDescription(templateDTO.getDescription());
                //  template16.setRuleTemplateCategory(templateCategory);
                template16.setBalanceType(templateDTO.getBalanceType());
                template16.setMinimumDurationBetweenShifts(templateDTO.getMinimumDurationBetweenShifts());
                oldTemplate = save(template16);
                break;
            case TEMPLATE17:
                WtaTemplate17 template17 = (WtaTemplate17) getTemplateByType(countryId, templateType);

                template17.setDescription(templateDTO.getDescription());
                //   template17.setRuleTemplateCategory(templateCategory);
                template17.setContinuousWeekRest(templateDTO.getContinuousWeekRest());
                oldTemplate = save(template17);
                break;
            case TEMPLATE18:
                WtaTemplate18 template18 = (WtaTemplate18) getTemplateByType(countryId, templateType);

                template18.setDescription(templateDTO.getDescription());
                // template18.setRuleTemplateCategory(templateCategory);

                template18.setBalanceType(templateDTO.getBalanceType());
                template18.setInterval(templateDTO.getInterval());
                template18.setIntervalUnit(templateDTO.getIntervalUnit());
                template18.setValidationStartDate(templateDTO.getValidationStartDate());
                template18.setContinuousDayRestHours(templateDTO.getContinousDayRestHours());
                template18.setAverageRest(templateDTO.getAverageRest());
                template18.setShiftAffiliation(templateDTO.getShiftAffiliation());
                oldTemplate = save(template18);
                break;
            case TEMPLATE19:
                WtaTemplate19 template19 = (WtaTemplate19) getTemplateByType(countryId, templateType);

                template19.setDescription(templateDTO.getDescription());
                // template19.setRuleTemplateCategory(templateCategory);
                template19.setBalanceType(templateDTO.getBalanceType());
                template19.setInterval(templateDTO.getInterval());
                template19.setIntervalUnit(templateDTO.getIntervalUnit());
                template19.setValidationStartDate(templateDTO.getValidationStartDate());
                template19.setNumber(templateDTO.getNumber());
                template19.setOnlyCompositeShifts(templateDTO.isOnlyCompositeShifts());
                oldTemplate = save(template19);
                break;
            case TEMPLATE20:
                WtaTemplate20 template20 = (WtaTemplate20) getTemplateByType(countryId, templateType);

                template20.setDescription(templateDTO.getDescription());
                // template20.setRuleTemplateCategory(templateCategory);
                template20.setInterval(templateDTO.getInterval());
                template20.setIntervalUnit(templateDTO.getIntervalUnit());
                template20.setValidationStartDate(templateDTO.getValidationStartDate());
                template20.setNumber(templateDTO.getNumber());
                template20.setActivityCode(templateDTO.getActivityCode());

                oldTemplate = save(template20);
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");
        }

        oldTemplate.setActive(templateDTO.isActive());
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = templateCategory.getWtaBaseRuleTemplates();
        wtaBaseRuleTemplates.add(oldTemplate);
        templateCategory.setWtaBaseRuleTemplates(wtaBaseRuleTemplates);
        save(templateCategory);
        return wtaRuleTemplateGraphRepository.getRuleTemplateAndCategoryById(oldTemplate.getId());
    }

    private WTABaseRuleTemplate getTemplateByType(Long countryId, String templateType) {
        return countryGraphRepository.getTemplateByType(countryId, templateType);
    }

    public Map<String,Object> updateRuleTemplateCategory(WtaRuleTemplateDTO wtaRuleTemplateDTO, long countryId){

        wtaRuleTemplateGraphRepository.deleteOldCategories(wtaRuleTemplateDTO.getRuleTemplateIds());
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaRuleTemplateGraphRepository.getWtaBaseRuleTemplateByIds(wtaRuleTemplateDTO.getRuleTemplateIds());
        RuleTemplateCategory  ruleTemplateCategory = ruleTemplateCategoryRepository.findByName(countryId,wtaRuleTemplateDTO.getCategoryName());
        Map<String,Object> response = new HashedMap();
        if(ruleTemplateCategory == null){
            ruleTemplateCategory = new RuleTemplateCategory(wtaRuleTemplateDTO.getCategoryName());
            Country country = countryGraphRepository.findOne(countryId);
            if(country == null){
                throw new InternalError("country is null");
            }
            List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
            ruleTemplateCategories.add(ruleTemplateCategory);
            country.setRuleTemplateCategories(ruleTemplateCategories);
            countryGraphRepository.save(country);
            response.put("category",ruleTemplateCategory);
        }
        List<WTABaseRuleTemplate> baseRuleTemplates = ruleTemplateCategory.getWtaBaseRuleTemplates();
        baseRuleTemplates.addAll(wtaBaseRuleTemplates);
        ruleTemplateCategory.setWtaBaseRuleTemplates(baseRuleTemplates);
        save(ruleTemplateCategory);
        response.put("templateList",getJsonOfUpdatedTemplates(wtaBaseRuleTemplates,ruleTemplateCategory));
        return response;
    }

    private List<WTABaseRuleTemplateDTO> getJsonOfUpdatedTemplates(List<WTABaseRuleTemplate> wtaBaseRuleTemplates,RuleTemplateCategory ruleTemplateCategory){

        ObjectMapper objectMapper = new ObjectMapper();
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS = new ArrayList<>(wtaBaseRuleTemplates.size());
        wtaBaseRuleTemplates.forEach(wtaBaseRuleTemplate -> {
            WTABaseRuleTemplateDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(wtaBaseRuleTemplate,WTABaseRuleTemplateDTO.class);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplateCategory);
            wtaBaseRuleTemplateDTOS.add(wtaBaseRuleTemplateDTO);
        });

        return wtaBaseRuleTemplateDTOS;
    }

}
