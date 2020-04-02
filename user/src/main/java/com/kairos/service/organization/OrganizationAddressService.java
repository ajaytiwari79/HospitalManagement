package com.kairos.service.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.default_data.Currency;
import com.kairos.persistence.model.country.default_data.PaymentType;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CurrencyGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
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
import java.util.*;

import static com.kairos.constants.UserMessagesConstants.*;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class OrganizationAddressService {

    public static final String ZIP_CODE_ID = "zipCodeId";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UnitGraphRepository unitGraphRepository;
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
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;

    public Map<String, Object> getAddress(long id) {
        Map<String, Object> response = new HashMap<>(2);
        Long countryId = UserContext.getUserDetails().getCountryId();
        OrganizationContactAddress contactAddressData = unitGraphRepository.getContactAddressOfOrg(id);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(contactAddressData.getContactAddress(), Map.class);
        map.put(ZIP_CODE_ID, contactAddressData.getZipCode().getId());
        map.put("municipalityId", (contactAddressData.getMunicipality() == null) ? null : contactAddressData.getMunicipality().getId());
        response.put("contactAddress", map);
        response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));
        response.put("municipalities", FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(contactAddressData.getZipCode().getId())));
        Map<String, Object> billingAddress = unitGraphRepository.getBillingAddress(id);
        response.put("billingAddress", billingAddress);
        response.put("billingMunicipalities", (billingAddress.get(ZIP_CODE_ID) == null) ? Collections.emptyList() :
                FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData((long) billingAddress.get(ZIP_CODE_ID))));
        response.put("paymentTypes", paymentTypeService.getPaymentTypes(countryId));
        response.put("currencies", currencyService.getCurrencies(countryId));


        return response;
    }

    public ContactAddress updateContactAddressOfUnit(AddressDTO addressDTO, long id) {
        ContactAddress contactAddress= contactAddressGraphRepository.findById(addressDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATIONADDRESS_CONTACTADDRESS_NOTFOUND)));
        ZipCode zipCode;
        if (addressDTO.isVerifiedByGoogleMap()) {
            if (addressDTO.getZipCode().getZipCode() == 0) {
                logger.debug("No ZipCode value received");
                return null;
            }
            zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCode().getZipCode());
            contactAddress.setLatitude(addressDTO.getLatitude());
            contactAddress.setLongitude(addressDTO.getLongitude());
        } else {
            logger.info("Sending address to verify from TOM TOM server");
            // Send Address to verify
            Map<String, Object> tomtomResponse = addressVerificationService.verifyAddress(addressDTO, id);
            if (tomtomResponse != null) {
                // -------Parse Address from DTO -------- //

                contactAddress.setCountry("Denmark");
                // Coordinates
                contactAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
                contactAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));

                // Start And End Dates for Address
                contactAddress.setStartDate(addressDTO.getStartDate());
                contactAddress.setEndDate(addressDTO.getEndDate());
                contactAddress.setVerifiedByVisitour(true);
                zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCode().getId());
            } else {
                return null;
            }
        }


        if (zipCode == null) {
            logger.debug("Incorrect zipcode id");
            return null;
        }

        Municipality municipality = municipalityGraphRepository.findById(addressDTO.getMunicipality().getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_MUNICIPALITY_NOTFOUND)));
        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData != null) {
            contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
            contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
            contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
        }
        logger.info("Geography Data: {}" , geographyData);


        // Geography Data
        contactAddress.setMunicipality(municipality);

        contactAddress.setZipCode(zipCode);
        contactAddress.setCity(zipCode.getName());
        contactAddress.setVerifiedByVisitour(true);
        contactAddress.setStreetUrl(addressDTO.getStreetUrl());
        // Native Details
        contactAddress.setStreet(addressDTO.getStreet());
        contactAddress.setHouseNumber(addressDTO.getHouseNumber());
        contactAddress.setFloorNumber(addressDTO.getFloorNumber());
        contactAddress.setCity(zipCode.getName());


        Unit unit = unitGraphRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, id)));
        unit.setContactAddress(contactAddress);
        unitGraphRepository.save(unit);
        return contactAddress;
    }

    public Map<String, Object> saveBillingAddress(AddressDTO addressDTO, long unitId, boolean isAddressAlreadyExist) {

        ContactAddress billingAddress = getBillingAddress(addressDTO, isAddressAlreadyExist);
        Unit unit = unitGraphRepository.findById(unitId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId)));
        ZipCode zipCode;

        if (addressDTO.isVerifiedByGoogleMap()) {
            zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCode().getZipCode());
            billingAddress.setLongitude(addressDTO.getLongitude());
            billingAddress.setLatitude(addressDTO.getLatitude());
        } else {
            Map<String, Object> tomtomResponse = addressVerificationService.verifyAddress(addressDTO, unitId);
            if (tomtomResponse != null) {
                // Coordinates
                billingAddress.setLongitude(Float.valueOf(String.valueOf(tomtomResponse.get("yCoordinates"))));
                billingAddress.setLatitude(Float.valueOf(String.valueOf(tomtomResponse.get("xCoordinates"))));

                // Start And End Dates for Address
                billingAddress.setStartDate(addressDTO.getStartDate());
                billingAddress.setEndDate(addressDTO.getEndDate());
                billingAddress.setVerifiedByVisitour(true);
                zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCode().getId());
            } else {
                return null;
            }
        }

        if (zipCode == null) {
            logger.debug("Incorrect zipcode id");
            return null;
        }

        Municipality municipality = municipalityGraphRepository.findById(addressDTO.getMunicipality().getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_MUNICIPALITY_NOTFOUND)));
        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData != null) {
            billingAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
            billingAddress.setCountry(String.valueOf(geographyData.get("countryName")));
            billingAddress.setRegionName(String.valueOf(geographyData.get("regionName")));

        }
        logger.info("Geography Data: {}" , geographyData);


        // Geography Data
        billingAddress.setMunicipality(municipality);

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

        Currency currency = currencyGraphRepository.findById(currencyId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATIONADDRESS_CURRENCYORPAYMENTID_INCORRECT)));
        PaymentType paymentType = paymentTypeGraphRepository.findById(paymentTypeId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATIONADDRESS_CURRENCYORPAYMENTID_INCORRECT)));
        billingAddress.setPaymentType(paymentType);
        billingAddress.setCurrency(currency);
        unit.setBillingAddress(billingAddress);
        unitGraphRepository.save(unit);

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
        response.put(ZIP_CODE_ID, zipCode.getId());
        response.put("latitude", billingAddress.getLatitude());
        response.put("longitude", billingAddress.getLongitude());
        response.put("municipalities", FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(zipCode.getId())));
        return response;
    }

    private ContactAddress getBillingAddress(AddressDTO addressDTO, boolean isAddressAlreadyExist) {
        ContactAddress billingAddress=null;
        if (isAddressAlreadyExist && addressDTO.getId() == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONADDRESS_CONTACTAADDRESS_NOTNULL);
        } else if (isAddressAlreadyExist && addressDTO.getId() != null) {
            billingAddress = contactAddressGraphRepository.findOne(addressDTO.getId());
            if (billingAddress == null)
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONADDRESS_CONTACTADDRESS_NOTFOUND);

        }
        return billingAddress==null?new ContactAddress():billingAddress;
    }

    /**
     * This method is used to add Organizations's unit address of given UnitId.
     *
     * @param unitId
     * @param addressMap
     * @return
     */
    public ContactAddress addUnitAddress(Long unitId, Map<String, Object> addressMap) {
        Unit unit = unitGraphRepository.findOne(unitId);
        if (unit != null) {
            ContactAddress address = unitGraphRepository.getOrganizationAddressDetails(unitId);
            if (address == null) {
                logger.info("No address found");
                address = new ContactAddress();
            }
            logger.info("New address {}" , addressMap);
            address.setHouseNumber((String) (addressMap.get("houseNumber")));
            address.setFloorNumber(Integer.parseInt(String.valueOf(addressMap.get("floorNumber"))));
            address.setStreet((String) (addressMap.get("street1")));
            address.setCity((String) addressMap.get("city"));
            address.setLatitude(Float.parseFloat((String) addressMap.get("latitude")));
            address.setLongitude(Float.parseFloat((String) addressMap.get("longitude")));
            unit.setContactAddress(address);
            unitGraphRepository.save(unit);
            return unit.getContactAddress();
        }
        return null;
    }

    public ReasonCodeWrapper getAddressAndReasonCodeOfOrganization(Set<Long> absenceReasonCodeIds, Long unitId) {
        Map<String, Object> contactAddressData = unitGraphRepository.getContactAddressOfParentOrganization(unitId);
        List<ReasonCode> reasonCodes = reasonCodeGraphRepository.findByIds(absenceReasonCodeIds);
        List<ReasonCodeDTO> reasonCodeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(reasonCodes, ReasonCodeDTO.class);
        return new ReasonCodeWrapper(reasonCodeDTOS, contactAddressData);
    }


}
