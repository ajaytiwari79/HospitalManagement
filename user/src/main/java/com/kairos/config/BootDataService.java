package com.kairos.config;

import com.kairos.config.scheduler.DynamicCronScheduler;
import com.kairos.constants.AppConstants;
import com.kairos.enums.Gender;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.CitizenStatus;
import com.kairos.persistence.model.country.equipment.EquipmentCategory;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.staff.employment.Employment;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitEmpAccessRelationship;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.control_panel.ControlPanel;
import com.kairos.persistence.model.user.department.Department;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.payment_type.PaymentType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.repository.organization.*;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.UserBaseRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientLanguageRelationGraphRepository;
import com.kairos.persistence.repository.user.client.ClientOrganizationRelationGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CurrencyGraphRepository;
import com.kairos.persistence.repository.user.country.EquipmentCategoryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.payment_type.PaymentTypeGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ProvinceGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.agreement.RuleTemplateCategoryService;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.service.auth.RoleServiceUser;
import com.kairos.service.auth.UserRoleServiceUser;
import com.kairos.service.auth.UserService;
import com.kairos.service.client.ClientOrganizationRelationService;
import com.kairos.service.client.ClientService;
import com.kairos.service.control_panel.ControlPanelService;
import com.kairos.service.country.CountryService;
import com.kairos.service.organization.OpenningHourService;
import com.kairos.service.organization.OrganizationTypeService;
import com.kairos.service.organization.TeamService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffService;
import com.kairos.user.access_permission.AccessGroupRole;
import com.kairos.util.CPRUtil;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by kairosCountryLevel on 8/12/16.
 */
