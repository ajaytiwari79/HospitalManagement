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
import com.kairos.persistance.model.common.MongoSequence;
import com.kairos.persistance.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.common.MongoSequenceRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.jackrabbit_service.JackrabbitService;
import com.kairos.utils.ComparisonUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
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
    private MongoSequenceRepository mongoSequence;

    @Inject
    private JackrabbitService jackrabbitService;

    public PolicyAgreementTemplate createPolicyAgreementTemplate(PolicyAgreementTemplateDto policyAgreementTemplateDto) {
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

            OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto(orgTypeIds,orgSubTypeIds,orgServiceIds,orgSubServiceIds);
            OrganizationTypeAndServiceResultDto requestResult = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);
            PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate();

            if (Optional.ofNullable(requestResult).isPresent()) {

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
                    agreementSectionService.getAgreementSectionByIds(agreementSectionIds);
                    policyAgreementTemplate.setAgreementSections(agreementSectionIds);
                }

                policyAgreementTemplate.setName(policyAgreementTemplateDto.getName());
                policyAgreementTemplate.setDescription(policyAgreementTemplateDto.getDescription());
                policyAgreementTemplate.setCountryId(policyAgreementTemplateDto.getCountryId());

            } else {

                throw new DataNotExists("data not found in kairos User");

            }

            System.err.println("sequence generator   "+mongoSequence.nextSequence(PolicyAgreementTemplate.class.getName()));
            return save(policyAgreementTemplate);

        }

    }




    public PolicyAgreementTemplate getPolicyAgreementTemplateById(BigInteger id)
    {

        PolicyAgreementTemplate exist=policyAgreementTemplateRepository.findByIdAndNonDeleted(id);
        if (Optional.ofNullable(exist).isPresent())
        {
            return exist;

        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id "+id);



    }



    public Boolean deletePolicyAgreementTemplate(BigInteger id)
    {

        PolicyAgreementTemplate exist=policyAgreementTemplateRepository.findByIdAndNonDeleted(id);
        if (Optional.ofNullable(exist).isPresent())
        {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id "+id);



    }

















}
