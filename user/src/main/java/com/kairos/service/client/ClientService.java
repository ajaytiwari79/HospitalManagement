package com.kairos.service.client;

import com.kairos.commons.config.ApplicationContextProviderNonManageBean;
import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.task.EscalateTaskWrapper;
import com.kairos.dto.activity.task.EscalatedTasksWrapper;
import com.kairos.dto.user.client.ClientExceptionDTO;
import com.kairos.dto.user.organization.skill.OrganizationClientWrapper;
import com.kairos.dto.activity.task.TaskDemandRequestWrapper;
import com.kairos.dto.activity.task_type.TaskTypeAggregateResult;
import com.kairos.dto.planner.vrp.TaskAddress;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.staff.ContactPersonDTO;
import com.kairos.dto.user.staff.client.ClientExceptionTypesDTO;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.dto.user.staff.client.ClientStaffInfoDTO;
import com.kairos.enums.Gender;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.*;
import com.kairos.persistence.model.client.query_results.ClientHomeAddressQueryResult;
import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.client.query_results.ClientOrganizationIdsDTO;
import com.kairos.persistence.model.client.query_results.ClientStaffQueryResult;
import com.kairos.persistence.model.client.relationships.ClientContactPersonRelationship;
import com.kairos.persistence.model.client.relationships.ClientLanguageRelation;
import com.kairos.persistence.model.client.relationships.ClientOrganizationRelation;
import com.kairos.persistence.model.country.default_data.CitizenStatusDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonQueryResultByService;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonStructuredData;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.model.staff.StaffClientData;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffAdditionalInfoQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.language.LanguageLevel;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationMetadataRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.repository_impl.OrganizationGraphRepositoryImpl;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.*;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageLevelGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.*;
import com.kairos.service.AsynchronousService;
import com.kairos.service.country.CitizenStatusService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.IntegrationService;
import com.kairos.service.organization.TimeSlotService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.service.staff.StaffService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.DateUtil;
import com.kairos.utils.FormatUtil;
import com.kairos.utils.user_context.UserContext;
import com.kairos.wrapper.ClientPersonalCalenderPrerequisiteDTO;
import com.kairos.wrapper.ContactPersonTabDataDTO;
import com.kairos.wrapper.task_demand.TaskDemandVisitWrapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.enums.CitizenHealthStatus.DECEASED;
import static com.kairos.enums.CitizenHealthStatus.TERMINATED;
import static com.kairos.utils.DateUtil.MONGODB_QUERY_DATE_FORMAT;

/**
 * Created by oodles on 28/9/16.
 */
