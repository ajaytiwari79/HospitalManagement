package com.kairos.service.client;

import com.kairos.client.ClientServiceRestClient;
import com.kairos.client.PlannerServiceRestTemplateClient;
import com.kairos.client.dto.*;
import com.kairos.config.env.EnvConfig;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.organization.AddressDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.client.*;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.language.LanguageLevel;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.StaffAdditionalInfoQueryResult;
import com.kairos.persistence.model.user.staff.StaffClientData;
import com.kairos.persistence.repository.organization.*;
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
import com.kairos.response.dto.web.ClientStaffInfoDTO;
import com.kairos.response.dto.web.EscalateTaskWrapper;
import com.kairos.response.dto.web.EscalatedTasksWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.CitizenStatusService;
import com.kairos.service.integration.IntegrationService;
import com.kairos.service.organization.TimeSlotService;
import com.kairos.service.staff.StaffService;
import com.kairos.util.FormatUtil;
import com.kairos.util.userContext.UserContext;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

import static com.kairos.constants.AppConstants.KAIROS;

/**
 * Created by oodles on 28/9/16.
 */
@Service
@Transactional
public class ClientService extends UserBaseService {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
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
    ClientServiceRestClient clientServiceRestClient;
    @Autowired
    PlannerServiceRestTemplateClient plannerServiceRestTemplateClient;
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

    public Client createCitizen(Client client) {


        if (client.getEmail() == null) {
            logger.debug("Creating email with CPR");
            String cpr = client.getCprNumber();
            String email = cpr + KAIROS;
            client.setEmail(email);
            client.setUserName(email);
        }
        if (checkCitizenCPRConstraint(client)) {
            logger.debug("Creating Client..........");
            Client createClient = clientGraphRepository.save(generateAgeAndGenderFromCPR(client));
            createClient.setNextToKin(new Client());
            return (Client) save(createClient);
        }
        return null;

    }


    public Client createCitizen(ClientMinimumDTO clientMinimumDTO, Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Can't find Organization with providedId");
        }

