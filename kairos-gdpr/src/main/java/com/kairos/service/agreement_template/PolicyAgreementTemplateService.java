package com.kairos.service.agreement_template;


import com.kairos.client.OrganizationTypeRestClient;
import com.kairos.client.dto.OrganizationTypeAndServiceRestClientRequestDto;
import com.kairos.client.dto.OrganizationTypeAndServiceResultDto;
import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.dto.PolicyAgreementTemplateDto;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.model.enums.VersionNode;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.common.MongoSequenceRepository;
import com.kairos.response.dto.agreement_template.AgreementSectionResponseDto;
import com.kairos.response.dto.agreement_template.PolicyAgreementTemplateResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.jackrabbit_service.JackrabbitService;
import com.kairos.utils.ComparisonUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class PolicyAgreementTemplateService extends MongoBaseService {


    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;

    @Inject
    private OrganizationTypeRestClient organizationTypeAndServiceRestClient;

    @Inject
    private ComparisonUtils comparisonUtils;

    @Inject
    private AccountTypeService accountTypeService;

    @Inject
    private AgreementSectionService agreementSectionService;

    @Inject
    private MongoSequenceRepository mongoSequenceRepository;

    @Inject
    private JackrabbitService jackrabbitService;

    public PolicyAgreementTemplate createPolicyAgreementTemplate(PolicyAgreementTemplateDto policyAgreementTemplateDto) throws RepositoryException {
        String name = policyAgreementTemplateDto.getName();
        if (policyAgreementTemplateRepository.findByName(name) != null) {
            throw new DuplicateDataException("policy document template With name " + name + " already exist");
        } else {

            Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
            orgTypeIds = policyAgreementTemplateDto.getOrganizationTypes();
            orgSubTypeIds = policyAgreementTemplateDto.getOrganizationSubTypes();
            orgServiceIds = policyAgreementTemplateDto.getOrganizationServices();
            orgSubServiceIds = policyAgreementTemplateDto.getOrganizationSubServices();
            Set<BigInteger> agreementSectionIds = policyAgreementTemplateDto.getAgreementSections();
            Set<BigInteger> accountTypeIds = policyAgreementTemplateDto.getAccountTypes();

            OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto(orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds);
            PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate();
            OrganizationTypeAndServiceResultDto requestResult = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);

            if (Optional.ofNullable(requestResult).isPresent()) {
                List<AgreementSectionResponseDto> sectionResponseDtos = new ArrayList<>();
                if (orgSubTypeIds != null && orgServiceIds.size() != 0) {
                    List<OrganizationTypeAndServiceBasicDto> orgSubTypes = requestResult.getOrganizationSubTypes();
                    comparisonUtils.checkOrgTypeAndService(orgSubTypeIds, orgSubTypes);
                    policyAgreementTemplate.setOrganizationSubTypes(orgSubTypes);
                }
                if (orgServiceIds != null && orgServiceIds.size() != 0) {
                    List<OrganizationTypeAndServiceBasicDto> orgServices = requestResult.getOrganizationServices();
                    comparisonUtils.checkOrgTypeAndService(orgServiceIds, orgServices);
                    policyAgreementTemplate.setOrganizationServices(orgServices);
                }
                if (orgSubServiceIds != null && orgSubServiceIds.size() != 0) {
                    List<OrganizationTypeAndServiceBasicDto> orgSubServices = requestResult.getOrganizationSubServices();
                    comparisonUtils.checkOrgTypeAndService(orgSubServiceIds, orgSubServices);
                    policyAgreementTemplate.setOrganizationSubServices(orgSubServices);
                }
                if (accountTypeIds.size() != 0) {
                    accountTypeService.getAccountListByIds(accountTypeIds);
                    policyAgreementTemplate.setAccountTypes(accountTypeIds);
                }

                if (agreementSectionIds.size() != 0) {
                    sectionResponseDtos = agreementSectionService.getAgreementSectionWithDataList(agreementSectionIds);
                    policyAgreementTemplate.setAgreementSections(agreementSectionIds);
                }
                comparisonUtils.checkOrgTypeAndService(orgTypeIds, requestResult.getOrganizationTypes());
                policyAgreementTemplate.setOrganizationTypes(requestResult.getOrganizationTypes());
                policyAgreementTemplate.setName(policyAgreementTemplateDto.getName());
                policyAgreementTemplate.setDescription(policyAgreementTemplateDto.getDescription());
                policyAgreementTemplate.setCountryId(policyAgreementTemplateDto.getCountryId());
                policyAgreementTemplate = save(policyAgreementTemplate);
                jackrabbitService.addAgreementTemplateJackrabbit(policyAgreementTemplate.getId(), policyAgreementTemplate, sectionResponseDtos);
            } else {

                throw new DataNotExists("data not found in kairos User");

            }

            return policyAgreementTemplate;

        }

    }


    public PolicyAgreementTemplate getPolicyAgreementTemplateById(BigInteger id) {
        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndNonDeleted(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);
    }


    public Boolean deletePolicyAgreementTemplate(BigInteger id) {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndNonDeleted(id);
        if (Optional.ofNullable(exist).isPresent()) {
            exist.setDeleted(true);
            save(exist);
            return true;
        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);

    }


    public PolicyAgreementTemplate updatePolicyAgreementTemplate(BigInteger id, com.kairos.dto.PolicyAgreementTemplateDto policyAgreementTemplateDto) throws RepositoryException {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);
        } else {

            Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
            orgTypeIds = policyAgreementTemplateDto.getOrganizationTypes();
            orgSubTypeIds = policyAgreementTemplateDto.getOrganizationSubTypes();
            orgServiceIds = policyAgreementTemplateDto.getOrganizationServices();
            orgSubServiceIds = policyAgreementTemplateDto.getOrganizationSubServices();
            Set<BigInteger> agreementSectionIds = policyAgreementTemplateDto.getAgreementSections();
            Set<BigInteger> accountTypeIds = policyAgreementTemplateDto.getAccountTypes();

            OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto(orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds);
            OrganizationTypeAndServiceResultDto requestResult = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);

            if (Optional.ofNullable(requestResult).isPresent()) {
                List<AgreementSectionResponseDto> sectionResponseDtos = new ArrayList<>();
                if (orgSubTypeIds != null && orgServiceIds.size() != 0) {
                    List<OrganizationTypeAndServiceBasicDto> orgSubTypes = requestResult.getOrganizationSubTypes();
                    comparisonUtils.checkOrgTypeAndService(orgSubTypeIds, orgSubTypes);
                    exist.setOrganizationSubTypes(orgSubTypes);
                }
                if (orgServiceIds != null && orgServiceIds.size() != 0) {
                    List<OrganizationTypeAndServiceBasicDto> orgServices = requestResult.getOrganizationServices();
                    comparisonUtils.checkOrgTypeAndService(orgServiceIds, orgServices);
                    exist.setOrganizationServices(orgServices);
                }
                if (orgSubServiceIds != null && orgSubServiceIds.size() != 0) {
                    List<OrganizationTypeAndServiceBasicDto> orgSubServices = requestResult.getOrganizationSubServices();
                    comparisonUtils.checkOrgTypeAndService(orgSubServiceIds, orgSubServices);
                    exist.setOrganizationSubServices(orgSubServices);
                }
                if (accountTypeIds.size() != 0) {
                    accountTypeService.getAccountListByIds(accountTypeIds);
                    exist.setAccountTypes(accountTypeIds);
                }

                if (agreementSectionIds.size() != 0) {
                    sectionResponseDtos = agreementSectionService.getAgreementSectionWithDataList(agreementSectionIds);
                    exist.setAgreementSections(agreementSectionIds);
                }
                comparisonUtils.checkOrgTypeAndService(orgTypeIds, requestResult.getOrganizationTypes());
                exist.setOrganizationTypes(requestResult.getOrganizationTypes());
                exist.setName(policyAgreementTemplateDto.getName());
                exist.setDescription(policyAgreementTemplateDto.getDescription());
                exist.setCountryId(policyAgreementTemplateDto.getCountryId());

                jackrabbitService.agreementTemplateVersioning(id, exist, sectionResponseDtos);
            } else {

                throw new DataNotExists("data not found in kairos User");

            }

            return save(exist);

        }


    }


    public String getPolicyTemplateVersion(BigInteger id, VersionNode version) throws RepositoryException {
        switch (version) {
            case ROOT_VERSION:
                return jackrabbitService.getpolicyTemplateVersion(id, "1.0");
            case BASE_VERSION:
                return jackrabbitService.getpolicyTemplateBaseVersion(id);
            default:
                return null;

        }

    }


    public PolicyAgreementTemplateResponseDto getPolicyAgreementTemplateWithDataById(BigInteger id) {
        PolicyAgreementTemplateResponseDto exist = policyAgreementTemplateRepository.getpolicyAgreementWithData(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);
    }




}
