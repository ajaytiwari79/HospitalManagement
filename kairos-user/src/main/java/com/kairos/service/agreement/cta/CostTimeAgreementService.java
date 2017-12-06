package com.kairos.service.agreement.cta;

import com.kairos.config.listener.ApplicationContextProviderNonManageBean;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplate;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateDTO;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateType;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.country.TimeType;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.agreement.cta.CTARuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.TimeTypeGraphRepository;
import com.kairos.response.dto.web.cta.CTARuleTemplateCategoryWrapper;
import com.kairos.response.dto.web.cta.CTARuleTemplateDayTypeDTO;
import com.kairos.service.AsynchronousService;
import com.kairos.service.UserBaseService;
import com.kairos.service.auth.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class CostTimeAgreementService extends UserBaseService {
    private Logger logger = LoggerFactory.getLogger(CostTimeAgreementService.class);
    private @Autowired
    UserService userService;
    private @Autowired
    RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    private @Autowired
    CountryGraphRepository countryGraphRepository;
    private @Autowired
    CTARuleTemplateGraphRepository ctaRuleTemplateGraphRepository;
    private @Autowired AsynchronousService asynchronousService;
    private @Autowired DayTypeGraphRepository dayTypeGraphRepository;
    private @Autowired  EmploymentTypeGraphRepository employmentTypeGraphRepository;
    private @Autowired AccessGroupRepository accessGroupRepository;
    private @Autowired TimeTypeGraphRepository timeTypeGraphRepository;


    public void createDefaultCtaRuleTemplate() {
        RuleTemplateCategory category = ruleTemplateCategoryGraphRepository
                .findByNameAndRuleTemplateCategoryType("NONE", RuleTemplateCategoryType.CTA);
        if (category != null) {
            Arrays.stream(CTARuleTemplateType.values()).parallel().forEach(cTARuleTemplate -> {
                CTARuleTemplate ctaRuleTemplate = createRuleTemplate(cTARuleTemplate);
                category.addRuleTemplate(ctaRuleTemplate);
            });

            this.save(category);
        } else {
            logger.info("default CTARuleTemplateCategory is not exist");
        }

    }

    private CTARuleTemplate createRuleTemplate(CTARuleTemplateType ctaRuleTemplateType) {
        CTARuleTemplate ctaRuleTemplate = null;
        switch (ctaRuleTemplateType) {
            case RULE_TEMPLATE_1:
                ctaRuleTemplate = new CTARuleTemplate("Working Evening Shifts",
                        "CTA rule for evening shift, from 17-23 o'clock.  For this organization/unit this is payroll type '210:  Evening compensation'",
                        CTARuleTemplateType.RULE_TEMPLATE_1, "210:  Evening compensation", "xyz");
                break;
            case RULE_TEMPLATE_2:

                ctaRuleTemplate = new CTARuleTemplate("Working Night Shifts",
                        "CTA rule for night shift, from 23-07 o. clock.  For this organization/unit this is payroll type “212:  Night compensation”",
                        CTARuleTemplateType.RULE_TEMPLATE_2, "212:  Night compensation", "xyz");
                break;
            case RULE_TEMPLATE_3:

                ctaRuleTemplate = new CTARuleTemplate("Working On a Saturday",
                        "CTA rule for Saturdays shift, from 08-24 o. clock. For this organization/unit this is payroll type " +
                                "“214:  Saturday compensation”. If you are working from 00-07 on Saturday, you only gets evening " +
                                "compensation", CTARuleTemplateType.RULE_TEMPLATE_3,
                        "214:  Saturday compensation", "xyz");
                break;
            case RULE_TEMPLATE_4:
                ctaRuleTemplate = new CTARuleTemplate("Working On a Sunday",
                        "CTA rule for Saturdays shift, from 00-24 o. clock. For this organization/unit this is " +
                                "payroll type “214:Saturday compensation”.All working time on Sundays gives compensation"
                        , CTARuleTemplateType.RULE_TEMPLATE_4,
                        "214:Saturday compensation", "xyz");
                break;
            case RULE_TEMPLATE_5:
                ctaRuleTemplate = new CTARuleTemplate("Working On a Full Public Holiday",
                        "CTA rule for full public holiday shift, from 00-24 o. clock.  For this organization/unit this is " +
                                "payroll type “216:  public holiday compensation”. All working time on full PH gives " +
                                "compensation", CTARuleTemplateType.RULE_TEMPLATE_5,
                        "216:public holiday compensation", "xyz");
                break;
            case RULE_TEMPLATE_6:
                ctaRuleTemplate = new CTARuleTemplate("Working On a Half Public Holiday",
                        "CTA rule for full public holiday shift, from 12-24 o. clock. For this organization/unit" +
                                " this is payroll type “218:  half public holiday compensation”.All working time on " +
                                "half PH gives compensation", CTARuleTemplateType.RULE_TEMPLATE_6,
                        "218: half public holiday compensation", "xyz");
                break;
            case RULE_TEMPLATE_7:
                ctaRuleTemplate = new CTARuleTemplate("Working Overtime",
                        "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                                " 50% overtime compensation”.", CTARuleTemplateType.RULE_TEMPLATE_7,
                        "230:50% overtime compensation", "xyz");
                break;
            case RULE_TEMPLATE_8:
                ctaRuleTemplate = new CTARuleTemplate("Working Extratime",
                        "CTA rule for extra time shift, from 00-24 o. clock.  For this organization/unit this is payroll type" +
                                " “250:  extratime compensation”. ", CTARuleTemplateType.RULE_TEMPLATE_8,
                        "250:  extratime compensation", "xyz");
                break;
            case RULE_TEMPLATE_9:
                ctaRuleTemplate = new CTARuleTemplate("Late Notice Compensation",
                        "CTA rule for late notification on changes to working times.  If notice of change is done within 72 hours" +
                                " before start of working day, then staff is entitled to at compensation of 105 kroner",
                        CTARuleTemplateType.RULE_TEMPLATE_9,
                        "", "xyz");
                break;
            case RULE_TEMPLATE_10:
                ctaRuleTemplate = new CTARuleTemplate("Extra Dutyfree Day For Each Public Holiday",
                        "CTA rule for each public holiday.  Whenever there is a public holiday staff are entitled to an" +
                                " extra day off, within 3 month or just compensated in the timebank.", CTARuleTemplateType.RULE_TEMPLATE_1,
                        "", "xyz");
                break;
            default:
                throw new IllegalArgumentException("invalid template type");

        }
        return ctaRuleTemplate;


    }

    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByCountry(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        List<RuleTemplateCategory> ruleTemplateCategories = country.getRuleTemplateCategories();
        List<RuleTemplateCategory> ctaRuleTemplateCategoryList = ruleTemplateCategories.parallelStream().filter(
                ruleTemplateCategory -> RuleTemplateCategoryType.CTA.equals(ruleTemplateCategory.getRuleTemplateCategoryType()))
                .collect(Collectors.toList());
        List<Long> ruleTemplateCategoryIds=ctaRuleTemplateCategoryList.parallelStream().map(RuleTemplateCategory::getId)
                .collect(Collectors.toList());
        List<CTARuleTemplateDTO> ruleTemplates=ctaRuleTemplateGraphRepository.findByRuleTemplateCategoryIdInAndDeletedFalseAndDisabledFalse(ruleTemplateCategoryIds);
        CTARuleTemplateCategoryWrapper ctaRuleTemplateCategoryWrapper=new CTARuleTemplateCategoryWrapper();
        ctaRuleTemplateCategoryWrapper.getRuleTemplateCategories().addAll(ctaRuleTemplateCategoryList);
        ctaRuleTemplateCategoryWrapper.getRuleTemplates().addAll(ruleTemplates);
        return ctaRuleTemplateCategoryWrapper;
    }

    public CTARuleTemplateDTO updateCTARuleTemplate(Long countryId,Long id,CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException {

        CTARuleTemplate ctaRuleTemplate= ctaRuleTemplateGraphRepository.findOne(id);
        this.buildCTARuleTemplate(ctaRuleTemplate,ctaRuleTemplateDTO);
        this.save(ctaRuleTemplate);
        return ctaRuleTemplateDTO;
    }

    public CTARuleTemplate buildCTARuleTemplate(CTARuleTemplate ctaRuleTemplate,CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException {
        BeanUtils.copyProperties(ctaRuleTemplateDTO,ctaRuleTemplate);
        CompletableFuture<Boolean> hasUpdated= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(CostTimeAgreementService.class)
                .buildTimeTypesEmploymentTypeAndAccessGroups(ctaRuleTemplate,ctaRuleTemplateDTO);
        //Load reference only
        RuleTemplateCategory ruleTemplateCategory=
                ruleTemplateCategoryGraphRepository.findOne(ctaRuleTemplateDTO.getRuleTemplateCategory(),0);
        List<Long> dayTypesIds=ctaRuleTemplateDTO.getCalculateOnDayTypes().parallelStream().map(CTARuleTemplateDayTypeDTO::getDayType).collect(Collectors.toList());
        Iterable<DayType>dayTypeList=dayTypeGraphRepository.findAll(dayTypesIds,0);

        List<Long> countryHolidayCalendersIds=ctaRuleTemplateDTO.getCalculateOnDayTypes().parallelStream()
                .map(CTARuleTemplateDayTypeDTO::getCountryHolidayCalenders).flatMap(countryHolidayCalenders ->
                {return countryHolidayCalenders.stream();}).collect(Collectors.toList());

        ctaRuleTemplate.setRuleTemplateCategory(ruleTemplateCategory);

        // Wait until they are all done
        CompletableFuture.allOf(hasUpdated).join();

     return ctaRuleTemplate;
    }
    @Async
    public CompletableFuture<Boolean> buildTimeTypesEmploymentTypeAndAccessGroups
            (CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO)
            throws InterruptedException, ExecutionException {

     Callable<List<TimeType>> timeTypesTask=()->{
        Iterable<TimeType> timeTypes=timeTypeGraphRepository.findAll(ctaRuleTemplateDTO.getTimeTypes(),0);
        return  StreamSupport.stream(timeTypes.spliterator(), true).collect(Collectors.toList());
    };

    Future<List<TimeType>>timeTypesFuture=asynchronousService.executeAsynchronously(timeTypesTask);

    Callable<List<EmploymentType>> employmentTypesTask=()->{
        Iterable<EmploymentType> employmentTypes=employmentTypeGraphRepository.findAll(ctaRuleTemplateDTO.getEmploymentTypes(),0);
        return  StreamSupport.stream(employmentTypes.spliterator(), true).collect(Collectors.toList());
    };

    Future<List<EmploymentType>>employmentTypesFuture=asynchronousService.executeAsynchronously(employmentTypesTask);

    Callable<List<AccessGroup>> accessGroupsTask=()->{
        Iterable<AccessGroup>   accessGroups=accessGroupRepository.findAll(ctaRuleTemplateDTO.getCalculateValueIfPlanned(),0);
        return  StreamSupport.stream(accessGroups.spliterator(), true).collect(Collectors.toList());


    };
    Future<List<AccessGroup>>accessGroupsFuture=asynchronousService.executeAsynchronously(accessGroupsTask);
    //set data
    ctaRuleTemplate.setTimeTypes(timeTypesFuture.get());
    ctaRuleTemplate.setEmploymentTypes(employmentTypesFuture.get());
    ctaRuleTemplate.setCalculateValueIfPlanned(accessGroupsFuture.get());
    return CompletableFuture.completedFuture(true);
    }


}
