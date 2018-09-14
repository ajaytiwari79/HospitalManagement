package com.kairos.service.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.Currency;
import com.kairos.persistence.model.user.payment_type.PaymentType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CurrencyGraphRepository;
import com.kairos.persistence.repository.user.payment_type.PaymentTypeGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.client.AddressVerificationService;
import com.kairos.service.country.CurrencyService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.payment_type.PaymentTypeService;
import com.kairos.utils.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.AppConstants.TEAM;
import static com.kairos.constants.AppConstants.ORGANIZATION;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class OrganizationAddressService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private PaymentTypeService paymentTypeService;
    @Inject
    private CurrencyService currencyService;
    @Inject
    private TeamGraphRepository teamGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;
    @Inject
    private AddressVerificationService addressVerificationService;
    @Inject
    private CurrencyGraphRepository currencyGraphRepository;
    @Inject
    private PaymentTypeGraphRepository paymentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private ExceptionService exceptionService;
    public HashMap<String, Object> getAddress(long id, String type) {

        HashMap<String, Object> response = new HashMap<>(2);
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            Long countryId = countryGraphRepository.getCountryIdByUnitId(id);
            OrganizationContactAddress contactAddressData = organizationGraphRepository.getContactAddressOfOrg(id);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.convertValue(contactAddressData.getContactAddress(), Map.class);
            map.put("zipCodeId", contactAddressData.getZipCode().getId());
            map.put("municipalityId", (contactAddressData.getMunicipality() == null) ? null : contactAddressData.getMunicipality().getId());
            response.put("contactAddress", map);
            response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));
            response.put("municipalities", FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(contactAddressData.getZipCode().getId())));
            Map<String, Object> billingAddress = organizationGraphRepository.getBillingAddress(id);
            response.put("billingAddress", billingAddress);
            response.put("billingMunicipalities", (billingAddress.get("zipCodeId") == null) ? Collections.emptyList() :
                    FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData((long) billingAddress.get("zipCodeId"))));
            response.put("paymentTypes", paymentTypeService.getPaymentTypes(countryId));
            response.put("currencies", currencyService.getCurrencies(countryId));

        } else if (TEAM.equalsIgnoreCase(type)) {
            Long countryId = countryGraphRepository.getCountryOfTeam(id);
            if (countryId == null) {
                List<Country> countries = countryGraphRepository.findByName("Denmark");
                Country country;
                if (countries.isEmpty()) {
                    exceptionService.dataNotFoundByIdException("message.organizationAddress.teamAddress.notBelongs");

                }
                country = countries.get(0);
                countryId = country.getId();
            }
            Map<String, Object> contactAddress = teamGraphRepository.getContactAddressOfTeam(id);
            response.put("contactAddress", contactAddress);
            response.put("billingAddress", Collections.emptyMap());
            response.put("municipalities", (contactAddress.get("zipCodeId") == null) ? Collections.emptyList() :
                    municipalityGraphRepository.getMunicipalitiesByZipCode(((long) contactAddress.get("zipCodeId"))));
            response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));
            response.put("paymentTypes", Collections.emptyMap());
            response.put("currencies", Collections.emptyMap());
        }
        return response;
    }

    public ContactAddress updateContactAddressOfUnit(AddressDTO addressDTO, long id, String type) {
        Long unitId = organizationService.getOrganizationIdByTeamIdOrGroupIdOrOrganizationId(type, id);
        ContactAddress contactAddress;
        contactAddress = contactAddressGraphRepository.findOne(addressDTO.getId());
        if (contactAddress == null) {
            logger.info("Creating new Address");
            exceptionService.dataNotFoundByIdException("message.organizationAddress.contactAddress.notFound");

        }

        ZipCode zipCode;
        if (addressDTO.isVerifiedByGoogleMap()) {
            if (addressDTO.getZipCodeValue() == 0) {
                logger.debug("No ZipCode value received");
                return null;
            }
            zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
            contactAddress.setLatitude(addressDTO.getLatitude());
            contactAddress.setLongitude(addressDTO.getLongitude());
        } else {
            logger.info("Sending address to verify from TOM TOM server");
            // Send Address to verify
            Map<String, Object> tomtomResponse = addressVerificationService.verifyAddress(addressDTO, unitId);
            if (tomtomResponse != null) {
                // -------Parse Address from DTO -------- //

                contactAddress.setCountry("Denmark");
                // Coordinates
                contactAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
                contactAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));

                // Start And End Dates for Address
                logger.debug("StartDate: " + addressDTO.getStartDate() + " End date " + addressDTO.getEndDate());
                contactAddress.setStartDate(addressDTO.getStartDate());
                contactAddress.setEndDate(addressDTO.getEndDate());
                contactAddress.setVerifiedByVisitour(true);
                zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId());
            } else {
                return null;
            }
        }


        if (zipCode == null) {
            logger.debug("Incorrect zipcode id");
            return null;
        }

        Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId());
        if (municipality == null) {
            exceptionService.dataNotFoundByIdException("message.municipality.notFound");

        }
        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData == null) {
            logger.info("Geography  not found with zipcodeId: " + municipality.getId());
            exceptionService.dataNotFoundByIdException("message.geographyData.notFound",municipality.getId());

        }
        logger.info("Geography Data: " + geographyData);


        // Geography Data
        contactAddress.setMunicipality(municipality);
        contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
        contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
        contactAddress.setZipCode(zipCode);
        contactAddress.setCity(zipCode.getName());
        contactAddress.setVerifiedByVisitour(true);
        contactAddress.setStreetUrl(addressDTO.getStreetUrl());
        // Native Details
        contactAddress.setStreet(addressDTO.getStreet());
        contactAddress.setHouseNumber(addressDTO.getHouseNumber());
        contactAddress.setFloorNumber(addressDTO.getFloorNumber());
        contactAddress.setCity(zipCode.getName());

        if (ORGANIZATION.equalsIgnoreCase(type)) {

            Organization organization = organizationGraphRepository.findOne(id);
            if (organization == null) {
                exceptionService.dataNotFoundByIdException("message.organization.id.notFound",id);


            }
            organization.setContactAddress(contactAddress);
            organizationGraphRepository.save(organization);
        } else if (TEAM.equalsIgnoreCase(type)) {
            Team team = teamGraphRepository.findOne(id);
            if (team == null) {
                exceptionService.dataNotFoundByIdException("message.organizationAddress.team.notFound");
  }
            team.setContactAddress(contactAddress);
            teamGraphRepository.save(team);
        }
        return contactAddress;
    }

    public Map<String, Object> saveBillingAddress(AddressDTO addressDTO, long unitId, boolean isAddressAlreadyExist) {

        ContactAddress billingAddress=null;
        if (isAddressAlreadyExist && addressDTO.getId() == null) {
            exceptionService.dataNotFoundByIdException("message.organizationAddress.contactaAddress.notNull");
            //throw new DataNotFoundByIdException("Address not found to update");
        } else if (isAddressAlreadyExist && addressDTO.getId() != null) {
            billingAddress = contactAddressGraphRepository.findOne(addressDTO.getId());
            if (billingAddress == null)
                exceptionService.dataNotFoundByIdException("message.organizationAddress.contactAddress.notFound");

        } else {
            billingAddress = new ContactAddress();
        }

        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound",unitId);

        }

        ZipCode zipCode;

        if (addressDTO.isVerifiedByGoogleMap()) {
            zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
            billingAddress.setLongitude(addressDTO.getLongitude());
            billingAddress.setLatitude(addressDTO.getLatitude());
        } else {
            Map<String, Object> tomtomResponse = addressVerificationService.verifyAddress(addressDTO, unitId);
            if (tomtomResponse != null) {
                // Coordinates
                billingAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
                billingAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));

                // Start And End Dates for Address
                logger.debug("StartDate: " + addressDTO.getStartDate() + " End date " + addressDTO.getEndDate());

                billingAddress.setStartDate(addressDTO.getStartDate());
                billingAddress.setEndDate(addressDTO.getEndDate());
                billingAddress.setVerifiedByVisitour(true);
                zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId());
            } else {
                return null;
            }
        }

        if (zipCode == null) {
            logger.debug("Incorrect zipcode id");
            return null;
        }

        Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId());
        if (municipality == null) {
            exceptionService.dataNotFoundByIdException("message.municipality.notFound");

        }


        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData == null) {
            logger.info("Geography  not found with zipcodeId: " + municipality.getId());
            exceptionService.dataNotFoundByIdException("message.geographyData.notFound",municipality.getId());

        }
        logger.info("Geography Data: " + geographyData);


        // Geography Data
        billingAddress.setMunicipality(municipality);
        billingAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
        billingAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        billingAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
        billingAddress.setZipCode(zipCode);
        billingAddress.setCity(zipCode.getName());
        billingAddress.setVerifiedByVisitour(true);
        billingAddress.setStreetUrl(addressDTO.getStreetUrl());
        // Native Details
        billingAddress.setStreet(addressDTO.getStreet());
        billingAddress.setHouseNumber(addressDTO.getHouseNumber());
        billingAddress.setFloorNumber(addressDTO.getFloorNumber());
        billingAddress.setCity(zipCode.getName());
        billingAddress.setContactPersonForBillingAddress(addressDTO.getBillingPerson());
        long paymentTypeId = addressDTO.getPaymentTypeId();
        long currencyId = addressDTO.getCurrencyId();

        Currency currency = currencyGraphRepository.findOne(currencyId);
        PaymentType paymentType = paymentTypeGraphRepository.findOne(paymentTypeId);

        if (currency == null || paymentType == null) {
            exceptionService.dataNotFoundByIdException("message.organizationAddress.currencyorpaymentid.incorrect");

        }
        billingAddress.setPaymentType(paymentType);
        billingAddress.setCurrency(currency);
        organization.setBillingAddress(billingAddress);
        organizationGraphRepository.save(organization);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentTypeId", paymentType.getId());
        response.put("currencyId", currency.getId());
        response.put("billingAddress", billingAddress.getZipCode().getName());
        response.put("province", billingAddress.getProvince());
        response.put("region", billingAddress.getRegionName());
        response.put("municipalityId", billingAddress.getMunicipality().getId());
        response.put("city", billingAddress.getCity());
        response.put("street1", billingAddress.getStreet());
        response.put("houseNumber", billingAddress.getHouseNumber());
        response.put("floorNumber", billingAddress.getFloorNumber());
        response.put("streetUrl", billingAddress.getStreetUrl());
        response.put("billingPerson", billingAddress.getContactPersonForBillingAddress());
        response.put("zipCodeId", zipCode.getId());
        response.put("latitude", billingAddress.getLatitude());
        response.put("longitude", billingAddress.getLongitude());
        response.put("municipalities", FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(zipCode.getId())));
        return response;
    }

    /**
     * This method is used to add Organizations's unit address of given UnitId.
     *
     * @param unitId
     * @param addressMap
     * @return
     */
    public ContactAddress addUnitAddress(Long unitId, Map<String, Object> addressMap) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization != null) {
            ContactAddress address = organizationGraphRepository.getOrganizationAddressDetails(unitId);
            if (address == null) {
                logger.info("No address found");
                address = new ContactAddress();
            }
            logger.info("New address " + addressMap);
            address.setHouseNumber((String) (addressMap.get("houseNumber")));
            address.setFloorNumber(Integer.parseInt(String.valueOf(addressMap.get("floorNumber"))));
            address.setStreet((String) (addressMap.get("street1")));
            address.setCity((String) addressMap.get("city"));
            address.setLatitude(Float.parseFloat((String) addressMap.get("latitude")));
            address.setLongitude(Float.parseFloat((String) addressMap.get("longitude")));
            organization.setContactAddress(address);
            organizationGraphRepository.save(organization);
            return organization.getContactAddress();
        }
        return null;
    }




}
