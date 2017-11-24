package com.kairos.service.client;

import com.kairos.config.env.EnvConfig;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.organization.AddressDTO;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.*;
import com.kairos.persistence.model.user.country.CitizenStatus;
import com.kairos.persistence.model.user.country.RelationType;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.*;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.constants.AppConstants.IMAGES_PATH;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_HOME_ADDRESS;


/**
 * Created by Jasgeet on 22/5/17.
 */
@Service
@Transactional
public class ClientExtendedService extends UserBaseService {

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
    private ClientAddressService clientAddressService;
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
    @Inject
    private CountryGraphRepository countryGraphRepository;


    public NextToKinDTO saveNextToKin(Long unitId, Long clientId, NextToKinDTO nextToKinDTO) {
        Client client = clientGraphRepository.findOne(clientId);
        if (client == null) {
            logger.debug("Searching client with id " + clientId + " in unit " + unitId);
            throw new DataNotFoundByIdException("Incorrect client " + clientId);
        }

        if(clientGraphRepository.citizenInNextToKinList(clientId,nextToKinDTO.getCprNumber())){
            logger.error("Next to kin already exist with CPR number " + nextToKinDTO.getCprNumber());
            throw new DuplicateDataException("Next to kin already exist with CPR number");
        }

        Long homeAddressId = null;
        Client nextToKin = validateCPRNumber(nextToKinDTO.getCprNumber());
        ContactDetail contactDetail = null;
        if(!Optional.ofNullable(nextToKin).isPresent()){
            nextToKin = new Client();
        } else {
            if(nextToKin.getId().equals(clientId)){
                throw new DataNotMatchedException("Add another next to kin");
            }
            homeAddressId = clientGraphRepository.getIdOfHomeAddress(nextToKin.getId());
            contactDetail = clientGraphRepository.getContactDetailOfNextToKin(nextToKin.getId());
        }
        ContactAddress homeAddress;
        if(!Optional.ofNullable(homeAddressId).isPresent()){
            homeAddress = ContactAddress.getInstance();
        } else {
            homeAddress = contactAddressGraphRepository.findOne(homeAddressId);
        }

        if(!Optional.ofNullable(contactDetail).isPresent()){
            contactDetail = new ContactDetail();
        }
        nextToKin.saveBasicDetail(nextToKinDTO);
        nextToKin.setProfilePic(nextToKinDTO.getProfilePic());
        nextToKin.saveContactDetail(nextToKinDTO, contactDetail);
        nextToKin.setContactDetail(contactDetail);
        homeAddress = verifyAndSaveAddressOfNextToKin(unitId, nextToKinDTO.getHomeAddress(),homeAddress);
        if (!Optional.ofNullable(homeAddress).isPresent()) {
            return null;
        }
        nextToKin.setHomeAddress(homeAddress);
        CitizenStatus citizenStatus = saveCivilianStatus(nextToKinDTO,nextToKin);
        nextToKin.setCivilianStatus(citizenStatus);
        clientGraphRepository.save(nextToKin);
        saveCitizenRelation(nextToKinDTO.getRelationTypeId(), unitId, nextToKin, client.getId());
        if(!hasAlreadyNextToKin(clientId,nextToKin.getId())){
            createNextToKinRelationship(client, nextToKin);
        }
        if(!gettingServicesFromOrganization(nextToKin.getId(),unitId)){
            assignOrganizationToNextToKin(nextToKin, unitId);
        }
        return new NextToKinDTO().buildResponse(nextToKin,envConfig.getServerHost() + FORWARD_SLASH,
                nextToKinDTO.getRelationTypeId(),nextToKinDTO);
    }

    private Client validateCPRNumber(String cprNumber){
        Client client = clientGraphRepository.findByCprNumber(cprNumber.trim());
        if(Optional.ofNullable(client).isPresent() && client.isCitizenDead()){
            throw new DuplicateDataException("You can't enter the CPR of dead citizen " + cprNumber);
        }
        return client;
    }