@Service
public class BootDataService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private ClientLanguageRelationGraphRepository clientLanguageRelationGraphRepository;

    @Inject
    UserService userService;
    @Inject
    com.kairos.service.organization.OrganizationService organizationService;
    @Inject
    SkillService skillService;
    @Inject
    CountryService countryService;
    @Inject
    RoleServiceUser roleService;
    @Inject
    ClientService clientService;
    @Inject
    UserRoleServiceUser userRoleService;
    @Inject
    TeamService teamService;
    @Inject
    ControlPanelService controlPanelService;
    @Inject
    StaffGraphRepository staffGraphRepository;
    @Inject
    ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    ClientOrganizationRelationGraphRepository clientOrganizationRelationGraphRepository;
    @Inject
    AccessGroupRepository accessGroupRepository;
    @Inject
    OpeningHourGraphRepository openingHourGraphRepository;
    @Inject
    OrganizationServiceRepository organizationServiceRepository;
    @Inject
    CountryGraphRepository countryGraphRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    SkillGraphRepository skillGraphRepository;
    @Inject
    ResourceGraphRepository resourceGraphRepository;
    @Inject
    UserGraphRepository userGraphRepository;
    @Inject
    TeamGraphRepository teamGraphRepository;
    @Inject
    AccessGroupService accessGroupService;
    @Inject
    UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    StaffService staffService;
    @Inject
    ClientOrganizationRelationService relationService;
    @Inject
    EmploymentGraphRepository employmentGraphRepository;
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
    ContactAddressGraphRepository contactAddressGraphRepository;
    @Inject
    OrganizationTypeService organizationTypeService;
    @Inject
    PaymentTypeGraphRepository paymentTypeGraphRepository;
    @Inject
    CurrencyGraphRepository currencyGraphRepository;
    @Inject
    TimeSlotGraphRepository timeSlotGraphRepository;
    @Inject
    DynamicCronScheduler dynamicCronScheduler;
    @Inject
    private OpenningHourService openningHourService;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private UnitEmpAccessGraphRepository unitEmpAccessGraphRepository;
    @Inject

    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    @Inject
    private CostTimeAgreementService costTimeAgreementService;
    @Inject
    private EquipmentCategoryGraphRepository equipmentCategoryGraphRepository;
    @Inject
    private UserBaseRepository userBaseRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject private StaffRelationshipGraphRespository staffRelationshipGraphRespository;
    private List<Long> skillList;
    private OrganizationService homeCareService;

    private OrganizationService medicalCareService;

    private Country denmark = null;
    private Country germany = null;

    private Organization kairosCountryLevel = null;
    private Organization kairosRegionLevel = null;
    private Organization oodlesCityLevel = null;

    private OrganizationSetting oodlesSetting = null;

    private Client johnOliver = null;

    private Department hrDept;
    private Group operationsGroup;
    private Group serviceGroup;

    private Team nestingTeam;
    private Team experiencedTeam;

    private Resource van = null;
    private Resource bicycle = null;
    private Resource ambulance = null;

    private OrganizationType type1 = null;

    private OrganizationType publicOrganization = null;
    private OrganizationType privateOrganization = null;
    private OrganizationType ngoOrganization = null;

    private User alma;
    private User michal;
    private User liva;
    private User admin;

    private Staff almaAsStaff;
    private Staff michalAsStaff;
    private Staff livaAsStaff;
    private Staff adminAsStaff;
    private AccessGroup accessGroup;
    private Employment employmentForMichal;
    private Employment employmentForLiva;
    private Employment employmentForAlma;
    private Employment employmentForAdmin;

    private Language danish;

    Region hovedstaden;
    Region Syddanmark;

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
       /* if (countryGraphRepository.findAll().isEmpty()) {

            userBaseRepository.createFirstDBNode();

            createStandardTimeSlots();
            //createMasterSkills();
            //createOrganizationServices();
            createCountries();
            //createOrganizationTypes();
            //createPaymentTypes();
            createCurrency();
            createGeoGraphicalData();
            createUser();
            createStaff();
            createCountryLevelOrganization();
            createRegionLevelOrganization();
            createCTARuleTemplateCategory();
            //createCityLevelOrganization();
            //createCitizen();
        }

        createCTARuleTemplateCategory();
        startRegisteredCronJobs();
        createEquipmentCategories();
*/

    }

    private void startRegisteredCronJobs() {
        List<ControlPanel> controlPanels = controlPanelService.getAllControlPanels();
        if (controlPanels.size() != 0) {
            for (ControlPanel controlPanel : controlPanels) {
                logger.info("Register Cron Job of process name " + controlPanel.getName());
                dynamicCronScheduler.setCronScheduling(controlPanel);
            }
        }
    }

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
            skill.setCreationDate(DateUtil.getCurrentDate().getTime());
            skill.setLastModificationDate(DateUtil.getCurrentDate().getTime());
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }
        SkillCategory medicalSkillCategory = new SkillCategory("Medical Skills");
        medicalSkillCategory.setCountry(denmark);
        String medicalSkills[] = new String[]{"Pharma knowledge", "Basic Medical Checkup", "Basic Nursing"};
        for (String medicalSkill : medicalSkills) {
            skill = new Skill(medicalSkill, medicalSkillCategory);
            skill.setCreationDate(DateUtil.getCurrentDate().getTime());
            skill.setLastModificationDate(DateUtil.getCurrentDate().getTime());
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }
        SkillCategory homeSkillCategory = new SkillCategory("Home Skills");
        homeSkillCategory.setCountry(denmark);
        String homeSkills[] = new String[]{"Home Cleaning", "Dish Washing", "Cooking food"};
        for (String homeSkill : homeSkills) {
            skill = new Skill(homeSkill, homeSkillCategory);
            skill.setCreationDate(DateUtil.getCurrentDate().getTime());
            skill.setLastModificationDate(DateUtil.getCurrentDate().getTime());
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
        homeCareService.setCreationDate(DateUtil.getCurrentDate().getTime());
        homeCareService.setLastModificationDate(DateUtil.getCurrentDate().getTime());

        medicalCareService = new OrganizationService("Medical Service");
        medicalCareService.setOrganizationSubService(Arrays.asList(
                new OrganizationService("Basic Checkup"),
                new OrganizationService("Disease Diagnose"),
                new OrganizationService("Medication")));
        medicalCareService.setCreationDate(DateUtil.getCurrentDate().getTime());
        medicalCareService.setLastModificationDate(DateUtil.getCurrentDate().getTime());

        organizationServiceRepository.saveAll(Arrays.asList(homeCareService, medicalCareService));

    }

    private void createOrganizationTypes() {
        publicOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.PUBLIC.value, homeCareService.getOrganizationSubService());
        publicOrganization.setCreationDate(DateUtil.getCurrentDate().getTime());
        publicOrganization.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        publicOrganization.setCountry(denmark);

        privateOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.PRIVATE.value, medicalCareService.getOrganizationSubService());
        privateOrganization.setCreationDate(DateUtil.getCurrentDate().getTime());
        privateOrganization.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        privateOrganization.setCountry(denmark);

        ngoOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.NGO.value,
                homeCareService.getOrganizationSubService());
        ngoOrganization.setCountry(denmark);


        ngoOrganization.setCreationDate(DateUtil.getCurrentDate().getTime());
        ngoOrganization.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        ngoOrganization.setCountry(denmark);


        organizationTypeGraphRepository.save(publicOrganization);
        organizationTypeGraphRepository.save(privateOrganization);
        organizationTypeGraphRepository.save(ngoOrganization);


    }

    private void createCountries() {
        denmark = new Country();
        denmark.setName("Denmark");
        // denmark.setCountryHolidayCalenderList(Arrays.asList(new CountryHolidayCalender("Halloween", DateUtil.getCurrentDate().getTime()), new CountryHolidayCalender("Christmas",  1474696870000L)));
        // denmark.setOrganizationServices(Arrays.asList(homeCareService, medicalCareService));
        countryGraphRepository.save(denmark);
        //  createCitizenStatus();


    }

    private void createCitizenStatus() {
        CitizenStatus deadStatus = new CitizenStatus();
        deadStatus.setName("Dead");
        deadStatus.setDescription("When pulse stops , patient is declared dead");


        CitizenStatus singleStatus = new CitizenStatus();
        singleStatus.setDescription("Single");
        singleStatus.setDescription("A Citizen is not married ");
        singleStatus.setCountry(denmark);

        CitizenStatus marriedStatus = new CitizenStatus();
        marriedStatus.setName("Married");
        marriedStatus.setDescription("A Citizen is have a wife");
        marriedStatus.setCountry(denmark);

        CitizenStatus disvorcedStatus = new CitizenStatus();
        disvorcedStatus.setName("Divorced");
        disvorcedStatus.setDescription("A Citizen was married ,but now divorced ");
        disvorcedStatus.setCountry(denmark);

        CitizenStatus livingPartnerStatus = new CitizenStatus();
        livingPartnerStatus.setName("Longest living partner");
        livingPartnerStatus.setDescription("A Citizen is living Partner");
        livingPartnerStatus.setCountry(denmark);

        CitizenStatus registeredStatus = new CitizenStatus();
        registeredStatus.setName("Registered partnership");
        registeredStatus.setDescription("Relationship of Citizen is registered formally");
        registeredStatus.setCountry(denmark);

        citizenStatusGraphRepository.saveAll(Arrays.asList(registeredStatus, livingPartnerStatus, disvorcedStatus, marriedStatus, singleStatus, deadStatus));
    }
