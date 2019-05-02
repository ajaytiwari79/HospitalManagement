package com.kairos.config;

import com.kairos.constants.AppConstants;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.Gender;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.Currency;
import com.kairos.persistence.model.country.default_data.PaymentType;
import com.kairos.persistence.model.country.equipment.EquipmentCategory;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.UserBaseRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CurrencyGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentCategoryGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.payment_type.PaymentTypeGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ProvinceGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffRelationshipGraphRespository;
import com.kairos.persistence.repository.user.staff.UnitPermissionAndAccessPermissionGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitPermissionGraphRepository;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OpenningHourService;
import com.kairos.utils.CPRUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.user.UserType.SYSTEM_ACCOUNT;

/**
 * Created by kairosCountryLevel on 8/12/16.
 */
@Service
public class BootDataService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    com.kairos.service.organization.OrganizationService organizationService;
    @Inject
    StaffGraphRepository staffGraphRepository;
    @Inject
    AccessGroupRepository accessGroupRepository;
    @Inject
    OrganizationServiceRepository organizationServiceRepository;
    @Inject
    CountryGraphRepository countryGraphRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    SkillGraphRepository skillGraphRepository;
    @Inject
    UserGraphRepository userGraphRepository;
    @Inject
    TeamGraphRepository teamGraphRepository;
    @Inject
    UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    LanguageGraphRepository languageGraphRepository;
    @Inject
    CitizenStatusGraphRepository citizenStatusGraphRepository;
    @Inject
    ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    ProvinceGraphRepository provinceGraphRepository;
    @Inject
    RegionGraphRepository regionGraphRepository;
    @Inject
    PaymentTypeGraphRepository paymentTypeGraphRepository;
    @Inject
    CurrencyGraphRepository currencyGraphRepository;
    @Inject
    TimeSlotGraphRepository timeSlotGraphRepository;
      @Inject
    private OpenningHourService openningHourService;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private UnitPermissionAndAccessPermissionGraphRepository unitPermissionAndAccessPermissionGraphRepository;
    @Inject
    private EquipmentCategoryGraphRepository equipmentCategoryGraphRepository;
    @Inject
    private UserBaseRepository userBaseRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject private StaffRelationshipGraphRespository staffRelationshipGraphRespository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;

    private List<Long> skillList;
    private OrganizationService homeCareService;

    private OrganizationService medicalCareService;

    private Country denmark = null;

    private Organization kairosCountryLevel = null;
    private Organization kairosRegionLevel = null;

    private Team nestingTeam;

    private OrganizationType publicOrganization = null;
    private OrganizationType privateOrganization = null;
    private OrganizationType ngoOrganization = null;

    private User admin;
    private User systemUser;

    private Staff adminAsStaff;
    private AccessGroup accessGroup;
    private Position positionForAdmin;

    private Language danish;

    Region hovedstaden;

    Province copenhagenCity;
    Municipality copenhagen;
    Municipality frederiksberg;

    Province bornholm;
    Municipality ertholmene;


    Province Fyn;
    Municipality Odense;

    ZipCode kobenhavn;
    ZipCode allegade;

    public void createData() {
        if (countryGraphRepository.findAll().isEmpty()) {

            userBaseRepository.createFirstDBNode();

            createStandardTimeSlots();
            createMasterSkills();
            createOrganizationServices();
            createCountries();
            createOrganizationTypes();
            createPaymentTypes();
            createCurrency();
            createGeoGraphicalData();
            createUser();
            createSystemUser();
            createStaff();
            createCountryLevelOrganization();
            createRegionLevelOrganization();
            createTimeTypes();
//            createCTARuleTemplateCategory();
//            createCityLevelOrganization();
        }

        //createCTARuleTemplateCategory();
        //startRegisteredCronJobs();
        createEquipmentCategories();


    }

   /* private void startRegisteredCronJobs() {
        List<ControlPanel> controlPanels = controlPanelService.getAllControlPanels();
        if (controlPanels.size() != 0) {
            for (ControlPanel controlPanel : controlPanels) {
                logger.info("Register Cron Job of process name " + controlPanel.getName());
                dynamicCronScheduler.setCronScheduling(controlPanel);
            }
        }

    }*/

    private void createStandardTimeSlots() {
        String timeSlotsNames[] = new String[]{"Day", "Evening", "Night"};
        List<TimeSlot> standardTimeSlots = new ArrayList<>();
        for (String timeSlotName : timeSlotsNames) {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setName(timeSlotName);
            timeSlot.setSystemGeneratedTimeSlots(true);
            standardTimeSlots.add(timeSlot);
        }
        timeSlotGraphRepository.saveAll(standardTimeSlots);
    }

    private void createGeoGraphicalData() {

        hovedstaden = new Region();
        hovedstaden.setCountry(denmark);
        hovedstaden.setName("hovedstaden");
        hovedstaden.setCode("1084");
        regionGraphRepository.save(hovedstaden);

        copenhagenCity = new Province();
        copenhagenCity.setName("Copenhagen City");
        copenhagenCity.setRegion(hovedstaden);
        copenhagenCity = provinceGraphRepository.save(copenhagenCity);


        copenhagen = new Municipality();
        copenhagen.setName("Copenhagen");
        copenhagen.setProvince(copenhagenCity);
        copenhagen = municipalityGraphRepository.save(copenhagen);

        frederiksberg = new Municipality();
        frederiksberg.setName("Frederiksberg");
        frederiksberg.setProvince(copenhagenCity);
        frederiksberg = municipalityGraphRepository.save(frederiksberg);

        bornholm = new Province();
        bornholm.setName("Bornholm");
        bornholm.setRegion(hovedstaden);
        bornholm = provinceGraphRepository.save(bornholm);


        ertholmene = new Municipality();
        ertholmene.setName("Ertholmene");
        ertholmene.setProvince(bornholm);
        ertholmene = municipalityGraphRepository.save(ertholmene);


        Fyn = new Province();
        Fyn.setName("Fyn");
        Fyn = provinceGraphRepository.save(Fyn);

        Odense = new Municipality();
        Odense.setName("Odense");
        Odense.setProvince(Fyn);
        Odense = municipalityGraphRepository.save(Odense);

        kobenhavn = new ZipCode();
        List<Municipality> municipalities = kobenhavn.getMunicipalities();
        municipalities.add(frederiksberg);
        kobenhavn.setMunicipalities(municipalities);
        kobenhavn.setName("Kobenhavn");
        kobenhavn.setZipCode(1000);
        kobenhavn = zipCodeGraphRepository.save(kobenhavn);

        allegade = new ZipCode();
        municipalities = allegade.getMunicipalities();
        municipalities.add(frederiksberg);
        allegade.setMunicipalities(municipalities);
        allegade.setName("Allegade");
        allegade.setZipCode(2000);
        allegade = zipCodeGraphRepository.save(allegade);


    }

    private void createMasterSkills() {
        String personalSkills[] = new String[]{"Bathing Partial", "Full Bathing", "Assist with feeding"};
        SkillCategory personalSkillCategory = new SkillCategory("Personal Skills");
        personalSkillCategory.setCountry(denmark);
        skillList = new ArrayList<>();
        Skill skill;
        for (String personalSkill : personalSkills) {
            skill = new Skill(personalSkill, personalSkillCategory);
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }
        SkillCategory medicalSkillCategory = new SkillCategory("Medical Skills");
        medicalSkillCategory.setCountry(denmark);
        String medicalSkills[] = new String[]{"Pharma knowledge", "Basic Medical Checkup", "Basic Nursing"};
        for (String medicalSkill : medicalSkills) {
            skill = new Skill(medicalSkill, medicalSkillCategory);
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }
        SkillCategory homeSkillCategory = new SkillCategory("Home Skills");
        homeSkillCategory.setCountry(denmark);
        String homeSkills[] = new String[]{"Home Cleaning", "Dish Washing", "Cooking food"};
        for (String homeSkill : homeSkills) {
            skill = new Skill(homeSkill, homeSkillCategory);
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }

        danish = new Language("Danish", false);
        danish.setCountry(denmark);
        danish = languageGraphRepository.save(danish);
    }

    private void createOrganizationServices() {
        homeCareService = new OrganizationService("Home Care");
        homeCareService.setOrganizationSubService(Arrays.asList(
                new OrganizationService("Home Dusting"),
                new OrganizationService("Home Cooking"),
                new OrganizationService("Home Maintenance")));

        medicalCareService = new OrganizationService("Medical Service");
        medicalCareService.setOrganizationSubService(Arrays.asList(
                new OrganizationService("Basic Checkup"),
                new OrganizationService("Disease Diagnose"),
                new OrganizationService("Medication")));

        organizationServiceRepository.saveAll(Arrays.asList(homeCareService, medicalCareService));

    }

    private void createOrganizationTypes() {
        publicOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.PUBLIC.value, homeCareService.getOrganizationSubService());
        publicOrganization.setCountry(denmark);

        privateOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.PRIVATE.value, medicalCareService.getOrganizationSubService());
        privateOrganization.setCountry(denmark);

        ngoOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.NGO.value,
                homeCareService.getOrganizationSubService());
        ngoOrganization.setCountry(denmark);
        ngoOrganization.setCountry(denmark);

        organizationTypeGraphRepository.save(publicOrganization);
        organizationTypeGraphRepository.save(privateOrganization);
        organizationTypeGraphRepository.save(ngoOrganization);
    }

    private void createCountries() {
        denmark = new Country();
        denmark.setName("Denmark");
        countryGraphRepository.save(denmark);
    }

     private void createCountryLevelOrganization() {

        kairosCountryLevel = new OrganizationBuilder().createOrganization();
        kairosCountryLevel.setKairosHub(true);
        kairosCountryLevel.setBoardingCompleted(true);
        kairosCountryLevel.setFormalName("Kairos (HUB)");
        kairosCountryLevel.setName(KAIROS);
        kairosCountryLevel.setEanNumber("501234567890");
        kairosCountryLevel.setEmail(KAIROS_DENMARK_EMAIL);
        kairosCountryLevel.setContactDetail(new ContactDetail("info@kairos.com", KAIROS_DENMARK_EMAIL, "431311", "653322"));
//        kairosCountryLevel.setContactAddress(new ContactAddress("Thorsgade", 2, 5000, "Odense", 4345, "Commercial"));
//        kairosCountryLevel.setOrganizationType(privateOrganization);
        kairosCountryLevel.setCostCenterCode("OD12");
        kairosCountryLevel.setOrganizationLevel(OrganizationLevel.COUNTRY);
        kairosCountryLevel.setCountry(denmark);
        kairosCountryLevel.setParentOrganization(true);
        ContactAddress contactAddress = new ContactAddress();
        contactAddress.setZipCode(allegade);
        contactAddress.setFloorNumber(10);
        contactAddress.setHouseNumber("403");
        contactAddress.setStreet("Kastanievej 1");
        contactAddress.setLongitude(77.026638f);
        contactAddress.setLatitude(28.459497f);
        contactAddress.setMunicipality(frederiksberg);
        contactAddress.setRegionCode(frederiksberg.getProvince().getRegion().getCode());
        contactAddress.setRegionName(frederiksberg.getProvince().getRegion().getName());
        contactAddress.setProvince(frederiksberg.getProvince().getName());
        kairosCountryLevel.setContactAddress(contactAddress);
        OrganizationSetting organizationSetting = openningHourService.getDefaultSettings();
        kairosCountryLevel.setOrganizationSetting(organizationSetting);

        organizationService.createOrganization(kairosCountryLevel, null, true);
        //organizationGraphRepository.addSkillInOrganization(kairosCountryLevel.getId(),skillList,DateUtil.getCurrentDate().getTime(),DateUtil.getCurrentDate().getTime());

        // Create AccessGroup for Ulrik as AG_COUNTRY_ADMIN
        createSuperAdminAccessGroup();
        createPosition();
        createTeam();

        linkingOfStaffAndTeam();
        createUnitEmploymentForCountryLevel();
    }

    private void createUser() {
        admin = new User();
        admin.setCprNumber("0309514297");
        admin.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(admin.getCprNumber()));
        admin.setUserName(ULRIK_EMAIL);
        admin.setEmail(ULRIK_EMAIL);
        admin.setPassword(new BCryptPasswordEncoder().encode("admin@kairos"));
        admin.setFirstName(ULRIK);
        admin.setNickName(ULRIK);
        admin.setLastName("Rasmussen");
        admin.setGender(Gender.MALE);
