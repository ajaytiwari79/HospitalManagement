package com.kairos.service.agreement_template;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.PolicyAgreementTemplateDTO;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.common.MongoSequenceRepository;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.template_type.TemplateTypeService;
import com.kairos.utils.ComparisonUtils;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;


@Service
public class PolicyAgreementTemplateService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAgreementTemplateService.class);

    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;


    @Inject
    private ComparisonUtils comparisonUtils;

    @Inject
    private AccountTypeService accountTypeService;

    @Inject
    private AgreementSectionService agreementSectionService;

    @Inject
    private MongoSequenceRepository mongoSequenceRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TemplateTypeService templateTypeService;


    /**
     * @param countryId
     * @param organizationId
     * @param policyAgreementTemplateDto
     * @return return object of basic policy agreement template.
     * @description this method creates a basic policy Agreement template with basic detail about organization type,
     * organizationSubTypes ,service Category and sub service Category.
     */
    public PolicyAgreementTemplate createBasicPolicyAgreementTemplate(Long countryId, Long organizationId, PolicyAgreementTemplateDTO policyAgreementTemplateDto) {

        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByName(countryId, organizationId, policyAgreementTemplateDto.getName());
        if (Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Policy Agreement Template ", policyAgreementTemplateDto.getName());
        }
        templateTypeService.getTemplateById(policyAgreementTemplateDto.getTemplateTypeId(), countryId);
        List<AccountType> accountTypes = accountTypeService.getAccountTypeList(countryId, policyAgreementTemplateDto.getAccountTypes());
        PolicyAgreementTemplate newPolicyAgreementTemplate = new PolicyAgreementTemplate(
                policyAgreementTemplateDto.getName(),
                policyAgreementTemplateDto.getDescription(),
                countryId,
                policyAgreementTemplateDto.getOrganizationTypes(),
                policyAgreementTemplateDto.getOrganizationSubTypes(),
                policyAgreementTemplateDto.getOrganizationServices(),
                policyAgreementTemplateDto.getOrganizationSubServices());
        newPolicyAgreementTemplate.setAccountTypes(accountTypes);
        newPolicyAgreementTemplate.setTemplateType(policyAgreementTemplateDto.getTemplateTypeId());
        newPolicyAgreementTemplate.setOrganizationId(organizationId);
        try {
            newPolicyAgreementTemplate = policyAgreementTemplateRepository.save(sequenceGenerator(newPolicyAgreementTemplate));
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return newPolicyAgreementTemplate;

    }


    /**
     * @param countryId
     * @param organizationId
     * @param id
     * @return
     * @description -method getPolicyAgreementWithSectionsAndClausesById()  uses Mongo QUuery which  return agreement template with sections and clauses if agreementSections is  present ,if not agreementSections present then
     * it return agreementSections as agreementSections[ {} ] from data base ,which is converted into AgreementSectionResponseDTO[{id=null ,name=null}], thats  why we are checking if id is null present then simply add new agreementSections[]
     * instead of agreementSections[ {} ] .
     */
    public PolicyAgreementTemplateResponseDTO getPolicyAgreementTemplateWithAgreementSectionAndClausesById(Long countryId, Long organizationId, BigInteger id) {
        PolicyAgreementTemplateResponseDTO policyAgreementTemplateResponseDTO = policyAgreementTemplateRepository.getPolicyAgreementWithSectionsAndClausesById(countryId, organizationId, id);
        if (policyAgreementTemplateResponseDTO.getAgreementSections().get(0).getId() == null) {
            policyAgreementTemplateResponseDTO.setAgreementSections(new ArrayList<>());
        }
        return policyAgreementTemplateResponseDTO;

    }


    /**
     * @param countryId
     * @param organizationId
     * @return -method return list all policy Agreement Template with sections and Clauses
     * @description -method getAllPolicyAgreementWithSectionsAndClauses()  uses Mongo QUuery which  return agreement template with sections and clauses if agreementSections is  present ,if not agreementSections present then
     * it return agreementSections as agreementSections[ {} ] from data base ,which is converted into AgreementSectionResponseDTO[{id=null ,name=null}], thats  why we are checking if id is null present then simply add new agreementSections[]
     * instead of agreementSections[ {} ] .
     */
    public List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementTemplateWithAgreementSectionAndClauses(Long countryId, Long organizationId) {
        List<PolicyAgreementTemplateResponseDTO> policyAgreementTemplateResponseDTOList = policyAgreementTemplateRepository.getAllPolicyAgreementWithSectionsAndClauses(countryId, organizationId);
        policyAgreementTemplateResponseDTOList.forEach(policyAgreementTemplateResponseDTO -> {

            if (policyAgreementTemplateResponseDTO.getAgreementSections().get(0).getId() == null) {
                policyAgreementTemplateResponseDTO.setAgreementSections(new ArrayList<>());
            }
        });
        return policyAgreementTemplateResponseDTOList;

    }


    public Boolean deletePolicyAgreementTemplate(Long countryId, Long organizationId, BigInteger id) {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", id);
        }
        delete(exist);
        return true;

    }





}