        if (clientMinimumDTO != null) {


            Client client = clientGraphRepository.findByCPRNumber(clientMinimumDTO.getCprnumber());

            if (client != null) {
                logger.debug("Using Existing Client..........");

                int count = relationService.checkClientOrganizationRelation(client.getId(), unitId);

                if (count != 0) {
                    throw new DuplicateDataException("Can't create citizen with same CPR in same Organization");
                }
                logger.debug("Creating Existing Client relationship : " + client.getId());
                ClientOrganizationRelation relation = new ClientOrganizationRelation(client, organization, new Date().getTime());
                relationService.createRelation(relation);
                return client;


            } else {
                logger.debug("Creating New Client..........");

                client = new Client();
                client.setFirstName(clientMinimumDTO.getFirstName());
                client.setLastName(clientMinimumDTO.getLastName());
                client.setCprNumber(clientMinimumDTO.getCprnumber());

                if (client.getEmail() == null) {
                    logger.debug("Creating email with CPR");
                    String cpr = client.getCprNumber();
                    String email = cpr + KAIROS;
                    client.setEmail(email);
                    client.setUserName(email);
                    Client nextToKin = new Client();
                    client.setNextToKin(nextToKin);
                }

                Client createdClient = generateAgeAndGenderFromCPR(client);
                client.setNextToKin(new Client());
                if (createdClient != null) {
                    save(createdClient);
                    ClientOrganizationRelation clientOrganizationRelation = new ClientOrganizationRelation(createdClient, organization, new DateTime().getMillis());
                    logger.debug("Creating Relation with Organization: " + organization.getName());
                    relationGraphRepository.save(clientOrganizationRelation);
                    return createdClient;
                }
            }


        }
        return null;
    }

    public boolean checkCitizenCPRConstraint(Client client) {
        logger.debug("Checking CRP Constraints...");
        if (client.getCprNumber() != null && clientGraphRepository.findByCPRNumber(client.getCprNumber()) != null) {
            logger.debug("CPR number matched !");
            return false;
        }
        return true;
    }


    public Client generateAgeAndGenderFromCPR(Client client) {
        logger.debug("Generating Gender and Age from CPR....");
        String cpr = client.getCprNumber();
        String defaultPicUrl;
        if (cpr == null || cpr == "") {
            logger.debug("CPR number not found");
            return null;
        }
        Integer ageVariable = Integer.valueOf(cpr.substring(cpr.length() - 1));
        if (ageVariable % 2 == 0) {
            logger.debug("Gender detected for Client: Female");
            client.setGender(Gender.FEMALE);
            defaultPicUrl = "default_female_icon.png";

        } else {
            logger.debug("Gender detected for Client: Male");
            client.setGender(Gender.MALE);
            defaultPicUrl = "default_male_icon.png";
        }
        client.setProfilePic(defaultPicUrl);
//        save(client);
        return client;
    }


    public Client getCitizenById(Long id) {
        return clientGraphRepository.findOne(id);
    }


    public void safeDeleteCitizen(Long id) {
        super.safeDeleteEntity(id);
    }


    public Map<String, Object> setGeneralDetails(ClientPersonalDto client) {
        Client currentClient = clientGraphRepository.findOne(client.getId());
        if (currentClient != null) {
            currentClient.setCprNumber(client.getCprNumber());
            currentClient.setFirstName(client.getFirstName());
            currentClient.setLastName(client.getLastName());
            currentClient.setNameAmongStaff(client.getNameAmongStaff());
            if (client.getCivilianStatus().getId() != null) {
                currentClient.setCivilianStatus(citizenStatusGraphRepository.findOne(client.getCivilianStatus().getId()));
            }
            currentClient.setGender(client.getGender());
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
            String imageUrl = envConfig.getServerHost() + File.separator + image;
            data.put("profilePic", imageUrl);


            List<Map<String, Object>> languageUnderstands = findLanguageUnderstands(updatedClient.getId());
            if (languageUnderstands != null) {
                data.put("languageUnderstands", languageUnderstands);

            }
            return data;
        }
        return null;
    }


    public Map<String, Object> retrieveGeneralDetails(long clientId, long unitId) {
        Map<String, Object> response = new HashMap<>();
        Client currentClient = clientGraphRepository.findOne(clientId);

        // Client Data
        if (currentClient != null) {
            // Client General Information
            Map<String, Object> clientGeneralDetails = currentClient.retrieveClientGeneralDetails();

            // Client Profile Picture
            String url = envConfig.getServerHost() + File.separator;

            clientGeneralDetails.put("profilePic", url + (String) clientGeneralDetails.get("profilePic"));
            clientGeneralDetails.put("civilianStatus", clientGraphRepository.findCitizenCivilianStatus(clientId));

            // Client Language Data
            clientGeneralDetails.put("languageUnderstands", findLanguageUnderstands(clientId));

            clientGeneralDetails.put("languageUnderstandsIds", clientLanguageRelationGraphRepository.findClientLanguagesId(clientId).toArray());
            Long countryId = countryGraphRepository.getCountryOfUnit(unitId);

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
            } else {
                logger.debug("Country not found");
            }


            // NextToKin
            Client kin = (Client) clientGraphRepository.getNextToKin(clientId);
            if (kin != null) {
                Map<String, Object> nextToKinDetails = clientGraphRepository.findOne(kin.getId(), 2).retrieveNextToKinDetails();
                if (nextToKinDetails != null) {
                    String imageUrl = envConfig.getServerHost() + File.separator + (String) nextToKinDetails.get("profilePic");
                    nextToKinDetails.put("profilePic", imageUrl);
                }
                response.put("nextToKin", nextToKinDetails);
            }

            // Social Media Details
            Map<String, Object> socialMediaDetails = getSocialMediaDetails(clientId);
            response.put("socialMediaDetails", socialMediaDetails != null ? socialMediaDetails : Collections.EMPTY_MAP);
            response.put("civilianStatus", citizenStatusService.getCitizenStatusByCountryIdAnotherFormat(countryId));

            // client General
            response.put("clientGeneral", clientGeneralDetails);
            return response;
        }
        return null;
    }


    private List<Map<String, Object>> findLanguageUnderstands(long clientId) {
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
        save(client);
        return true;
    }


    public List<Map<String, Object>> getOrganizationHierarchy(Long clientId) {
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
    public List<OrganizationService> getClientServices(Long clientId, long orgId) {
        logger.debug("Getting Demands  ClientId:" + clientId + " UnitId: " + orgId);
        List<OrganizationService> serviceList = new ArrayList<>();
        //List<Long> serviceIdList = taskService.getClientTaskServices(clientId, orgId);
        //implements task service rest template client
        List<Long> serviceIdList = clientServiceRestClient.getClientTaskServices(clientId, orgId);

        for (Long id : serviceIdList) {
            OrganizationService service = organizationServiceRepository.findOne(Long.valueOf(id));
            serviceList.add(service);
        }
        return serviceList;
    }

    public List<Long> getClientServicesIds(Long clientId, long orgId) {
        logger.debug("Getting Demands  ClientId:" + clientId + " UnitId: " + orgId);
        List<Long> serviceList = new ArrayList<>();
        // List<Long> serviceIdList = taskService.getClientTaskServices(clientId, orgId);
        //anil maurya  implements task service rest template client
        List<Long> serviceIdList = clientServiceRestClient.getClientTaskServices(clientId, orgId);

        for (Long id : serviceIdList) {
            OrganizationService service = organizationServiceRepository.findOne(Long.valueOf(id));
            if (service != null) serviceList.add(service.getId());
        }
        return serviceList;

    }

    public Map<String, Object> getUnitData(Long clientId, long unitId) {
        Map<String, Object> response = new HashMap<>();
        response.put("units", getOrganizationHierarchy(clientId));
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

    public List<Object> getAllUsers(Long teamID, Long clientId) {
        List<Map<String, Object>> data = clientGraphRepository.getTeamMembers(teamID, clientId, envConfig.getServerHost() + File.separator);
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

    public Map<String, Object> addHouseholdToClient(ClientMinimumDTO minimumDTO, long unitId, long clientId) throws CloneNotSupportedException {
        Client client = clientGraphRepository.findOne(clientId);
        if (client == null) {
            throw new InternalError("Client can't null");
        }
        Client createdHousehold = createCitizen(minimumDTO, unitId);
        Client nextToKin = clientGraphRepository.getNextToKin(clientId);
        ContactAddress homeAddress = (client.getHomeAddress() == null) ? null : ContactAddress.copyProperties(client.getHomeAddress(), ContactAddress.getInstance());
        createdHousehold.setHomeAddress(homeAddress);
        HouseHoldPeopleRelationship houseHoldPeopleRelationship = new HouseHoldPeopleRelationship();
        houseHoldPeopleRelationship.setClient(client);
        houseHoldPeopleRelationship.setPeopleInHouseHold(createdHousehold);
        save(houseHoldPeopleRelationship);
        client.setNextToKin(nextToKin);
        save(client);
        return createdHousehold.retrieveMinimumDetails();
    }


    public List<Map<String, Object>> getPeopleInHousehold(long clientId) {
        List<Map<String, Object>> data = clientGraphRepository.getPeopleInHouseholdList(clientId);
        if (data != null) {
            return FormatUtil.formatNeoResponse(data);
        }
        return null;
    }

    public List<Long> getPreferredStaffVisitourIds(Long id) {

        List<Map<String, Object>> ids = clientGraphRepository.findPreferredStaffVisitourIds(id);

        List<Long> visitourIds = new ArrayList<>();
        for (Map<String, Object> map : ids) {
            Map<String, Object> visitourId = (Map<String, Object>) map.get("ids");
            visitourIds.add((long) visitourId.get("visitourId"));
        }

        return (visitourIds.size() > 0 ? visitourIds : Collections.EMPTY_LIST);
    }

    public List<Long> getForbiddenStaffVisitourIds(Long id) {


        List<Map<String, Object>> ids = clientGraphRepository.findForbidStaffVisitourIds(id);

        List<Long> visitourIds = new ArrayList<>();
        for (Map<String, Object> map : ids) {
            Map<String, Object> visitourId = (Map<String, Object>) map.get("ids");
            visitourIds.add((long) visitourId.get("visitourId"));
        }

        return (visitourIds.size() > 0 ? visitourIds : Collections.EMPTY_LIST);
    }


    /**
     * @param clientId
     * @return
     * @auther anil maurya
     */
    public boolean markClientAsDead(Long clientId) {
        Client client = clientGraphRepository.findOne(clientId);
        if (client == null) {
            throw new DataNotFoundByIdException("Incorrect client id ::" + clientId);
        }
        client.setCitizenDead(true);
        clientGraphRepository.save(client);
        //return plannerService.deleteTasksForCitizen(clientId);
        return clientServiceRestClient.deleteTaskForCitizen(clientId);

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
        clientGraphRepository.assignStaffToClient(citizenId, staffId, staffType, new Date().getTime(), new Date().getTime());
        return true;
    }

    public boolean assignMultipleStaffToClient(long unitId, ClientStaffRelation.StaffType staffType) {

        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> staffQueryData = staffService.getStaffWithBasicInfo(unitId);
        List<Long> staffIds = new ArrayList<>();
        for (Map<String, Object> map : staffQueryData) {
            staffIds.add((long) ((Map<String, Object>) map.get("data")).get("id"));
        }
        clientGraphRepository.assignMultipleStaffToClient(unitId, staffIds, staffType, new Date().getTime(), new Date().getTime());
        long endTime = System.currentTimeMillis();
        logger.info("time taken, client>>assignStaffToCitizen " + (endTime - startTime) + "  ms");
        return true;
    }

    public HashMap<String, Object> getAssignedStaffOfCitizen(long unitId) {
        long startTime = System.currentTimeMillis();
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResults = staffService.getStaffWithAdditionalInfo(unitId);

        List<Long> staffIds = new ArrayList<>();
        //TODO append or set image name from query itself than applying a loop here
        staffAdditionalInfoQueryResults.forEach(staff -> {
            staffIds.add(staff.getId());
            if (staff.getProfilePic() != null)
                staff.setProfilePic(envConfig.getServerHost() + File.separator + staff.getProfilePic());
        });

        List<ClientStaffQueryResult> clientStaffQueryResults = clientGraphRepository.getClientStaffRel(unitId, staffIds);

        List<Map<String, Object>> citizenStaffList = new ArrayList<>();

        List<Long> citizenIds = new ArrayList<>();
        clientStaffQueryResults.forEach(clientStaffQueryResult -> citizenIds.add(clientStaffQueryResult.getId()));
        //List<TaskTypeAggregateResult> results = customTaskTypeRepository.getTaskTypesOfCitizens(citizenIds);
        //anil maurya implements rest template here to call task service
        List<TaskTypeAggregateResult> results = clientServiceRestClient.getTaskTypesOfCitizens(citizenIds);

        clientStaffQueryResults.forEach(client -> {

            Optional<TaskTypeAggregateResult> taskTypeAggregateResult = results.stream().filter(citizenTaskType -> citizenTaskType.getId() == client.getId()).findFirst();
            HashMap<String, Object> citizen = new HashMap<>();
            citizen.put("id", client.getId());
            citizen.put("name", client.getName());
            citizen.put("gender", client.getGender());
            citizen.put("age", client.getAge());
            citizen.put("profilePic", (client.getProfilePic() == null) ? null :
                    envConfig.getServerHost() + File.separator + client.getProfilePic());
            citizen.put("taskTypes", (taskTypeAggregateResult.isPresent()) ? taskTypeAggregateResult.get().getTaskTypeIds() : Collections.emptyList());
            Map<Long, Object> staffData = new HashMap<>();
            client.getStaff().forEach(staff -> {
                staffData.put((Long) staff.get("id"), staff.get("type"));
            });
            citizen.put("staff", staffData);
            //  Map<String , String> clientDemandsHoursTasksData = taskDemandService.countCitizenTaskDemandsHoursAndTasks(client.getId(), unitId);
            citizen.put("noOfVisitationHours", 0);
            citizen.put("noOfVisitationTasks", 0);
            citizenStaffList.add(citizen);
        });

        //meta data preparation
        HashMap<String, Object> orgData = new HashMap<>();
        List<Map<String, Object>> skills = organizationGraphRepository.getSkillsOfOrganization(unitId);

        List<Map<String, Object>> filterSkillData = new ArrayList<>();
        for (Map<String, Object> map : skills) {
            filterSkillData.add((Map<String, Object>) map.get("data"));
        }
        orgData.put("taskTypes", clientServiceRestClient.getTaskTypesOfUnit(unitId));
        // orgData.put("taskTypes", customTaskTypeRepository.getTaskTypesOfUnit(unitId));
        orgData.put("skills", filterSkillData);
        orgData.put("teams", teamGraphRepository.getTeamsByOrganization(unitId));

        long endTime = System.currentTimeMillis();
        logger.info("Time taken by ClientService>>getAssignedStaffOfCitizen " + (endTime - startTime) + "  ms");
        HashMap<String, Object> response = new HashMap<>();
        response.put("staffList", staffAdditionalInfoQueryResults);
        response.put("clientList", citizenStaffList);
        response.put("organization", orgData);
        return response;


    }


    /**
     * @param organizationId

     * @return
     * @auther Anil maurya
     */
    public Map<String, Object> getOrganizationClientsWithPlanning(Long organizationId) {
        Map<String, Object> response = new HashMap<>();
        List<Object> clientList = new ArrayList<>();

        logger.debug("Finding citizen with Id: " + organizationId);
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId, envConfig.getServerHost() + File.separator);
        logger.debug("CitizenList Size: " + mapList.size());

        Staff staff = staffGraphRepository.getByUser(UserContext.getUserDetails().getId());
        //anil maurya move some business logic in task demand service (task micro service )
        Map<String, Object> responseFromTask = clientServiceRestClient.getOrganizationClientsWithPlanning(organizationId, staff.getId(), mapList);
        response.putAll(responseFromTask);

        Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);

        if (timeSlotData != null) {
            response.put("timeSlotList", timeSlotData);
        }

        return response;
    }


    public Map<String, Object> getOrganizationClients(Long organizationId) {

        Map<String, Object> clientData = new HashMap<String, Object>();
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganization(organizationId, envConfig.getServerHost() + File.separator);

        if (mapList.isEmpty()) {
            return null;
        }

        //anilm2 replace it with rest template
        Map<String, Object> clientInfo = clientServiceRestClient.getOrganizationClientsInfo(organizationId, mapList);
        Long countryId = countryGraphRepository.getCountryOfUnit(organizationId);
        List<Map<String, Object>> clientStatusList = citizenStatusService.getCitizenStatusByCountryId(countryId);

        clientData.putAll(clientInfo);

        clientData.put("clientStatusList", clientStatusList);
        List<Object> localAreaTagsList = new ArrayList<>();
        List<Map<String, Object>> tagList = organizationMetadataRepository.findAllByIsDeletedAndUnitId(organizationId);
        for (Map<String, Object> map : tagList) {
            localAreaTagsList.add(map.get("tags"));
        }
        clientData.put("localAreaTags", localAreaTagsList);
        Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);
        if (timeSlotData != null) {
            clientData.put("timeSlotList", timeSlotData);
        }
        List<Long> serviceIds = organizationServiceRepository.getServiceIdsByOrgId(organizationId);
        clientData.put("serviceTypes", organizationServiceRepository.findAll(serviceIds));

        return clientData;

    }



    public HashMap<String, Object> getOrganizationAllClients(long organizationId, long unitId, long staffId) {
        List<Map<String, Object>> mapList = organizationGraphRepository.getAllClientsOfOrganization(organizationId);
        List<Object> clientList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            clientList.add(map.get("Client"));
        }


        HashMap<String, Object> response = new HashMap<>();
        response.put("clients", clientList);
        response.put("tableSetting", Arrays.asList(clientServiceRestClient.getTableConfiguration(organizationId, unitId, staffId)));
        return response;
    }


    /**
     * @param organizationId
     * @return
     * @auther anil maurya
     */
    public List<Map<String, Object>> getOrganizationClientsExcludeDead(Long organizationId) {
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId, envConfig.getServerHost() + File.separator);
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
        Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByUserName(loggedInUserName).getId());
        if (client == null || staff == null) {
            throw new DataNotFoundByIdException("Either Client or Staff Id is invalid");
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
        staffAndCitizenHouseholdsInfo.put("lastModifiedBy", staffGraphRepository.findOne(staffId).getFirstName());
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
        if (citizen == null) {
            logger.debug("Searching client in database by id " + citizenId);
            throw new DataNotFoundByIdException("Incorrect client id " + citizenId);
        }
        citizenDetails.put("id", citizen.getId());
        citizenDetails.put("name", citizen.getFirstName() + " " + citizen.getLastName());
        citizenDetails.put("age", citizen.getAge());
        citizenDetails.put("profilePic", citizen.getProfilePic() != null ? envConfig.getServerHost() + File.separator + citizen.getProfilePic() : "");
        citizenDetails.put("phone", citizen.getContactDetail() != null ? citizen.getContactDetail().retreiveContactNumbers() : "");
        citizenDetails.put("address", citizen.getHomeAddress());
        citizenDetails.put("cprNumber", citizen.getCprNumber());
        citizenDetails.put("privateNumber", citizen.getContactDetail() != null ? citizen.getContactDetail().getPrivatePhone() : "NA");
        citizenDetails.put("privateAddress", citizen.getHomeAddress());
        citizenDetails.put("gender", citizen.getGender());
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
            throw new DataNotFoundByIdException(citizen.getFirstName() + "'s HomeAddress in not available");
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

        Client client = clientGraphRepository.findOne(clientId);
        ClientTemporaryAddress clientTemporaryAddress = null;
        if (clientExceptionDto.getTempAddress() != null) {
            clientTemporaryAddress = updateClientTemporaryAddress(clientExceptionDto, unitId, client);
                    /*if (clientTemporaryAddress != null) {
                        map.put("tempAddress", clientTemporaryAddress);
                    }*/
        }
        if (clientExceptionDto.getTemporaryAddress() != null) {
            clientTemporaryAddress = (ClientTemporaryAddress) contactAddressGraphRepository.findOne(clientExceptionDto.getTemporaryAddress());
            if (clientTemporaryAddress == null) {
                throw new InternalError("Address not found");
            }

        }

        return clientTemporaryAddress;
    }


    private ClientTemporaryAddress updateClientTemporaryAddress(ClientExceptionDTO clientExceptionDto, long unitId, Client client) {

        AddressDTO addressDTO = clientExceptionDto.getTempAddress();
        ZipCode zipCode;

        ClientTemporaryAddress clientTemporaryAddress = ClientTemporaryAddress.getInstance();
        if (addressDTO.isVerifiedByGoogleMap()) {
            clientTemporaryAddress.setLongitude(addressDTO.getLongitude());
            clientTemporaryAddress.setLatitude(addressDTO.getLatitude());
            zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId());
        } else {
            Map<String, Object> tomtomResponse = addressVerificationService.verifyAddressClientException(addressDTO, unitId);
            if (tomtomResponse == null) {
                throw new InternalError("Address not verified by tomtom");
            }
            clientTemporaryAddress.setVerifiedByVisitour(true);
            clientTemporaryAddress.setCountry("Denmark");
            clientTemporaryAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));
            clientTemporaryAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
            zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
        }

        if (zipCode == null) {
            throw new InternalError("Zip code not found");
        }
        Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId());
        if (municipality == null) {
            throw new InternalError("Municipality not found");
        }


        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData == null) {
            logger.info("Geography  not found with zipcodeId: " + zipCode.getId());
            throw new InternalError("Geography data not found with provided municipality");
        }
        logger.info("Geography Data: " + geographyData);
        clientTemporaryAddress.setMunicipality(municipality);
        clientTemporaryAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
        clientTemporaryAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        clientTemporaryAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
        clientTemporaryAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        clientTemporaryAddress.setStreet1(addressDTO.getStreet1());
        clientTemporaryAddress.setHouseNumber(addressDTO.getHouseNumber());
        clientTemporaryAddress.setFloorNumber(addressDTO.getFloorNumber());
        clientTemporaryAddress.setCity(zipCode.getName());
        clientTemporaryAddress.setZipCode(zipCode);
        clientTemporaryAddress.setCity(zipCode.getName());
        List<ClientTemporaryAddress> temporaryAddressList = client.getTemporaryAddress();
        temporaryAddressList.add(clientTemporaryAddress);
        client.setTemporaryAddress(temporaryAddressList);
        clientGraphRepository.save(client);
        return clientTemporaryAddress;
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
        taskAddress.setStreet(homeAddress.getStreet1());
        taskAddress.setHouseNumber(homeAddress.getHouseNumber());

        Map<String, Object> timeSlotMap = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotId(taskDemandWrapper.getUnitId(), taskDemandWrapper.getTimeSlotId());
        Long countryId = countryGraphRepository.getCountryOfUnit(taskDemandWrapper.getUnitId());

        List<Long> publicHolidayList = countryGraphRepository.getAllCountryHolidaysBetweenDates(countryId, taskDemandWrapper.getStartDate().getTime(), taskDemandWrapper.getEndDate().getTime());

        TaskDemandVisitWrapper taskDemandVisitWrapper = new TaskDemandVisitWrapper.TaskDemandVisitWrapperBuilder(client,
                forbiddenStaff, preferredStaff, taskAddress).timeSlotMap(timeSlotMap).countryId(countryId)
                .publicHolidayList(publicHolidayList).build();

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
        taskAddress.setStreet(homeAddress.getStreet1());
        taskAddress.setHouseNumber(homeAddress.getHouseNumber());

        Staff loggedInUser = staffGraphRepository.getByUser(userGraphRepository.findByUserName(userName).getId());
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


    /**
     *  @auther anil maurya
     *  method is called from task micro service
     * @param organizationId
     * @param auth2Authentication
     * @return
     */
   public OrganizationClientWrapper getOrgnizationClients(Long organizationId, OAuth2Authentication auth2Authentication){

       logger.debug("Finding citizen with Id: " + organizationId);
       List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganizationExcludeDead(organizationId,envConfig.getServerHost() + File.separator);
       logger.debug("CitizenList Size: " + mapList.size());

       Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByUserName(auth2Authentication.getUserAuthentication().getPrincipal().toString()).getId());
       Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);
       OrganizationClientWrapper organizationClientWrapper=new OrganizationClientWrapper(mapList,timeSlotData);
       organizationClientWrapper.setStaffId(staff.getId());
       return organizationClientWrapper;


   }

    /**
     *  @auther anil maurya
     *  method is called from task micro service
     * @param organizationId
     *
     * @return
     */
    public OrganizationClientWrapper getOrgnizationClients(Long organizationId,List<Long> citizenId){

        logger.info("Finding citizen with Id: " + citizenId);
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsByClintIdList(citizenId);
        logger.info("CitizenList Size: " + mapList.size());
        Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(organizationId);
        OrganizationClientWrapper organizationClientWrapper=new OrganizationClientWrapper(mapList,timeSlotData);

        return organizationClientWrapper;


    }


    public List<EscalateTaskWrapper> getClientAggregation(Long unitId){
        List<EscalateTaskWrapper> escalatedTaskData = new ArrayList<>();
       // List<EscalatedTasksWrapper> escalatedTasksWrappers = taskMongoRepository.getStaffNotAssignedTasksGroupByCitizen(unitId);
        List<EscalatedTasksWrapper> escalatedTasksWrappers=clientServiceRestClient.getStaffNotAssignedTasks(unitId);
        for(EscalatedTasksWrapper escalatedTasksWrapper : escalatedTasksWrappers){
            Client client = clientGraphRepository.findOne(escalatedTasksWrapper.getId());
            for(EscalateTaskWrapper escalateTaskWrapper : escalatedTasksWrapper.getTasks()){
                escalateTaskWrapper.setCitizenName(client.getFullName());
                if(client.getGender() != null)  escalateTaskWrapper.setGender(client.getGender().name());
                escalateTaskWrapper.setAge(client.getAge());
                escalateTaskWrapper.setCitizenId(client.getId());
                escalatedTaskData.add(escalateTaskWrapper);
            }
        }
        return escalatedTaskData;

    }
}