/*
    private void createCitizen() {
        johnOliver = new Client();
        johnOliver.setClientType(ClientEnum.INDIVIDUAL);
        johnOliver.setFirstName("John");
        johnOliver.setLastName("Oliver");
        johnOliver.setNickName("Johnny");
//        johnOliver.setAge(57);
//        johnOliver.setCivilianStatus(ClientEnum.CivilianStatus.MARRIED);
        johnOliver.setOccupation("Stock Broker");
        johnOliver.setGender(Gender.MALE);
        johnOliver.setCitizenship(ClientEnum.CitizenShip.DANISH);

        List<Map<String, Object>> zipCodeMapList = zipCodeGraphRepository.getAllZipCodeByCountryId(denmark.getId());

        AccessToLocation accessToLocation = new AccessToLocation("AlphaNumeric", "21", "412398", "Cellular phone", "UYTRE7654321", "Unlocking the door",
                "Simple keySystem with password security", true, "Checking purpose", "Enter the passcode and press unlock", "Must lock the door after serving purpose");

        AccessToLocation accessToLocation2 = new AccessToLocation("AlphaNumeric", "45", "512342", "Cellular phone", "MNTLE7654321", "Unlocking the door",
                "Simple keySystem with password security", true, "Checking purpose", "Enter the passcode and press unlock", "Must lock the door after serving purpose");

        johnOliver.setContactDetail(new ContactDetail("john.oliver@stockExchange.com", "john.oliver21@gmail.com", "4234423", "4139156"));
        ContactAddress home = new ContactAddress("Park Street", 3, "12", allegade);
        home.setLatitude(28.412582F);
        home.setLongitude(77.043488F);
        home.setAccessToLocation(accessToLocation);
//        ContactAddress second = new ContactAddress("Smith Street", 2, 766112264, "Copenhagen", 722, "Apartment", accesToLocation2, true);
//        ContactAddress partner = new ContactAddress("Rosewood Street", 1, 1131, "Copenhagen", 122, "Apartment");
//        ContactAddress office = new ContactAddress("Metalbuen", 2, 7662246, "Ballerup", 123, "Commercial");
        home = contactAddressGraphRepository.save(home);
        johnOliver.setHomeAddress(home);
//        johnOliver.setOfficeAddress(office);
//        johnOliver.setSecondaryAddress(second);
//        johnOliver.setPartnerAddress(partner);

        johnOliver.setRequiredEquipmentsList("Oxygen Support, Walk Stick, Other Equipment");
        johnOliver.setDriverLicenseNumber("ABOC7534");
        johnOliver.setPlaceOfBirth("Copenhagen");
        johnOliver.setDoRequireTranslationAssistance(false);
        johnOliver.setLivesAlone(true);
        johnOliver.setPeopleInHousehold(false);
//        Client firstHousehold= new Client();
//        firstHousehold.setFirstName("Tilde");
//        firstHousehold.setLastName("O. Sørensen");
//        firstHousehold.setCprNumber("120943-0416");
//        firstHousehold
//        Client secondHousehold= new Client();
//        secondHousehold.setFirstName("Stine");
//        secondHousehold.setLastName("Laursen");
//        secondHousehold .setCprNumber("310849-4742");
//
/        johnOliver.setPeopleInHouseholdList(Arrays.asList(firstHousehold,secondHousehold))


        johnOliver.setWheelChair(false);
        johnOliver.setLiftBus(false);
        johnOliver.setRequire2peopleForTransport(false);
        johnOliver.setRequireMinibusForTransport(false);
        johnOliver.setRequireOxygenUnderTransport(true);
        johnOliver.setPortPhoneNumber("32");
        johnOliver.setMemberOfDenmark(true);
        johnOliver.setCitizenGettingPension(true);
        johnOliver.setCitizenPensionType("Regular");
        johnOliver.setCitizenTerminal(true);
        johnOliver.setCitizenTerminalDescription("No Description");
        johnOliver.setDoesHaveAllergies(false);
        johnOliver.setDoesHaveDiseases(true);
        johnOliver.setDoesHaveDiagnoses(false);
        johnOliver.setClientAllergiesList(null);
        johnOliver.setClientDiagnoseList(null);
        johnOliver.setCprNumber("1508574653");
        johnOliver.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(johnOliver.getCprNumber()));
        johnOliver.setClientDiagnoseList(Arrays.asList(new ClientDiagnose("Heart Diagnose", " Moderate Condition", "positive", "Eat Healthy"),
                new ClientDiagnose("Lung Diagnose", " Poor Condition", "positive", "Quit Smoking")));
        johnOliver.setTranslationLanguage(new String[]{"Danish"});
        johnOliver.setClientDiseaseList(Arrays.asList(new ClientDisease("Hepatitis B", "Common", "Don't allow Junk food")));

        ClientMinimumDTO minimumDTO = new ClientMinimumDTO();
        minimumDTO.setFirstName("Kin");
        minimumDTO.setLastName("yong");
        minimumDTO.setCprnumber("1106513681");
        Client nextKin = clientService.createCitizen(minimumDTO, oodlesCityLevel.getId());

        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setMobilePhone("4566353");
        contactDetail.setLandLinePhone("223221");
        contactDetail.setWorkPhone("765436");
        nextKin.setContactDetail(contactDetail);

//        johnOliver.setNextToKin(nextKin);

        johnOliver.setClientAllergiesList(Arrays.asList(
                new ClientAllergies("type1 ", "Allergy1", true, new String[]{"Avoid1", "Avoid2", "Avoid3"}
                )));
        Client client = clientService.createCitizen(johnOliver);
        ClientOrganizationRelation clientOrganizationRelation = new ClientOrganizationRelation(client, oodlesCityLevel, new DateTime().getMillis());
        relationService.createRelation(clientOrganizationRelation);

        ClientLanguageRelation clientLanguageRelation = new ClientLanguageRelation();
        clientLanguageRelation.setClient(client);
        clientLanguageRelation.setLanguage(danish);
        clientLanguageRelationGraphRepository.save(clientLanguageRelation);

    }
*/    private void createCountryLevelOrganization() {

        kairosCountryLevel = new Organization();
        kairosCountryLevel.setKairosHub(true);
        kairosCountryLevel.setBoardingCompleted(true);
        kairosCountryLevel.setFormalName("Kairos (HUB)");
        kairosCountryLevel.setName("Kairos");
        kairosCountryLevel.setEanNumber("501234567890");
        kairosCountryLevel.setEmail("kairos_denmark@kairos.com");
        kairosCountryLevel.setContactDetail(new ContactDetail("info@kairos.com", "kairos_denmark@kairos.com", "431311", "653322"));
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
        contactAddress.setStreet1("Kastanievej 1");
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
        createCountryAdminAccessGroup();
        createEmployment();
        createTeam();

        linkingOfStaffAndTeam();
        createUnitEmploymentForCountryLevel();
//        createGroup();
    }

    private void createUser() {
        admin = new User();
        admin.setCprNumber("0309514297");
        admin.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(admin.getCprNumber()));
        admin.setUserName("ulrik@kairos.com");
        admin.setEmail("ulrik@kairos.com");
        admin.setPassword(new BCryptPasswordEncoder().encode("admin@kairos"));
        admin.setFirstName("Ulrik");
        admin.setNickName("Ulrik");
        admin.setLastName("Rasmussen");
        admin.setGender(Gender.MALE);
