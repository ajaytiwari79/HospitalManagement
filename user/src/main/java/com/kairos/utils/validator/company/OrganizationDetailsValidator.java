package com.kairos.utils.validator.company;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.service.exception.ExceptionService;

import java.util.List;
import java.util.Optional;

import static com.kairos.constants.UserMessagesConstants.*;

/**
 * CreatedBy vipulpandey on 22/8/18
 **/
public class OrganizationDetailsValidator {
    public static void validateBasicDetails(List<OrganizationBaseEntity> units, ExceptionService exceptionService) {
        units.forEach(organization -> {
            if (!Optional.ofNullable(organization.getDesiredUrl()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_ORGANIZATION_DESIREDURL_NOTNULL, organization.getName());
            }
            if (!Optional.ofNullable(organization.getCompanyCategory()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_ORGANIZATION_COMPANYCATEGORY_NOTNULL, organization.getName());
            }
            validateTypeDetails(exceptionService, organization);
            if (organization instanceof Organization && !Optional.ofNullable(organization.getAccountType()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_ORGANIZATION_ACCOUNTTYPE_NOTNULL, organization.getName());
            }
            if (!Optional.ofNullable(organization.getKairosCompanyId()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_ORGANIZATION_KAIROSID_NOTNULL, organization.getName());
            }
        });
    }

    private static void validateTypeDetails(ExceptionService exceptionService, OrganizationBaseEntity unit) {
        if (!Optional.ofNullable(unit.getOrganizationType()).isPresent()) {
            exceptionService.invalidRequestException(ERROR_ORGANIZATION_ORGTYPE_NOTNULL, unit.getName());
        }
        if (!Optional.ofNullable(unit.getOrganizationSubTypes()).isPresent() || unit.getOrganizationSubTypes().isEmpty()) {
            exceptionService.invalidRequestException(ERROR_ORGANIZATION_ORGSUBTYPE_NOTNULL, unit.getName());
        }
        if (!Optional.ofNullable(unit.getBusinessTypes()).isPresent()) {
            exceptionService.invalidRequestException(ERROR_ORGANIZATION_BUSINESSTYPE_NOTNULL, unit.getName());
        }
    }

    public static void validateUserDetails(List<StaffPersonalDetailDTO> staffPersonalDetailDTOS, ExceptionService exceptionService) {
        if(ObjectUtils.isCollectionNotEmpty(staffPersonalDetailDTOS)) {
            staffPersonalDetailDTOS.forEach(staffPersonalDetailDTO -> {
                if (!Optional.ofNullable(staffPersonalDetailDTO.getCprNumber()).isPresent() || staffPersonalDetailDTO.getCprNumber().length() != 10) {
                    exceptionService.invalidRequestException(ERROR_CPRNUMBER_NOTNULL, staffPersonalDetailDTO.getOrganizationId());
                }
                if (!Optional.ofNullable(staffPersonalDetailDTO.getFirstName()).isPresent()) {
                    exceptionService.invalidRequestException(ERROR_FIRSTNAME_NOTNULL, staffPersonalDetailDTO.getOrganizationId());
                }
                if (!Optional.ofNullable(staffPersonalDetailDTO.getLastName()).isPresent()) {
                    exceptionService.invalidRequestException(ERROR_LASTNAME_NOTNULL, staffPersonalDetailDTO.getOrganizationId());
                }
                if (!Optional.ofNullable(staffPersonalDetailDTO.getEmail()).isPresent()) {
                    exceptionService.invalidRequestException(ERROR_EMAIL_NOTNULL, staffPersonalDetailDTO.getOrganizationId());
                }
                if (!Optional.ofNullable(staffPersonalDetailDTO.getAccessGroupId()).isPresent()) {
                    exceptionService.invalidRequestException(ERROR_ORGANIZATION_UNITMANAGER_ACCESSGROUP_NOTNULL, staffPersonalDetailDTO.getOrganizationId());
                }
            });
        }else{
            exceptionService.invalidRequestException(ERROR_USER_DETAILS_MISSING);
        }
    }

    public static void validateAddressDetails(List<OrganizationContactAddress> organizationContactAddresses, ExceptionService exceptionService) {
        organizationContactAddresses.forEach(address -> {
            if (!Optional.ofNullable(address.getContactAddress()).isPresent() || !Optional.ofNullable(address.getContactAddress().getHouseNumber()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_CONTACTADDRESS_HOUSENUMBER_NOTNULL, address.getUnit().getName());
            }
            if (!Optional.ofNullable(address.getContactAddress().getStreet()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_CONTACTADDRESS_STREET_NOTNULL, address.getUnit().getName());
            }
            if (!Optional.ofNullable(address.getZipCode()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_CONTACTADDRESS_ZIPCODE_NOTNULL, address.getUnit().getName());
            }
            if (!Optional.ofNullable(address.getMunicipality()).isPresent()) {
                exceptionService.invalidRequestException(ERROR_CONTACTADDRESS_MUNICIPALITY_NOTNULL, address.getUnit().getName());
            }
        });
    }
}
