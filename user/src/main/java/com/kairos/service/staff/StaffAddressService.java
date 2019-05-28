package com.kairos.service.staff;

import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.staff.client.ContactAddressDTO;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.DistanceCalculator;
import com.kairos.utils.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_GEOGRAPHYDATA_NOTFOUND;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_MUNICIPALITY_NOTFOUND;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class StaffAddressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffAddressService.class);

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private ExceptionService exceptionService;

     void saveAddress(Staff staff, List<AddressDTO> addressDTOs) {
        List<ContactAddress> contactAddresses = contactAddressGraphRepository.findAllById(addressDTOs.stream().map(AddressDTO::getId).collect(Collectors.toList()));
        Map<Long, ContactAddress> contactAddressMap = contactAddresses.stream().collect(Collectors.toMap(ContactAddress::getId, Function.identity()));
        List<ZipCode> zipCodes = zipCodeGraphRepository.findAllByZipCode(addressDTOs.stream().map(AddressDTO::getZipCodeValue).collect(Collectors.toList()));
        Map<Integer, ZipCode> zipCodeMap = zipCodes.stream().collect(Collectors.toMap(ZipCode::getZipCode, Function.identity()));
        List<Municipality> municipalities = municipalityGraphRepository.findAllById(addressDTOs.stream().map(AddressDTO::getMunicipalityId).collect(Collectors.toList()));
        Map<Long, Municipality> municipalityMap = municipalities.stream().collect(Collectors.toMap(Municipality::getId, Function.identity()));
        for (AddressDTO addressDTO : addressDTOs) {
            ContactAddress contactAddress = contactAddressMap.getOrDefault(addressDTO.getId(), new ContactAddress());
            contactAddress.setPrimary(addressDTO.isPrimary());
            // Verify Address here
                LOGGER.info("Google Map verified address received ");

                //ZipCode
                if (addressDTO.getZipCodeValue() == 0) {
                    LOGGER.info("No ZipCode value received");
                    continue;
                }
                ZipCode zipCode = zipCodeMap.get(addressDTO.getZipCodeValue());
                if (zipCode == null) {
                    LOGGER.info("ZipCode Not Found returning null");
                    continue;
                }
                Municipality municipality = municipalityMap.get(addressDTO.getMunicipalityId());
                if (municipality == null) {
                    exceptionService.dataNotFoundByIdException(MESSAGE_MUNICIPALITY_NOTFOUND);

                }


                Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
                if (geographyData == null) {
                    LOGGER.info("Geography  not found with zipcodeId: " + zipCode.getId());
                    exceptionService.dataNotFoundByIdException(MESSAGE_GEOGRAPHYDATA_NOTFOUND, municipality.getId());

                }
                LOGGER.info("Geography Data: " + geographyData);


                // Geography Data
                contactAddress.setMunicipality(municipality);
                contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
                contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
                contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
                contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));

                // Start And End Dates for Address
                contactAddress.setStartDate(addressDTO.getStartDate());
                LOGGER.info("StartDate: " + addressDTO.getStartDate());

                contactAddress.setEndDate(addressDTO.getEndDate());
                LOGGER.info("EndDate: " + addressDTO.getEndDate());


                // Coordinates
                contactAddress.setLongitude(addressDTO.getLongitude());
                contactAddress.setLatitude(addressDTO.getLatitude());

                // Native Details
                contactAddress.setStreet(addressDTO.getStreet());
                contactAddress.setHouseNumber(addressDTO.getHouseNumber());
                contactAddress.setFloorNumber(addressDTO.getFloorNumber());
                contactAddress.setZipCode(zipCode);
                contactAddress.setCity(zipCode.getName());
                contactAddress.setPrivateAddress(addressDTO.isAddressProtected());

                if (addressDTO.isPrimary()) {
                    staff.setContactAddress(contactAddress);
                } else {
                    staff.setSecondaryContactAddress(contactAddress);
                }
            }
    }


    public Map<String, Object> getAddress(long unitId, long staffId) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        Staff staff = staffGraphRepository.findOne(staffId, 2);
        if (staff == null) {
            return null;
        }

        ContactAddress staffAddress = staff.getContactAddress();

        Unit unit = unitGraphRepository.findOne(unitId);
        if (unit == null) {
            return null;
        }
        ContactAddress address = unit.getContactAddress();
        double distance = 0;
        if (address != null && staffAddress != null) {
            distance = DistanceCalculator.distance(address.getLatitude(), address.getLongitude(), staffAddress.getLatitude(), staffAddress.getLongitude(), "K");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("primaryAddress", getContactAddress(staff.getContactAddress()));
        response.put("secondaryAddress", getContactAddress(staff.getSecondaryContactAddress()));
        response.put("distanceFromWork", distance);

        if (countryId != null) {
            ZipCode zipCode = (staffAddress == null) ? null : staffAddress.getZipCode();
            response.put("municipalities", (zipCode == null) ? null : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(zipCode.getId())));
            response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));
        }
        return response;
    }

    private ContactAddressDTO getContactAddress(ContactAddress contactAddress) {
        ContactAddressDTO contactAddressDTO;
        if (Optional.ofNullable(contactAddress).isPresent()) {
            contactAddressDTO = new ContactAddressDTO(contactAddress.getHouseNumber(), contactAddress.getFloorNumber(), contactAddress.getStreet(), contactAddress.getCity(),
                    contactAddress.getRegionName(), contactAddress.getCountry(), contactAddress.getLatitude(), contactAddress.getLongitude(), contactAddress.getProvince(), contactAddress.getCountry(),
                    contactAddress.isAddressProtected(), contactAddress.isVerifiedByVisitour());
            contactAddressDTO.setZipCodeId(contactAddress.getZipCode() != null ? contactAddress.getZipCode().getId() : null);
            contactAddressDTO.setMunicipalityId((contactAddress.getMunicipality() == null) ? null : contactAddress.getMunicipality().getId());
        } else {
            contactAddressDTO = new ContactAddressDTO();
        }
        return contactAddressDTO;
    }

     ContactAddress getStaffContactAddressByOrganizationAddress(Unit unit) {
        ContactAddress organizationAddress = contactAddressGraphRepository.findOne(unit.getContactAddress().getId());
        if (Optional.ofNullable(organizationAddress).isPresent()) {
            ContactAddress contactAddress = new ContactAddress();
            contactAddress.setCity(organizationAddress.getCity());
            contactAddress.setStreet(organizationAddress.getStreet());
            contactAddress.setZipCode(organizationAddress.getZipCode());
            contactAddress.setHouseNumber(organizationAddress.getHouseNumber());
            contactAddress.setLongitude(organizationAddress.getLongitude());
            contactAddress.setLatitude(organizationAddress.getLatitude());
            contactAddress.setFloorNumber(organizationAddress.getFloorNumber());
            contactAddress.setMunicipality(organizationAddress.getMunicipality());
            contactAddress.setCountry(organizationAddress.getCountry());
            contactAddress.setRegionName(organizationAddress.getRegionName());
            contactAddress.setRegionCode(organizationAddress.getRegionCode());
            contactAddress.setProvince(organizationAddress.getProvince());
            contactAddress.setPrimary(true);
            return contactAddress;
        }
        return null;
    }

}
