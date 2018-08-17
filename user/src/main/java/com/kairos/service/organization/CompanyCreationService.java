package com.kairos.service.organization;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBuilder;
import com.kairos.persistence.model.organization.company.CompanyValidationQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.organization.CompanyType;
import com.kairos.user.organization.OrganizationBasicDTO;
import com.kairos.user.organization.OrganizationResponseWrapper;
import com.kairos.util.user_context.UserContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;

/**
 * CreatedBy vipulpandey on 17/8/18
 **/
@Service
@Transactional
public class CompanyCreationService {

    @Inject private CountryGraphRepository countryGraphRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private OrganizationGraphRepository organizationGraphRepository;
    @Inject private AccountTypeGraphRepository accountTypeGraphRepository;

    public OrganizationBasicDTO createParentOrganization(OrganizationBasicDTO orgDetails, long countryId, Long organizationId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }

        CompanyValidationQueryResult orgExistWithUrl = organizationGraphRepository.checkOrgExistWithUrlOrUrl("(?i)"+orgDetails.getDesiredUrl(),"(?i)"+orgDetails.getName(),orgDetails.getName().substring(0, 3) );
        if (orgExistWithUrl.getDesiredUrl()) {
            exceptionService.dataNotFoundByIdException("error.Organization.desiredUrl.duplicate", orgDetails.getDesiredUrl());
        }
        if (orgExistWithUrl.getName()) {
            exceptionService.dataNotFoundByIdException("error.Organization.name.duplicate", orgDetails.getName());
        }


        AccountType accountType = null;
        if (CompanyType.COMPANY.equals(orgDetails.getCompanyType())) {
            accountType = accountTypeGraphRepository.findOne(orgDetails.getAccountTypeId(),0);
            if (!Optional.ofNullable(accountType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.accountType.notFound");
            }
        }
        Map<Long, Long> countryAndOrgAccessGroupIdsMap = new HashMap<>();

        String kairosId;
        if (orgExistWithUrl.getKairosId() == null) {
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + ONE;
        } else {
            int lastSuffix = new Integer(orgExistWithUrl.getKairosId().substring(4, orgExistWithUrl.getKairosId().length()));
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + (++lastSuffix);
        }

        Organization organization = new OrganizationBuilder()
                .setIsParentOrganization(true)
                .setCountry(country)
                .setAccountType(accountType)
                .setCompanyType(orgDetails.getCompanyType())
                .setKairosId(kairosId)
                .setTimeZone(ZoneId.of(TIMEZONE_UTC))
                .createOrganization();

        organizationGraphRepository.save(organization);

        orgDetails.setId(organization.getId());
        return orgDetails;
    }

}
