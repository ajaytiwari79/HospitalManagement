package com.kairos.service.staff;

import com.kairos.enums.OrganizationLevel;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.utils.external_plateform_shift.GetEmploymentByIdResponse;
import com.kairos.utils.external_plateform_shift.GetEmploymentByIdResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class ApiExternalStaffService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private StaffAddressService staffAddressService;
    @Inject
    private StaffService staffService;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private ExceptionService exceptionService;

    public Staff createTimeCareStaff(Map<String, Object> data) {
        if (data != null) {
            String firstName = String.valueOf(data.get("firstName"));
            String lastName = String.valueOf(data.get("lastName"));
            String nickName = String.valueOf(data.get("familyName"));
            Long externalId = Long.valueOf(String.valueOf(data.get("externalId")));
            Long orgnaizationId = Long.valueOf(String.valueOf(data.get("organizationId")));
            String badge = String.valueOf(data.get("employmentNumber"));

            Staff staff = new Staff();
            staff.setFirstName(firstName);
            staff.setLastName(lastName);
            staff.setFamilyName(nickName);
            staff.setExternalId(externalId);
            staff.setOrganizationId(orgnaizationId);
            staff.setBadgeNumber(badge);
            staff = staffGraphRepository.save(staff);
            List<Long> staffList = new ArrayList<>(1);
            staffList.add(staff.getId());
            logger.info("Creating Staff using organizationId " + orgnaizationId);

            Organization unit =organizationGraphRepository.findOne(orgnaizationId,0);

            if(unit == null){
                exceptionService.dataNotFoundByIdException("message.unit.notfound",orgnaizationId);
                //throw new InternalError("unit can't be null");
            }

            Organization parent = null;
            if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
                parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

            } else if(!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
                parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
            }

            if(parent == null){
                employmentGraphRepository.createEmployments(unit.getId(),staffList,unit.getId());
            } else {
                employmentGraphRepository.createEmployments(parent.getId(),staffList,unit.getId());
            }

            AddressDTO address = new AddressDTO();
            address.setCity("Odense");
            address.setCountry("Denmark");
            address.setStreet("Thorsgade");
            address.setLatitude(Float.valueOf("10.376834"));
            address.setLongitude(Float.valueOf("55.3958"));
            address.setHouseNumber("8");
            address.setFloorNumber(2);
            address.setZipCodeId(102L);
            staffAddressService.saveAddress(staff.getId(), address, orgnaizationId);
            return staff;
        }
        return null;
    }

    public void updateExternalId(long staffId,long externalId){

        Staff staff = staffGraphRepository.findOne(staffId);
        if(staff == null){
            return;
        }
        staff.setExternalId(externalId);
        staffGraphRepository.save(staff);
    }

    public String getStaffFromTimeCare(GetEmploymentByIdResponse personsResponse){
        try {
            logger.info(" Staffs---> " + personsResponse.getGetEmploymentByIdResult().size());
            for (GetEmploymentByIdResult person : personsResponse.getGetEmploymentByIdResult()) {
                Staff staff = staffService.getByExternalId(person.getId());
                Organization organization = organizationService.getOrganizationByExternalId(person.getParentWorkPlaceId().toString());
                if(staff == null) {
                    Map<String, Object> engineerMetaData = new HashMap<>();
                    engineerMetaData.put("firstName", person.getFirstName());
                    engineerMetaData.put("lastName", person.getLastName());
                    engineerMetaData.put("familyName", person.getShortName());
                    engineerMetaData.put("employmentNumber", person.getEmploymentNumber());
                    engineerMetaData.put("externalId", person.getId());
                    engineerMetaData.put("organizationId", organization.getId());
                    createTimeCareStaff(engineerMetaData);
                }

            }
            return "Received";
        } catch (Exception exception) {
            logger.warn("Exception while hitting rest for saving Staff", exception);
        }
        return null;
    }
}
