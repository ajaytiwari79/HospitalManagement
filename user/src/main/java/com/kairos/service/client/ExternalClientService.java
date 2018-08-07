package com.kairos.service.client;

import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.relationships.ClientOrganizationRelation;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.common.CitizenStatus;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.client.ContactDetailsGraphRepository;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.region.RegionService;
import com.kairos.user.organization.AddressDTO;
import com.kairos.user.patient.PatientRelative;
import com.kairos.user.patient.PatientWrapper;
import com.kairos.user.staff.CurrentAddress;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_HOME_ADDRESS;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SECONDARY_ADDRESS;


/**
 * Created by Jasgeet on 22/5/17.
 */
@Service
@Transactional
public class ExternalClientService extends UserBaseService {

    @Inject
    private ClientService clientService;

    @Inject
    private CitizenStatusGraphRepository citizenStatusGraphRepository;

    @Inject
    private ContactDetailsGraphRepository contactDetailsGraphRepository;

    @Inject
    private ClientOrganizationRelationService relationService;

    @Inject
    private ClientGraphRepository clientGraphRepository;

    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;

    @Inject
    private AddressVerificationService addressVerificationService;

    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;

    @Inject
    private RegionService regionService;

    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private ExceptionService exceptionService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // ToDO FIX AND USE PENTHAO server to import
    public void addClientRelativeDetailsFromExternalService(PatientRelative patientRelative, Client client, long unitId) {
        /*Client nextToKin = client.getNextToKin();
        if (nextToKin == null) {
            nextToKin = new Client();
            client.setNextToKin(nextToKin);
        }*/

        Client nextToKin = new Client();

        // Check if Contact details Exist
        ContactDetail detail = nextToKin.getContactDetail();
        if (detail == null) {
            detail = new ContactDetail();
        }

        if (patientRelative.getRelatedPatient() == null) {
       //     nextToKin.setFirstName(patientRelative.getContact().getFirstName());
       //     nextToKin.setLastName(patientRelative.getContact().getLastName());
            // Contact details
            detail.setMobilePhone(patientRelative.getContact().getPhoneNumber());
            detail.setPrivatePhone(patientRelative.getContact().getSecondaryPhoneNumber());

        } else {
       //     nextToKin.setFirstName(patientRelative.getRelatedPatient().getFirstName());
        //    nextToKin.setLastName(patientRelative.getRelatedPatient().getLastName());
            // Contact details
            detail.setMobilePhone(patientRelative.getRelatedPatient().getMobilePhoneNumber());
            detail.setPrivatePhone(patientRelative.getRelatedPatient().getHomePhoneNumber());
            detail.setWorkPhone(patientRelative.getRelatedPatient().getWorkPhoneNumber());

//            nextToKin.setCprNumber(patientRelative.getRelatedPatient().getPatientIdentifier().getIdentifier().replace("-", ""));
  //          nextToKin.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(nextToKin.getCprNumber()));
         //   nextToKin = clientService.generateAgeAndGenderFromCPR(nextToKin);
        }

        CitizenStatus citizenStatus = citizenStatusGraphRepository.findByName(patientRelative.getContact().getMaritalStatus());
        if (citizenStatus != null) {
            client.setCivilianStatus(citizenStatus);
        }

        detail = contactDetailsGraphRepository.save(detail);
        nextToKin.setContactDetail(detail);
        save(nextToKin);
        Long homeAddressId = null;
        if (nextToKin.getHomeAddress() != null) homeAddressId = nextToKin.getHomeAddress().getId();

        if (patientRelative.getRelatedPatient() != null)
            saveContactAddressFromKmd(patientRelative.getRelatedPatient().getCurrentAddress(), nextToKin.getId(), HAS_HOME_ADDRESS, unitId, homeAddressId);

 //       client.setNextToKin(nextToKin);
        save(client);
        logger.info("nexttokin-----------> " + nextToKin.getId());

    }