//        admin.setContactAddress(new ContactAddress("Rosewood Street", 1, 5421, "Glostrup", 2123, "Apartments"));
        admin.setContactDetail(new ContactDetail("ulrik_01@kairoscountrylevel.com", "alma007@gmail.com", "536533", "facebook.com/ulrik_cool"));
        //        admin.setAge(28);
        //userGraphRepository.save(Arrays.asList(michal, liva, alma,admin));
        userGraphRepository.save(admin);
    }

    private void createSystemUser() {
        systemUser = new User();

        systemUser.setUserName("systemuser@kairos.com");
        systemUser.setEmail("systemuser@kairos.com");
        systemUser.setPassword(new BCryptPasswordEncoder().encode("admin@kairos"));
        systemUser.setUserType(SYSTEM_ACCOUNT);
        userGraphRepository.save(systemUser);
    }


    private void createStaff() {
        adminAsStaff = new Staff();
        ContactAddress contactAddress = new ContactAddress();
//        adminAsStaff.setContactAddress(new ContactAddress("Rosewood Street", 3, 1221, "Glostrup", 4533, "Apartments"));
        adminAsStaff.setGeneralNote("Will manage the platform");
        adminAsStaff.setUser(admin);
        adminAsStaff.setSignature("ulrik");
        adminAsStaff.setCardNumber("LPSPSW1134");
        adminAsStaff.setCopyKariosMailToLogin(true);
        adminAsStaff.setContactDetail(new ContactDetail("admin@kairos.com", "admin@kairos.com", "536533", "facebook.com/ulrik"));
        adminAsStaff.setFamilyName(ULRIK);
        adminAsStaff.setFirstName(ULRIK);
        adminAsStaff.setLastName("Rasmussen");
        adminAsStaff.setCurrentStatus(StaffStatusEnum.ACTIVE);
        adminAsStaff.setEmail(ULRIK_EMAIL);
        adminAsStaff.setNationalInsuranceNumber("NIN44500331");
        adminAsStaff.setLanguage(danish);
        adminAsStaff.setPassword("kairos");
        adminAsStaff.setPassword(new BCryptPasswordEncoder().encode("kairos"));

        staffGraphRepository.save(adminAsStaff);


    }

    private void createTeam() {
        //creating teams
        nestingTeam = new Team();
        nestingTeam.setName("Nesting Team");
        teamGraphRepository.saveAll(Arrays.asList(nestingTeam));
    }


    private void linkingOfStaffAndTeam() {
        staffRelationshipGraphRespository.save(new StaffRelationship(nestingTeam, adminAsStaff));
    }


    private void createSuperAdminAccessGroup() {
        accessGroup = new AccessGroup(AppConstants.SUPER_ADMIN, "Country Admin Access Group", AccessGroupRole.MANAGEMENT);
        accessGroupRepository.save(accessGroup);
    }

    private void createPosition() {
        positionForAdmin = new Position("working as country admin", adminAsStaff);
        kairosCountryLevel.getPositions().add(positionForAdmin);
        organizationGraphRepository.save(kairosCountryLevel);
    }

    private void createUnitEmploymentForCountryLevel() {

//        accessGroup = accessGroupRepository.getAccessGroupOfOrganizationByName(kairosCountryLevel.getId(), AppConstants.AG_COUNTRY_ADMIN);
        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(kairosCountryLevel);
        unitPermission.setAccessGroup(accessGroup);
//        accessGroup = accessGroupRepository.findAccessGroupByName(kairosCountryLevel.getId(), AppConstants.AG_COUNTRY_ADMIN);


//        AccessPermission accessPermission = new AccessPermission(accessGroup);
        unitPermissionGraphRepository.save(unitPermission);
//        UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitPermission, accessPermission);
//        unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
//        accessPageService.setPagePermissionToAdmin(accessPermission);
        positionForAdmin.getUnitPermissions().add(unitPermission);
        kairosCountryLevel.getPositions().add(positionForAdmin);
        organizationGraphRepository.save(kairosCountryLevel);
    }


    private void createRegionLevelOrganization() {


        kairosRegionLevel = new OrganizationBuilder().createOrganization();
        kairosRegionLevel.setFormalName(KAIROS);
        kairosRegionLevel.setName(KAIROS);
        kairosRegionLevel.setEanNumber("501234567890");
        kairosRegionLevel.setEmail("kairos_zealand@kairos.com");
        kairosRegionLevel.setContactDetail(new ContactDetail("info@kairos.com", KAIROS_DENMARK_EMAIL, "431311", "653322"));
        //    kairosRegionLevel.setOrganizationType(privateOrganization);
        kairosRegionLevel.setCostCenterCode("OD12");
        kairosRegionLevel.setOrganizationLevel(OrganizationLevel.REGION);
        kairosRegionLevel.setCountry(denmark);
        kairosRegionLevel.setKairosHub(false);
        kairosRegionLevel.setBoardingCompleted(true);
        ContactAddress contactAddress = new ContactAddress();
        contactAddress.setZipCode(allegade);
        contactAddress.setFloorNumber(10);
        contactAddress.setHouseNumber("403");
        contactAddress.setStreet("Kastanievej 1");
        contactAddress.setLongitude(77.026638f);
        contactAddress.setLatitude(28.459497f);
        contactAddress.setMunicipality(frederiksberg);
        contactAddress.setRegionCode(frederiksberg.getProvince().getRegion().getCode());
        contactAddress.setRegionName(frederiksberg.getProvince().getRegion().getName());
        contactAddress.setProvince(frederiksberg.getProvince().getName());
        kairosRegionLevel.setContactAddress(contactAddress);
        OrganizationSetting organizationSetting = openningHourService.getDefaultSettings();
        kairosRegionLevel.setOrganizationSetting(organizationSetting);
        organizationService.createOrganization(kairosRegionLevel, kairosCountryLevel.getId(), true);

        //organizationGraphRepository.addOrganizationServiceInUnit(kairosRegionLevel.getId(),Arrays.asList(privateOrganization.getOrganizationServiceList().get(0).getId()),DateUtil.getCurrentDate().getTime(),DateUtil.getCurrentDate().getTime());
    }

    private void createPaymentTypes() {
        PaymentType creditCard = new PaymentType();
        creditCard.setName("Credit Cards");
        creditCard.setCountry(denmark);

        PaymentType paySafecard = new PaymentType();
        paySafecard.setName("Paysafecard");
        paySafecard.setCountry(denmark);
        paymentTypeGraphRepository.saveAll(Arrays.asList(creditCard, paySafecard));
    }

    private void createCurrency() {
        Currency currency = new Currency();
        currency.setName("krone");
        currency.setCountry(denmark);
        currencyGraphRepository.save(currency);
    }

    /*private void createCTARuleTemplateCategory() {
        RuleTemplateCategory category = ruleTemplateCategoryGraphRepository.findByName(denmark.getId(), "NONE", RuleTemplateCategoryType.CTA);
        if (!Optional.ofNullable(category).isPresent()) {
            category = new RuleTemplateCategory("NONE", RuleTemplateCategoryType.CTA);
            category.setCountry(denmark);
//            ruleTemplateCategoryService.createDefaultRuleTemplateCategory( category);
        }*/

        // No need to create default CTA Rule templates
        /*if (costTimeAgreementService.isDefaultCTARuleTemplateExists()) {
            logger.info("default CTA rule template already exist");
        } else {
            logger.info("creating CTA rule template");
            costTimeAgreementService.createDefaultCtaRuleTemplate(country.getId());
        }

    }*/

    private void createEquipmentCategories() {
        if (!equipmentCategoryGraphRepository.ifEquipmentCategoryExists()) {
            EquipmentCategory equipmentCategorySmall = new EquipmentCategory();
            equipmentCategorySmall.setName("Small");
            equipmentCategorySmall.setDescription("Small");
            equipmentCategorySmall.setWeightInKg(20F);
            equipmentCategorySmall.setHeightInCm(20F);
            equipmentCategorySmall.setWidthInCm(20F);
            equipmentCategorySmall.setLengthInCm(20F);
            equipmentCategorySmall.setVolumeInCm(20F);

            EquipmentCategory equipmentCategoryMedium = new EquipmentCategory();
            equipmentCategoryMedium.setName("Medium");
            equipmentCategoryMedium.setDescription("Medium");
            equipmentCategoryMedium.setWeightInKg(50F);
            equipmentCategoryMedium.setHeightInCm(50F);
            equipmentCategoryMedium.setWidthInCm(50F);
            equipmentCategoryMedium.setLengthInCm(50F);
            equipmentCategoryMedium.setVolumeInCm(50F);

            EquipmentCategory equipmentCategoryLarge = new EquipmentCategory();
            equipmentCategoryLarge.setName("Large");
            equipmentCategoryLarge.setDescription("Large");
            equipmentCategoryLarge.setWeightInKg(100F);
            equipmentCategoryLarge.setHeightInCm(100F);
            equipmentCategoryLarge.setWidthInCm(100F);
            equipmentCategoryLarge.setLengthInCm(100F);
            equipmentCategoryLarge.setVolumeInCm(100F);

            equipmentCategoryGraphRepository.saveAll(Arrays.asList(equipmentCategorySmall, equipmentCategoryMedium, equipmentCategoryLarge));
        }
    }


    private void createTimeTypes() {
        try {
            activityIntegrationService.createTimeTypes(denmark.getId());
        }
        catch (HttpClientErrorException e){
            logger.debug("Some error occurred in activity micro service");
        }

    }
}