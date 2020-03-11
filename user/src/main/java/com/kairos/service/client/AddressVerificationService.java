package com.kairos.service.client;

import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.ClientTemporaryAddress;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.country.HousingTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.IntegrationService;
import com.kairos.service.region.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 8/2/17.
 */
@Service
public class AddressVerificationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;

    @Inject
    private RegionService regionService;

    @Inject
    private HousingTypeService housingTypeService;
    @Inject
    IntegrationService integrationService;

    @Inject
    private ClientGraphRepository clientGraphRepository;

    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;

    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;

    @Inject
    private ExceptionService exceptionService;
    /*
      By Yasir
      Commented below method as we are no longer using FLS Visitour
    */
    public Map<String, Object> verifyAddress(AddressDTO contactAddress, long unitId) {
        /*Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        ZipCode zipCodeData = zipCodeGraphRepository.findOne(contactAddress.getZipCodeId());
        int zipCode;
        if(zipCodeData != null){
            zipCode = zipCodeData.getZipCode();
        }else{
            zipCode = Integer.valueOf(contactAddress.getZipCodeId().toString());
        }
        logger.debug("Verifying with Information \n house: " + contactAddress.getHouseNumber() + "\n City:" + contactAddress.getCity() + "\n ZipCode: " + zipCode +
                "\n Street: " + contactAddress.getStreet());

        Map<String, Object> addressToVerify = new HashMap<>();
        addressToVerify.put("country", "DK");
        addressToVerify.put("zip", zipCode);
        addressToVerify.put("city", contactAddress.getCity());
        addressToVerify.put("street", contactAddress.getStreet());
        addressToVerify.put("hnr", contactAddress.getHouseNumber());
        Map<String, Object> geoCodeResponse = scheduler.getGeoCode(addressToVerify, flsCredentials);

        if ((boolean) geoCodeResponse.get("isAddressVerified")) {
            logger.debug("GeoCode Response from TOM TOM : " + geoCodeResponse);
            return geoCodeResponse;
        }
        logger.debug("Address not verified with TOMTOM");*/
        return null;
    }



    /*
    By Yasir
    Commented below method as we are no longer using FLS Visitour
     */
    public Map<String, Object> verifyAddressSheet(AddressDTO contactAddress, long unitId) {
//        int zipCode = zipCodeGraphRepository.findOne(contactAddress.getZipCodeId()).getZipCode();
       /* logger.debug("Verifying with Information \n house: " + contactAddress.getHouseNumber() + "\n City:" + contactAddress.getCity() + "\n ZipCode: " + contactAddress.getZipCodeValue() +
                "\n Street: " + contactAddress.getStreet());
        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, Object> addressToVerify = new HashMap<>();
        addressToVerify.put("country", "DK");
        addressToVerify.put("zip", contactAddress.getZipCodeValue());
        addressToVerify.put("city", contactAddress.getCity());
        addressToVerify.put("street", contactAddress.getStreet());
        addressToVerify.put("hnr", contactAddress.getHouseNumber());
        Map<String, Object> geoCodeResponse = scheduler.getGeoCode(addressToVerify, flsCredentials);

        if (geoCodeResponse != null) {
            logger.debug("GeoCode Response from TOM TOM : " + geoCodeResponse);
            return geoCodeResponse;
        }
        logger.debug("Address not verified with TOMTOM");*/
        return null;
    }


    public ContactAddress saveAndUpdateClientAddress(Client client, ContactAddress contactAddress, String type) {

        switch (type) {
            case HAS_HOME_ADDRESS:
                client.setHomeAddress(contactAddress);
                clientGraphRepository.save(client);
                break;
            case HAS_PARTNER_ADDRESS:
                client.setPartnerAddress(contactAddress);
                break;
            case HAS_SECONDARY_ADDRESS:
                client.setSecondaryAddress(contactAddress);
                break;
            case HAS_TEMPORARY_ADDRESS:
                List<ClientTemporaryAddress> clientTemporaryAddressList = client.getTemporaryAddress();
                clientTemporaryAddressList.add((ClientTemporaryAddress)contactAddress);
                client.setTemporaryAddress(clientTemporaryAddressList);
                break;
            default:
                exceptionService.dataNotFoundByIdException(MESSAGE_CLIENT_ADDRESSTYPE_NOTFOUND);

        }
        clientGraphRepository.save(client);
        return contactAddress;
    }


}
