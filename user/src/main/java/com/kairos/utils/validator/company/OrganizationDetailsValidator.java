package com.kairos.utils.validator.company;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.organization.CompanyType;

import java.util.List;
import java.util.Optional;

/**
 * CreatedBy vipulpandey on 22/8/18
 **/
public class OrganizationDetailsValidator {
    public static void validateBasicDetails(List<Organization> organizations, ExceptionService exceptionService) {
        organizations.forEach(organization -> {
            if (!Optional.ofNullable(organization.getDesiredUrl()).isPresent()) {
                exceptionService.invalidRequestException("error.Organization.desiredUrl.notNull", organization.getName());
            }
            if (!Optional.ofNullable(organization.getCompanyCategory()).isPresent()) {
                exceptionService.invalidRequestException("error.organization.companyCategory.notNull", organization.getName());
            }
            if (!Optional.ofNullable(organization.getOrganizationType()).isPresent()) {
                exceptionService.invalidRequestException("error.Organization.orgType.notNull", organization.getName());
            }
            if (!Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent() || organization.getOrganizationSubTypes().isEmpty()) {
                exceptionService.invalidRequestException("error.Organization.orgSubType.notNull", organization.getName());
            }
            if (!Optional.ofNullable(organization.getBusinessTypes()).isPresent()) {
                exceptionService.invalidRequestException("error.Organization.businesstype.notnull", organization.getName());
            }
            if (CompanyType.COMPANY.equals(organization.getCompanyType()) && !Optional.ofNullable(organization.getAccountType()).isPresent()) {
                exceptionService.invalidRequestException("error.Organization.accountType.notNull", organization.getName());
            }
            if (!Optional.ofNullable(organization.getKairosCompanyId()).isPresent()) {
                exceptionService.invalidRequestException("error.Organization.kairosId.notnull", organization.getName());
            }
//            if (!Optional.ofNullable(organization.getVatId()).isPresent()) {
//                exceptionService.invalidRequestException("error.Organization.vattype.notnull", organization.getName());
//            }
        });
    }

    public static void validateUserDetails(List<StaffPersonalDetailDTO> staffPersonalDetailDTOS, ExceptionService exceptionService) {
        staffPersonalDetailDTOS.forEach(staffPersonalDetailDTO -> {
            if (!Optional.ofNullable(staffPersonalDetailDTO.getCprNumber()).isPresent()|| staffPersonalDetailDTO.getCprNumber().length()!=10) {
                exceptionService.invalidRequestException("error.cprnumber.notnull", staffPersonalDetailDTO.getOrganizationId());
            }
            if (!Optional.ofNullable(staffPersonalDetailDTO.getFirstName()).isPresent()) {
                exceptionService.invalidRequestException("error.firstname.notnull", staffPersonalDetailDTO.getOrganizationId());
            }
            if (!Optional.ofNullable(staffPersonalDetailDTO.getLastName()).isPresent()) {
                exceptionService.invalidRequestException("error.lastname.notnull", staffPersonalDetailDTO.getOrganizationId());
            }
            if (!Optional.ofNullable(staffPersonalDetailDTO.getEmail()).isPresent()) {
                exceptionService.invalidRequestException("error.email.notnull", staffPersonalDetailDTO.getOrganizationId());
            }
//            if (!Optional.ofNullable(staffPersonalDetailDTO.getAccessGroupId()).isPresent()) {
//                exceptionService.invalidRequestException("error.Organization.unitmanager.accessgroup.notnull", staffPersonalDetailDTO.getOrganizationId());
//            }
        });
    }

    public static void validateAddressDetails(List<OrganizationContactAddress> organizationContactAddresses, ExceptionService exceptionService) {
        organizationContactAddresses.forEach(address -> {
            if (!Optional.ofNullable(address.getContactAddress()).isPresent() || !Optional.ofNullable(address.getContactAddress().getHouseNumber()).isPresent()) {
                exceptionService.invalidRequestException("error.ContactAddress.HouseNumber.notnull", address.getOrganization().getName());
            }
            if (!Optional.ofNullable(address.getContactAddress().getStreet()).isPresent()) {
                exceptionService.invalidRequestException("error.ContactAddress.street.notnull", address.getOrganization().getName());
            }
            if (!Optional.ofNullable(address.getZipCode()).isPresent()) {
                exceptionService.invalidRequestException("error.ContactAddress.zipcode.notnull", address.getOrganization().getName());
            }
            if (!Optional.ofNullable(address.getMunicipality()).isPresent()) {
                exceptionService.invalidRequestException("error.ContactAddress.municipality.notnull", address.getOrganization().getName());
            }
        });
    }
}