//        admin.setContactAddress(new ContactAddress("Rosewood Street", 1, 5421, "Glostrup", 2123, "Apartments"));
        admin.setContactDetail(new ContactDetail("ulrik_01@kairoscountrylevel.com", "alma007@gmail.com", "536533", "facebook.com/ulrik_cool"));
        //        admin.setAge(28);
        admin.setCreationDate(DateUtil.getCurrentDate().getTime());
        admin.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        //userGraphRepository.save(Arrays.asList(michal, liva, alma,admin));
        userGraphRepository.save(admin);
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
        adminAsStaff.setFamilyName("Ulrik");
        adminAsStaff.setFirstName("Ulrik");
        adminAsStaff.setLastName("Rasmussen");
        adminAsStaff.setCurrentStatus(StaffStatusEnum.ACTIVE);
        adminAsStaff.setEmail("ulrik@kairos.com");
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
        nestingTeam.setCreationDate(DateUtil.getCurrentDate().getTime());
        nestingTeam.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        teamGraphRepository.saveAll(Arrays.asList(nestingTeam));
    }


    private void linkingOfStaffAndTeam() {
        staffRelationshipGraphRespository.save(new StaffRelationship(nestingTeam, adminAsStaff));
    }

    private void createGroup() {
        operationsGroup = new Group();
        operationsGroup.setName("Operations Group");
        operationsGroup.setTeamList(Arrays.asList(nestingTeam));

        kairosCountryLevel.setGroupList(Arrays.asList(operationsGroup));
        kairosCountryLevel = organizationGraphRepository.save(kairosCountryLevel);
    }

    private void createCountryAdminAccessGroup() {
        accessGroup = new AccessGroup(AppConstants.AG_COUNTRY_ADMIN, "Country Admin Access Group", AccessGroupRole.MANAGEMENT);
        accessGroup.setCreationDate(DateUtil.getCurrentDate().getTime());
        accessGroup.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        accessGroupRepository.save(accessGroup);
    }

    private void createEmployment() {
        employmentForAdmin = new Employment("working as country admin", adminAsStaff);
        kairosCountryLevel.getEmployments().add(employmentForAdmin);
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
        employmentForAdmin.getUnitPermissions().add(unitPermission);
        kairosCountryLevel.getEmployments().add(employmentForAdmin);
        organizationGraphRepository.save(kairosCountryLevel);
    }

    private void createOodlesDepartments() {
        Staff hr = new Staff("August");
        hr.setLastName("Knudsen");

        Staff hrManager = new Staff("Olivia");
        hrManager.setLastName("Mikkelsen");

        hrDept = new Department();
        hrDept.setName("Human Resource");
        hrDept.setTeams(Arrays.asList(hrManager, hr));


        oodlesCityLevel.setDepartments(Arrays.asList(hrDept));
        organizationGraphRepository.save(oodlesCityLevel);
    }

    private void createRegionLevelOrganization() {


        kairosRegionLevel = new Organization();
        kairosRegionLevel.setFormalName("Kairos");
        kairosRegionLevel.setName("Kairos");
        kairosRegionLevel.setEanNumber("501234567890");
        kairosRegionLevel.setEmail("kairos_zealand@kairos.com");
        kairosRegionLevel.setContactDetail(new ContactDetail("info@kairos.com", "kairos_denmark@kairos.com", "431311", "653322"));
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
        contactAddress.setStreet1("Kastanievej 1");
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


    private void createPublicPhoneNumberForCityLevel() {
        PublicPhoneNumber publicPhoneNumber = new PublicPhoneNumber("7100000000");
        List<PublicPhoneNumber> publicPhoneNumberList = new ArrayList<PublicPhoneNumber>();
        publicPhoneNumberList.add(publicPhoneNumber);
        oodlesCityLevel.setPublicPhoneNumberList(publicPhoneNumberList);
        organizationGraphRepository.save(oodlesCityLevel);
    }

    private void createTeamsForCityLevel() {
        nestingTeam = new Team();
        nestingTeam.setName("Nesting Team");
        nestingTeam.setCreationDate(DateUtil.getCurrentDate().getTime());
        nestingTeam.setLastModificationDate(DateUtil.getCurrentDate().getTime());

        experiencedTeam = new Team();
        experiencedTeam.setName("Experienced Team");
        experiencedTeam.setCreationDate(DateUtil.getCurrentDate().getTime());
        experiencedTeam.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        teamGraphRepository.saveAll(Arrays.asList(nestingTeam, experiencedTeam));
    }

    private void createGroupForCityLevel() {
        operationsGroup = new Group();
        operationsGroup.setName("oodles-operations Group");
        operationsGroup.setTeamList(Arrays.asList(nestingTeam));

        serviceGroup = new Group();
        serviceGroup.setName("oodles-Service Group");
        serviceGroup.setTeamList(Arrays.asList(experiencedTeam));
        oodlesCityLevel.setGroupList(Arrays.asList(operationsGroup, serviceGroup));
        organizationGraphRepository.save(oodlesCityLevel);
    }

    private void linkingOfStaffAndTeamForCityLevel() {
        staffRelationshipGraphRespository.save(new StaffRelationship(nestingTeam, almaAsStaff));
        staffRelationshipGraphRespository.save(new StaffRelationship(nestingTeam, livaAsStaff));
        staffRelationshipGraphRespository.save(new StaffRelationship(nestingTeam, michalAsStaff));
    }

    private void createEmploymentForCityLevel() {
        employmentForMichal = new Employment("working as visitator", michalAsStaff);
        employmentForLiva = new Employment("working as planner", livaAsStaff);
        employmentForAlma = new Employment("working as task giver", almaAsStaff);
        oodlesCityLevel.getEmployments().add(employmentForMichal);
        oodlesCityLevel.getEmployments().add(employmentForLiva);
        oodlesCityLevel.getEmployments().add(employmentForAlma);
        organizationGraphRepository.save(oodlesCityLevel);

    }

    private void createUnitEmploymentForCityLevel() {
        accessGroup = accessGroupRepository.findAccessGroupByName(oodlesCityLevel.getId(), AppConstants.VISITATOR);
        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(oodlesCityLevel);
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitPermission, accessPermission);
        unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
        accessPageService.setPagePermissionToStaff(accessPermission, accessGroup.getId());
        employmentForMichal.getUnitPermissions().add(unitPermission);
        oodlesCityLevel.getEmployments().add(employmentForMichal);

        accessGroup = accessGroupRepository.findAccessGroupByName(oodlesCityLevel.getId(), AppConstants.TASK_GIVERS);
        unitPermission = new UnitPermission();
        unitPermission.setOrganization(oodlesCityLevel);
        accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship taskGiverAccess = new UnitEmpAccessRelationship(unitPermission, accessPermission);
        unitEmpAccessGraphRepository.save(taskGiverAccess);
        accessPageService.setPagePermissionToStaff(accessPermission, accessGroup.getId());
        employmentForAlma.getUnitPermissions().add(unitPermission);
        oodlesCityLevel.getEmployments().add(employmentForAlma);

        accessGroup = accessGroupRepository.findAccessGroupByName(oodlesCityLevel.getId(), AppConstants.PLANNER);
        unitPermission = new UnitPermission();
        unitPermission.setOrganization(oodlesCityLevel);
        accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship plannerAccess = new UnitEmpAccessRelationship(unitPermission, accessPermission);
        unitEmpAccessGraphRepository.save(plannerAccess);
        accessPageService.setPagePermissionToStaff(accessPermission, accessGroup.getId());
        employmentForLiva.getUnitPermissions().add(unitPermission);
        oodlesCityLevel.getEmployments().add(employmentForLiva);
        organizationGraphRepository.save(oodlesCityLevel);
    }

    private void createPaymentTypes() {
        PaymentType creditCard = new PaymentType();
        creditCard.setName("Credit Cards");
        creditCard.setCreationDate(DateUtil.getCurrentDate().getTime());
        creditCard.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        creditCard.setCountry(denmark);

        PaymentType paySafecard = new PaymentType();
        paySafecard.setName("Paysafecard");
        paySafecard.setCreationDate(DateUtil.getCurrentDate().getTime());
        paySafecard.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        paySafecard.setCountry(denmark);
        paymentTypeGraphRepository.saveAll(Arrays.asList(creditCard, paySafecard));
    }

    private void createCurrency() {
        com.kairos.persistence.model.country.Currency currency = new com.kairos.persistence.model.country.Currency();
        currency.setName("krone");
        currency.setCreationDate(DateUtil.getCurrentDate().getTime());
        currency.setLastModificationDate(DateUtil.getCurrentDate().getTime());
        currency.setCountry(denmark);
        currencyGraphRepository.save(currency);
    }

    private void createCTARuleTemplateCategory() {
        RuleTemplateCategory category = ruleTemplateCategoryGraphRepository.findByName(denmark.getId(), "NONE", RuleTemplateCategoryType.CTA);
        if (!Optional.ofNullable(category).isPresent()) {
            category = new RuleTemplateCategory("NONE", RuleTemplateCategoryType.CTA);
            category.setCountry(denmark);
            ruleTemplateCategoryService.createDefaultRuleTemplateCategory( category);
        }

        // No need to create default CTA Rule templates
        /*if (costTimeAgreementService.isDefaultCTARuleTemplateExists()) {
            logger.info("default CTA rule template already exist");
        } else {
            logger.info("creating CTA rule template");
            costTimeAgreementService.createDefaultCtaRuleTemplate(country.getId());
        }*/

    }

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
}