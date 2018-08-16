package com.kairos.config;

import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillCategoryGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.service.country.CountryHolidayCalenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates below mentioned bootstrap data(if Not Available)
 * 1. User
 * 2. Role
 * 3. Organization
 * 4. Units
 */
@Component

public class AppBootstrapListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(AppBootstrapListener.class);

    @Inject
    SkillCategoryGraphRepository skillCategoryGraphRepository;

    @Inject
    SkillGraphRepository skillGraphRepository;

    @Inject
    CountryGraphRepository countryGraphRepository;

    @Inject
    ExpertiseGraphRepository expertiseGraphRepository;

    @Inject
    AccessPageRepository accessPageRepository;

    @Inject
    Environment environment;

    @Inject
    BootDataService bootDataService;


    @Inject
    CountryHolidayCalenderService countryHolidayCalenderService;

    /**
     * Executes on application ready event
     * Check's if data exists & calls createUsersAndRolesData
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        generateSequence(); // This method create sequence table for mongodb
        //createAccessPages();
        bootDataService.createData();
        //flsVisitourChangeService.registerReceiver("visitourChange");
    }

    public void createSkillCategoryAndSkills() {
        String skillCategories[] = new String[]{"Basic Skills", "Extra care Skills"};
        SkillCategory skillCategory;
        for (String value : skillCategories) {
            if (("Basic Skills").equals(value)) {
                skillCategory = new SkillCategory(value);
                skillCategoryGraphRepository.save(skillCategory);
                Skill skill1 = new Skill("Assist with feeding", skillCategory);
                Skill skill2 = new Skill("Radial Pulse Rate", skillCategory);
                Skill skill3 = new Skill("Bathing Partial", skillCategory);
                skillGraphRepository.saveAll(Arrays.asList(skill1, skill2, skill3));
            } else {
                skillCategory = new SkillCategory(value);
                skillCategoryGraphRepository.save(skillCategory);
                Skill skill1 = new Skill("Cultural Assessment", skillCategory);
                Skill skill2 = new Skill("Develop care plan", skillCategory);
                skillGraphRepository.saveAll(Arrays.asList(skill1, skill2));
            }
        }
    }

    public void saveExpertise() {
        Country country = countryGraphRepository.getCountryByName("Denmark");
        if (country != null) {
            logger.info("creating expertise master data");
            String expertises[] = new String[]{"Engineer", "Doctor"};
            for (String value : expertises) {
                Expertise expertise = new Expertise(value, country);
                expertiseGraphRepository.save(expertise);
            }
        }
    }

    public void createAccessPages() {

        if (accessPageRepository.count() == 0) {

            String taskTypeArray[] = new String[]{
                    "taskType.agreements",
                    "taskType.balanceSettings",
                    "taskType.general",
                    "taskType.communication",
                    "taskType.costIncome",
                    "taskType.creationRules",
                    "taskType.dependencies",
                    "taskType.logging",
                    "taskType.mainTask",
                    "taskType.notification",
                    "taskType.planningRules",
                    "taskType.points",
                    "taskType.resources",
                    "taskType.restingTime",
                    "taskType.skill",
                    "taskType.staffType",
                    "taskType.taskTypeSkill",
                    "taskType.timeFrame",
                    "taskType.timeRules",
                    "taskType.visitation",
                    "taskType.definations"
            };
            List<AccessPage> taskTypePages = new ArrayList<>();
            for (String taskTypePage : taskTypeArray) {
                AccessPage accessPage = new AccessPage(taskTypePage, environment.getProperty(taskTypePage));
                taskTypePages.add(accessPage);
            }
            AccessPage taskType = new AccessPage("task-type", environment.getProperty("taskType"));
            taskType.setModule(true);
            taskType.setSubPages(taskTypePages);

            String organizationArray[] = new String[]{
                    "unit.general",
                    "unit.location",
                    "unit.services",
                    "unit.skills",
                    "unit.staff",
                    "unit.manageHerichy",
                    "unit.citizens",
                    "unit.sms",
                    "unit.integration",
                    "unit.socialInfo",
                    "unit.resources",
                    "unit.management",
                    "unit.openingHours",
                    "unit.teams"
            };

            List<AccessPage> organizationPages = new ArrayList<>();
            for (String organizationPage : organizationArray) {
                AccessPage accessPage = new AccessPage(organizationPage, environment.getProperty(organizationPage));
                organizationPages.add(accessPage);
            }
            AccessPage organization = new AccessPage("unit", environment.getProperty("unit"));
            organization.setModule(true);
            organization.setSubPages(organizationPages);

            String countryArray[] = new String[]{
                    "countrySettings.service",
                    "countrySettings.organizationTypes",
                    "countrySettings.calander",
                    "countrySettings.skill",
                    "countrySettings.mapSymbol",
                    "countrySettings.taskType",
                    "countrySettings.unitTypes"
            };
            List<AccessPage> countryPages = new ArrayList<>();
            for (String countryPage : countryArray) {
                AccessPage accessPage = new AccessPage(countryPage, environment.getProperty(countryPage));
                countryPages.add(accessPage);
            }
            AccessPage country = new AccessPage("country-settings", environment.getProperty("countrySettings"));
            country.setModule(true);
            country.setSubPages(countryPages);

            String visitatorArray[] = new String[]{"visitatorWorkflow.manageTask"};
            List<AccessPage> visitatorPages = new ArrayList<>();
            for (String visitatorPage : visitatorArray) {
                AccessPage accessPage = new AccessPage(visitatorPage, environment.getProperty(visitatorPage));
                visitatorPages.add(accessPage);
            }
            AccessPage visitator = new AccessPage("visitator-workflow", environment.getProperty("visitatorWorkflow"));
            visitator.setModule(true);
            visitator.setSubPages(visitatorPages);

            //modules having without tab ids
            AccessPage dashBoard = new AccessPage("dashboard", environment.getProperty("dashboard"));
            dashBoard.setModule(true);

            AccessPage citizen = new AccessPage("citizen", environment.getProperty("citizen"));
            citizen.setModule(true);

            AccessPage planning = new AccessPage("planning",environment.getProperty("planning"));
            planning.setModule(true);

            accessPageRepository.saveAll(Arrays.asList(dashBoard, citizen, taskType, country, organization, visitator,planning));
        }
    }

    public void generateSequence(){
        // mongoSequenceRepository.save();
    }

   /* @Scheduled(cron = "0 10 15 * * ?")
    private void startHolidayDataFetchJob() throws URISyntaxException, ParseException {
        logger.debug("calendar job is running");
        try {
            countryHolidayCalenderService.updateCountryHolidayCalendar();
        }catch (URISyntaxException ue){
            logger.warn(ue.getMessage());
        }catch (ParseException pe){
            logger.warn(pe.getErrorOffset());
        }
    }*/
}
