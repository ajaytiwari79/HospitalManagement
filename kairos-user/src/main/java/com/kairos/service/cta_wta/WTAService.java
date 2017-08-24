package com.kairos.service.cta_wta;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreementQueryResult;
import com.kairos.persistence.model.user.agreement.wta.templates.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.cta_wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.cta_wta.template.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.cta_wta.template.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.response.dto.web.WtaDTO;
import com.kairos.service.UserBaseService;
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

    public WorkingTimeAgreement createWta(long countryId, WtaDTO wtaDTO) {

        WorkingTimeAgreement wta = prepareWta(countryId, wtaDTO);

        Country country = countryRepository.findOne(countryId);

        if (country == null) {
            throw new DataNotFoundByIdException("Invalid Country id");
        }
        wta.setCountry(country);

        save(wta);
        wta.getExpertise().setCountry(null);
        return wta;
    }


    private WorkingTimeAgreement prepareWta(long countryId, WtaDTO wtaDTO) {

        WorkingTimeAgreement wta = new WorkingTimeAgreement();

        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());

        Expertise expertise = expertiseRepository.findOne(wtaDTO.getExpertiseId());
        if (expertise == null) {
            throw new DataNotFoundByIdException("Invalid expertiseId");
        }
        wta.setExpertise(expertise);
        List<OrganizationType> organizationTypes = new ArrayList<OrganizationType>();

        for (long orgTypeId : wtaDTO.getOrganizationTypes()) {
            OrganizationType orgType = organizationTypeRepository.findOne(orgTypeId);
            if (orgType == null) {
                throw new DataNotFoundByIdException("Invalid organisation type");
            }
            organizationTypes.add(orgType);
        }
        wta.setOrganizationTypes(organizationTypes);
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        wtaBaseRuleTemplates = setRuleTemplates(countryId, wta, wtaDTO);

        wta.setRuleTemplates(wtaBaseRuleTemplates);
        Region region = regionRepository.findOne(wtaDTO.getRegionId());
        if (region == null) {
            throw new DataNotFoundByIdException("Invalid Region");
        }
        wta.setRegion(region);
        if (wtaDTO.getStartDate() == 0) {

            wta.setStartDate(new Date().getTime());
        }
        else wta.setStartDate(wtaDTO.getStartDate());

        if (wtaDTO.getEndDate() != 0) {
            wta.setEndDate(wtaDTO.getEndDate());
        }



        return wta;
    }

    private List<WTABaseRuleTemplate> setRuleTemplates(long countryId, WorkingTimeAgreement wta, WtaDTO wtaDTO) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();
        for (long ruleTemplateId : wtaDTO.getRuleTemplates()) {
            WTABaseRuleTemplate wtaBaseRuleTemplate = wtaBaseRuleTemplateGraphRepository.findOne(ruleTemplateId);
            if (wtaBaseRuleTemplate == null) {
                throw new DataNotFoundByIdException("Invalid RuleTemplate Id");

            }
            String templateType = wtaBaseRuleTemplate.getTemplateType();
            switch (templateType) {

                case TEMPLATE1:

                    WtaTemplate1 template1 = (WtaTemplate1) getTemplateByType(countryId, templateType);
                    WtaTemplate1 template1Copy = new WtaTemplate1();

                    template1Copy.setDescription(template1.getDescription());
                    template1Copy.setTime(template1.getTime());
                    template1Copy.setBalanceType(template1.getBalanceType());
                    template1Copy.setCheckAgainstTimeRules(template1.isCheckAgainstTimeRules());
                    save(template1Copy);
                    wtaBaseRuleTemplates.add(template1Copy);

                    break;

                case TEMPLATE2:
                    WtaTemplate2 template2 = (WtaTemplate2) getTemplateByType(countryId, templateType);
                    WtaTemplate2 template2Copy = new WtaTemplate2();
                    template2Copy.setDescription(template2.getDescription());
                    template2Copy.setTime(template2.getTime());
                    template2Copy.setBalanceType(template2.getBalanceType());
                    template2Copy.setCheckAgainstTimeRules(template2.isCheckAgainstTimeRules());
                    save(template2Copy);
                    wtaBaseRuleTemplates.add(template2Copy);
                    break;

                case TEMPLATE3:
                    WtaTemplate3 template3 = (WtaTemplate3) getTemplateByType(countryId, templateType);
                    WtaTemplate3 template3Copy = new WtaTemplate3();
                    template3Copy.setDescription(template3.getDescription());
                    template3Copy.setDays(template3.getDays());
                    template3Copy.setBalanceType(template3.getBalanceType());
                    template3Copy.setCheckAgainstTimeRules(template3.isCheckAgainstTimeRules());
                    save(template3);
                    wtaBaseRuleTemplates.add(template3Copy);
                    break;

                case TEMPLATE4:
                    WtaTemplate4 template4 = (WtaTemplate4) getTemplateByType(countryId, templateType);
                    WtaTemplate4 template4Copy = new WtaTemplate4();
                    template4Copy.setDescription(template4.getDescription());
                    //template4.setRuleTemplateCategory(templateCategory);
                    template4Copy.setMinimumRest(template4.getMinimumRest());
                    template4Copy.setDaysWorked(template4.getDaysWorked());
                    save(template4Copy);
                    wtaBaseRuleTemplates.add(template4Copy);
                    break;

                case TEMPLATE5:
                    WtaTemplate5 template5 = (WtaTemplate5) getTemplateByType(countryId, templateType);
                    WtaTemplate5 template5Copy = new WtaTemplate5();
                    template5Copy.setDescription(template5.getDescription());
                    //template5.setRuleTemplateCategory(templateCategory);
                    template5Copy.setTime(template5.getTime());
                    template5Copy.setBalanceType(template5.getBalanceType());
                    template5Copy.setCheckAgainstTimeRules(template5.isCheckAgainstTimeRules());
                    save(template5Copy);
                    wtaBaseRuleTemplates.add(template5Copy);
                    break;

                case TEMPLATE6:
                    WtaTemplate6 template6 = (WtaTemplate6) getTemplateByType(countryId, templateType);
                    WtaTemplate6 template6Copy = new WtaTemplate6();
                    template6Copy.setDescription(template6.getDescription());
                    //template6.setRuleTemplateCategory(templateCategory);
                    template6Copy.setNumberOfDays(template6.getNumberOfDays());
                    wtaBaseRuleTemplates.add(template6Copy);
                    break;

                case TEMPLATE7:
                    WtaTemplate7 template7 = (WtaTemplate7) getTemplateByType(countryId, templateType);
                    WtaTemplate7 template7Copy = new WtaTemplate7();
                    template7Copy.setDescription(template7.getDescription());
                    //template7.setRuleTemplateCategory(templateCategory);
                    template7Copy.setNightsWorked(template7.getNightsWorked());
                    template7Copy.setBalanceType(template7.getBalanceType());
                    template7Copy.setCheckAgainstTimeRules(template7.isCheckAgainstTimeRules());
                    save(template7Copy);
                    wtaBaseRuleTemplates.add(template7Copy);
                    break;
                case TEMPLATE8:
                    WtaTemplate8 template8 = (WtaTemplate8) getTemplateByType(countryId, templateType);
                    WtaTemplate8 template8Copy = new WtaTemplate8();
                    template8Copy.setDescription(template8.getDescription());
                    //template8.setRuleTemplateCategory(templateCategory);
                    template8Copy.setNightsWorked(template8.getNightsWorked());
                    template8Copy.setBalanceType(template8.getBalanceType());
                    template8Copy.setMinimumRest(template8.getMinimumRest());
                    save(template8Copy);
                    wtaBaseRuleTemplates.add(template8Copy);
                    break;
                case TEMPLATE9:
                    WtaTemplate9 template9 = (WtaTemplate9) getTemplateByType(countryId, templateType);
                    WtaTemplate9 template9Copy = new WtaTemplate9();
                    template9Copy.setDescription(template9.getDescription());
                    template9Copy.setNightsWorked(template9.getNightsWorked());
                    template9Copy.setBalanceType(template9.getBalanceType());
                    template9Copy.setInterval(template9.getInterval());
                    template9Copy.setValidationStartDate(template9.getValidationStartDate());
                    save(template9Copy);
                    wtaBaseRuleTemplates.add(template9Copy);
                    break;
                case TEMPLATE10:
                    WtaTemplate10 template10 = (WtaTemplate10) getTemplateByType(countryId, templateType);
                    WtaTemplate10 template10Copy = new WtaTemplate10();
                    template10Copy.setDescription(template10.getDescription());
                    //template10.setRuleTemplateCategory(templateCategory);
                    template10Copy.setInterval(template10.getInterval());
                    template10Copy.setValidationStartDate(template10.getValidationStartDate());
                    template10Copy.setBalanceType(template10.getBalanceType());
                    template10Copy.setMinimumDaysOff(template10.getMinimumDaysOff());
                    save(template10Copy);
                    wtaBaseRuleTemplates.add(template10Copy);
                    break;
                case TEMPLATE11:
                    WtaTemplate11 template11 = (WtaTemplate11) getTemplateByType(countryId, templateType);
                    WtaTemplate11 template11Copy = new WtaTemplate11();
                    template11Copy.setDescription(template11.getDescription());
                    // template11.setRuleTemplateCategory(templateCategory);
                    template11Copy.setInterval(template11.getInterval());
                    template11Copy.setBalanceType(template11.getBalanceType());
                    template11Copy.setValidationStartDate(template11.getValidationStartDate());
                    template11Copy.setMinimumDaysOff(template11.getMinimumDaysOff());
                    save(template11Copy);
                    wtaBaseRuleTemplates.add(template11Copy);
                    break;
                case TEMPLATE12:
                    WtaTemplate12 template12 = (WtaTemplate12) getTemplateByType(countryId, templateType);
                    WtaTemplate12 template12Copy = new WtaTemplate12();
                    template12Copy.setDescription(template12.getDescription());
                    template12Copy.setMaximumVeto(template12.getMaximumVeto());
                    save(template12Copy);
                    wtaBaseRuleTemplates.add(template12Copy);
                    break;
                case TEMPLATE13:
                    WtaTemplate13 template13 = (WtaTemplate13) getTemplateByType(countryId, templateType);
                    WtaTemplate13 template13Copy = new WtaTemplate13();
                    template13Copy.setDescription(template13.getDescription());
                    // template13.setRuleTemplateCategory(templateCategory);
                    template13Copy.setNumberShiftsPerPeriod(template13.getNumberShiftsPerPeriod());
                    template13Copy.setNumberOfWeeks(template13.getNumberOfWeeks());
                    template13Copy.setFromDayOfWeek(template13.getFromDayOfWeek());
                    template13Copy.setFromTime(template13.getFromTime());
                    template13Copy.setProportional(template13.getProportional());
                    save(template13Copy);
                    wtaBaseRuleTemplates.add(template13Copy);
                    break;
                case TEMPLATE14:
                    WtaTemplate14 template14 = (WtaTemplate14) getTemplateByType(countryId, templateType);
                    WtaTemplate14 template14Copy = new WtaTemplate14();
                    template14Copy.setDescription(template14.getDescription());
                    // template14.setRuleTemplateCategory(templateCategory);
                    template14Copy.setInterval(template14.getInterval());
                    template14Copy.setValidationStartDate(template14.getValidationStartDate());
                    save(template14Copy);
                    wtaBaseRuleTemplates.add(template14Copy);
                    break;
                case TEMPLATE15:
                    WtaTemplate15 template15Copy = new WtaTemplate15();
                    WtaTemplate15 template15 = (WtaTemplate15) getTemplateByType(countryId, templateType);
                    template15Copy.setDescription(template15.getDescription());
                    template15Copy.setContinuousDayRestHours(template15.getContinuousDayRestHours());
                    save(template15Copy);
                    wtaBaseRuleTemplates.add(template15Copy);
                    break;
                case TEMPLATE16:
                    WtaTemplate16 template16 = (WtaTemplate16) getTemplateByType(countryId, templateType);
                    WtaTemplate16 template16Copy = new WtaTemplate16();
                    template16Copy.setDescription(template16.getDescription());
                    template16Copy.setBalanceType(template16.getBalanceType());
                    template16Copy.setMinimumDurationBetweenShifts(template16.getMinimumDurationBetweenShifts());
                    save(template16Copy);
                    wtaBaseRuleTemplates.add(template16Copy);
                    break;
                case TEMPLATE17:
                    WtaTemplate17 template17 = (WtaTemplate17) getTemplateByType(countryId, templateType);
                    WtaTemplate17 template17Copy = new WtaTemplate17();
                    template17Copy.setDescription(template17.getDescription());
                    template17Copy.setContinuousWeekRest(template17.getContinuousWeekRest());
                    save(template17Copy);
                    wtaBaseRuleTemplates.add(template17Copy);
                    break;
                case TEMPLATE18:
                    WtaTemplate18 template18 = (WtaTemplate18) getTemplateByType(countryId, templateType);
                    WtaTemplate18 template18Copy = new WtaTemplate18();
                    template18Copy.setDescription(template18.getDescription());
                    // template18.setRuleTemplateCategory(templateCategory);

                    template18Copy.setBalanceType(template18.getBalanceType());
                    template18Copy.setInterval(template18.getInterval());
                    template18Copy.setIntervalUnit(template18.getIntervalUnit());
                    template18Copy.setValidationStartDate(template18.getValidationStartDate());
                    template18Copy.setContinuousDayRestHours(template18.getContinuousDayRestHours());
                    template18Copy.setAverageRest(template18.getAverageRest());
                    template18Copy.setShiftAffiliation(template18.getShiftAffiliation());
                    save(template18Copy);
                    wtaBaseRuleTemplates.add(template18Copy);
                    break;
                case TEMPLATE19:
                    WtaTemplate19 template19 = (WtaTemplate19) getTemplateByType(countryId, templateType);
                    WtaTemplate19 template19Copy = new WtaTemplate19();
                    template19Copy.setDescription(template19.getDescription());
                    // template19.setRuleTemplateCategory(templateCategory);
                    template19Copy.setBalanceType(template19.getBalanceType());
                    template19Copy.setInterval(template19.getInterval());
                    template19Copy.setIntervalUnit(template19.getIntervalUnit());
                    template19Copy.setValidationStartDate(template19.getValidationStartDate());
                    template19Copy.setNumber(template19.getNumber());
                    template19Copy.setOnlyCompositeShifts(template19.isOnlyCompositeShifts());
                    save(template19Copy);
                    wtaBaseRuleTemplates.add(template19Copy);
                    break;
                case TEMPLATE20:
                    WtaTemplate20 template20 = (WtaTemplate20) getTemplateByType(countryId, templateType);
                    WtaTemplate20 template20Copy = new WtaTemplate20();
                    template20Copy.setDescription(template20.getDescription());
                    // template20.setRuleTemplateCategory(templateCategory);
                    template20Copy.setInterval(template20.getInterval());
                    template20Copy.setIntervalUnit(template20.getIntervalUnit());
                    template20Copy.setValidationStartDate(template20.getValidationStartDate());
                    template20Copy.setNumber(template20.getNumber());
                    template20Copy.setActivityCode(template20.getActivityCode());
                    save(template20Copy);
                    wtaBaseRuleTemplates.add(template20Copy);
                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }
        }

        return wtaBaseRuleTemplates;

    }

    private WTABaseRuleTemplate getTemplateByType(Long countryId, String templateType) {
        return countryRepository.getTemplateByType(countryId, templateType);
    }

    public Map<String, Object> updateWta(long wtaId, WtaDTO wta) {
        WorkingTimeAgreement oldWta = wtaRepository.findOne(wtaId);

        if (oldWta == null) {
            return null;
        }

        prepareWta(oldWta, wta);

        save(oldWta);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("id", oldWta.getId());
        response.put("name", oldWta.getName());

        return response;
    }

    private void prepareWta(WorkingTimeAgreement oldWta, WtaDTO wta) {

        if (oldWta.getExpertise().getId() != wta.getExpertiseId()) {
            Expertise expertise = expertiseRepository.findOne(wta.getExpertiseId());
            if (expertise == null) {
                throw new NullPointerException("Expertize Cannot be null");
            }
            oldWta.setExpertise(expertise);

        }


        //Todo On WTA update use Javers to maintain its versions

        oldWta.setName(wta.getName());
        oldWta.setStartDate(wta.getStartDate());
        oldWta.setEndDate(wta.getEndDate());
        oldWta.setDescription(wta.getDescription());

    }

    public WorkingTimeAgreement getWta(long wtaId) {
        return wtaRepository.findOne(wtaId);
    }

    public boolean removeWta(long wtaId) {
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (wta == null) {
            return false;
        }
        wta.setEnabled(false);
        save(wta);
        if (wtaRepository.findOne(wtaId).isEnabled()) {
            return false;
        }
        return true;
    }

    public List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationId(long organizationId) {
        return wtaRepository.getAllWTAByOrganizationId(organizationId);
    }

    public List<Map<String,Object>> getAllWTAByCountryId(long countryId) {
        return wtaRepository.getAllWTAByCountryId(countryId);
    }

    public List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
        return wtaRepository.getAllWTAByOrganizationSubType(organizationSubTypeId);
    }
    public List<Object> getAllWTAWithOrganization(long countryId){
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithOrganization(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public List<Object>getAllWTAWithWTAId(long countryId,long wtaId){
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithWTAId(countryId,wtaId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }
}