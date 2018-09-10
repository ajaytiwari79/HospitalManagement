package com.kairos.service.agreement_template;


import com.kairos.dto.gdpr.PolicyAgreementTemplateDTO;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.template_type.TemplateTypeService;
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
    private AgreementSectionService agreementSectionService;

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
    public PolicyAgreementTemplateDTO createBasicPolicyAgreementTemplate(Long countryId, Long organizationId, PolicyAgreementTemplateDTO policyAgreementTemplateDto) {

        PolicyAgreementTemplate previousTemplate = policyAgreementTemplateRepository.findByName(countryId, organizationId, policyAgreementTemplateDto.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Policy Agreement Template ", policyAgreementTemplateDto.getName());
        }
        templateTypeService.getTemplateById(policyAgreementTemplateDto.getTemplateTypeId(), countryId);
        PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate(
                policyAgreementTemplateDto.getName(),
                policyAgreementTemplateDto.getDescription(),
                countryId,
                policyAgreementTemplateDto.getOrganizationTypes(),
                policyAgreementTemplateDto.getOrganizationSubTypes(),
                policyAgreementTemplateDto.getOrganizationServices(),
                policyAgreementTemplateDto.getOrganizationSubServices());
        policyAgreementTemplate.setAccountTypes(policyAgreementTemplateDto.getAccountTypes());
        policyAgreementTemplate.setTemplateType(policyAgreementTemplateDto.getTemplateTypeId());
        policyAgreementTemplate.setOrganizationId(organizationId);
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        policyAgreementTemplateDto.setId(policyAgreementTemplate.getId());
        return policyAgreementTemplateDto;

    }


    /**
     * @description method return policy agreement template with basic details
     * @param countryId
     * @param organizationId
     * @return
     */
    public List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementTemplateWithAgreementSectionAndClauses(Long countryId, Long organizationId) {
        return policyAgreementTemplateRepository.getAllPolicyAgreementTemplateByCountryId(countryId, organizationId);
    }


    /**
     * @param countryId
     * @param unitId
     * @param agreementTemplateId
     * @return
     * @description method return list of Agreement sections with sub sections of policy agreement template
     */
    public List<AgreementSectionResponseDTO> getAllAgreementSectionsAndSubSectionsOfAgreementTemplateByTemplateId(Long countryId, Long unitId, BigInteger agreementTemplateId) {
        return policyAgreementTemplateRepository.getAgreementTemplateAllSectionAndSubSections(countryId, unitId, agreementTemplateId);
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


