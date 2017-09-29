package com.kairos.config;

import com.kairos.config.scheduler.DynamicCronScheduler;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.enums.ClientEnum;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.*;
import com.kairos.persistence.model.user.control_panel.ControlPanel;
import com.kairos.persistence.model.user.country.CitizenStatus;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.department.Department;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.payment_type.PaymentType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.resources.FuelType;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.resources.ResourceUnAvailability;
import com.kairos.persistence.model.user.resources.VehicleType;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.model.user.staff.*;
import com.kairos.persistence.repository.organization.*;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientLanguageRelationGraphRepository;
import com.kairos.persistence.repository.user.client.ClientOrganizationRelationGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CurrencyGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.payment_type.PaymentTypeGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ProvinceGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitEmpAccessGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitEmploymentGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
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
import com.kairos.service.phase.PhaseService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

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
    UnitEmploymentGraphRepository unitEmploymentGraphRepository;
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
    private PhaseService phaseService;




    private List<Long> skillList;
    private com.kairos.persistence.model.organization.OrganizationService homeCareService;

    private com.kairos.persistence.model.organization.OrganizationService medicalCareService;

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

    Region Hovedstaden;
    Region Syddanmark;

    Province copenhagenCity;
    Municipality copenhagen;
    Municipality  frederiksberg;

    Province bornholm;
    Municipality ertholmene;


    Province Fyn;
    Municipality  Odense;

    ZipCode kobenhavn;
    ZipCode allegade;

    public void createData()  {
        if(countryGraphRepository.findAll().isEmpty()){
            createStandardTimeSlots();
            //createMasterSkills();
            //createOrganizationServices();
            createCountries();
            //createOrganizationTypes();
            //createPaymentTypes();
            //createCurrency();
            createGeoGraphicalData();
            createUser();
            createStaff();
            createCountryLevelOrganization();
            createRegionLevelOrganization();

            //createCityLevelOrganization();
            //createCitizen();
        }
        phaseService.createPhases();
        phaseService.linkPhasesWithAllOrganizations();

        startRegisteredCronJobs();

    }

    private void startRegisteredCronJobs()  {
        List<ControlPanel> controlPanels = controlPanelService.getAllControlPanels();
        if(controlPanels.size() != 0) {
            for (ControlPanel controlPanel : controlPanels) {
                logger.info("Register Cron Job of process name " + controlPanel.getName());
                dynamicCronScheduler.setCronScheduling(controlPanel);
            }
        }
    }

    private void createStandardTimeSlots(){
        String timeSlotsNames[] = new String[]{"Day","Evening","Night"};
        TimeSlot timeSlot;
        for(String timeSlotName : timeSlotsNames){
            timeSlot = new TimeSlot();
            timeSlot.setName(timeSlotName);
            timeSlot.setTimeSlotType(TimeSlot.TYPE.STANDARD);
            timeSlotGraphRepository.save(timeSlot);
        }
    }

    private void createGeoGraphicalData() {

        copenhagenCity = new Province();
        copenhagenCity.setName("Copenhagen City");
        copenhagenCity.setRegion(Hovedstaden);
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
        bornholm.setRegion(Hovedstaden);
        bornholm = provinceGraphRepository.save(bornholm);


        ertholmene= new Municipality();
        ertholmene.setName("Ertholmene");
        ertholmene.setProvince(bornholm);
        ertholmene = municipalityGraphRepository.save(ertholmene);


        Fyn = new Province();
        Fyn.setName("Fyn");
        Fyn  = provinceGraphRepository.save(Fyn);

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
            skill.setCreationDate(new Date().getTime());
            skill.setLastModificationDate(new Date().getTime());
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }
        SkillCategory medicalSkillCategory = new SkillCategory("Medical Skills");
        medicalSkillCategory.setCountry(denmark);
        String medicalSkills[] = new String[]{"Pharma knowledge", "Basic Medical Checkup", "Basic Nursing"};
        for (String medicalSkill : medicalSkills) {
            skill = new Skill(medicalSkill, medicalSkillCategory);
            skill.setCreationDate(new Date().getTime());
            skill.setLastModificationDate(new Date().getTime());
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }
        SkillCategory homeSkillCategory = new SkillCategory("Home Skills");
        homeSkillCategory.setCountry(denmark);
        String homeSkills[] = new String[]{"Home Cleaning", "Dish Washing", "Cooking food"};
        for (String homeSkill : homeSkills) {
            skill = new Skill(homeSkill, homeSkillCategory);
            skill.setCreationDate(new Date().getTime());
            skill.setLastModificationDate(new Date().getTime());
            skillGraphRepository.save(skill);
            skillList.add(skill.getId());
        }

        danish =new Language("Danish", false);
        danish.setCountry(denmark);
        danish = languageGraphRepository.save(danish);
    }

    private void createOrganizationServices() {
        homeCareService = new com.kairos.persistence.model.organization.OrganizationService("Home Care");
        homeCareService.setOrganizationSubService(Arrays.asList(
                new com.kairos.persistence.model.organization.OrganizationService("Home Dusting"),
                new com.kairos.persistence.model.organization.OrganizationService("Home Cooking"),
                new com.kairos.persistence.model.organization.OrganizationService("Home Maintenance")));
        homeCareService.setCreationDate(new Date().getTime());
        homeCareService.setLastModificationDate(new Date().getTime());

        medicalCareService = new com.kairos.persistence.model.organization.OrganizationService("Medical Service");
        medicalCareService.setOrganizationSubService(Arrays.asList(
                new com.kairos.persistence.model.organization.OrganizationService("Basic Checkup"),
                new com.kairos.persistence.model.organization.OrganizationService("Disease Diagnose"),
                new com.kairos.persistence.model.organization.OrganizationService("Medication")));
        medicalCareService.setCreationDate(new Date().getTime());
        medicalCareService.setLastModificationDate(new Date().getTime());

        organizationServiceRepository.save(Arrays.asList(homeCareService, medicalCareService));

    }

    private void createOrganizationTypes() {
        publicOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.PUBLIC.value,homeCareService.getOrganizationSubService());
        publicOrganization.setCreationDate(new Date().getTime());
        publicOrganization.setLastModificationDate(new Date().getTime());
        publicOrganization.setCountry(denmark);

        privateOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.PRIVATE.value,medicalCareService.getOrganizationSubService());
        privateOrganization.setCreationDate(new Date().getTime());
        privateOrganization.setLastModificationDate(new Date().getTime());
        privateOrganization.setCountry(denmark);

        ngoOrganization = new OrganizationType(OrganizationType.OrganizationTypeEnum.NGO.value,
                homeCareService.getOrganizationSubService());
        ngoOrganization.setCountry(denmark);


        ngoOrganization.setCreationDate(new Date().getTime());
        ngoOrganization.setLastModificationDate(new Date().getTime());
        ngoOrganization.setCountry(denmark);


        organizationService.save(publicOrganization);
        organizationService.save(privateOrganization);
        organizationService.save(ngoOrganization);


    }

    private void createCountries() {
        denmark = new Country();
        denmark.setName("Denmark");
        // denmark.setCountryHolidayCalenderList(Arrays.asList(new CountryHolidayCalender("Halloween", new Date().getTime()), new CountryHolidayCalender("Christmas",  1474696870000L)));
        // denmark.setOrganizationServices(Arrays.asList(homeCareService, medicalCareService));
        countryGraphRepository.save(denmark);

        germany = new Country();
        germany.setName("Germany");
        Long d = new DateTime().withTime(9,20,0,0).getMillis();
        Long e = new DateTime().withTime(15,20,0,0).getMillis();
        logger.info("Half day Leave timing: "+d+" and :   "+e);
        // germany.setCountryHolidayCalenderList(Arrays.asList(new CountryHolidayCalender("Halloween", new Date().getTime() ,d,e), new CountryHolidayCalender("Christmas",  1474696870000L)));
        // germany.setOrganizationServices(Arrays.asList(homeCareService, medicalCareService));
        countryGraphRepository.save(germany);


        Hovedstaden = new Region();
        Hovedstaden.setName("Hovedstaden");
        Syddanmark = new Region();
        Syddanmark.setName("Syddanmark");

        Hovedstaden.setCountry(denmark);
        Syddanmark.setCountry(denmark);

        Hovedstaden = regionGraphRepository.save(Hovedstaden);
        Syddanmark  = regionGraphRepository.save(Syddanmark);
        //  createCitizenStatus();


    }

    private void createCitizenStatus() {
        CitizenStatus deadStatus  = new CitizenStatus();
        deadStatus.setName("Dead");
        deadStatus.setDescription("When pulse stops , patient is declared dead");


        CitizenStatus singleStatus  = new CitizenStatus();
        singleStatus.setDescription("Single");
        singleStatus.setDescription("A Citizen is not married ");
        singleStatus.setCountry(denmark);

        CitizenStatus marriedStatus  = new CitizenStatus();
        marriedStatus.setName("Married");
        marriedStatus.setDescription("A Citizen is have a wife");
        marriedStatus.setCountry(denmark);

        CitizenStatus disvorcedStatus  = new CitizenStatus();
        disvorcedStatus.setName("Divorced");
        disvorcedStatus.setDescription("A Citizen was married ,but now divorced ");
        disvorcedStatus.setCountry(denmark);

        CitizenStatus livingPartnerStatus  = new CitizenStatus();
        livingPartnerStatus.setName("Longest living partner");
        livingPartnerStatus.setDescription("A Citizen is living Partner");
        livingPartnerStatus.setCountry(denmark);

        CitizenStatus registeredStatus  = new CitizenStatus();
        registeredStatus.setName("Registered partnership");
        registeredStatus.setDescription("Relationship of Citizen is registered formally");
        registeredStatus.setCountry(denmark);

        citizenStatusGraphRepository.save(Arrays.asList(registeredStatus,livingPartnerStatus,disvorcedStatus,marriedStatus,singleStatus,deadStatus));
    }

    private void createCitizen() {
        johnOliver = new Client();
        johnOliver.setClientType(ClientEnum.CITIZEN);
        johnOliver.setFirstName("John");
        johnOliver.setLastName("Oliver");
        johnOliver.setNickName("Johnny");
        johnOliver.setAge(57);
//        johnOliver.setCivilianStatus(ClientEnum.CivilianStatus.MARRIED);
        johnOliver.setOccupation("Stock Broker");
        johnOliver.setGender(Gender.MALE);
        johnOliver.setCitizenship(ClientEnum.CitizenShip.DANISH);

        List<Map<String,Object>> zipCodeMapList = zipCodeGraphRepository.getAllZipCodeByCountryId(denmark.getId());

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
//        johnOliver.setPeopleInHouseholdList(Arrays.asList(firstHousehold,secondHousehold));


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
        johnOliver.setClientDiagnoseList(Arrays.asList(new ClientDiagnose("Heart Diagnose", " Moderate Condition", "positive", "Eat Healthy"),
                new ClientDiagnose("Lung Diagnose", " Poor Condition", "positive", "Quit Smoking")));
        johnOliver.setTranslationLanguage(new String[]{"Danish"});
        johnOliver.setClientDiseaseList(Arrays.asList(new ClientDisease("Hepatitis B", "Common", "Don't allow Junk food")));

        ClientMinimumDTO minimumDTO = new ClientMinimumDTO();
        minimumDTO.setFirstName("Kin");
        minimumDTO.setLastName("yong");
        minimumDTO.setCprnumber("1106513681");
        Client nextKin = clientService.createCitizen(minimumDTO,oodlesCityLevel.getId());

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
        ClientOrganizationRelation clientOrganizationRelation = new ClientOrganizationRelation(client, oodlesCityLevel,new DateTime().getMillis());
        relationService.createRelation(clientOrganizationRelation);

        ClientLanguageRelation clientLanguageRelation = new ClientLanguageRelation();
        clientLanguageRelation.setClient(client);
        clientLanguageRelation.setLanguage(danish);
        clientLanguageRelationGraphRepository.save(clientLanguageRelation);

    }

    private void createCountryLevelOrganization() {


        kairosCountryLevel = new Organization();
        kairosCountryLevel.setKairosHub(true);
        kairosCountryLevel.setFormalName("Kairos (HUB)");
        kairosCountryLevel.setName("Kairos");
        kairosCountryLevel.setEanNumber("501234567890");
        kairosCountryLevel.setEmail("kairos_denmark@kairos.com");
        kairosCountryLevel.setContactDetail(new ContactDetail("info@kairos.com", "kairos_denmark@kairos.com", "431311", "653322"));
//        kairosCountryLevel.setContactAddress(new ContactAddress("Thorsgade", 2, 5000, "Odense", 4345, "Commercial"));
//        kairosCountryLevel.setOrganizationTypes(privateOrganization);
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

        organizationService.createOrganization(kairosCountryLevel, null);
        //organizationGraphRepository.addSkillInOrganization(kairosCountryLevel.getId(),skillList,new Date().getTime(),new Date().getTime());
        createEmployment();
        createTeam();
        createGroup();
        linkingOfStaffAndTeam();
        createUnitEmploymentForCountryLevel();
    }

    private void createOodlesResource() {
        van = new Resource();
        van.setName(VehicleType.CAR);
        van.setCostPerKM(20);
        van.setFuelType(FuelType.DIESEL);
        van.setEnabled(true);
        van.setModelDescription("Van with capacity of 4 people");
        van.setModelDescription("STR BS-IV");
        van.setResourceAvailabilities(Arrays.asList(new ResourceUnAvailability(1482172200000L, 1483036200000L),
                new ResourceUnAvailability(1483986600000L, 1484850600000L)));
        van = resourceGraphRepository.save(van);


        ambulance = new Resource();
        ambulance.setName(VehicleType.AMBULANCE);
        ambulance.setCostPerKM(50);
        ambulance.setFuelType(FuelType.PETROL);
        ambulance.setEnabled(true);
        ambulance.setModelDescription("Medical Ambulance for Emergency situations");
        ambulance.setModelDescription("STR BS-V");
        ambulance.setResourceAvailabilities(Arrays.asList(new ResourceUnAvailability(1482172200000L, 1483036200000L),
                new ResourceUnAvailability(1483986600000L, 1484850600000L)));
        ambulance = resourceGraphRepository.save(ambulance);


        bicycle = new Resource();
        bicycle.setName(VehicleType.BICYCLE);
        bicycle.setCostPerKM(1);
        bicycle.setFuelType(FuelType.ELECTRIC);
        bicycle.setEnabled(true);
        bicycle.setModelDescription("Electric bicycle for nearby situations");
        bicycle.setModelDescription("BMX");
        bicycle.setResourceAvailabilities(Arrays.asList(new ResourceUnAvailability(1482172200000L, 1483036200000L),
                new ResourceUnAvailability(1483986600000L, 1484850600000L)));
        bicycle = resourceGraphRepository.save(bicycle);

        oodlesCityLevel.setResourceList(Arrays.asList(ambulance, van, bicycle));
        organizationGraphRepository.save(oodlesCityLevel);

    }

    private void createUser() {

      /*  michal = new User();
        michal.setCprNumber("2311840471");
        michal.setUserName("micky21@kairoscountrylevel.com");
        michal.setEmail("micky21@kairoscountrylevel.com");
        michal.setPassword(new BCryptPasswordEncoder().encode("12345"));
        michal.setFirstName("Michael");
        michal.setNickName("Micky");
        michal.setLastName("John");
        michal.setGender(Gender.MALE);
//        michal.setHomeAddress(new ContactAddress("Baker Street", 3, 1221, "Glostrup", 4533, "Apartments"));
        michal.setContactDetail(new ContactDetail("micky21@kairoscountrylevel.com", "micky21@gmail.com", "536533", "facebook.com/micky_original"));
        michal.setAge(27);
        michal.setCreationDate(new Date().getTime());
        michal.setLastModificationDate(new Date().getTime());

        liva = new User();
        liva.setCprNumber("1808920669");
        liva.setUserName("liva33@kairoscountrylevel.com");
        liva.setEmail("liva33@kairoscountrylevel.com");
        liva.setPassword(new BCryptPasswordEncoder().encode("12345"));
        liva.setFirstName("Liva");
        liva.setNickName("Liva");
        liva.setLastName("Rasmussen");
        liva.setGender(Gender.FEMALE);
//        liva.setHomeAddress(new ContactAddress("Rosewood Street", 1, 5421, "Glostrup", 2123, "Apartments"));
        liva.setContactDetail(new ContactDetail("liva33@kairosCountryLevel.com", "liva31@gmail.com", "536533", "facebook.com/liva33_cool"));
        liva.setAge(24);
        liva.setCreationDate(new Date().getTime());
        liva.setLastModificationDate(new Date().getTime());

        alma = new User();
        alma.setCprNumber("2512864166");
        alma.setUserName("alma@kairoscountrylevel.com");
        alma.setEmail("alma@kairoscountrylevel.com");
        alma.setPassword(new BCryptPasswordEncoder().encode("12345"));
        alma.setFirstName("Alma");
        alma.setNickName("alma");
        alma.setLastName("Krogh");
        alma.setGender(Gender.FEMALE);
//        alma.setHomeAddress(new ContactAddress("Rosewood Street", 1, 5421, "Glostrup", 2123, "Apartments"));
        alma.setContactDetail(new ContactDetail("alma_01@kairoscountrylevel.com", "alma007@gmail.com", "536533", "facebook.com/alma_cool"));
        alma.setAge(28);
        alma.setCreationDate(new Date().getTime());
        alma.setLastModificationDate(new Date().getTime());*/

        admin = new User();
        admin.setCprNumber("0309514297");
        admin.setUserName("ulrik@kairos.com");
        admin.setEmail("ulrik@kairos.com");
        admin.setPassword(new BCryptPasswordEncoder().encode("admin@kairos"));
        admin.setFirstName("Ulrik");
        admin.setNickName("Ulrik");
        admin.setLastName("Rasmussen");
        admin.setGender(Gender.MALE);
//        admin.setHomeAddress(new ContactAddress("Rosewood Street", 1, 5421, "Glostrup", 2123, "Apartments"));
        admin.setContactDetail(new ContactDetail("ulrik_01@kairoscountrylevel.com", "alma007@gmail.com", "536533", "facebook.com/ulrik_cool"));
        admin.setAge(28);
        admin.setCreationDate(new Date().getTime());
        admin.setLastModificationDate(new Date().getTime());
        //userGraphRepository.save(Arrays.asList(michal, liva, alma,admin));
        userGraphRepository.save(admin);
    }

    private void createStaff() {

        // Michael Staff

/*

        michalAsStaff = new Staff();
//        michalAsStaff.setContactAddress(new ContactAddress("Baker Street", 3, 1221, "Glostrup", 4533, "Apartments"));
        michalAsStaff.setGeneralNote("New Joiner, Need to learn to handle client interaction");
        michalAsStaff.setUser(michal);
        michalAsStaff.setSignature("mick");
        michalAsStaff.setCardNumber("FLMPSW1134");
        michalAsStaff.setCopyKariosMailToLogin(true);
        michalAsStaff.setContactDetail(new ContactDetail("micky21@kairoscountrylevel.com", "micky21@gmail.com", "536533", "facebook.com/micky_original"));
        michalAsStaff.setFamilyName("Micky");
        michalAsStaff.setFirstName("Michael J");
        michalAsStaff.setLastName("John");
        michalAsStaff.setActive(true);
        michalAsStaff.getContactDetail().setPrivatePhone("9711588672");
        michalAsStaff.getContactDetail().setWorkPhone("9718420411");
        michalAsStaff.getContactDetail().setMobilePhone("7000000000");
        michalAsStaff.setEmail("micky21@oodlestechnologies.com");
        michalAsStaff.setNationalInsuranceNumber("NIN44500221");
        michalAsStaff.setLanguage(danish);
        michalAsStaff.setPassword(new BCryptPasswordEncoder().encode("oodles"));

        livaAsStaff = new Staff();
//        livaAsStaff.setContactAddress(new ContactAddress("Rosewood Street", 3, 1221, "Glostrup", 4533, "Apartments"));
        livaAsStaff.setGeneralNote("New Joiner, Need to learn to handle client interaction");
        livaAsStaff.setUser(liva);
        livaAsStaff.setSignature("liva");
        livaAsStaff.setCardNumber("QAMPSW1134");
        livaAsStaff.setCopyKariosMailToLogin(true);
        livaAsStaff.setContactDetail(new ContactDetail("liva33@kairoscountrylevel.com", "liva31@gmail.com", "536533", "facebook.com/liva33_cool"));
        livaAsStaff.setFamilyName("Liva");
        livaAsStaff.setFirstName("Liva J");
        livaAsStaff.setLastName("Rasmussen");
        livaAsStaff.setActive(true);
        livaAsStaff.setNationalInsuranceNumber("NIN44500981");
        livaAsStaff.setEmail("liva@oodlestechnologies.com");
        livaAsStaff.setLanguage(danish);
        livaAsStaff.setPassword(new BCryptPasswordEncoder().encode("oodles"));
*/


 /*       almaAsStaff = new Staff();
//        almaAsStaff.setContactAddress(new ContactAddress("Rosewood Street", 3, 1221, "Glostrup", 4533, "Apartments"));
        almaAsStaff.setGeneralNote("New Joiner, Need to learn to handle client interaction");
        almaAsStaff.setUser(alma);
        almaAsStaff.setSignature("liva");
        almaAsStaff.setCardNumber("LPSPSW1134");
        almaAsStaff.setCopyKariosMailToLogin(true);
        almaAsStaff.setContactDetail(new ContactDetail("alma_01@kairoscountrylevel.com", "alma007@gmail.com", "536533", "facebook.com/alma_cool"));
        almaAsStaff.setContactDetail(new ContactDetail("alma_01@kairoscountrylevel.com", "alma007@gmail.com", "536533", "facebook.com/alma_cool"));
        almaAsStaff.setFamilyName("Alma");
        almaAsStaff.setFirstName("Alma L");
        almaAsStaff.setLastName("Krogh");
        almaAsStaff.setActive(true);
        almaAsStaff.setEmail("alma@oodlestechnologies.com");
        almaAsStaff.setNationalInsuranceNumber("NIN44500331");
        almaAsStaff.setLanguage(danish);
        almaAsStaff.setPassword(new BCryptPasswordEncoder().encode("oodles"));*/

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
        adminAsStaff.setActive(true);
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
        nestingTeam.setCreationDate(new Date().getTime());
        nestingTeam.setLastModificationDate(new Date().getTime());
        teamGraphRepository.save(Arrays.asList(nestingTeam));
    }

    private void linkingOfStaffAndTeam() {
        organizationService.save(new StaffRelationship(nestingTeam, adminAsStaff));
    }

    private void createGroup() {
        operationsGroup = new Group();
        operationsGroup.setName("Operations Group");
        operationsGroup.setTeamList(Arrays.asList(nestingTeam));

        kairosCountryLevel.setGroupList(Arrays.asList(operationsGroup));
        kairosCountryLevel = organizationGraphRepository.save(kairosCountryLevel);
    }

    private void createEmployment(){
        employmentForAdmin = new Employment("working as country admin",adminAsStaff);
        kairosCountryLevel.getEmployments().add(employmentForAdmin);
        organizationGraphRepository.save(kairosCountryLevel);
    }

    private void createUnitEmploymentForCountryLevel(){
        accessGroup = accessGroupRepository.findAccessGroupByName(kairosCountryLevel.getId(), AppConstants.COUNTRY_ADMIN);
        UnitEmployment unitEmployment = new UnitEmployment();
        unitEmployment.setOrganization(kairosCountryLevel);
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        unitEmploymentGraphRepository.save(unitEmployment);
        UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitEmployment,accessPermission);
        unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
        accessPageService.setPagePermissionToAdmin(accessPermission);
        employmentForAdmin.getUnitEmployments().add(unitEmployment);
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
        kairosRegionLevel.setKairosHub(true);
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
        organizationService.createOrganization(kairosRegionLevel, kairosCountryLevel.getId());

        //organizationGraphRepository.addOrganizationServiceInUnit(kairosRegionLevel.getId(),Arrays.asList(privateOrganization.getOrganizationServiceList().get(0).getId()),new Date().getTime(),new Date().getTime());
    }


    private void createPublicPhoneNumberForCityLevel(){
        PublicPhoneNumber publicPhoneNumber = new PublicPhoneNumber("7100000000");
        List<PublicPhoneNumber> publicPhoneNumberList = new ArrayList<PublicPhoneNumber>();
        publicPhoneNumberList.add(publicPhoneNumber);
        oodlesCityLevel.setPublicPhoneNumberList(publicPhoneNumberList);
        organizationGraphRepository.save(oodlesCityLevel);
    }

    private void createTeamsForCityLevel(){
        nestingTeam = new Team();
        nestingTeam.setName("Nesting Team");
        nestingTeam.setCreationDate(new Date().getTime());
        nestingTeam.setLastModificationDate(new Date().getTime());

        experiencedTeam = new Team();
        experiencedTeam.setName("Experienced Team");
        experiencedTeam.setCreationDate(new Date().getTime());
        experiencedTeam.setLastModificationDate(new Date().getTime());
        teamGraphRepository.save(Arrays.asList(nestingTeam, experiencedTeam));
    }

    private void createGroupForCityLevel(){
        operationsGroup = new Group();
        operationsGroup.setName("oodles-operations Group");
        operationsGroup.setTeamList(Arrays.asList(nestingTeam));

        serviceGroup = new Group();
        serviceGroup.setName("oodles-Service Group");
        serviceGroup.setTeamList(Arrays.asList(experiencedTeam));
        oodlesCityLevel.setGroupList(Arrays.asList(operationsGroup,serviceGroup));
        organizationGraphRepository.save(oodlesCityLevel);
    }

    private void linkingOfStaffAndTeamForCityLevel() {
        organizationService.save(new StaffRelationship(nestingTeam, almaAsStaff));
        organizationService.save(new StaffRelationship(nestingTeam, livaAsStaff));
        organizationService.save(new StaffRelationship(nestingTeam, michalAsStaff));
    }

    private void createEmploymentForCityLevel(){
        employmentForMichal = new Employment("working as visitator",michalAsStaff);
        employmentForLiva = new Employment("working as planner",livaAsStaff);
        employmentForAlma = new Employment("working as task giver",almaAsStaff);
        oodlesCityLevel.getEmployments().add(employmentForMichal);
        oodlesCityLevel.getEmployments().add(employmentForLiva);
        oodlesCityLevel.getEmployments().add(employmentForAlma);
        organizationGraphRepository.save(oodlesCityLevel);

    }

    private void createUnitEmploymentForCityLevel(){
        accessGroup = accessGroupRepository.findAccessGroupByName(oodlesCityLevel.getId(), AppConstants.VISITATOR);
        UnitEmployment unitEmployment = new UnitEmployment();
        unitEmployment.setOrganization(oodlesCityLevel);
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitEmployment,accessPermission);
        unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
        accessPageService.setPagePermissionToStaff(accessPermission,accessGroup.getId());
        employmentForMichal.getUnitEmployments().add(unitEmployment);
        oodlesCityLevel.getEmployments().add(employmentForMichal);

        accessGroup = accessGroupRepository.findAccessGroupByName(oodlesCityLevel.getId(), AppConstants.TASK_GIVERS);
        unitEmployment = new UnitEmployment();
        unitEmployment.setOrganization(oodlesCityLevel);
        accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship taskGiverAccess = new UnitEmpAccessRelationship(unitEmployment,accessPermission);
        unitEmpAccessGraphRepository.save(taskGiverAccess);
        accessPageService.setPagePermissionToStaff(accessPermission,accessGroup.getId());
        employmentForAlma.getUnitEmployments().add(unitEmployment);
        oodlesCityLevel.getEmployments().add(employmentForAlma);

        accessGroup = accessGroupRepository.findAccessGroupByName(oodlesCityLevel.getId(), AppConstants.PLANNER);
        unitEmployment = new UnitEmployment();
        unitEmployment.setOrganization(oodlesCityLevel);
        accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship plannerAccess = new UnitEmpAccessRelationship(unitEmployment,accessPermission);
        unitEmpAccessGraphRepository.save(plannerAccess);
        accessPageService.setPagePermissionToStaff(accessPermission,accessGroup.getId());
        employmentForLiva.getUnitEmployments().add(unitEmployment);
        oodlesCityLevel.getEmployments().add(employmentForLiva);
        organizationGraphRepository.save(oodlesCityLevel);
    }

    private void createPaymentTypes(){
        PaymentType creditCard = new PaymentType();
        creditCard.setName("Credit Cards");
        creditCard.setCreationDate(new Date().getTime());
        creditCard.setLastModificationDate(new Date().getTime());
        creditCard.setCountry(denmark);

        PaymentType paySafecard = new PaymentType();
        paySafecard.setName("Paysafecard");
        paySafecard.setCreationDate(new Date().getTime());
        paySafecard.setLastModificationDate(new Date().getTime());
        paySafecard.setCountry(denmark);
        paymentTypeGraphRepository.save(Arrays.asList(creditCard,paySafecard));
    }

    private void createCurrency(){
        com.kairos.persistence.model.user.country.Currency currency = new com.kairos.persistence.model.user.country.Currency();
        currency.setName("krone");
        currency.setCreationDate(new Date().getTime());
        currency.setLastModificationDate(new Date().getTime());
        currency.setCountry(denmark);
        currencyGraphRepository.save(currency);
    }
}