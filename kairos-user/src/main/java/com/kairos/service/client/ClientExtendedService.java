package com.kairos.service.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kairos.config.env.EnvConfig;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.utils.FileUtil;
import com.kairos.persistence.Gender;
import com.kairos.persistence.model.organization.AddressDTO;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.AccessToLocation;
import com.kairos.persistence.model.user.client.Client;
import com.kairos.persistence.model.user.client.ClientAllergies;
import com.kairos.persistence.model.user.client.ClientDiagnose;
import com.kairos.persistence.model.user.client.ClientDoctor;
import com.kairos.persistence.model.user.client.ClientLanguageRelation;
import com.kairos.persistence.model.user.client.ClientOrganizationRelation;
import com.kairos.persistence.model.user.client.ClientRelativeRelation;
import com.kairos.persistence.model.user.client.ClientStaffRelation;
import com.kairos.persistence.model.user.client.ContactAddress;
import com.kairos.persistence.model.user.client.ContactDetail;
import com.kairos.persistence.model.user.client.ContactDetailSocialDTO;
import com.kairos.persistence.model.user.client.NextToKinDTO;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.AccessToLocationGraphRepository;
import com.kairos.persistence.repository.user.client.ClientAllergiesGraphRepository;
import com.kairos.persistence.repository.user.client.ClientDiagnoseGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.client.ClientLanguageRelationGraphRepository;
import com.kairos.persistence.repository.user.client.ClientOrganizationRelationGraphRepository;
import com.kairos.persistence.repository.user.client.ClientRelativeGraphRepository;
import com.kairos.persistence.repository.user.client.ClientStaffRelationGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.client.ContactDetailsGraphRepository;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.UserBaseService;

import static com.kairos.constants.AppConstants.IMAGES_PATH;

/**
 * Created by Jasgeet on 22/5/17.
 */