    private Boolean hasAlreadyNextToKin(Long clientId,Long nextToKinId){
        return clientGraphRepository.hasAlreadyNextToKin(clientId,nextToKinId);
    }

    private Boolean gettingServicesFromOrganization(Long clientId,Long unitId){
        return  relationService.checkClientOrganizationRelation(clientId, unitId)>0;
    }

    private void createNextToKinRelationship(Client client, Client nextToKin) {
        ClientNextToKinRelationship clientNextToKinRelationship = new ClientNextToKinRelationship();
        clientNextToKinRelationship.setClient(client);
        clientNextToKinRelationship.setNextToKin(nextToKin);
        save(clientNextToKinRelationship);
    }

    private void assignOrganizationToNextToKin(Client nextToKin, long unitId) {
        relationGraphRepository.createClientRelationWithOrganization(nextToKin.getId(), unitId, new DateTime().getMillis(),
                UUID.randomUUID().toString().toUpperCase());
    }


    private ContactAddress verifyAndSaveAddressOfNextToKin(long unitId, AddressDTO addressDTO,
                                                           ContactAddress contactAddressToSave) {

        Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId());
        if (municipality == null) {
            logger.debug("Finding municipality using id " + addressDTO.getMunicipalityId());
            throw new DataNotFoundByIdException("Incorrect municipality id " + addressDTO.getMunicipalityId());
        }