    public void saveAddressDetails(PatientWrapper patientWrapper, Client client, long unitId) {
        client = clientGraphRepository.findOne(client.getId());
        Long homeAddressId = null;
        if (client.getHomeAddress() != null) homeAddressId = client.getHomeAddress().getId();
        Long secondaryAddressId = null;
        if (client.getSecondaryAddress() != null) secondaryAddressId = client.getSecondaryAddress().getId();


        if (patientWrapper.getPrimaryAddress() != null && patientWrapper.getPrimaryAddress().getAddressLine1() != null)
            saveContactAddressFromKmd(patientWrapper.getPrimaryAddress(), client.getId(), HAS_HOME_ADDRESS, unitId, homeAddressId);
        if (patientWrapper.getSecondaryAddress() != null && patientWrapper.getSecondaryAddress().getAddressLine1() != null)
            saveContactAddressFromKmd(patientWrapper.getSecondaryAddress(), client.getId(), HAS_SECONDARY_ADDRESS, unitId, secondaryAddressId);

    }

    public ContactAddress saveContactAddressFromKmd(CurrentAddress addressDTO, Long clientId, String type, long unitId, Long addressId) {

        ContactAddress contactAddress = null;
        if (addressId != null) {
            contactAddress = contactAddressGraphRepository.findOne(addressId);
        }
        if (contactAddress == null) {
            logger.debug("Creating new Address");
            contactAddress = new ContactAddress();
        }
        Client currentClient = (Client) findOne(clientId);


        logger.debug("Sending address to verify from TOM TOM server");
        // Send Address to verify
        AddressDTO addressDTO1 = new AddressDTO();
        String addressLine1 = addressDTO.getAddressLine1();
        String street = addressLine1.substring(0, addressLine1.indexOf(" "));
        String hnr = addressLine1.substring(addressLine1.indexOf(" "));
        addressDTO1.setStreet1(street);
        addressDTO1.setHouseNumber(hnr);
        addressDTO1.setCity(addressDTO.getPostalDistrict());
        addressDTO1.setMunicipalityName(addressDTO.getPostalDistrict());
        addressDTO1.setZipCodeId(addressDTO.getPostalCode());
        Map<String, Object> tomtomResponse = addressVerificationService.verifyAddress(addressDTO1, unitId);
        logger.info("tomtomResponse-----------> "+tomtomResponse);
        if (tomtomResponse != null) {
            // -------Parse Address from DTO -------- //

            contactAddress.setCountry("Denmark");
            contactAddress.setVerifiedByVisitour(true);

            // Coordinates
            contactAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
            contactAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));

            logger.debug("Setting housing type null");
            contactAddress.setTypeOfHousing(null);


        } else {

            contactAddress.setCountry("Denmark");
            contactAddress.setVerifiedByVisitour(false);

            // Coordinates
            if (addressDTO.getGeoCoordinates().getX() != null)
                contactAddress.setLongitude(Float.valueOf(String.valueOf(addressDTO.getGeoCoordinates().getX())));
            if (addressDTO.getGeoCoordinates().getY() != null)
                contactAddress.setLatitude(Float.valueOf(String.valueOf(addressDTO.getGeoCoordinates().getY())));

            logger.debug("Setting housing type null");
            contactAddress.setTypeOfHousing(null);

        }

        ZipCode zipCode = zipCodeGraphRepository.findOne(addressDTO1.getZipCodeId());
        if (zipCode == null)
            zipCode = zipCodeGraphRepository.findByZipCode(Integer.valueOf(addressDTO.getPostalCode().toString()));
        logger.info("zipCode-----------> " + zipCode);
        if (zipCode != null) {
            contactAddress.setZipCode(zipCode);
            //   logger.debug("ZipCode Not Found returning null");
            // return null;
            logger.debug("ZipCode found: " + zipCode.getName());
            Map<String, Object> geographyData = regionService.getAllZipCodesData(zipCode.getId());
            if (geographyData != null) {
                //  logger.debug("Geography  not found with zipcodeId: " + zipCode.getId());
                //  return null;
                Municipality municipality = municipalityGraphRepository.getMunicipalityByZipCodeId(zipCode.getId());
                contactAddress.setMunicipality(municipality);
                contactAddress.setProvince(String.valueOf(geographyData.get("province")));
                contactAddress.setRegionName(String.valueOf(geographyData.get("region")));
                logger.debug("Geography Data: " + geographyData);
            }
        } else {
            /*ZipCode zipCode1 = new ZipCode();
>>>>>>> development
            zipCode1.setZipCode(Integer.valueOf(addressDTO.getPostalCode().toString()));
            zipCodeGraphRepository.save(zipCode1);
            contactAddress.setZipCode(zipCode1);*/
            Municipality municipality = municipalityGraphRepository.getMunicipalityByZipCodeId(Integer.valueOf(addressDTO.getPostalCode().toString()));
            contactAddress.setMunicipality(municipality);
        }


        contactAddress.setCity(addressDTO.getPostalDistrict());

        // Native Details
        contactAddress.setStreet1(addressDTO1.getStreet1());
        contactAddress.setHouseNumber(addressDTO1.getHouseNumber());
        // contactAddress.setFloorNumber(addressDTO1.getFloorNumber());
        contactAddressGraphRepository.save(contactAddress);
        return addressVerificationService.saveAndUpdateClientAddress(currentClient, contactAddress, type);

    }