@Service
@Transactional
public class ClientExtendedService  extends UserBaseService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private ClientGraphRepository clientGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ClientService clientService;
    @Inject
    private AddressVerificationService addressVerificationService;
    @Inject
    private ClientOrganizationRelationService relationService;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;
    @Inject
    EnvConfig envConfig;
    @Inject
    private CitizenStatusGraphRepository citizenStatusGraphRepository;
    @Inject
    private ContactDetailsGraphRepository contactDetailsGraphRepository;
    @Inject
    private ClientOrganizationRelationGraphRepository relationGraphRepository;
    @Inject
    private ClientStaffRelationGraphRepository staffRelationGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private ClientLanguageRelationGraphRepository clientLanguageRelationGraphRepository;
    @Inject
    private LanguageGraphRepository languageGraphRepository;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private ClientRelativeGraphRepository clientRelativeGraphRepository;
    @Inject
    private ClientDiagnoseGraphRepository clientDiagnoseGraphRepository;
    @Inject
    private ClientAllergiesGraphRepository clientAllergiesGraphRepository;
    @Inject
    private AccessToLocationGraphRepository accessToLocationGraphRepository;


    public Map<String, Object> updateNextToKin(NextToKinDTO kinDTO, long unitId, long clientId) {
        Client nextToKin = null;
        if (kinDTO.getId() != null) {
            nextToKin = clientGraphRepository.findOne(kinDTO.getId());
        } else if (nextToKin == null) {
            logger.debug("Kin not found  Creating new ");
            nextToKin = new Client();
        }


        logger.debug("NextToKin: " + kinDTO.getFirstName());

        nextToKin.setFirstName(kinDTO.getFirstName());
        nextToKin.setLastName(kinDTO.getLastName());
        nextToKin.setNickName(kinDTO.getNickName());

        List<Client> clientList = clientGraphRepository.findAllByCPRNumber(String.valueOf(kinDTO.getCprNumber()));
        if (clientList.size() > 1) {
            logger.debug("More than 1 Client found with provided  CPR Number");
            throw new DataNotFoundByIdException("Citizen with provided CPR already exist: " + clientList.get(0).getFirstName() + " " + clientList.get(0).getLastName());
        }


        Long cprNumber = kinDTO.getCprNumber();
        nextToKin.setCprNumber(cprNumber.toString());
        logger.debug("CPR number: " + cprNumber);

        nextToKin = clientService.generateAgeAndGenderFromCPR(nextToKin);

        // Setting Gender from CPR number
        if (cprNumber != null) {
            if (cprNumber % 2 == 0) {
                nextToKin.setGender(Gender.FEMALE);
            } else {
                nextToKin.setGender(Gender.MALE);
            }
        }


        // Civilian Status
        Long civilianStatusId = kinDTO.getCivilianStatus().getId();
        nextToKin.setCivilianStatus(citizenStatusGraphRepository.findOne(civilianStatusId));


        // Check if Contact details Exist
        ContactDetail detail = nextToKin.getContactDetail();
        if (detail == null) {
            detail = new ContactDetail();
        }
        // Contact details
        detail.setMobilePhone(kinDTO.getContactDetail().getMobilePhone());
        detail.setPrivatePhone(kinDTO.getContactDetail().getPrivatePhone());
        detail.setWorkPhone(kinDTO.getContactDetail().getWorkPhone());


        //Social Media Details
        detail.setFacebookAccount(kinDTO.getContactDetail().getFacebookAccount());
        detail.setMessenger(kinDTO.getContactDetail().getMessenger());
        detail.setLinkedInAccount(kinDTO.getContactDetail().getLinkedInAccount());
        detail.setTwitterAccount(kinDTO.getContactDetail().getTwitterAccount());
        detail.setPrivateEmail(kinDTO.getContactDetail().getPrivateEmail());
        detail = contactDetailsGraphRepository.save(detail);

        nextToKin.setContactDetail(detail);


        // Address Details
        AddressDTO addressDTO = kinDTO.getHomeAddress();
        ContactAddress homeAddress = nextToKin.getHomeAddress();
        if (addressDTO == null) {
            logger.debug("No Address to verify");
            return null;
        }
        if (homeAddress == null) {
            logger.debug("Initializing home Address");
            homeAddress = new ContactAddress();
            homeAddress = contactAddressGraphRepository.save(homeAddress);
        }

        if (addressDTO.isVerifiedByGoogleMap()) {
            logger.debug("Google Map verified address received ");
            // -------Parse Address from DTO -------- //
            //ZipCode
            if (addressDTO.getZipCodeValue() == 0) {
                logger.debug("No ZipCode value received");
                return null;
            }
            ZipCode zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
            if (zipCode == null) {
                logger.debug("ZipCode Not Found returning null");
                return null;
            }
            Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId());
            if (municipality == null) {
                throw new InternalError("Municpality not found");
            }


            Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
            if (geographyData == null) {
                logger.info("Geography  not found with zipcodeId: " + zipCode.getId());
                throw new InternalError("Geography data not found with provided municipality");
            }
            logger.info("Geography Data: " + geographyData);


            // Geography Data
            homeAddress.setMunicipality(municipality);
            homeAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
            homeAddress.setCountry(String.valueOf(geographyData.get("countryName")));
            homeAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
            homeAddress.setCountry(String.valueOf(geographyData.get("countryName")));


            // Coordinates
            homeAddress.setLongitude(addressDTO.getLongitude());
            homeAddress.setLatitude(addressDTO.getLatitude());


            // Native Details
            homeAddress.setStreet1(addressDTO.getStreet1());
            homeAddress.setHouseNumber(addressDTO.getHouseNumber());
            homeAddress.setFloorNumber(addressDTO.getFloorNumber());
            homeAddress.setZipCode(zipCode);

        } else {
            logger.debug("Sending address to verify from TOM TOM server");
            // Send Address to verify
            Map<String, Object> tomtomResponse = addressVerificationService.verifyAddress(addressDTO, unitId);
            if (tomtomResponse == null) {
                logger.debug("Address not verified by TomTom ");
                return null;
            }
            // -------Parse Address from DTO -------- //

            // Coordinates
            homeAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
            homeAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));


            ZipCode zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId());
            if (zipCode == null) {
                logger.debug("ZipCode Not Found returning null");
                return null;
            }
            Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId());
            if (municipality == null) {
                throw new InternalError("Municpality not found");
            }


            Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
            if (geographyData == null) {
                logger.info("Geography  not found with zipcodeId: " + zipCode.getId());
                throw new InternalError("Geography data not found with provided municipality");
            }
            logger.info("Geography Data: " + geographyData);


            // Geography Data
            homeAddress.setMunicipality(municipality);
            homeAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
            homeAddress.setCountry(String.valueOf(geographyData.get("countryName")));
            homeAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
            homeAddress.setZipCode(zipCode);

            // Native Details
            homeAddress.setStreet1(addressDTO.getStreet1());
            homeAddress.setHouseNumber(addressDTO.getHouseNumber());
            homeAddress.setFloorNumber(addressDTO.getFloorNumber());
            homeAddress.setCity(addressDTO.getZipCodeName());

        }
        nextToKin.setHomeAddress(homeAddress);


        // Creating kin organizationRelation
        Client parentClient = clientGraphRepository.findOne(clientId);
        logger.debug("Saving Kin Object");
        clientGraphRepository.save(nextToKin);
        logger.debug("Creating  Kin Relationship With " + parentClient.getFirstName() + " " + parentClient.getLastName());
        clientGraphRepository.createNextToKinRelation(parentClient.getId(), nextToKin.getId());

        int count = relationService.checkClientOrganizationRelation(nextToKin.getId(), unitId);
        if (count == 0) {
            logger.debug("Creating KIN relationship with Organization");
            relationGraphRepository.createClientRelationWithOrganization(nextToKin.getId(), unitId, new DateTime().getMillis(), UUID.randomUUID().toString().toUpperCase());
        }


        logger.debug("Preparing response");

        //Preparing response
        Map<String, Object> nextToKinDetails = nextToKin.retrieveNextToKinDetails();
        logger.debug("Next To Kin Map:  " + nextToKinDetails);

        nextToKinDetails.put("profilePic", envConfig.getServerHost() + File.separator + nextToKin.getProfilePic());
        logger.debug("Profile: " + nextToKin.getProfilePic());

        return nextToKinDetails;

    }

    public List<Object> setNeutralStaff(Long clientId, Long[] data) {
        Client client = clientGraphRepository.findOne(clientId, 0);

        if (client != null) {
            logger.debug("Iterating id to set Neutal Staff");
            for (long id : data) {
                Staff currentStaff = staffGraphRepository.findOne(id);
                if (currentStaff != null) {
                    ClientStaffRelation staffRelation = staffGraphRepository.checkRestrictedStaff(clientId, id);
                    // check if Relation already exists
                    if (staffRelation != null) {
                        // if Preferred
                        if (staffRelation.getType() == ClientStaffRelation.StaffType.NONE) {
                            logger.debug("Staff already NONE");
                        } else {
                            staffRelation.setType(ClientStaffRelation.StaffType.NONE);
                            staffRelationGraphRepository.save(staffRelation);
                        }
                    } else {
                        logger.debug("Client Staff relation is null");
                        logger.debug("Staff to None: " + currentStaff.getFirstName() + " " + currentStaff.getLastName());
                        ClientStaffRelation clientStaffRelation = new ClientStaffRelation(client, currentStaff, ClientStaffRelation.StaffType.NONE);
                        staffRelationGraphRepository.save(clientStaffRelation);
                    }
                } else {
                    logger.debug("Staff not found with provided id ");

                }
            }
        }

        List<Map<String, Object>> mapList = clientGraphRepository.findNeutualStaff(clientId);
        if (mapList != null) {
            List<Object> objectList = new ArrayList<>();
            for (Map<String, Object> map :
                    mapList) {
                Object o = map.get("staffList");
                objectList.add(o);
            }
            return objectList;
        }
        return null;

    }

    public List<Object> getClientTaskUnits(long orgId, Long clientId) {
        Client client = clientGraphRepository.findOne(clientId);
        List<Object> unitList = new ArrayList<>();
        Set<String> taskTypeIds = new HashSet<>();
        if (client != null) {
            List<Map<String, Object>> unitData = organizationGraphRepository.getClientServingOrganizations(client);
            logger.debug("Number of Organizations: " + unitData.size());
            // Get All Serving Organization to the Client
            for (Map<String, Object> map : unitData) {
                logger.debug("Unit: " + map);
                unitList.add(map.get("result"));
            }
            return unitList;
        }
        return null;
    }

    public ClientOrganizationRelation setOrganizationDetails(ClientOrganizationRelation clientOrganizationRelation) {
        // Organization Relation
        ClientOrganizationRelation relation = (ClientOrganizationRelation) findOne(clientOrganizationRelation.getId());
        if (relation != null) {
            relation.setJoinDate(clientOrganizationRelation.getJoinDate());
            return save(clientOrganizationRelation);
        }
        return null;
    }




    public Map<String, Object> setTransportationDetails(Client client) {
        Client currentClient = clientGraphRepository.findOne(client.getId());

        if (currentClient != null) {
            //Update Transport Details
            currentClient.setDriverLicenseNumber(client.getDriverLicenseNumber());
            currentClient.setUseWheelChair(client.isUseWheelChair());
            currentClient.setLiftBus(client.isLiftBus());
            currentClient.setRequiredEquipmentsList(client.getRequiredEquipmentsList());
            currentClient.setUseOwnVehicle(client.isUseOwnVehicle());
            currentClient.setWantToUserOwnVehicle(client.isWantToUserOwnVehicle());

            List<Language> languages = client.getLanguageUnderstands();
            List<Language> languageList = new ArrayList<>();

            logger.debug("Language Understands: " + client.getLanguageUnderstands().size());
            languageGraphRepository.removeAllLanguagesFromClient(currentClient.getId());
            for (Language lang : languages) {
                Language language = languageGraphRepository.findOne(lang.getId());
                ClientLanguageRelation languageRelation;
                if (language != null) {
                    languageRelation = new ClientLanguageRelation(currentClient, language);
                    logger.debug("Adding Language to list: " + language.getName());
                    languageList.add(language);
                    clientLanguageRelationGraphRepository.save(languageRelation);
                }
            }
            logger.debug("Adding Language Understand: " + languageList.size());
            //currentClient.setLanguageUnderstands(languageList);
            currentClient.setDoRequireTranslationAssistance(client.isDoRequireTranslationAssistance());
            currentClient.setRequire2peopleForTransport(client.isRequire2peopleForTransport());
            currentClient.setRequireOxygenUnderTransport(client.isRequireOxygenUnderTransport());
            save(currentClient);
            return getTransportationDetails(currentClient.getId());
        }
        return null;
    }


    public Map<String, Object> getTransportationDetails(Long clientId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> transportDetails = clientGraphRepository.findOne(clientId).retrieveTransportationDetails();

        if (transportDetails != null) {
            List<Long> languageDetails = clientLanguageRelationGraphRepository.findClientLanguagesIds(clientId);
            response.put("transportDetails", transportDetails == null ? Collections.EMPTY_MAP : transportDetails);
            response.put("languageUnderstands", languageDetails == null ? Collections.EMPTY_LIST : languageDetails.toArray());
            return response;
        }
        return null;
    }

    public List<Map<String, Object>> getRelativeDetails(Long id) {
        return clientGraphRepository.getRelativesListByClientId(id);
    }


    public ClientRelativeRelation setRelativeDetails(Map<String, Object> relativeProperties, Long clientId, Long relativeId) {
        User relative = userGraphRepository.findOne(relativeId);
        Long relationId = Long.valueOf(((String.valueOf(relativeProperties.get("relationId")))));
        ClientRelativeRelation clientRelativeRelation = clientRelativeGraphRepository.findOne(relationId);

        if (relative != null) {
            // First update UserDetails
            relative.setFirstName(String.valueOf(relativeProperties.get("firstName")));
            relative.setLastName(String.valueOf(relativeProperties.get("lastName")));

            // AddressDTO
            Long addressId = Long.valueOf((Integer) relativeProperties.get("addressId"));
            ContactAddress contactAddress = contactAddressGraphRepository.findOne(addressId);
            if (contactAddress != null) {
                logger.debug("AddressDTO found: Updating...");
                contactAddress.setStreet1((String) relativeProperties.get("street1"));
                contactAddress.setCity((String) relativeProperties.get("city"));
//                contactAddress.setZipCodeValue((Integer) relativeProperties.get("zipCode"));
                contactAddress.setCountry((String) relativeProperties.get("country"));

                contactAddress.setLongitude(Float.valueOf((Integer) relativeProperties.get("longitude")));
                contactAddress.setLatitude(Float.valueOf((Integer) relativeProperties.get("latitude")));
            }

            // Contact
            Long contactId = Long.valueOf(((String.valueOf(relativeProperties.get("contactId")))));
            ContactDetail contactDetails = contactDetailsGraphRepository.findOne(contactId);
            if (contactDetails != null) {
                logger.debug("Contact Details: Updating...");
                contactDetails.setWorkEmail((String) relativeProperties.get("workEmail"));
                contactDetails.setWorkPhone((String) relativeProperties.get("workPhone"));
                contactDetails.setMobilePhone((String) relativeProperties.get("mobilePhone"));
                contactDetails.setPrivateEmail((String) relativeProperties.get("privateEmail"));
                contactDetails.setPrivatePhone((String) relativeProperties.get("privatePhone"));
                contactDetails.setTwitterAccount((String) relativeProperties.get("twitterAccount"));
                contactDetails.setFacebookAccount((String) relativeProperties.get("facebookAccount"));
            }
            relative.setHomeAddress(contactAddress);
            relative.setContactDetail(contactDetails);
            userGraphRepository.save(relative);
        }

        if (clientRelativeRelation != null) {
            // Relation
            clientRelativeRelation.setCanUpdateOnPublicPortal((Boolean) relativeProperties.get("canUpdateOnPublicPortal"));
            clientRelativeRelation.setFullGuardian((Boolean) relativeProperties.get("isFullGuardian"));
            clientRelativeRelation.setDistanceToRelative(String.valueOf(relativeProperties.get("distanceToRelative")));
            clientRelativeRelation.setRemarks((String) relativeProperties.get("remarks"));
            clientRelativeRelation.setRelation((String) relativeProperties.get("relation"));
            clientRelativeRelation.setPriority((String) relativeProperties.get("priority"));
            return save(clientRelativeRelation);
        }
        return null;
    }


    //TODO create relative details
    public ClientRelativeRelation setNewRelativeDetails(Map<String, Object> relativeProperties, Long clientId) {
        Client relative = new Client();
        ClientRelativeRelation clientRelativeRelation = new ClientRelativeRelation();

        if (relative != null) {
            // First update UserDetails
            relative.setFirstName(String.valueOf(relativeProperties.get("firstName")));
            relative.setLastName(String.valueOf(relativeProperties.get("lastName")));

            // AddressDTO
            Long addressId = Long.valueOf((Integer) relativeProperties.get("addressId"));
            ContactAddress contactAddress = contactAddressGraphRepository.findOne(addressId);
            if (contactAddress != null) {
                logger.debug("AddressDTO found: Updating...");
                contactAddress.setStreet1((String) relativeProperties.get("street1"));
                contactAddress.setCity((String) relativeProperties.get("city"));
//                contactAddress.setZipCodeValue((Integer) relativeProperties.get("zipCode"));
                contactAddress.setCountry((String) relativeProperties.get("country"));
                contactAddress.setLongitude(Float.valueOf((Integer) relativeProperties.get("longitude")));
                contactAddress.setLatitude(Float.valueOf((Integer) relativeProperties.get("latitude")));
            }

            // Contact
            Long contactId = Long.valueOf(((String.valueOf(relativeProperties.get("contactId")))));
            ContactDetail contactDetails = contactDetailsGraphRepository.findOne(contactId);
            if (contactDetails != null) {
                logger.debug("Contact Details: Updating...");
                contactDetails.setWorkEmail((String) relativeProperties.get("workEmail"));
                contactDetails.setWorkPhone((String) relativeProperties.get("workPhone"));
                contactDetails.setMobilePhone((String) relativeProperties.get("mobilePhone"));
                contactDetails.setPrivateEmail((String) relativeProperties.get("privateEmail"));
                contactDetails.setPrivatePhone((String) relativeProperties.get("privatePhone"));
                contactDetails.setTwitterAccount((String) relativeProperties.get("twitterAccount"));
                contactDetails.setFacebookAccount((String) relativeProperties.get("facebookAccount"));
            }
            relative.setHomeAddress(contactAddress);
            relative.setContactDetail(contactDetails);
            userGraphRepository.save(relative);
        }

        // Relation
        clientRelativeRelation.setCanUpdateOnPublicPortal((Boolean) relativeProperties.get("canUpdateOnPublicPortal"));
        clientRelativeRelation.setFullGuardian((Boolean) relativeProperties.get("isFullGuardian"));
        clientRelativeRelation.setDistanceToRelative(String.valueOf(relativeProperties.get("distanceToRelative")));
        clientRelativeRelation.setRemarks((String) relativeProperties.get("remarks"));
        clientRelativeRelation.setRelation((String) relativeProperties.get("relation"));
        clientRelativeRelation.setPriority((String) relativeProperties.get("priority"));
        return save(clientRelativeRelation);
    }

    public ClientDoctor setMedicalDetails(Long clientId, ClientDoctor clientDoctor) {
        Client currentClient = clientGraphRepository.findOne(clientId);

        if (currentClient.getClientDoctorList() == null) {
            currentClient.setClientDoctorList(Arrays.asList(clientDoctor));
            clientGraphRepository.save(currentClient);
            return clientDoctor;
        }
        List<ClientDoctor> clientDoctorList = currentClient.getClientDoctorList();
        clientDoctorList.add(clientDoctor);
        clientGraphRepository.save(currentClient);
        return clientDoctor;
    }


    public ClientAllergies setHealthDetails(ClientAllergies clientAllergies, Long clientId) {
        Client currentClient = clientGraphRepository.findOne(clientId);
        if (currentClient.getClientAllergiesList() == null) {
            currentClient.setClientAllergiesList(Arrays.asList(clientAllergies));
            clientGraphRepository.save(currentClient);
            return clientAllergies;
        }
        List<ClientAllergies> clientAllergiesList = currentClient.getClientAllergiesList();
        clientAllergiesList.add(clientAllergies);
        clientGraphRepository.save(currentClient);
        return clientAllergies;
    }


    public ContactDetail setSocialMediaDetails(Long clientId, ContactDetailSocialDTO socialMediaDetail) {
        // Client Social Media
        Client currentClient = clientGraphRepository.findOne(clientId);
        logger.debug("Client found to set Social details: " + currentClient.getFirstName() + " with id: " + clientId);

        if (currentClient != null) {
            ContactDetail detail = currentClient.getContactDetail();
            if (detail == null) {
                detail = new ContactDetail();
            }


            detail.setFacebookAccount(String.valueOf(socialMediaDetail.getFacebookAccount()));
            detail.setTwitterAccount(String.valueOf(socialMediaDetail.getTwitterAccount()));
            detail.setLinkedInAccount(String.valueOf(socialMediaDetail.getLinkedInAccount()));
            detail.setMessenger(String.valueOf(socialMediaDetail.getMessenger()));

            detail.setHideMobilePhone(socialMediaDetail.isHideMobilePhone());
            detail.setHideWorkPhone(socialMediaDetail.isHideWorkPhone());
            detail.setHidePrivatePhone(socialMediaDetail.isHidePrivatePhone());

            detail.setMobilePhone(String.valueOf(socialMediaDetail.getMobilePhone()));
            detail.setWorkPhone(String.valueOf(socialMediaDetail.getWorkPhone()));
            detail.setPrivatePhone(String.valueOf(socialMediaDetail.getPrivatePhone()));
            currentClient.setContactDetail(contactDetailsGraphRepository.save(detail));

            // try saving with native repo of Node
            clientGraphRepository.save(currentClient);
            return detail;
        }
        return null;
    }


    public Map<String, Object> getMedicalDetails(Long clientId) {
        return clientGraphRepository.findOne(clientId).retrieveMedicalDetails();
        // TODO: 24/10/16 type of doctor enum
    }

    public List<ClientAllergies> getHealthDetails(Long clientId) {
        return clientGraphRepository.findOne(clientId).getClientAllergiesList();
    }

    public ClientDiagnose addDiagnoseToMedicalInformation(Long clientId, ClientDiagnose clientDiagnose) {
        Client currentClient = clientGraphRepository.findOne(clientId);
        if (currentClient.getClientDiagnoseList() == null) {
            currentClient.setClientDiagnoseList(Arrays.asList(clientDiagnose));
            clientGraphRepository.save(currentClient);
            return clientDiagnose;
        }
        List<ClientDiagnose> clientDiagnoseList = currentClient.getClientDiagnoseList();
        clientDiagnoseList.add(clientDiagnose);
        currentClient.setClientDiagnoseList(clientDiagnoseList);
        clientGraphRepository.save(currentClient);
        return clientDiagnose;

    }



    public ClientAllergies updateClientAllergy(ClientAllergies clientAllergies) {
        ClientAllergies allergies = clientAllergiesGraphRepository.findOne(clientAllergies.getId());
        allergies.setAvoidance(clientAllergies.getAvoidance());
        allergies.setAllergyValidated(clientAllergies.isAllergyValidated());
        allergies.setAllergyType(clientAllergies.getAllergyType());
        allergies.setAllergyName(clientAllergies.getAllergyName());
        return clientAllergiesGraphRepository.save(clientAllergies);
    }


    public boolean deleteMedicalDiagnose(Long diagnoseId) {
        if (clientDiagnoseGraphRepository.exists(diagnoseId)) {
            logger.debug("diagnose exist");
        }
        clientDiagnoseGraphRepository.delete(diagnoseId);
        boolean result = clientDiagnoseGraphRepository.exists(diagnoseId);
        if (!result) {
            logger.debug("deleted diagnose not exist ");
        }
        return result;
    }

    public String uploadAccessToLocationImage(Long accessToLocationId, MultipartFile multipartFile) {
        AccessToLocation accessToLocation = accessToLocationGraphRepository.findOne(accessToLocationId);
        if (accessToLocation == null) {
            logger.debug("accessToLocation is null");
            return null;
        }

        String fileName = new Date().getTime() + multipartFile.getOriginalFilename();
        createDirectory(IMAGES_PATH);
        final String path = IMAGES_PATH + File.separator + fileName.trim();
        try {
            if (new File(IMAGES_PATH).isDirectory()) {
                logger.debug("Writing file to: " + path.toString());
                FileUtil.writeFile(path, multipartFile);
            }
        } catch (IOException e) {
            logger.debug("IO exception " + e);
            fileName = null;
        } catch (Exception e) {
            fileName = null;
        }
        accessToLocation.setAccessPhotoURL(fileName);
        accessToLocationGraphRepository.save(accessToLocation);
        return envConfig.getServerHost() + File.separator + fileName;
    }


    public String uploadPortrait(Long clientId, MultipartFile multipartFile) {
        Client client = clientGraphRepository.findOne(clientId);
        if (client == null) {
            logger.debug("Client is null");
            return null;
        }

        String fileName = new Date().getTime() + multipartFile.getOriginalFilename();
        createDirectory(IMAGES_PATH);
        final String path = IMAGES_PATH + File.separator + fileName.trim();
        try {
            if (new File(IMAGES_PATH).isDirectory()) {
                logger.debug("Writing file to: " + path.toString());
                FileUtil.writeFile(path, multipartFile);
            }
        } catch (IOException e) {
            logger.debug("IO exception " + e);
            fileName = null;
        } catch (Exception e) {
            fileName = null;
        }
        client.setProfilePic(fileName);
        clientGraphRepository.save(client);
        return envConfig.getServerHost() + File.separator + fileName;
    }


    private boolean createDirectory(String imagesPath) {
        File theDir = new File(imagesPath);
        if (!theDir.exists()) {
            logger.debug("creating directory: " + imagesPath);
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                logger.debug(se);
            }
            if (result) {
                logger.debug("DIR created");
            }
        }
        return false;
    }


    public boolean deleteImage(Long clientId) {
        Client currentClient = clientGraphRepository.findOne(clientId);
        String defaultPic = (Gender.MALE.equals(currentClient.getGender())) ? "default_male_icon.png" : "default_female_icon.png";
        currentClient.setProfilePic(defaultPic);
        clientGraphRepository.save(currentClient);
        return false;
    }

}