@Service
@Transactional
public class ClientService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    EnvConfig envConfig;
    @Inject
    private CitizenStatusGraphRepository citizenStatusGraphRepository;
    @Inject
    private ClientLanguageRelationGraphRepository clientLanguageRelationGraphRepository;
    @Inject
    private ClientGraphRepository clientGraphRepository;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private ClientTeamRelationGraphRepository staffTeamRelationGraphRepository;
    @Inject
    private TeamGraphRepository teamGraphRepository;
    @Inject
    private ClientOrganizationRelationGraphRepository relationGraphRepository;
    @Inject
    private LanguageGraphRepository languageGraphRepository;
    @Inject
    private LanguageLevelGraphRepository languageLevelGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private CitizenStatusService citizenStatusService;
    @Inject
    private ClientOrganizationRelationService relationService;
    @Inject
    private StaffService staffService;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private OrganizationMetadataRepository organizationMetadataRepository;
    @Autowired
    AddressVerificationService addressVerificationService;
    @Autowired
    MunicipalityGraphRepository municipalityGraphRepository;
    @Autowired
    RegionGraphRepository regionGraphRepository;
    @Autowired
    ContactAddressGraphRepository contactAddressGraphRepository;
    @Autowired
    TimeSlotGraphRepository timeSlotGraphRepository;
    @Autowired
    IntegrationService integrationService;
    @Autowired
    TaskServiceRestClient taskServiceRestClient;
    @Autowired
    PlannerRestClient plannerRestClient;
    @Autowired
    TaskTypeRestClient taskTypeRestClient;
    @Autowired
    TaskDemandRestClient taskDemandRestClient;
    @Autowired
    TableConfigRestClient tableConfigRestClient;
    @Inject
    OrganizationGraphRepositoryImpl organizationGraphRepositoryImpl;
    @Inject
    ClientAddressService clientAddressService;
    @Inject
    private ClientExceptionRestClient clientExceptionRestClient;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private AsynchronousService asynchronousService;
    @Inject private ClientContactPersonRelationshipRepository clientContactPersonRelationshipRepository;
    @Inject private ClientContactPersonGraphRepository clientContactPersonGraphRepository;
    @Inject private StaffRetrievalService staffRetrievalService;
    @Inject
    private com.kairos.service.organization.OrganizationService organizationService;

    /*
        public Client createCitizen(Client client) {
                  User user = new User();
            Client createClient = null;
            //if (client.getEmail() == null) {
            logger.debug("Creating email with CPR");
            String cpr = user.getCprNumber();
            String email = cpr + KAIROS;
            client.setEmail(email);
            client.setUserName(email);
            //}
            if (checkCitizenCPRConstraint(client)) {
                logger.debug("Creating Client..........");
                createClient = clientGraphRepository.save(generateAgeAndGenderFromCPR(client));
                //createClient.setNextToKin(new Client());
                save(createClient);
            }
            return createClient;

        }

    */
    public void  delete(Long clientId){
         clientGraphRepository.delete(clientId);;
    }
    public Client createCitizen(ClientMinimumDTO clientMinimumDTO, Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.client.organisation.notFound", unitId);
        }

        if (!invalidCPRNumber(clientMinimumDTO.getCprnumber())) {
            exceptionService.dataNotFoundByIdException("message.client.CRPNumber.notFound");
        }

        User user = userGraphRepository.findUserByCprNumber(clientMinimumDTO.getCprnumber());

        Client client;
        if (user != null) {
            client = clientGraphRepository.getClientByUserId(user.getId());
            if (Optional.ofNullable(client).isPresent()) {
                if (client.isCitizenDead()) {
                    exceptionService.duplicateDataException("message.client.CRPNumber.deadcitizen", clientMinimumDTO.getCprnumber());
                }
                int count = relationService.checkClientOrganizationRelation(client.getId(), unitId);
                if (count != 0) {
                    exceptionService.duplicateDataException("message.client.CRPNumber.duplicate");
                }
                logger.debug("Creating Existing Client relationship : " + client.getId());
                ClientOrganizationRelation relation = new ClientOrganizationRelation(client, organization, DateUtil.getCurrentDateMillis());
                relationService.createRelation(relation);

            } else {
                client = new Client();
                user.setFirstName(clientMinimumDTO.getFirstName());
                user.setLastName(clientMinimumDTO.getLastName());
                user.setCprNumber(clientMinimumDTO.getCprnumber());
                user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(user.getCprNumber()));
                client.setProfilePic(generateAgeAndGenderFromCPR(user));
                client.setUser(user);
                client.setClientType(clientMinimumDTO.getClientType());
                clientGraphRepository.save(client);
                ClientOrganizationRelation relation = new ClientOrganizationRelation(client, organization, DateUtil.getCurrentDateMillis());
                relationService.createRelation(relation);
            }
        } else {
            logger.debug("Creating New Client & user..........");
            user = new User(clientMinimumDTO.getFirstName().trim(), clientMinimumDTO.getLastName(), clientMinimumDTO.getCprnumber(), CPRUtil.fetchDateOfBirthFromCPR(clientMinimumDTO.getCprnumber()));
            client = new Client();
            client.setProfilePic(generateAgeAndGenderFromCPR(user));
            client.setUser(user);
            client.setClientType(clientMinimumDTO.getClientType());
            clientGraphRepository.save(client);
            ClientOrganizationRelation clientOrganizationRelation = new ClientOrganizationRelation(client, organization, new DateTime().getMillis());
            logger.debug("Creating Relation with Organization: " + organization.getName());
            relationGraphRepository.save(clientOrganizationRelation);

        }
        return client;
    }

    public boolean checkCitizenCPRConstraint(User client) {
        logger.debug("Checking CRP Constraints...");
        boolean cprExists = true;
        if (client.getCprNumber() != null && userGraphRepository.findUserByCprNumber(client.getCprNumber()) != null) {
            logger.debug("CPR number matched !");
            cprExists = false;
        }
        return cprExists;
    }


    public String generateAgeAndGenderFromCPR(User user) {
        logger.debug("Generating Gender and Age from CPR....");
        String cpr = user.getCprNumber();
        String defaultPicUrl;
        if (cpr == null || cpr == "") {
            logger.debug("CPR number not found");
            return null;
        }
        Integer ageVariable = Integer.valueOf(cpr.substring(cpr.length() - 1));
        if (ageVariable % 2 == 0) {
            logger.debug("Gender detected for Client: Female");
            user.setGender(Gender.FEMALE);
            defaultPicUrl = "default_female_icon.png";

        } else {
            logger.debug("Gender detected for Client: Male");
            user.setGender(Gender.MALE);
            defaultPicUrl = "default_male_icon.png";
        }

        return defaultPicUrl;
    }

    public Client getCitizenById(Long id) {
        return clientGraphRepository.findOne(id);
    }


    public Map<String, Object> setGeneralDetails(ClientPersonalDto client) {
        Client currentClient = clientGraphRepository.findOne(client.getId());
        if (currentClient == null || currentClient.getUser() == null) {
            exceptionService.dataNotFoundByIdException("message.client.citizen.notFound", client.getId());
        }
        currentClient.getUser().setCprNumber(client.getCprNumber());
        currentClient.getUser().setFirstName(client.getFirstName());
        currentClient.getUser().setLastName(client.getLastName());
        currentClient.setNameAmongStaff(client.getNameAmongStaff());
        if (client.getCivilianStatus().getId() != null) {
            currentClient.setCivilianStatus(citizenStatusGraphRepository.findOne(client.getCivilianStatus().getId()));
        }
        currentClient.getUser().setGender(client.getGender());
        currentClient.setOccupation(client.getOccupation());
        currentClient.setDoHaveFreeChoiceServices(client.isDoHaveFreeChoiceServices());
        // Language Translation
        currentClient.setDoRequireTranslationAssistance(client.isDoRequireTranslationAssistance());

        currentClient.setVisitourTeamId(client.getVisitourTeamId());

        currentClient.setNationalityType(client.getNationalityType());
        currentClient.setCitizenship(client.getCitizenship());

        currentClient.setPeopleInHousehold(client.isPeopleInHousehold());

        currentClient.setLivesAlone(client.isLivesAlone());
        // Save Client
        Client updatedClient = clientGraphRepository.save(currentClient);

        logger.debug("Saved Client and Now Updating language preferences...");

        // Update LanguageDetails
        List<Language> languageList = client.getLanguageUnderstands();
        logger.debug("Languages found: " + languageList.size());
        ClientLanguageRelation clientLanguageRelation;
        languageGraphRepository.removeAllLanguagesFromClient(currentClient.getId());

        for (Language language : languageList) {
            // Get All Level Id
            long readId = language.getReadLevel();
            long speakId = language.getSpeakLevel();
            long writeId = language.getWriteLevel();
            logger.debug("Languages List Size: " + languageList.size());
            logger.debug("ReadId: " + readId);
            logger.debug("speakId: " + speakId);
            logger.debug("writeId: " + writeId);

            // Check if Relation Exists
            clientLanguageRelation = clientLanguageRelationGraphRepository.checkRelationExist(client.getId(), language.getId());
            if (clientLanguageRelation == null) {
                logger.debug("Creating New Relationship-----------");
                clientLanguageRelation = new ClientLanguageRelation();
            }

            Language currentLanguage = languageGraphRepository.findOne(language.getId());
            if (currentLanguage != null) {
                clientLanguageRelation.setLanguage(currentLanguage);
                clientLanguageRelation.setClient(currentClient);

                // Setting Levels
                clientLanguageRelation.setReadLevel(readId);
                clientLanguageRelation.setWriteLevel(writeId);
                clientLanguageRelation.setSpeakLevel(speakId);
                logger.debug("Saving Language : " + language.getName() + "levelId: " + language.getReadLevel());
                clientLanguageRelationGraphRepository.save(clientLanguageRelation);
            } else {
                logger.debug("Current Language is Null ");
            }
        }

        Map<String, Object> data = updatedClient.retrieveClientGeneralDetails();

        // Profile Picture
        String image = (String) data.get("profilePic");
        String imageUrl = envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + image;
        data.put("profilePic", imageUrl);


        List<Map<String, Object>> languageUnderstands = languagesKnownToCitizen(updatedClient.getId());
        if (languageUnderstands != null) {
            data.put("languageUnderstands", languageUnderstands);

        }
        return data;

    }


    public Map<String, Object> retrieveCompleteDetails(long clientId, long unitId) {
        Map<String, Object> response = new HashMap<>();
        Client currentClient = clientGraphRepository.findOne(clientId);
        //Client currentClient = clientGraphRepository.getClientByClientIdAndUnitId(clientId, unitId);
        if (!Optional.ofNullable(currentClient).isPresent() || !Optional.ofNullable(currentClient.getUser()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.client.id.notFound", clientId);
        }
        // Client General Information
        Map<String, Object> clientGeneralDetails = currentClient.retrieveClientGeneralDetails();

        // Client Profile Picture
        String url = envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath();
        clientGeneralDetails.put("profilePic", url + (String) clientGeneralDetails.get("profilePic"));
        clientGeneralDetails.put("civilianStatus", clientGraphRepository.findCitizenCivilianStatus(clientId));

        // Client Language Data
        clientGeneralDetails.put("languageUnderstands", languagesKnownToCitizen(clientId));
        // If
        clientGeneralDetails.put("hasHomeAddress", clientGraphRepository.isHomeAddressExists(clientId));
        clientGeneralDetails.put("languageUnderstandsIds", clientLanguageRelationGraphRepository.findClientLanguagesId(clientId).toArray());
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);

        if (countryId != null) {
            logger.debug("Country Found");

            List<Map<String, Object>> languageLevelData = languageLevelGraphRepository.getLanguageLevelByCountryId(countryId);
            if (languageLevelData != null) {
                clientGeneralDetails.put("languageLevelData", FormatUtil.formatNeoResponse(languageLevelData));
            }

            List<Map<String, Object>> zipCodeMapList = zipCodeGraphRepository.getAllZipCodeByCountryIdAnotherFormat(countryId);
            if (zipCodeMapList != null) {
                response.put("zipCodeData", FormatUtil.formatNeoResponse(zipCodeMapList));
            }

            List<Map<String, Object>> languageData = languageGraphRepository.getLanguageByCountryIdAnotherFormat(countryId);
            if (languageData != null) {
                response.put("languageData", FormatUtil.formatNeoResponse(languageData));
            }
            response.put("relationTypes", countryGraphRepository.getRelationTypesByCountry(countryId));
            response.put("civilianStatus", citizenStatusService.getCitizenStatusByCountryIdAnotherFormat(countryId));
        } else {
            logger.debug("Country not found");
        }

        // NextToKin
        List<NextToKinQueryResult> nextToKinDetails = clientGraphRepository.getNextToKinDetail(clientId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        response.put("nextToKin", nextToKinDetails);
        // Social Media Details
        Map<String, Object> socialMediaDetails = getSocialMediaDetails(clientId);
        response.put("socialMediaDetails", socialMediaDetails != null ? socialMediaDetails : Collections.EMPTY_MAP);


        // client General
        response.put("clientGeneral", clientGeneralDetails);
        return response;


    }


    private List<Map<String, Object>> languagesKnownToCitizen(long clientId) {
        List<Map<String, Object>> languageData = clientLanguageRelationGraphRepository.findClientLanguages(clientId);
        List<Map<String, Object>> responseMapList = new ArrayList<>();
        if (languageData != null && languageData.size() != 0) {
            logger.debug("Client Understand languages: " + languageData.size());
            Map<String, Object> languageUnderstand = new HashMap<>();
            Map<String, Object> response;
            LanguageLevel languageReadLevel;
            LanguageLevel languageWriteLevel;
            LanguageLevel languageSpeakLevel;
            for (Map<String, Object> map : languageData) {
                response = new HashMap<>();
                languageUnderstand = (Map<String, Object>) map.get("result");
                logger.debug("Language Understand:    " + languageUnderstand);

                languageReadLevel = languageLevelGraphRepository.findOne(Long.valueOf(String.valueOf(languageUnderstand.get("readLevel"))));
                languageWriteLevel = languageLevelGraphRepository.findOne(Long.valueOf(String.valueOf(languageUnderstand.get("writeLevel"))));
                languageSpeakLevel = languageLevelGraphRepository.findOne(Long.valueOf(String.valueOf(languageUnderstand.get("speakLevel"))));


                response.put("readLevel", languageReadLevel != null ? languageReadLevel.getId() : "");
                response.put("writeLevel", languageWriteLevel != null ? languageWriteLevel.getId() : "");
                response.put("speakLevel", languageSpeakLevel != null ? languageSpeakLevel.getId() : "");
                response.put("name", languageUnderstand.get("name"));
                response.put("id", languageUnderstand.get("id"));
                response.put("description", languageUnderstand.get("description"));

                logger.debug("adding Response: " + response);
                responseMapList.add(response);
            }
        }


        return responseMapList;
    }


    public Map<String, Object> getSocialMediaDetails(Long clientId) {
        return clientGraphRepository.findOne(clientId).retrieveSocialMediaDetails();
    }

    public boolean saveContactPersonForClient(long clientId, long staffId) {
        Client client = clientGraphRepository.findOne(clientId);
        if (client == null)
            return false;
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return false;
        }
        client.setContactPerson(staff);
        clientGraphRepository.save(client);
        return true;
    }


    public List<Map<String, Object>> getOrganizationsByClient(Long clientId) {
        logger.debug("Creating:");
        List<Organization> list = clientGraphRepository.getClientOrganizationIdList(clientId);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Organization org : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", org.getId());
            map.put("name", org.getName());
            logger.debug("Adding ");
            mapList.add(map);
        }
        return mapList;
    }


    /**
     * @param clientId
     * @param orgId
     * @return
     * @auther anil maurya
     */
    public List<OrganizationServiceQueryResult> getClientServices(Long clientId, long orgId) {
        logger.debug("Getting Demands  ClientId:" + clientId + " UnitId: " + orgId);
        List<OrganizationServiceQueryResult> serviceList = new ArrayList<>();
        //List<Long> serviceIdList = taskService.getClientTaskServices(clientId, orgId);
        //implements task service rest template client
        List<Long> serviceIdList = taskServiceRestClient.getClientTaskServices(clientId, orgId);
        return organizationServiceRepository.getOrganizationServiceByOrgIdAndServiceIds(orgId, serviceIdList);
        /*for (Long id : serviceIdList) {
            OrganizationService service = organizationServiceRepository.findOne(Long.valueOf(id));
            serviceList.add(service);
        }
        return serviceList;*/
    }

    public List<Long> getClientServicesIds(Long clientId, long orgId) {
        logger.debug("Getting Demands  ClientId:" + clientId + " UnitId: " + orgId);
        List<Long> serviceList = new ArrayList<>();
        // List<Long> serviceIdList = taskService.getClientTaskServices(clientId, orgId);
        //anil maurya  implements task service rest template client
        List<Long> serviceIdList = taskServiceRestClient.getClientTaskServices(clientId, orgId);

        for (Long id : serviceIdList) {
            OrganizationService service = organizationServiceRepository.findOne(Long.valueOf(id));
            if (service != null) serviceList.add(service.getId());
        }
        return serviceList;

    }

    public Map<String, Object> getUnitData(Long clientId, long unitId) {
        Map<String, Object> response = new HashMap<>();
        response.put("units", getOrganizationsByClient(clientId));
        return response;
    }
    //TODO not used
  /*  public List<Object> getClientTasksByService(Long clientId, Long serviceID, Long unitId) {
        return taskService.getTaskByServiceId(clientId, serviceID, unitId);
    }*/

    public List<Team> setRestrictedTeam(Long clientId, Map<String, Long[]> teamIds) {
        Client currentClient = clientGraphRepository.findOne(clientId);
        Long[] longs = teamIds.get("data");
        if (currentClient != null) {

            for (long id : longs) {
                Team currentTeam = teamGraphRepository.findOne(id);
                if (currentTeam != null) {
                    ClientTeamRelation clientTeamRelation = staffTeamRelationGraphRepository.checkRestrictedTeam(clientId, id);
                    // check if Relation already exists
                    if (clientTeamRelation != null) {
                        // if Restricted
                        if (clientTeamRelation.getType() == ClientStaffRelation.StaffType.PREFERRED) {
                            clientTeamRelation.setType(ClientStaffRelation.StaffType.FORBIDDEN);
                            staffTeamRelationGraphRepository.save(clientTeamRelation);
                        } else {
                            logger.debug("Team already restricted");
                        }
                    } else {
                        logger.debug("Client Team relation is null");
                        logger.debug("Team to Restricted: " + currentTeam.getName());
                        ClientTeamRelation teamRelation = new ClientTeamRelation(currentClient, currentTeam, ClientStaffRelation.StaffType.FORBIDDEN);
                        staffTeamRelationGraphRepository.save(teamRelation);
                    }
                } else {
                    logger.debug("Team not found with provided id ");

                }
            }

        }
        return clientGraphRepository.findForbidTeam(clientId);
    }

    public List<Object> getAllUsers(Long teamID, Long clientId, Long unitId) {
        List<Map<String, Object>> data = clientGraphRepository.getTeamMembers(teamID, clientId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath(), unitId);
        List<Object> response = new ArrayList<>();

        if (data == null) {
            return null;
        }

        for (Map<String, Object> map : data) {
            Map<String, Object> staffData = (Map<String, Object>) map.get("staffList");
            response.add(staffData);
            //    List taskTypeData = taskService.getStaffTaskTypes(staffData.get("id"));

          /*  if (taskTypeData != null) {
                Map<String, Object> completeData = new HashMap<>();
                staffData.forEach((String, Object) -> completeData.put(String, Object));
                completeData.put("taskTypes", taskTypeData);
                response.remove(staffData);
                logger.info("Complete data: " + completeData);
                response.add(completeData);

            }*/

        }
        return response;
    }


    public Map<String, Object> getClientServiceData(Long clientId, Long orgId) {
        Map<String, Object> response = new HashMap<>();
        response.put("services", getClientServices(clientId, orgId));
        return response;
    }

    /*public ClientMinimumDTO addHouseholdMemberOfClient(ClientMinimumDTO minimumDTO, long unitId, long clientId) {
        Client client = clientGraphRepository.findOne(clientId);
        if (!Optional.ofNullable(client).isPresent()) {
            logger.debug("Searching client with id " + clientId + " in unit " + unitId);
            throw new DataNotFoundByIdException("Incorrect client " + clientId);
        }
        Client houseHold = getDetailsOfHouseHold(minimumDTO);
        if(Optional.ofNullable(houseHold.getId()).isPresent()){
            clientGraphRepository.deleteHouseHoldWhoseAddressNotSame(client.getId(),houseHold.getId());
        }
        saveAddressOfHouseHold(client, houseHold);
        if (Optional.ofNullable(houseHold.getId()).isPresent()) {
            if(houseHold.getId().equals(clientId)){
                logger.error("You can't enter yourself in your house hold list : houseHoldId " + houseHold.getId() + " citizen id" + clientId);
                throw new DataNotMatchedException("Add another house hold");
            }

        } else {
            addHouseHoldInOrganization(houseHold, unitId);
        }
        save(houseHold);
        createHouseHoldRelationship(clientId, houseHold.getId());
        minimumDTO.setId(houseHold.getId());
        return minimumDTO;
    }*/


    public ClientMinimumDTO addHouseholdMemberOfClient(ClientMinimumDTO minimumDTO, long unitId, long clientId) {
        Client client = clientGraphRepository.findOne(clientId);
        if (!Optional.ofNullable(client).isPresent() || !Optional.ofNullable(client.getUser()).isPresent()) {
            logger.debug("Searching client with id " + clientId + " in unit " + unitId);
            exceptionService.dataNotFoundByIdException("message.client.id.notFound", clientId);

        }

        // Check if assigning household member as himself
        if (minimumDTO.getCprnumber().equals(client.getUser().getCprNumber())) {
            exceptionService.dataNotMatchedException("message.client.anotherHouseHold");

        }

        Client houseHold = getDetailsOfHouseHold(minimumDTO);

        Long addressIdOfHouseHold = null;
        Boolean addHouseHoldInCitizenList = false;
        if (houseHold.getId() == null) {
            addHouseHoldInCitizenList = true;
        }
        if (houseHold.getId() != null && houseHold.getHomeAddress() != null) {
            addressIdOfHouseHold = houseHold.getHomeAddress().getId();
        }

        saveAddressOfHouseHold(client, houseHold);
        clientGraphRepository.save(houseHold);

        if (addHouseHoldInCitizenList) {
            addHouseHoldInOrganization(houseHold, unitId);
        }

        // Check and Update Address of all household members
        if (addressIdOfHouseHold != null && minimumDTO.getUpdateAddressOfAllHouseholdMembers()) {
//            //  && minimumDTO.getUpdateAddressOfAllHouseholdMembers() == true
            clientAddressService.updateAddressOfAllHouseHoldMembers(client.getHomeAddress().getId(), addressIdOfHouseHold);
        }

        minimumDTO.setId(houseHold.getId());
        return minimumDTO;
    }

    private Client getDetailsOfHouseHold(ClientMinimumDTO clientMinimumDTO) {
        Client houseHoldPeople = clientGraphRepository.getClientByCPR(clientMinimumDTO.getCprnumber());
        if (!Optional.ofNullable(houseHoldPeople).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.client.id.notFound", clientMinimumDTO.getCprnumber());
        }
        if (houseHoldPeople.isCitizenDead()) {
            exceptionService.duplicateDataException("message.client.CRPNumber.deadcitizen", clientMinimumDTO.getCprnumber());
        }
        return houseHoldPeople;
    }

    private void addHouseHoldInOrganization(Client houseHold, long organizationId) {
        Organization organization = organizationGraphRepository.findOne(organizationId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.client.organisation.notFound", organizationId);

        }
        ClientOrganizationRelation clientOrganizationRelation = new ClientOrganizationRelation(houseHold, organization, new DateTime().getMillis());
        relationGraphRepository.save(clientOrganizationRelation);

    }

    private void createHouseHoldRelationship(long clientId, long houseHoldId) {
        clientGraphRepository.createHouseHoldRelationship(clientId, houseHoldId, DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime());
    }

    private void saveAddressOfHouseHold(Client client, Client houseHold) {
        if (Optional.ofNullable(client.getHomeAddress()).isPresent()) {
            houseHold.setHomeAddress(client.getHomeAddress());
        }
    }


    public List<ClientMinimumDTO> getPeopleInHousehold(long clientId) {
        return clientGraphRepository.getPeopleInHouseholdList(clientId);
    }

    public List<Long> getPreferredStaffVisitourIds(Long id) {

        List<Map<String, Object>> ids = clientGraphRepository.findPreferredStaffVisitourIds(id);

        List<Long> visitourIds = new ArrayList<>();
        for (Map<String, Object> map : ids) {
            Map<String, Object> visitourId = (Map<String, Object>) map.get("ids");
            visitourIds.add((long) visitourId.get("visitourId"));
        }

        return visitourIds;
    }

    public List<Long> getForbiddenStaffVisitourIds(Long id) {


        List<Map<String, Object>> ids = clientGraphRepository.findForbidStaffVisitourIds(id);

        List<Long> visitourIds = new ArrayList<>();
        for (Map<String, Object> map : ids) {
            Map<String, Object> visitourId = (Map<String, Object>) map.get("ids");
            visitourIds.add((long) visitourId.get("visitourId"));
        }

        return visitourIds;
    }


    /**
     * @param clientId
     * @return
     * @auther anil maurya
     */
    public boolean markClientAsDead(Long clientId, String deathDate) throws ParseException {
        Client citizen = clientGraphRepository.findOne(clientId);
        if (!Optional.ofNullable(citizen).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.client.id.notFound", clientId);

        }
        Date deathDateInDateFormat = DateUtil.convertToOnlyDate(deathDate, MONGODB_QUERY_DATE_FORMAT);
        switch (citizen.getHealthStatus()) {
            case ALIVE:
                citizen.setHealthStatus(DECEASED);
                citizen.setDeceasedDate(deathDateInDateFormat.getTime());
                plannerRestClient.deleteTaskForCitizen(clientId, DECEASED, deathDate);
                break;
            case DECEASED:
                citizen.setHealthStatus(TERMINATED);
                citizen.setTerminatedDate(deathDateInDateFormat.getTime());
                plannerRestClient.deleteTaskForCitizen(clientId, TERMINATED, deathDate);
                break;
        }
        clientGraphRepository.save(citizen);
        return true;

    }


    public List<Map<String, Object>> setClientStaffPreferredRelations(List<StaffClientData> dataList) {
        List<Map<String, Object>> staffData;
        Map<String, Object> result;
        List<Map<String, Object>> response = new ArrayList<>();

        logger.debug("Preparing response");
        for (StaffClientData data : dataList) {
            int count = staffGraphRepository.createClientStaffPreferredRelation(data.getClientId(), data.getStaffIds());
            logger.debug("No. of relationship created: " + count);
            staffData = FormatUtil.formatNeoResponse(clientGraphRepository.findPreferredStaff(data.getClientId()));

            result = new HashMap<>();
            result.put(data.getClientId().toString(), staffData);
            response.add(result);
        }
        return response;
    }


    public Object setClientStaffForbidRelations(List<StaffClientData> dataList) {
        List<Map<String, Object>> staffData;
        Map<String, Object> result;
        List<Map<String, Object>> response = new ArrayList<>();

        logger.debug("Preparing response");
        for (StaffClientData data : dataList) {
            int count = staffGraphRepository.createClientStaffForbidRelation(data.getClientId(), data.getStaffIds());
            logger.debug("No. of relationship created: " + count);
            staffData = FormatUtil.formatNeoResponse(clientGraphRepository.findForbidStaff(data.getClientId()));
            result = new HashMap<>();
            result.put(data.getClientId().toString(), staffData);
            response.add(result);
        }
        return response;
    }

    public Object setClientStaffNoneRelations(List<StaffClientData> dataList) {
        for (StaffClientData data : dataList) {
            staffGraphRepository.deleteClientStaffRelation(data.getClientId(), data.getStaffIds());
        }
        logger.debug("Preparing response");

        return null;
    }

    public List<Map<String, Object>> getClientStaffPreferredRelations(Long clientId) {
        return FormatUtil.formatNeoResponse(clientGraphRepository.findPreferredStaff(clientId));
    }

    public List<Map<String, Object>> getClientStaffForbidRelations(Long clientId) {
        return FormatUtil.formatNeoResponse(clientGraphRepository.findForbidStaff(clientId));
    }

    public Object getClientStaffForbidRelationsBulk(List<Long> clientIds) {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> staffData;
        Map<String, Object> result;
        logger.debug("No. of citizens: " + clientIds.size());
        for (Long id : clientIds) {
            result = new HashMap<>();
            staffData = FormatUtil.formatNeoResponse(clientGraphRepository.findForbidStaff(id));
            result.put("clientId", id.toString());
            result.put("staffList", staffData);

            response.add(result);
        }
        return response;
    }

    public Object getClientStaffPreferredRelationsBulk(List<Long> clientIds) {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> staffData;
        Map<String, Object> result = null;
        logger.debug("No. of citizens: " + clientIds.size());
        logger.debug("Ids. of citizens: " + clientIds);
        for (Long id : clientIds) {
            result = new HashMap<>();

            staffData = FormatUtil.formatNeoResponse(clientGraphRepository.findPreferredStaff(id));
            result.put("clientId", id.toString());
            result.put("staffList", staffData);

            response.add(result);
        }
        return response;
    }

    /**
     * @param citizenId
     * @param staffId
     * @param staffType // staff type can be prefered,forbidden,none
     * @return
     * @author prabjot
     * to assign staff to client,there can be multiple staff assign to multiple citizen
     */
    public boolean assignStaffToCitizen(long citizenId, long staffId, ClientStaffRelation.StaffType staffType) {
        clientGraphRepository.assignStaffToClient(citizenId, staffId, staffType, DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime());
        return true;
    }

    public boolean assignMultipleStaffToClient(long unitId, ClientStaffRelation.StaffType staffType) {

        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> staffQueryData = staffRetrievalService.getStaffWithBasicInfo(unitId);
        List<Long> staffIds = new ArrayList<>();
        for (Map<String, Object> map : staffQueryData) {
            staffIds.add((long) ((Map<String, Object>) map.get("data")).get("id"));
        }
        clientGraphRepository.assignMultipleStaffToClient(unitId, staffIds, staffType, DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime());
        long endTime = System.currentTimeMillis();
        logger.info("time taken, client>>assignStaffToCitizen " + (endTime - startTime) + "  ms");
        return true;
    }

    public HashMap<String, Object> getAssignedStaffOfCitizen(long unitId) {
        long startTime = System.currentTimeMillis();
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResults = staffRetrievalService.getStaffWithAdditionalInfo(unitId);

        List<Long> staffIds = new ArrayList<>();
        //TODO append or set image name from query itself than applying a loop here
        staffAdditionalInfoQueryResults.forEach(staff -> {
            staffIds.add(staff.getId());
            if (staff.getProfilePic() != null)
                staff.setProfilePic(envConfig.getServerHost() + FORWARD_SLASH + staff.getProfilePic());
        });

        List<ClientStaffQueryResult> clientStaffQueryResults = clientGraphRepository.getClientStaffRel(unitId, staffIds);

        List<Map<String, Object>> citizenStaffList = new ArrayList<>();

        List<Long> citizenIds = new ArrayList<>();
        clientStaffQueryResults.forEach(clientStaffQueryResult -> citizenIds.add(clientStaffQueryResult.getId()));
        //List<TaskTypeAggregateResult> results = customTaskTypeRepository.getTaskTypesOfCitizens(citizenIds);
        //anil maurya implements rest template here to call task service
        List<TaskTypeAggregateResult> results = taskDemandRestClient.getTaskTypesOfCitizens(citizenIds);

        clientStaffQueryResults.forEach(client -> {

            Optional<TaskTypeAggregateResult> taskTypeAggregateResult = results.stream().filter(citizenTaskType -> citizenTaskType.getId() == client.getId()).findFirst();
            HashMap<String, Object> citizen = new HashMap<>();
            HashMap<String, Object> clientMap = new HashMap<>();
            citizen.put("id", client.getId());
            citizen.put("name", client.getName());
            citizen.put("gender", client.getGender());
            citizen.put("age", client.getAge());
            citizen.put("localAreaTag", client.getLocalAreaTag());
            citizen.put("address", client.getAddress());
            citizen.put("profilePic", (client.getProfilePic() == null) ? null :
                    envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + client.getProfilePic());
            citizen.put("taskTypes", (taskTypeAggregateResult.isPresent()) ? taskTypeAggregateResult.get().getTaskTypeIds() : Collections.emptyList());
            Map<Long, Object> staffData = new HashMap<>();
            client.getStaff().forEach(staff -> {
                staffData.put((Long) staff.get("id"), staff.get("type"));
            });
            citizen.put("staff", staffData);
            clientMap.put("Client", citizen);
            citizenStaffList.add(clientMap);
        });

        //meta data preparation
        HashMap<String, Object> orgData = new HashMap<>();
        List<Map<String, Object>> skills = organizationGraphRepository.getSkillsOfOrganization(unitId);

        List<Map<String, Object>> filterSkillData = new ArrayList<>();
        for (Map<String, Object> map : skills) {
            filterSkillData.add((Map<String, Object>) map.get("data"));
        }
        orgData.put("taskTypes", taskTypeRestClient.getTaskTypesOfUnit(unitId));
        // orgData.put("taskTypes", customTaskTypeRepository.getTaskTypesOfUnit(unitId));
        orgData.put("skills", filterSkillData);
        orgData.put("teams", teamGraphRepository.getTeamsByOrganization(unitId));

        long endTime = System.currentTimeMillis();
        logger.info("Time taken by ClientService>>getAssignedStaffOfCitizen " + (endTime - startTime) + "  ms");
        Map<String, Object> clientInfo = taskDemandRestClient.getOrganizationClientsInfo(unitId, citizenStaffList);
        HashMap<String, Object> response = new HashMap<>();
        response.put("staffList", staffAdditionalInfoQueryResults);
        response.put("organization", orgData);
        response.putAll(clientInfo);
        return response;


    }


    /**
     * @param organizationId
     * @return
     * @auther Anil maurya
     */
    public Map<String, Object> getOrganizationClientsWithPlanning(Long organizationId) {
        Map<String, Object> response = new HashMap<>();

        logger.debug("Finding citizen with Id: " + organizationId);
        //   List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        //   logger.debug("CitizenList Size: " + mapList.size());

        //   Staff staff = staffGraphRepository.getByUser(UserContext.getUserDetails().getId());
        //anil maurya move some business logic in task demand service (task micro service )
        //    Map<String, Object> responseFromTask = taskDemandRestClient.getOrganizationClientsWithPlanning(staff.getId(), organizationId, mapList);
        // response.putAll(responseFromTask);

        Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);

        if (timeSlotData != null) {
            response.put("timeSlotList", timeSlotData);
        }

        return response;
    }

    @Async
    public CompletableFuture<Boolean> getPreRequisiteData(Long organizationId, Map<String, Object> clientData, List<Map<String, Object>> clientList) throws InterruptedException, ExecutionException {

        Callable<Map<String, Object>> callableTaskDemand = () -> {
            Map<String, Object> clientInfo = taskDemandRestClient.getOrganizationClientsInfo(organizationId, clientList);
            return clientInfo;
        };
        Future<Map<String, Object>> futureTaskDemand = asynchronousService.executeAsynchronously(callableTaskDemand);
        if (futureTaskDemand.get() != null) {
            clientData.putAll(futureTaskDemand.get());
        }

        // Time slot
        Callable<Map<String, Object>> callableTimeSlotData = () -> {
            Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);
            return timeSlotData;
        };
        Future<Map<String, Object>> futureTimeSlotData = asynchronousService.executeAsynchronously(callableTimeSlotData);
        if (futureTimeSlotData.get() != null) {
            clientData.put("timeSlotList", futureTimeSlotData.get());
        }

        // service Types

        Callable<List<OrganizationServiceQueryResult>> callableOrganizationServices = () -> {
            List<OrganizationServiceQueryResult> organizationServiceQueryResults = organizationServiceRepository.getOrganizationServiceByOrgId(organizationId);
            return organizationServiceQueryResults;
        };
        Future<List<OrganizationServiceQueryResult>> futureOrganizationServices = asynchronousService.executeAsynchronously(callableOrganizationServices);
        if (futureOrganizationServices.get() != null) {
            clientData.put("serviceTypes", futureOrganizationServices.get());
        }

        // Local area tags

        Callable<List<Map<String, Object>>> callableTagLists = () -> {
            List<Map<String, Object>> tagList = organizationMetadataRepository.findAllByIsDeletedAndUnitId(organizationId);
            return tagList;
        };
        Future<List<Map<String, Object>>> futureTagLists = asynchronousService.executeAsynchronously(callableTagLists);
        if (futureTagLists.get() != null) {
            List<Object> localAreaTagsList = new ArrayList<>();
            for (Map<String, Object> map : futureTagLists.get()) {
                localAreaTagsList.add(map.get("tags"));
            }
            clientData.put("localAreaTags", localAreaTagsList);
        }

        return CompletableFuture.completedFuture(true);
    }

    public Map<String, Object> getOrganizationClients(Long organizationId) throws InterruptedException, ExecutionException {

        Map<String, Object> clientData = new HashMap<String, Object>();
        List<Map<String, Object>> clientList = organizationGraphRepository.getClientsOfOrganization(organizationId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());

        if (clientList.isEmpty()) {
            return null;
        }
        Long countryId = countryGraphRepository.getCountryIdByUnitId(organizationId);
        List<CitizenStatusDTO> clientStatusList = citizenStatusService.getCitizenStatusByCountryId(countryId);
        clientData.put("clientStatusList", clientStatusList);

        CompletableFuture<Boolean> allBasicDetails = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(ClientService.class)
                .getPreRequisiteData(organizationId, clientData, clientList);
        CompletableFuture.allOf(allBasicDetails).join();

        return clientData;

    }


    public HashMap<String, Object> getOrganizationAllClients( long unitId, long staffId) {
        List<Map<String, Object>> mapList = organizationGraphRepository.getAllClientsOfOrganization(unitId);
        List<Object> clientList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            clientList.add(map.get("Client"));
        }

        HashMap<String, Object> response = new HashMap<>();
        response.put("clients", clientList);
        response.put("tableSetting", Arrays.asList(tableConfigRestClient.getTableConfiguration( unitId, staffId)));
        return response;
    }


    /**
     * @param organizationId
     * @return
     * @auther anil maurya
     */
    public List<Map<String, Object>> getOrganizationClientsExcludeDead(Long organizationId) {
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        return mapList;
    }

    /**
     * @param clientId
     * @return
     * @auther anil maurya
     * this method is called from task micro service
     */
    public ClientStaffInfoDTO getStaffClientInfo(Long clientId, String loggedInUserName) {
        Client client = getCitizenById(clientId);
        Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByUserNameIgnoreCase(loggedInUserName).getId());
        if (client == null || staff == null) {
            exceptionService.dataNotFoundByIdException("message.clientOrStaff.id.notfound");

        }
        return new ClientStaffInfoDTO(client.getId(), staff.getId());
    }

    /**
     * @param citizenId
     * @param staffId
     * @return
     * @atuher anil maurya
     * this method is called from task micro service
     */
    public Map<String, Object> getStaffAndCitizenHouseholds(Long citizenId, Long staffId) {
        Map<String, Object> staffAndCitizenHouseholdsInfo = new HashMap<>();
        Staff staff = (Optional.ofNullable(staffId).isPresent()) ? staffGraphRepository.findOne(staffId) : null;
        staffAndCitizenHouseholdsInfo.put("lastModifiedBy", (Optional.ofNullable(staff).isPresent()) ? staff.getFirstName() : "anonymous user");
        staffAndCitizenHouseholdsInfo.put("citizenHouseholds", getPeopleInHousehold(citizenId));
        return staffAndCitizenHouseholdsInfo;
    }

    /**
     * @param citizenId
     * @return
     * @auther anil maurya this method is called from task micro service
     */
    public Map<String, Object> getCitizenDetails(long citizenId) {
        Map<String, Object> citizenDetails = new HashMap<>();

        Client citizen = clientGraphRepository.findOne(citizenId);
        if (!Optional.ofNullable(citizen).isPresent() || !Optional.ofNullable(citizen.getUser()).isPresent()) {
            logger.debug("Searching client in database by id " + citizenId);
            exceptionService.dataNotFoundByIdException("message.client.id.notFound", citizenId);

        }
        citizenDetails.put("id", citizen.getId());
        citizenDetails.put("name", citizen.getUser().getFirstName() + " " + citizen.getUser().getLastName());
        citizenDetails.put("age", citizen.getUser().getAge());
        citizenDetails.put("profilePic", citizen.getProfilePic() != null ? envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + citizen.getProfilePic() : "");
        citizenDetails.put("phone", citizen.getContactDetail() != null ? citizen.getContactDetail().retreiveContactNumbers() : "");
        citizenDetails.put("address", citizen.getHomeAddress());
        citizenDetails.put("cprNumber", citizen.getUser().getCprNumber());
        citizenDetails.put("privateNumber", citizen.getContactDetail() != null ? citizen.getContactDetail().getPrivatePhone() : "NA");
        citizenDetails.put("privateAddress", citizen.getHomeAddress());
        citizenDetails.put("gender", citizen.getUser().getGender());
        citizenDetails.put("status", citizen.getCivilianStatus());
        return citizenDetails;
    }

    /**
     * @param citizenId
     * @return
     * @auther anil maurya
     * this method is call from task micro service from planner rest template
     */
    public Map<String, Object> getClientAddressInfo(Long citizenId) {

        Client citizen = clientGraphRepository.findOne(citizenId, 1);
        if (citizen.getHomeAddress() == null) {
            exceptionService.dataNotFoundByIdException("message.client.homeAddress.notAvailabe");
        }
        Map<String, Object> citizenPlanningMap = new HashMap<>();
        List<Map<String, Object>> temporaryAddressList = clientGraphRepository.getClientTemporaryAddressById(citizenId);
        citizenPlanningMap.put("temporaryAddressList", !temporaryAddressList.isEmpty() ? FormatUtil.formatNeoResponse(temporaryAddressList) : Collections.EMPTY_LIST);
        ContactAddress address = citizen.getHomeAddress();
        citizenPlanningMap.put("latitude", address.getLatitude());
        citizenPlanningMap.put("longitude", address.getLongitude());
        return citizenPlanningMap;

    }

    /**
     * @param clientExceptionDto
     * @param unitId
     * @param clientId
     * @return
     * @auther aniil maurya
     * this method is call from exception service from task micro service
     */
    public ClientTemporaryAddress changeLocationUpdateClientAddress(ClientExceptionDTO clientExceptionDto, Long unitId, Long clientId) {
        List<Long> citizensIncludedHouseHoldMembers = new ArrayList<>(clientExceptionDto.getHouseHoldMembers());
        citizensIncludedHouseHoldMembers.add(clientId);
        List<Client> citizens = clientGraphRepository.findByIdIn(citizensIncludedHouseHoldMembers);
        ClientTemporaryAddress clientTemporaryAddress = null;
        if (clientExceptionDto.getTempAddress() != null) {
            clientTemporaryAddress = updateClientTemporaryAddress(clientExceptionDto, unitId);
        }
        if (clientExceptionDto.getTemporaryAddress() != null) {
            clientTemporaryAddress = (ClientTemporaryAddress) contactAddressGraphRepository.findOne(clientExceptionDto.getTemporaryAddress());
        }
        for (Client citizen : citizens) {
            List<ClientTemporaryAddress> temporaryAddress = citizen.getTemporaryAddress();
            temporaryAddress.add(clientTemporaryAddress);
            citizen.setTemporaryAddress(temporaryAddress);
        }
        clientGraphRepository.saveAll(citizens);
        return clientTemporaryAddress;
    }


    private ClientTemporaryAddress updateClientTemporaryAddress(ClientExceptionDTO clientExceptionDto, long unitId) {
        AddressDTO addressDTO = clientExceptionDto.getTempAddress();
        ZipCode zipCode;
        ClientTemporaryAddress clientTemporaryAddress = ClientTemporaryAddress.getInstance();
        if (addressDTO.isVerifiedByGoogleMap()) {
            clientTemporaryAddress.setLongitude(addressDTO.getLongitude());
            clientTemporaryAddress.setLatitude(addressDTO.getLatitude());
            zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId());
        } else {

            //TODO igonored tomtom service for now, enable later
            /*Map<String, Object> tomtomResponse = addressVerificationService.verifyAddressClientException(addressDTO, unitId);
            if (tomtomResponse == null) {
                throw new InternalError("Address not verified by tomtom");
            }*/
            clientTemporaryAddress.setVerifiedByVisitour(false);
            clientTemporaryAddress.setCountry("Denmark");
            //clientTemporaryAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));
            // clientTemporaryAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
            clientTemporaryAddress.setLongitude(clientTemporaryAddress.getLongitude());
            clientTemporaryAddress.setLatitude(clientTemporaryAddress.getLatitude());
            zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
        }

        if (zipCode == null) {
            exceptionService.dataNotFoundByIdException("message.zipCode.notFound");

        }
        Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId());
        if (municipality == null) {
            exceptionService.dataNotFoundByIdException("message.municipality.notFound");

        }


        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData == null) {
            logger.info("Geography  not found with zipcodeId: " + zipCode.getId());
            exceptionService.dataNotFoundByIdException("message.geographyData.notFound", municipality.getId());

        }
        logger.info("Geography Data: " + geographyData);
        clientTemporaryAddress.setMunicipality(municipality);
        clientTemporaryAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
        clientTemporaryAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        clientTemporaryAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
        clientTemporaryAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        clientTemporaryAddress.setStreet(addressDTO.getStreet());
        clientTemporaryAddress.setHouseNumber(addressDTO.getHouseNumber());
        clientTemporaryAddress.setFloorNumber(addressDTO.getFloorNumber());
        clientTemporaryAddress.setCity(zipCode.getName());
        clientTemporaryAddress.setZipCode(zipCode);
        clientTemporaryAddress.setCity(zipCode.getName());
        clientTemporaryAddress.setLocationName(addressDTO.getLocationName());
        return contactAddressGraphRepository.save(clientTemporaryAddress);
    }


    public TaskDemandVisitWrapper getClientDetailsForTaskDemandVisit(TaskDemandRequestWrapper taskDemandWrapper) {
        Client client = clientGraphRepository.findOne(taskDemandWrapper.getCitizenId());
        List<Long> forbiddenStaff = getForbiddenStaffVisitourIds(taskDemandWrapper.getCitizenId());
        List<Long> preferredStaff = getPreferredStaffVisitourIds(taskDemandWrapper.getCitizenId());


        TaskAddress taskAddress = new TaskAddress();
        ClientHomeAddressQueryResult clientHomeAddressQueryResult = clientGraphRepository.getHomeAddress(client.getId());
        if (clientHomeAddressQueryResult == null) {
            return null;
        }
        ZipCode zipCode = clientHomeAddressQueryResult.getZipCode();
        ContactAddress homeAddress = clientHomeAddressQueryResult.getHomeAddress();
        taskAddress.setCountry("DK");
        taskAddress.setZip(zipCode.getZipCode());
        taskAddress.setCity(homeAddress.getCity());
        taskAddress.setStreet(homeAddress.getStreet());
        taskAddress.setHouseNumber(homeAddress.getHouseNumber());

        Map<String, Object> timeSlotMap = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotId(taskDemandWrapper.getUnitId(), taskDemandWrapper.getTimeSlotId());
        Long countryId = countryGraphRepository.getCountryIdByUnitId(taskDemandWrapper.getUnitId());

        List<LocalDate> publicHolidayList = countryGraphRepository.getAllCountryHolidaysBetweenDates(countryId, DateUtils.asLocalDate(taskDemandWrapper.getStartDate()), DateUtils.asLocalDate(taskDemandWrapper.getEndDate()));

        List<CountryHolidayCalendarQueryResult> countryHolidayCalenderList = countryGraphRepository.getCountryHolidayCalendarBetweenDates(countryId, DateUtils.asLocalDate(taskDemandWrapper.getStartDate()), DateUtils.asLocalDate(taskDemandWrapper.getEndDate()));

        TaskDemandVisitWrapper taskDemandVisitWrapper = new TaskDemandVisitWrapper.TaskDemandVisitWrapperBuilder(client,
                forbiddenStaff, preferredStaff, taskAddress).timeSlotMap(timeSlotMap).countryId(countryId)
                .publicHolidayList(publicHolidayList).build();
        taskDemandVisitWrapper.setCountryHolidayCalenderList(countryHolidayCalenderList);

        return taskDemandVisitWrapper;


    }


    /**
     * @param unitId
     * @param citizenId
     * @return
     */
    public TaskDemandVisitWrapper getPrerequisitesForTaskCreation(String userName, long unitId, long citizenId) {

        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Client citizen = clientGraphRepository.findOne(Long.valueOf(citizenId), 0);
        TaskAddress taskAddress = new TaskAddress();

        ClientHomeAddressQueryResult clientHomeAddressQueryResult = clientGraphRepository.getHomeAddress(citizen.getId());
        ZipCode zipCode = clientHomeAddressQueryResult.getZipCode();
        ContactAddress homeAddress = clientHomeAddressQueryResult.getHomeAddress();
        taskAddress.setCountry("DK");
        taskAddress.setZip(zipCode.getZipCode());
        taskAddress.setCity(homeAddress.getCity());
        taskAddress.setStreet(homeAddress.getStreet());
        taskAddress.setHouseNumber(homeAddress.getHouseNumber());

        Staff loggedInUser = staffGraphRepository.getByUser(userGraphRepository.findByUserNameIgnoreCase(userName).getId());
        List<Long> preferredStaffIds = getPreferredStaffVisitourIds(citizen.getId());
        List<Long> forbiddenStaffIds = getForbiddenStaffVisitourIds(citizen.getId());
        TaskDemandVisitWrapper taskDemandVisitWrapper = new TaskDemandVisitWrapper.TaskDemandVisitWrapperBuilder(citizen,
                preferredStaffIds, forbiddenStaffIds, taskAddress)
                .staffId(loggedInUser.getId())
                .flsCredentials(flsCredentials).build();
        return taskDemandVisitWrapper;

    }


    public List<Long> getClientIds(long unitId) {
        return clientGraphRepository.getCitizenIds(unitId);
    }

    public List<ClientOrganizationIdsDTO> getCitizenIdsByUnitIds(List<Long> unitIds) {
        return clientGraphRepository.getCitizenIdsByUnitIds(unitIds);
    }


    /**
     * @param organizationId
     * @param auth2Authentication
     * @return
     * @auther anil maurya
     * method is called from task micro service
     */
    public OrganizationClientWrapper getOrgnizationClients(Long organizationId, OAuth2Authentication auth2Authentication) {

        logger.debug("Finding citizen with Id: " + organizationId);
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        logger.debug("CitizenList Size: " + mapList.size());

        Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByUserNameIgnoreCase(auth2Authentication.getUserAuthentication().getPrincipal().toString()).getId());
        Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);
        OrganizationClientWrapper organizationClientWrapper = new OrganizationClientWrapper(mapList, timeSlotData);
        organizationClientWrapper.setStaffId(staff.getId());
        return organizationClientWrapper;


    }

    /**
     * @param organizationId
     * @return
     * @auther anil maurya
     * method is called from task micro service
     */
    public OrganizationClientWrapper getOrgnizationClients(Long organizationId, List<Long> citizenId) {

        logger.info("Finding citizen with Id: " + citizenId);
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsByClintIdList(citizenId);
        logger.info("CitizenList Size: " + mapList.size());
        Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);
        OrganizationClientWrapper organizationClientWrapper = new OrganizationClientWrapper(mapList, timeSlotData);

        return organizationClientWrapper;


    }


    public List<EscalateTaskWrapper> getClientAggregation(Long unitId) {
        List<EscalateTaskWrapper> escalatedTaskData = new ArrayList<>();
        // List<EscalatedTasksWrapper> escalatedTasksWrappers = taskMongoRepository.getStaffNotAssignedTasksGroupByCitizen(unitId);
        List<EscalatedTasksWrapper> escalatedTasksWrappers = taskServiceRestClient.getStaffNotAssignedTasks(unitId);
        for (EscalatedTasksWrapper escalatedTasksWrapper : escalatedTasksWrappers) {
            Client client = clientGraphRepository.findOne(escalatedTasksWrapper.getId());
            for (EscalateTaskWrapper escalateTaskWrapper : escalatedTasksWrapper.getTasks()) {
                escalateTaskWrapper.setCitizenName(client.getFullName());
                if (Optional.ofNullable(client.getUser()).isPresent()) {
                    if (client.getUser().getGender() != null)
                        escalateTaskWrapper.setGender(client.getUser().getGender().name());
                    escalateTaskWrapper.setAge(client.getUser().getAge());
                }
                escalateTaskWrapper.setCitizenId(client.getId());
                escalatedTaskData.add(escalateTaskWrapper);
            }
        }
        return escalatedTaskData;

    }

    public List<Client> getClientsByIdsInList(List<Long> citizenIds) {
        return clientGraphRepository.findByIdIn(citizenIds);
    }

    public ClientMinimumDTO findByCPRNumber(long clientId, long unitId, String cprNumber) {
        Client client = clientGraphRepository.findOne(clientId, unitId);
        if (!Optional.ofNullable(client).isPresent()) {
            logger.debug("Finding client by id " + client.getId());
            exceptionService.dataNotFoundByIdException("message.client.id.notFound", clientId);

        }
        Client houseHoldPerson = clientGraphRepository.getClientByCPR(cprNumber);
        ClientMinimumDTO clientMinimumDTO = null;
        if (Optional.ofNullable(houseHoldPerson).isPresent()) {
            if (houseHoldPerson.isCitizenDead()) {
                exceptionService.dataNotFoundByIdException("message.client.CRPNumber.deadcitizen", cprNumber);

            } else {
                User user = clientGraphRepository.getUserByClientId(clientId);
                clientMinimumDTO = new ClientMinimumDTO(houseHoldPerson.getId(), user.getFirstName()
                        , user.getLastName(), hasSameAddress(client, houseHoldPerson));
            }
        }
        return clientMinimumDTO;
    }


    private boolean hasSameAddress(Client client, Client houseHoldPeople) {
        ClientHomeAddressQueryResult addressOfClient = clientGraphRepository.getHomeAddress(client.getId());
        ClientHomeAddressQueryResult addressOfHouseHoldPerson = clientGraphRepository.getHomeAddress(houseHoldPeople.getId());

        boolean hasSameAddress = false;
        if (Optional.ofNullable(addressOfClient).isPresent() && Optional.ofNullable(addressOfHouseHoldPerson).isPresent()) {
            ContactAddress homeAddressOfClient = addressOfClient.getHomeAddress();
            ZipCode zipCodeOfClient = addressOfClient.getZipCode();
            ContactAddress homeAddressofHouseHoldPerson = addressOfHouseHoldPerson.getHomeAddress();
            ZipCode zipCodeOfHouseHoldPerson = addressOfHouseHoldPerson.getZipCode();
            hasSameAddress = (homeAddressOfClient.getStreet().equalsIgnoreCase(homeAddressofHouseHoldPerson.getStreet()) &&
                    homeAddressOfClient.getHouseNumber().equalsIgnoreCase(homeAddressofHouseHoldPerson.getHouseNumber()) &&
                    zipCodeOfClient.getZipCode().equals(zipCodeOfHouseHoldPerson.getZipCode()));
        }
        return hasSameAddress;
    }

    private boolean invalidCPRNumber(String cprNumber) {
        if (cprNumber == null) {
            return false;

        }
        if (cprNumber.length() == 9) {
            cprNumber = "0" + cprNumber;
        }
        if (cprNumber.length() != 10) {
            return false;
        }

        if (cprNumber != null) {
            Integer year = Integer.valueOf(cprNumber.substring(4, 6));
            Integer month = Integer.valueOf(cprNumber.substring(2, 4));
            Integer day = Integer.valueOf(cprNumber.substring(0, 2));
            Integer century = Integer.parseInt(cprNumber.substring(6, 7));

            if (century >= 0 && century <= 3) {
                century = 1900;
            }
            if (century == 4) {
                if (year <= 36) {
                    century = 2000;
                } else {
                    century = 1900;
                }
            }
            if (century >= 5 && century <= 8) {
                if (year <= 57) {
                    century = 2000;
                }
                if (year >= 58 && year <= 99) {
                    century = 1800;
                }
            }
            if (century == 9) {
                if (year <= 36) {
                    century = 2000;
                } else {
                    century = 1900;
                }
            }
            year = century + year;
            try {
                LocalDate today = LocalDate.now();
                LocalDate birthday = LocalDate.of(year, month, day);
                return true;
            } catch (Exception e) {
                return false;// Calculating age in yeas from DOB
            }


        }
        return false;
    }

    public ContactPersonTabDataDTO getDetailsForContactPersonTab(Long unitId, Long clientId) {
        List<OrganizationServiceQueryResult> organizationServices = organizationServiceRepository.getOrganizationServiceByOrgId(unitId);
        // TODO Fetch list of staff according to employment type ( According to dynamic value of employmnet type )
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS = staffGraphRepository.getAllMainEmploymentStaffDetailByUnitId(unitId);
        List<ClientMinimumDTO> clientMinimumDTOs = getPeopleInHousehold(clientId);
        List<Long> houseHoldIds = clientGraphRepository.getPeopleInHouseholdIdList(clientId);
        houseHoldIds.add(clientId);
        List<ClientContactPersonStructuredData> clientContactPersonQueryResults = refactorContactPersonList(clientId, clientGraphRepository.getClientContactPersonDataList(clientId));
        ContactPersonTabDataDTO contactPersonTabDataDTO = new ContactPersonTabDataDTO();
        contactPersonTabDataDTO.setOrganizationServices(organizationServices);
        contactPersonTabDataDTO.setStaffPersonalDetailDTOS(staffPersonalDetailDTOS);
        contactPersonTabDataDTO.setPeopleHouseHolds(clientMinimumDTOs);
        contactPersonTabDataDTO.setContactPersonDataList(clientContactPersonQueryResults);
        return contactPersonTabDataDTO;

    }

    public ClientContactPersonStructuredData saveContactPerson(Long clientId, ContactPersonDTO contactPersonDTO) {
        ClientContactPersonStructuredData clientContactPersonStructuredData = new ClientContactPersonStructuredData();
        try {

            clientContactPersonStructuredData.setHouseHolds(contactPersonDTO.getHouseHoldMembers());
            clientContactPersonStructuredData.setPrimaryStaffId(contactPersonDTO.getPrimaryStaffId());
            clientContactPersonStructuredData.setSecondaryStaffId(contactPersonDTO.getSecondaryStaffId1());
            clientContactPersonStructuredData.setSecondaryTwoStaffId(contactPersonDTO.getSecondaryStaffId2());
            clientContactPersonStructuredData.setSecondaryThreeStaffId(contactPersonDTO.getSecondaryStaffId3());
            clientContactPersonStructuredData.setServiceId(contactPersonDTO.getServiceTypeId());
            if (Optional.ofNullable(contactPersonDTO.getPrimaryStaffId()).isPresent()) {
                saveContactPersonWithGivenRelation(clientId, contactPersonDTO.getServiceTypeId(), contactPersonDTO.getPrimaryStaffId(), ClientContactPersonRelationship.ContactPersonRelationType.PRIMARY, contactPersonDTO.getHouseHoldMembers());
            }

            if (Optional.ofNullable(contactPersonDTO.getSecondaryStaffId1()).isPresent()) {
                saveContactPersonWithGivenRelation(clientId, contactPersonDTO.getServiceTypeId(), contactPersonDTO.getSecondaryStaffId1(), ClientContactPersonRelationship.ContactPersonRelationType.SECONDARY_ONE, contactPersonDTO.getHouseHoldMembers());
            }

            if (Optional.ofNullable(contactPersonDTO.getSecondaryStaffId2()).isPresent()) {
                saveContactPersonWithGivenRelation(clientId, contactPersonDTO.getServiceTypeId(), contactPersonDTO.getSecondaryStaffId2(), ClientContactPersonRelationship.ContactPersonRelationType.SECONDARY_TWO, contactPersonDTO.getHouseHoldMembers());
            }

            if (Optional.ofNullable(contactPersonDTO.getSecondaryStaffId3()).isPresent()) {
                saveContactPersonWithGivenRelation(clientId, contactPersonDTO.getServiceTypeId(), contactPersonDTO.getSecondaryStaffId3(), ClientContactPersonRelationship.ContactPersonRelationType.SECONDARY_THREE, contactPersonDTO.getHouseHoldMembers());
            }

        } catch (Exception exception) {
            logger.error("Error occurs while save contact person for client : " + clientId, exception);
            clientContactPersonStructuredData = null;
        }

        return clientContactPersonStructuredData;
    }

    public void saveContactPersonWithGivenRelation(Long clientId, Long serviceId, Long staffId, ClientContactPersonRelationship.ContactPersonRelationType contactPersonRelationType, List<Long> households) {
        ClientContactPerson clientContactPerson = clientGraphRepository.getClientContactPerson(clientId, contactPersonRelationType, serviceId);
        OrganizationService organizationService = organizationServiceRepository.findOne(serviceId);
        Staff staff = staffGraphRepository.findOne(staffId);

     /*   if(Optional.ofNullable(clientContactPerson).isPresent()){
            clientGraphRepository.removeClientContactPersonRelationship(households, contactPersonRelationType, serviceId);
        }*/
        if (!Optional.ofNullable(clientContactPerson).isPresent()) {
            households.add(clientId);
            clientContactPerson = new ClientContactPerson();
            clientContactPerson.setOrganizationService(organizationService);
            clientContactPerson.setStaff(staff);
            addClientContactPersonRelationShip(households, contactPersonRelationType, clientContactPerson);

            households.remove(clientId);
        } else {
            clientGraphRepository.removeClientContactPersonStaffRelation(clientContactPerson.getId());
            clientContactPerson.setOrganizationService(organizationService);
            clientContactPerson.setStaff(staff);
            clientContactPersonGraphRepository.save(clientContactPerson);
            if (households.isEmpty()) {
                //when household is  empty then we need to check existing housholds who may connected with contact person
                clientGraphRepository.removeClientContactPersonRelationship(clientGraphRepository.getPeopleInHouseholdIdList(clientId), clientContactPerson.getId());
            } else {
                clientGraphRepository.removeClientContactPersonRelationship(households, clientContactPerson.getId());
                addClientContactPersonRelationShip(households, contactPersonRelationType, clientContactPerson);
            }

        }

    }

    public void addClientContactPersonRelationShip(List<Long> households, ClientContactPersonRelationship.ContactPersonRelationType contactPersonRelationType, ClientContactPerson clientContactPerson) {
        for (Client client : clientGraphRepository.findAllById(households)) {
            ClientContactPersonRelationship clientContactPersonRelationship = new ClientContactPersonRelationship();
            clientContactPersonRelationship.setClient(client);
            clientContactPersonRelationship.setClientContactPerson(clientContactPerson);
            clientContactPersonRelationship.setContactPersonRelationType(contactPersonRelationType);
            clientContactPersonRelationshipRepository.save(clientContactPersonRelationship);
        }
    }

    public List<ClientContactPersonStructuredData> refactorContactPersonList(Long clientId, List<ClientContactPersonQueryResultByService> clientContactPersonQueryResultByServices) {

        List<ClientContactPersonStructuredData> clientContactPersonStructuredDataList = new ArrayList<>();
        for (ClientContactPersonQueryResultByService clientContactPersonQueryResultByService : clientContactPersonQueryResultByServices) {
            ClientContactPersonStructuredData clientContactPersonStructuredData = new ClientContactPersonStructuredData();
            clientContactPersonStructuredData.setServiceId(clientContactPersonQueryResultByService.getServiceId());
            List<Long> houseHolds = new ArrayList();
            for (Map<String, Object> clientContactPersonQueryResult : clientContactPersonQueryResultByService.getClientContactPersonQueryResults()) {
                if (Optional.ofNullable(clientContactPersonQueryResult.get("primaryStaffId")).isPresent())
                    clientContactPersonStructuredData.setPrimaryStaffId(Long.valueOf(clientContactPersonQueryResult.get("primaryStaffId").toString()));
                if (Optional.ofNullable(clientContactPersonQueryResult.get("secondaryStaffId")).isPresent())
                    clientContactPersonStructuredData.setSecondaryStaffId(Long.valueOf(clientContactPersonQueryResult.get("secondaryStaffId").toString()));
                if (Optional.ofNullable(clientContactPersonQueryResult.get("secondaryTwoStaffId")).isPresent())
                    clientContactPersonStructuredData.setSecondaryTwoStaffId(Long.valueOf(clientContactPersonQueryResult.get("secondaryTwoStaffId").toString()));
                if (Optional.ofNullable(clientContactPersonQueryResult.get("secondaryThreeStaffId")).isPresent())
                    clientContactPersonStructuredData.setSecondaryThreeStaffId(Long.valueOf(clientContactPersonQueryResult.get("secondaryThreeStaffId").toString()));
                if (Optional.ofNullable(clientContactPersonQueryResult.get("houseHold")).isPresent()) {
                    Long houseHoldId = Long.valueOf(clientContactPersonQueryResult.get("houseHold").toString());
                    if (!houseHoldId.equals(clientId) && !houseHolds.contains(houseHoldId))
                        houseHolds.add(houseHoldId);
                }
            }
            clientContactPersonStructuredData.setHouseHolds(houseHolds);
            clientContactPersonStructuredDataList.add(clientContactPersonStructuredData);
        }
        return clientContactPersonStructuredDataList;

    }

    public void removeClientContactPerson(Long clientId, Long serviceId, ClientContactPersonRelationship.ContactPersonRelationType contactPersonRelationType, List<Long> households) {
        ClientContactPerson clientContactPerson = clientGraphRepository.getClientContactPerson(clientId, contactPersonRelationType, serviceId);
        //TODO Need to find best practice instead of remove nodes
        if (Optional.ofNullable(clientContactPerson).isPresent()) {
            households.add(clientId);
            clientGraphRepository.removeClientContactPersonRelationship(households, clientContactPerson.getId());
            clientGraphRepository.removeClientContactPersonStaffRelation(clientContactPerson.getId());
            clientGraphRepository.removeClientContactPersonServiceRelation(clientContactPerson.getId());
            clientGraphRepository.removeClientContactPerson(clientContactPerson.getId());
            households.remove(clientId);
        }

    }

    /**
     * @param unitId
     * @return
     * @auther Anil maurya
     */
    public Map<String, Object> getOrganizationClientsWithFilter(Long unitId, ClientFilterDTO clientFilterDTO, String skip, String moduleId) {
        Map<String, Object> response = new HashMap<>();
        List<Long> citizenIds = new ArrayList<>();
        if (!clientFilterDTO.getServicesTypes().isEmpty() || !clientFilterDTO.getTimeSlots().isEmpty() || !clientFilterDTO.getTaskTypes().isEmpty() || clientFilterDTO.isNewDemands()) {
            List<TaskTypeAggregateResult> taskTypeAggregateResults = taskDemandRestClient.getCitizensByFilters(unitId, clientFilterDTO);
            citizenIds.addAll(taskTypeAggregateResults.stream().map(taskTypeAggregateResult -> taskTypeAggregateResult.getId()).collect(Collectors.toList()));
        }
        logger.debug("Finding citizen with Id: " + unitId);
        List<Map> mapList = new ArrayList<>();

       /* if(citizenIds.isEmpty() && clientFilterDTO.getServicesTypes().isEmpty() && clientFilterDTO.getTimeSlots().isEmpty() && clientFilterDTO.getTaskTypes().isEmpty() && !clientFilterDTO.isNewDemands()){
           if(clientFilterDTO.getLocalAreaTags().isEmpty())
                mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDeadWithFilterParameters(organizationId, envConfig.getServerHost() + FORWARD_SLASH, clientFilterDTO.getName(), clientFilterDTO.getCprNumber(), clientFilterDTO.getPhoneNumber(), clientFilterDTO.getClientStatus(), Integer.valueOf(skip));
           else
               mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDeadWithFilterParametersAndLatLng(organizationId, envConfig.getServerHost() + FORWARD_SLASH, clientFilterDTO.getName(), clientFilterDTO.getCprNumber(), clientFilterDTO.getPhoneNumber(), clientFilterDTO.getClientStatus(), Integer.valueOf(skip), clientFilterDTO.getLocalAreaTags());

        }else{
            if(clientFilterDTO.getLocalAreaTags().isEmpty())
                mapList = organizationGraphRepository.getClientsWithFilterParameters(organizationId, envConfig.getServerHost() + FORWARD_SLASH, clientFilterDTO.getName(), clientFilterDTO.getCprNumber(), clientFilterDTO.getPhoneNumber(), clientFilterDTO.getClientStatus(),Integer.valueOf(skip), citizenIds);
            else
                mapList = organizationGraphRepository.getClientsWithFilterParametersAndLatLng(organizationId, envConfig.getServerHost() + FORWARD_SLASH, clientFilterDTO.getName(), clientFilterDTO.getCprNumber(), clientFilterDTO.getPhoneNumber(), clientFilterDTO.getClientStatus(),Integer.valueOf(skip), citizenIds, clientFilterDTO.getLocalAreaTags());

        }*/

        String imagePath = envConfig.getServerHost() + FORWARD_SLASH;

        mapList.addAll(organizationGraphRepository.getClientsWithFilterParameters(clientFilterDTO, citizenIds, unitId, imagePath, skip, moduleId));
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Staff staff = staffGraphRepository.getStaffByUserId(UserContext.getUserDetails().getId(), parent.getId());
        //anil maurya move some business logic in task demand service (task micro service )
        Map<String, Object> responseFromTask = taskDemandRestClient.getOrganizationClientsWithPlanning(staff.getId(), unitId, mapList);
        response.putAll(responseFromTask);


        return response;
    }

    public ClientContactPersonStructuredData updateContactPerson(Long clientId, ContactPersonDTO contactPersonDTO) {
        Client client = clientGraphRepository.findOne(clientId);
        if (!Optional.ofNullable(client).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.client.id.notFound", clientId);

        }
        deleteContactPersonForService(contactPersonDTO.getServiceTypeId(), clientId);
        return saveContactPerson(clientId, contactPersonDTO);
    }

    private void deleteContactPersonForService(Long organizationServiceId, Long clientId) {
        clientGraphRepository.deleteContactPersonForService(organizationServiceId, clientId);
    }

    public ClientPersonalCalenderPrerequisiteDTO getPrerequisiteForPersonalCalender(Long unitId, Long clientId) {

        Organization organization = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.client.organisation.notFound", unitId);

        }

        List<Map<String, Object>> temporaryAddressList = FormatUtil.formatNeoResponse(clientGraphRepository.getClientTemporaryAddressById(clientId));
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getTimeSlots(organization.getId(), organization.getTimeSlotMode());
        List<ClientExceptionTypesDTO> clientExceptionTypesDTOS = clientExceptionRestClient.getClientExceptionTypes();
        ClientPersonalCalenderPrerequisiteDTO clientPersonalCalenderPrerequisiteDTO = new ClientPersonalCalenderPrerequisiteDTO(clientExceptionTypesDTOS,
                temporaryAddressList, timeSlotWrappers);
        return clientPersonalCalenderPrerequisiteDTO;
    }

}