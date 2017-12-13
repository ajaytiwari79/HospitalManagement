package com.kairos.cta;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.country.Currency;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.response.dto.web.cta.CTARuleTemplateCategoryWrapper;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.service.agreement.RuleTemplateCategoryService;
import com.kairos.service.country.CurrencyService;
import com.kairos.service.country.DayTypeService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CostTimeAgreementServiceTest {
    @Autowired private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Autowired private CostTimeAgreementService costTimeAgreementService;
    @Autowired private CurrencyService currencyService;
    @Autowired DayTypeService dayTypeService;

    @Test
    public void addCTARuleTemplateCategory(){
        RuleTemplateCategory category=new RuleTemplateCategory();
        category.setName("NONE");
        category.setRuleTemplateCategoryType(RuleTemplateCategoryType.CTA);
        ruleTemplateCategoryService.createRuleTemplateCategory(53L,category);
    }

    @Test
    public void addCTARuleTemplate()
    {
        costTimeAgreementService.createDefaultCtaRuleTemplate(53L);
    }
    @Test
    public void getHoliday(){
     Date date= Date.from(LocalDate.of(2018,1,2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        List<DayType> dayTypes= dayTypeService.getDayTypeByDate(53L,date);
        System.out.println(dayTypes);
    }
    @Test
    public void getAllRuleTemplate(){
        CTARuleTemplateCategoryWrapper ctaRuleTemplateDTOS= costTimeAgreementService.loadAllCTARuleTemplateByCountry(53L);
        System.out.println(ctaRuleTemplateDTOS);
    }
    @Test
    public void getCurrency(){

        Currency currency=currencyService.getCurrencyByCountryId(53L);
        System.out.println(currency);

    }


    @Test
    public void saveCom(){
        costTimeAgreementService.saveInterval();


    }
    @Test
    @Ignore
    public  void changeCTARuleTemplateCategory() throws Exception{
        CTARuleTemplateCategoryWrapper ctaRuleTemplateDTOS= costTimeAgreementService.loadAllCTARuleTemplateByCountry(53L);
        ArrayList arrayList = new ArrayList();
         List<Long> ctaRuleTemplateList=null;
        ctaRuleTemplateList.add(ctaRuleTemplateDTOS.getRuleTemplates().get(0).getId());
        String ruleTemplateCategory="test";
    }

}