// TODO FIX the import
    public Client createCitizenFromExternalService(PatientWrapper patientWrapper, Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }

        if (patientWrapper != null) {

            String cprNumber = patientWrapper.getPatientIdentifier().getIdentifier().replace("-", "");

            logger.info("cprNumber------------> " + cprNumber);
            Client client = clientGraphRepository.findByKmdNexusExternalId(patientWrapper.getId());
            client = (client == null) ? new Client() : client;


            client.setCitizenState(patientWrapper.getPatientState().getName());
            // Need to uncomment again
            //client.setEmail(patientWrapper.getPrimaryEmailAddress());
            client.setSecondaryEmail(patientWrapper.getSecondaryEmailAddress());
            client.setImportFromKMD(true);
            client.setKmdNexusExternalId(patientWrapper.getId());
            /*client.setFirstName(patientWrapper.getFirstName());
            client.setLastName(patientWrapper.getLastName());
            client.setCprNumber(cprNumber);
            client.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(client.getCprNumber()));
            */CitizenStatus citizenStatus = citizenStatusGraphRepository.findByDescription(countryGraphRepository.getCountryIdByUnitId(unitId), patientWrapper.getMaritalStatus());

            if (citizenStatus != null) {
                client.setCivilianStatus(citizenStatusGraphRepository.findOne(citizenStatus.getId()));
            }
    // TODO UNCOMMENT AFTER FIX
         /*   if (client.getEmail() == null) {
                logger.debug("Creating email with CPR");
                String cpr = client.getCprNumber();
                String email = cpr + KAIROS;
                client.setUserName(email);
                Client nextToKin = new Client();
//                client.setNextToKin(nextToKin);
            }

            client = clientService.generateAgeAndGenderFromCPR(client);
*/            save(client);
            int count = relationService.checkClientOrganizationRelation(client.getId(), unitId);
            if (count == 0) {
                logger.debug("Creating Existing Client relationship from KMD : " + client.getId());
                ClientOrganizationRelation relation = new ClientOrganizationRelation(client, organization, DateUtil.getCurrentDate().getTime());
                relationService.createRelation(relation);
            }
            saveAddressDetails(patientWrapper, client, unitId);
            saveContactDetails(client, patientWrapper);
            if (citizenStatus != null) {
                client.setCivilianStatus(citizenStatusGraphRepository.findOne(citizenStatus.getId()));
            }
            save(client);
            return client;

        }
        return null;
    }

    public ContactDetail saveContactDetails(Client currentClient, PatientWrapper patientWrapper) {
        // Client Contact Details
       // logger.debug("Client found to set Social details: " + currentClient.getFirstName() + " with id: " + currentClient.getId());

        if (currentClient != null) {
            ContactDetail contactDetail = currentClient.getContactDetail();
            if (contactDetail == null) {
                contactDetail = new ContactDetail();
            }

            contactDetail.setWorkPhone(patientWrapper.getWorkTelephone());
            contactDetail.setPrivatePhone(patientWrapper.getHomeTelephone());
            contactDetail.setMobilePhone(patientWrapper.getMobileTelephone());
            currentClient.setContactDetail(contactDetailsGraphRepository.save(contactDetail));

            // try saving with native repo of Node
            clientGraphRepository.save(currentClient);
            return contactDetail;
        }
        return null;
    }
}
