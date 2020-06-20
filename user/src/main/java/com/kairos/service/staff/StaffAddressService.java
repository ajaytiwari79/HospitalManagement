package com.kairos.service.staff;

import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
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
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private ExceptionService exceptionService;

     void saveAddress(Staff staff, List<AddressDTO> addressDTOs) {
         List<AddressDTO> addressDTOS = addressDTOs.stream().filter(addressDTO -> isNotNull(addressDTO)).collect(Collectors.toList());
         List<ContactAddress>  contactAddresses = contactAddressGraphRepository.findAllById(addressDTOS.stream().map(AddressDTO::getId).collect(Collectors.toList()));
         Map<Long, ContactAddress> contactAddressMap = contactAddresses.stream().collect(Collectors.toMap(ContactAddress::getId, Function.identity()));
        List<ZipCode> zipCodes = zipCodeGraphRepository.findAllByZipCode(addressDTOS.stream().map(addressDTO -> addressDTO.getZipCode().getZipCode()).collect(Collectors.toList()));
        Map<Integer, ZipCode> zipCodeMap = zipCodes.stream().collect(Collectors.toMap(ZipCode::getZipCode, Function.identity()));
        List<Municipality> municipalities = municipalityGraphRepository.findAllById(addressDTOS.stream().map(addressDTO->addressDTO.getMunicipality().getId()).collect(Collectors.toList()));
        Map<Long, Municipality> municipalityMap = municipalities.stream().collect(Collectors.toMap(Municipality::getId, Function.identity()));
        for (AddressDTO addressDTO : addressDTOS) {
            ContactAddress contactAddress = contactAddressMap.getOrDefault(addressDTO.getId(), new ContactAddress());
            contactAddress.setPrimary(addressDTO.isPrimary());
            // Verify Address here
                LOGGER.info("Google Map verified address received ");

                //ZipCode
                if (addressDTO.getZipCode().getZipCode() == 0) {
                    LOGGER.info("No ZipCode value received");
                    continue;
                }
                ZipCode zipCode = zipCodeMap.get(addressDTO.getZipCode().getZipCode());
                if (zipCode == null) {
                    LOGGER.info("ZipCode Not Found returning null");
                    continue;
                }
                Municipality municipality = municipalityMap.get(addressDTO.getMunicipality().getId());
                Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
                validateDetails(municipality, geographyData);
                setContactAddressInfo(addressDTO, contactAddress, zipCode, municipality, geographyData,staff);
            }
    }

    private void validateDetails(Municipality municipality, Map<String, Object> geographyData) {
        if (municipality == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_MUNICIPALITY_NOTFOUND);
        }

        if (geographyData == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_GEOGRAPHYDATA_NOTFOUND, municipality.getId());

        }
    }

    private void setContactAddressInfo(AddressDTO addressDTO, ContactAddress contactAddress, ZipCode zipCode, Municipality municipality, Map<String, Object> geographyData,Staff staff) {
        // Geography Data
        contactAddress.setMunicipality(municipality);
        contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
        contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
        contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
        // Start And End Dates for Address
        contactAddress.setStartDate(addressDTO.getStartDate());
        contactAddress.setEndDate(addressDTO.getEndDate());
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


    public Map<String, Object> getAddress(long staffId) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        Staff staff = staffGraphRepository.findOne(staffId, 2);
        if (staff == null) {
            return null;
        }
        List<ContactAddress> staffAddress = newArrayList(staff.getContactAddress(),staff.getSecondaryContactAddress());
        Map<String, Object> response = new HashMap<>();
        if (countryId != null) {
            Set<Long> zipCodeIds = (staffAddress == null) ? null : staffAddress.stream().map(s->s.getZipCode().getId()).collect(Collectors.toSet());
            response.put("municipalities", (zipCodeIds == null) ? null : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeDataByZipCodeIds(zipCodeIds)));
            response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));
        }
        return response;
    }

   /* private ContactAddressDTO getContactAddress(ContactAddress contactAddress) {
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
    }*/

    public ContactAddress getStaffContactAddressByOrganizationAddress(Organization organization) {
        if (isNull(organization.getContactAddress())) {
            return null;
        }
        ContactAddress organizationAddress = contactAddressGraphRepository.findOne(organization.getContactAddress().getId());
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
