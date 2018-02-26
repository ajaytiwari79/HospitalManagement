package com.kairos.service.staff;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.AddressDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.client.ContactAddress;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.client.AddressVerificationService;
import com.kairos.util.DistanceCalculator;
import com.kairos.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.kairos.constants.AppConstants.TEAM;
import static com.kairos.constants.AppConstants.ORGANIZATION;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class StaffAddressService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private TeamGraphRepository teamGraphRepository;
    @Inject
    private AddressVerificationService addressVerificationService;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    public ContactAddress saveAddress(long staffId, AddressDTO addressDTO, long unitId) {
        ContactAddress contactAddress = null;
        Staff staff = staffGraphRepository.findOne(staffId);

        if (staff == null) {
            throw new DataNotFoundByIdException("Can't find Staff with provided Id");

        }

        if (addressDTO.getId() != null) {
            contactAddress = contactAddressGraphRepository.findOne(addressDTO.getId());
        }
        if (contactAddress == null) {
            logger.info("Creating new Address");
            contactAddress = new ContactAddress();

        }
        contactAddress.setPrimary(addressDTO.isPrimary());
        // Verify Address here
        if (addressDTO.isVerifiedByGoogleMap()) {
            logger.info("Google Map verified address received ");

            // -------Parse Address from DTO -------- //

            //ZipCode
            if (addressDTO.getZipCodeValue() == 0) {
                logger.info("No ZipCode value received");
                return null;
            }
            ZipCode zipCode = zipCodeGraphRepository.findByZipCode(addressDTO.getZipCodeValue());
            if (zipCode == null) {
                logger.info("ZipCode Not Found returning null");
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
            contactAddress.setMunicipality(municipality);
            contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
            contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
            contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
            contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));

            // Start And End Dates for Address
            contactAddress.setStartDate(addressDTO.getStartDate());
            logger.info("StartDate: " + addressDTO.getStartDate());

            contactAddress.setEndDate(addressDTO.getEndDate());
            logger.info("EndDate: " + addressDTO.getEndDate());


            // Coordinates
            contactAddress.setLongitude(addressDTO.getLongitude());
            contactAddress.setLatitude(addressDTO.getLatitude());

            // Native Details
            contactAddress.setStreet1(addressDTO.getStreet1());
            contactAddress.setHouseNumber(addressDTO.getHouseNumber());
            contactAddress.setFloorNumber(addressDTO.getFloorNumber());
            contactAddress.setZipCode(zipCode);
            contactAddress.setCity(zipCode.getName());
            contactAddress.setPrivateAddress(addressDTO.isAddressProtected());

            if(addressDTO.isPrimary())
            {
                staff.setContactAddress(contactAddress);
            }else{
                staff.setSecondaryContactAddress(contactAddress);
            }

            staffGraphRepository.save(staff);
            // save directly
            return contactAddress;
        }
        else {
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
                contactAddress.setStartDate(addressDTO.getStartDate());
                logger.info("StartDate: " + addressDTO.getStartDate());

                contactAddress.setEndDate(addressDTO.getEndDate());
                logger.info("EndDate: " + addressDTO.getEndDate());


                ZipCode zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId());
                if (zipCode == null) {
                    logger.info("ZipCode Not Found returning null");
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
                contactAddress.setMunicipality(municipality);
                contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
                contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
                contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
                contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
                contactAddress.setZipCode(zipCode);
                contactAddress.setCity(zipCode.getName());
                contactAddress.setVerifiedByVisitour(true);


                // Native Details
                contactAddress.setStreet1(addressDTO.getStreet1());
                contactAddress.setHouseNumber(addressDTO.getHouseNumber());
                contactAddress.setFloorNumber(addressDTO.getFloorNumber());
                contactAddress.setCity(zipCode.getName());
                contactAddress.setPrivateAddress(addressDTO.isAddressProtected());

                if(addressDTO.isPrimary())
                {
                    staff.setContactAddress(contactAddress);
                }else{
                    staff.setSecondaryContactAddress(contactAddress);
                }
                staffGraphRepository.save(staff);
                // save directly
                return contactAddress;

            }
            return null;
        }
    }

    public Map<String, Object> getAddress(long unitId, long staffId, String type) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);


        Staff staff = staffGraphRepository.findOne(staffId, 2);
        if (staff == null) {
            return null;
        }
        ContactAddress address;
        ContactAddress staffAddress = staff.getContactAddress();

        if (ORGANIZATION.equalsIgnoreCase(type)) {
            Organization organization = organizationGraphRepository.findOne(unitId);
            if (organization == null) {
                return null;
            }
            address = organization.getContactAddress();
        } else if (TEAM.equalsIgnoreCase(type)) {
            Team team = teamGraphRepository.findOne(unitId);
            countryId = teamGraphRepository.getCountryByTeamId(unitId);
            address = team.getContactAddress();
        } else {
            throw new InternalError("type can not be null");
        }

        double distance = 0;
        if (address != null && staffAddress != null) {

            distance = DistanceCalculator.distance(address.getLatitude(), address.getLongitude(), staffAddress.getLatitude(), staffAddress.getLongitude(), "K");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("primaryStaffAddress", staff.fetchPrimaryContactAddressDetail());

        response.put("secondaryStaffAddress", (staff.getSecondaryContactAddress()==null)?initializeSecondaryAddress():staff.fetchSecondaryContactAddressDetail());
        response.put("distanceFromWork", distance);

        if (countryId != null) {
            ZipCode zipCode = (staffAddress == null) ? null : staffAddress.getZipCode();
            response.put("municipalities", (zipCode == null) ? null : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(zipCode.getId())));
            response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));
        }
        return response;
    }
    public Map<String,Object> initializeSecondaryAddress(){
        Map map = new HashMap<>();
        map.put("houseNumber", null);
        map.put("floorNumber", null);
        map.put("street1", null);
        map.put("zipCodeId", null);
        map.put("city", null);
        map.put("municipalityId", null);
        map.put("regionName",null);
        map.put("country", null);
        map.put("latitude", null);
        map.put("longitude", null);
        map.put("province", null);
        map.put("streetUrl", null);
        map.put("addressProtected",null);
        map.put("verifiedByVisitour", null);
        return map;
    }
    public ContactAddress getStaffContactAddressByOrganizationAddress(Organization organization) {
        ContactAddress organizationAddress = contactAddressGraphRepository.findOne(organization.getContactAddress().getId());
        if(Optional.ofNullable(organizationAddress).isPresent()){
            ContactAddress contactAddress = new ContactAddress();
            contactAddress.setCity(organizationAddress.getCity());
            contactAddress.setStreet1(organizationAddress.getStreet1());
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