        ZipCode zipCode;
        if (addressDTO.isVerifiedByGoogleMap()) {
            zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
            if (!Optional.ofNullable(zipCode).isPresent()) {
                logger.debug("Finding zip code in database by zip code value " + addressDTO.getZipCodeValue());
                throw new DataNotFoundByIdException("Incorrect zip code value " + addressDTO.getZipCodeValue());
            }
        } else {
            Map<String, Object> tomtomResponse = addressVerificationService.verifyAddress(addressDTO, unitId);
            if (!Optional.ofNullable(tomtomResponse).isPresent()) {
                logger.debug("Address not verified by TomTom ");
                return null;
            }
            contactAddressToSave.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
            contactAddressToSave.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));
            zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId());
            if (zipCode == null) {
                logger.debug("ZipCode Not Found returning null");
                return null;
            }
        }
        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData == null) {
            logger.info("Geography  not found with municipality id: " + municipality.getId());
            throw new InternalError("Geography data not found with provided municipality" + municipality);
        }
        contactAddressToSave.setMunicipality(municipality);
        contactAddressToSave.setProvince(String.valueOf(geographyData.get("provinceName")));
        contactAddressToSave.setCountry(String.valueOf(geographyData.get("countryName")));
        contactAddressToSave.setRegionName(String.valueOf(geographyData.get("regionName")));
        contactAddressToSave.setCountry(String.valueOf(geographyData.get("countryName")));
        contactAddressToSave.setZipCode(zipCode);

        // Native Details
        contactAddressToSave.setStreet1(addressDTO.getStreet1());
        contactAddressToSave.setHouseNumber(addressDTO.getHouseNumber());
        contactAddressToSave.setFloorNumber(addressDTO.getFloorNumber());
        contactAddressToSave.setCity(zipCode.getName());
        return contactAddressToSave;
    }

    private CitizenStatus saveCivilianStatus(NextToKinDTO nextToKinDTO, Client nextToKin) {

        if (Optional.ofNullable(nextToKinDTO.getCivilianStatusId()).isPresent()) {
            CitizenStatus citizenStatus = citizenStatusGraphRepository.findOne(nextToKinDTO.getCivilianStatusId());
            if (!Optional.ofNullable(citizenStatus).isPresent()) {
                logger.debug("Finding civilian status using id " + nextToKinDTO.getCivilianStatusId());
                throw new DataNotFoundByIdException("Incorrect id of civilian status " + citizenStatus);
            }
            return citizenStatus;
        } else {
            throw new DataNotFoundByIdException("Civilian status can't be empty");
        }
    }


    private void saveCitizenRelation(Long relationTypeId, Long unitId, Client nextToKin, Long clientId) {

        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        Client client = clientGraphRepository.findOne(clientId);
        if (Optional.ofNullable(relationTypeId).isPresent()) {
            RelationType relationType = countryGraphRepository.getRelationType(countryId, relationTypeId);
            ClientRelationType clientRelationTypeRelationship = clientGraphRepository.getClientRelationType(clientId,  nextToKin.getId());
            clientGraphRepository.removeClientRelationType( clientId,  nextToKin.getId());
            if(Optional.ofNullable(clientRelationTypeRelationship).isPresent()) clientGraphRepository.removeClientRelationById(clientRelationTypeRelationship.getId());
             clientRelationTypeRelationship = new ClientRelationType();
            clientRelationTypeRelationship.setRelationType(relationType);
            clientRelationTypeRelationship.setNextToKin(nextToKin);
            client.addClientRelations(clientRelationTypeRelationship);
            save(client);
        } else {
            throw new DataNotFoundByIdException("Relation Type can't be empty");
        }
    }

    // Add new home address of client after detaching all household members
    public ContactAddress addNewHomeAddress(long oldContactAddressId, AddressDTO addressDTO, Client client, long unitId, String type ){


        ContactAddress contactAddress = ContactAddress.getInstance();
        contactAddress = verifyAndSaveAddressOfNextToKin(unitId,addressDTO, contactAddress);
        if (contactAddress == null) {
            return null;
        }
        // Detach relationship with old address and hosehold members
        clientAddressService.detachHomeAddressFromClient(client.getId(), oldContactAddressId);
        clientAddressService.detachHouseHoldMembersFromClient(client.getId());

        return addressVerificationService.saveAndUpdateClientAddress(client, contactAddress, type);
    }

    public NextToKinDTO updateNextToKinDetail(long unitId,long nextToKinId,NextToKinDTO nextToKinDTO, long clientId, Boolean updateHouseholdAddress){
        Client nextToKin = clientGraphRepository.findOne(nextToKinId);
        if(!Optional.ofNullable(nextToKin).isPresent()){
            logger.debug("Finding next to kin by id " + nextToKinId);
            throw new DataNotFoundByIdException("Incorrect id of next to kin " + nextToKinId);
        }
        nextToKin.saveBasicDetail(nextToKinDTO);
        Long homeAddressId = clientGraphRepository.getIdOfHomeAddress(nextToKinId);
        if(!Optional.ofNullable(homeAddressId).isPresent()){
            throw new DataNotFoundByIdException("Home address not found");
        }


        ContactAddress homeAddress = contactAddressGraphRepository.findOne(homeAddressId);
        // Add new address for nextToKin of client if household adress are not being updated
        if(updateHouseholdAddress!=null && updateHouseholdAddress==false){
            homeAddress = addNewHomeAddress(homeAddressId,nextToKinDTO.getHomeAddress(),nextToKin,unitId, HAS_HOME_ADDRESS );
        } else {
            homeAddress = verifyAndSaveAddressOfNextToKin(unitId, nextToKinDTO.getHomeAddress(),homeAddress);
        }

        if (!Optional.ofNullable(homeAddress).isPresent()) {
            return null;
        }
        nextToKin.setHomeAddress(homeAddress);
        ContactDetail contactDetail = clientGraphRepository.getContactDetailOfNextToKin(nextToKinId);
        if(!Optional.ofNullable(contactDetail).isPresent()){
            throw new DataNotFoundByIdException("Contact detail not found");
        }
        nextToKin.saveContactDetail(nextToKinDTO,contactDetail);
        nextToKin.setContactDetail(contactDetail);
        CitizenStatus citizenStatus = saveCivilianStatus(nextToKinDTO,nextToKin);
        nextToKin.setCivilianStatus(citizenStatus);
        saveCitizenRelation(nextToKinDTO.getRelationTypeId(), unitId, nextToKin, clientId);
        logger.debug("Preparing response");
        clientGraphRepository.save(nextToKin);
        return new NextToKinDTO().buildResponse(nextToKin,envConfig.getServerHost() + FORWARD_SLASH,
                nextToKinDTO.getRelationTypeId(),nextToKinDTO);
    }

    public NextToKinQueryResult getNextToKinByCprNumber(String cprNumber){
        if(StringUtils.isEmpty(cprNumber) || cprNumber.length()<10){
            logger.error("Cpr number is incorrect " + cprNumber);
        }
        return clientGraphRepository.getNextToKinByCprNumber(cprNumber,envConfig.getServerHost() + FORWARD_SLASH);

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
            detail.setPrivateEmail(String.valueOf(socialMediaDetail.getWorkEmail()));
            detail.setEmergencyPhone(String.valueOf(socialMediaDetail.getEmergencyPhone()));
            detail.setHideEmergencyPhone(socialMediaDetail.isHideEmergencyPhone());
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
            return null;
        }
        String fileName = new Date().getTime() + multipartFile.getOriginalFilename();
        createDirectory(IMAGES_PATH);
        final String path = IMAGES_PATH + File.separator + fileName.trim();
        if(new File(IMAGES_PATH).isDirectory()){
            logger.debug("Writing file to: " + path.toString());
            FileUtil.writeFile(path, multipartFile);
        }
        accessToLocation.setAccessPhotoURL(fileName);
        accessToLocationGraphRepository.save(accessToLocation);
        return envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + fileName;
    }

    public void removeAccessToLocationImage(long accessToLocationId) {
        AccessToLocation accessToLocation = accessToLocationGraphRepository.findOne(accessToLocationId);
        if (accessToLocation == null) {
            throw new InternalError("Access to location is null");
        }
        accessToLocation.setAccessPhotoURL(null);
        accessToLocationGraphRepository.save(accessToLocation);
    }

    public HashMap<String, String> uploadImageOfNextToKin(MultipartFile multipartFile){
        String fileName = writeFile(multipartFile);
        HashMap<String,String> imageurls = new HashMap<>();
        imageurls.put("profilePic",fileName);
        imageurls.put("profilePicUrl",envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()+fileName);
        return imageurls;
    }

    private String writeFile(MultipartFile multipartFile){
        String fileName = new Date().getTime() + multipartFile.getOriginalFilename();
        createDirectory(IMAGES_PATH);
        final String path = IMAGES_PATH + File.separator + fileName.trim();
        if(new File(IMAGES_PATH).isDirectory()){
            logger.debug("Writing file to: " + path.toString());
            FileUtil.writeFile(path, multipartFile);
        }
        return fileName;
    }

    public HashMap<String,String> updateImageOfNextToKin(long unitId,long nextToKinId,MultipartFile multipartFile){
        Client nextToKin = clientGraphRepository.findOne(nextToKinId,unitId);
        if(nextToKin == null){
            logger.debug("Searching client with id " + nextToKin + " in unit " + unitId);
            throw new DataNotFoundByIdException("Incorrect client " + nextToKin);
        }
        String fileName = writeFile(multipartFile);
        nextToKin.setProfilePic(fileName);
        clientGraphRepository.save(nextToKin);
        HashMap<String,String> imageurls = new HashMap<>();
        imageurls.put("profilePic",fileName);
        imageurls.put("profilePicUrl",envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()+ fileName);
        return imageurls;
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
        if(new File(IMAGES_PATH).isDirectory()){
            logger.debug("Writing file to: " + path.toString());
            FileUtil.writeFile(path, multipartFile);
        }
        client.setProfilePic(fileName);
        clientGraphRepository.save(client);
        return envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()+ fileName;
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
