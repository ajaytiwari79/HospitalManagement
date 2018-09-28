package com.kairos.service.agreement_template;


import com.kairos.dto.gdpr.PolicyAgreementTemplateDTO;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
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
import java.util.stream.Collectors;


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
     * @param policyAgreementTemplateDto
     * @return return object of basic policy agreement template.
     * @description this method creates a basic policy Agreement template with basic detail about organization type,
     * organizationSubTypes ,service Category and sub service Category.
     */
    public PolicyAgreementTemplateDTO createBasicPolicyAgreementTemplate(Long countryId, PolicyAgreementTemplateDTO policyAgreementTemplateDto) {

        PolicyAgreementTemplate previousTemplate = policyAgreementTemplateRepository.findByName(countryId, policyAgreementTemplateDto.getName());
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
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        policyAgreementTemplateDto.setId(policyAgreementTemplate.getId());
        return policyAgreementTemplateDto;

    }


    /**
     * @param countryId
     * @return
     * @description method return policy agreement template with basic details
     */
    public List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementTemplate(Long countryId) {
        List<PolicyAgreementTemplateResponseDTO> policyAgreementTemplateResponseDTOS = policyAgreementTemplateRepository.getAllPolicyAgreementTemplateByCountryId(countryId);
        policyAgreementTemplateResponseDTOS.forEach(policyAgreementTemplateResponseDTO -> policyAgreementTemplateResponseDTO.setSections(new ArrayList<>()));
        return policyAgreementTemplateResponseDTOS;
    }


    /**
     *
     * @param countryId
     * @param agreementTemplateId
     * @param policyAgreementTemplateDto
     * @return
     */
    public PolicyAgreementTemplateDTO updatePolicyAgreementTemplateBasicDetails(Long countryId, BigInteger agreementTemplateId, PolicyAgreementTemplateDTO policyAgreementTemplateDto) {

        PolicyAgreementTemplate template = policyAgreementTemplateRepository.findByName(countryId, policyAgreementTemplateDto.getName());
        if (Optional.ofNullable(template).isPresent() && !agreementTemplateId.equals(template.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Policy Agreement Template ", policyAgreementTemplateDto.getName());
        }
        template = policyAgreementTemplateRepository.findOne(agreementTemplateId);
        template.setName(policyAgreementTemplateDto.getName())
                .setDescription(policyAgreementTemplateDto.getDescription())
                .setAccountTypes(policyAgreementTemplateDto.getAccountTypes())
                .setOrganizationTypes(policyAgreementTemplateDto.getOrganizationTypes())
                .setOrganizationSubTypes(policyAgreementTemplateDto.getOrganizationSubTypes())
                .setOrganizationServices(policyAgreementTemplateDto.getOrganizationServices())
                .setOrganizationSubServices(policyAgreementTemplateDto.getOrganizationSubServices())
                .setTemplateType(policyAgreementTemplateDto.getTemplateTypeId());
        policyAgreementTemplateRepository.save(template);
        policyAgreementTemplateDto.setId(template.getId());
        return policyAgreementTemplateDto;

    }


    /**
     * @param countryId
     * @param agreementTemplateId
     * @return
     * @description method return list of Agreement sections with sub sections of policy agreement template
     */
    public List<AgreementSectionResponseDTO> getAllAgreementSectionsAndSubSectionsOfAgreementTemplateByTemplateId(Long countryId, BigInteger agreementTemplateId) {


        List<AgreementSectionResponseDTO> agreementSectionResponseDTOS = policyAgreementTemplateRepository.getAgreementTemplateWithSectionsAndSubSections(countryId, agreementTemplateId);
        agreementSectionResponseDTOS.forEach(agreementSectionResponseDTO ->
                {
                    Map<BigInteger, ClauseBasicResponseDTO> clauseBasicResponseDTOS = agreementSectionResponseDTO.getClauses().stream().collect(Collectors.toMap(ClauseBasicResponseDTO::getId, clauseBasicDTO -> clauseBasicDTO));
                    sortClauseOfAgreementSectionAndSubSectionInResponseDTO(clauseBasicResponseDTOS, agreementSectionResponseDTO);
                    if (!Optional.ofNullable(agreementSectionResponseDTO.getSubSections().get(0).getId()).isPresent()) {
                        agreementSectionResponseDTO.getSubSections().clear();
                        ;
                    } else {
                        agreementSectionResponseDTO.getSubSections().forEach(agreementSubSectionResponseDTO -> {
                            Map<BigInteger, ClauseBasicResponseDTO> subSectionClauseBasicResponseDTOS = agreementSubSectionResponseDTO.getClauses().stream().collect(Collectors.toMap(ClauseBasicResponseDTO::getId, clauseBasicDTO -> clauseBasicDTO));
                            sortClauseOfAgreementSectionAndSubSectionInResponseDTO(subSectionClauseBasicResponseDTOS, agreementSubSectionResponseDTO);
                        });
                    }
                }
        );
        return agreementSectionResponseDTOS;
    }

    private void sortClauseOfAgreementSectionAndSubSectionInResponseDTO(Map<BigInteger, ClauseBasicResponseDTO> clauseBasicResponseDTOS, AgreementSectionResponseDTO agreementSectionResponseDTO) {
        agreementSectionResponseDTO.getClauses().clear();
        List<ClauseBasicResponseDTO> clauses = new ArrayList<>();
        List<BigInteger> clauseIdOrderIndex = agreementSectionResponseDTO.getClauseIdOrderedIndex();
        for (int i = 0; i < clauseIdOrderIndex.size(); i++) {
            clauses.add(clauseBasicResponseDTOS.get(clauseIdOrderIndex.get(i)));
        }
        agreementSectionResponseDTO.setClauses(clauses);
    }


    public Boolean deletePolicyAgreementTemplate(Long countryId, BigInteger id) {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndCountryId(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", id);
        }
        delete(exist);
        return true;

    }


}


