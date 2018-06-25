package com.kairos.service.agreement_template;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.dto.PolicyAgreementTemplateDTO;
import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.common.MongoSequenceRepository;
import com.kairos.response.dto.master_data.AgreementSectionResponseDTO;
import com.kairos.response.dto.master_data.PolicyAgreementTemplateResponseDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.jackrabbit_service.JackrabbitService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.math.BigInteger;
import java.util.*;


@Service
public class PolicyAgreementTemplateService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAgreementTemplateService.class);

    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;


    @Inject private ComparisonUtils comparisonUtils;

    @Inject
    private AccountTypeService accountTypeService;

    @Inject
    private AgreementSectionService agreementSectionService;

    @Inject
    private MongoSequenceRepository mongoSequenceRepository;

    @Inject
    private JackrabbitService jackrabbitService;

    @Inject
    private ExceptionService exceptionService;

    public PolicyAgreementTemplate createPolicyAgreementTemplate(Long countryId, PolicyAgreementTemplateDTO policyAgreementTemplateDto) throws RepositoryException {

        String name = policyAgreementTemplateDto.getName();
        if (policyAgreementTemplateRepository.findByName(name) != null) {
            throw new DuplicateDataException("policy document template With name " + name + " already exist");
        } else {


            List<AgreementSection> agreementSection = policyAgreementTemplateDto.getAgreementSections();
            Set<BigInteger> accountTypeIds = policyAgreementTemplateDto.getAccountTypes();
            Map<String, Object> sections=new HashMap<>();
            PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate(countryId, name, policyAgreementTemplateDto.getDescription());

            if (accountTypeService.getAccountTypeList(countryId,accountTypeIds).size()!=0) {
                policyAgreementTemplate.setAccountTypes(accountTypeIds);

                if (policyAgreementTemplateDto.getOrganizationTypes() != null && policyAgreementTemplateDto.getOrganizationTypes().size() != 0) {
                    policyAgreementTemplate.setOrganizationTypes(policyAgreementTemplateDto.getOrganizationTypes());

                }
                if (policyAgreementTemplateDto.getOrganizationSubTypes() != null && policyAgreementTemplateDto.getOrganizationSubTypes().size() != 0) {
                    policyAgreementTemplate.setOrganizationSubTypes(policyAgreementTemplateDto.getOrganizationSubTypes());

                }
                if (policyAgreementTemplateDto.getOrganizationServices() != null && policyAgreementTemplateDto.getOrganizationServices().size() != 0) {
                    policyAgreementTemplate.setOrganizationServices(policyAgreementTemplateDto.getOrganizationServices());

                }
                if (policyAgreementTemplateDto.getOrganizationSubServices() != null && policyAgreementTemplateDto.getOrganizationSubServices().size() != 0) {
                    policyAgreementTemplate.setOrganizationSubServices(policyAgreementTemplateDto.getOrganizationTypes());

                }
                if (agreementSection.size() != 0) {
                    sections = agreementSectionService.createAgreementSections(agreementSection);
                    policyAgreementTemplate.setAgreementSections((Set<BigInteger>) sections.get("ids"));
                }
                policyAgreementTemplate.setCountryId(countryId);
                policyAgreementTemplate = save(policyAgreementTemplate);
                jackrabbitService.addAgreementTemplateJackrabbit(policyAgreementTemplate.getId(), policyAgreementTemplate, (List<AgreementSectionResponseDTO>) sections.get("section"));
            }
            else {
                exceptionService.illegalArgumentException("account type not exist ");
            }
            return policyAgreementTemplate;

        }

    }


    public PolicyAgreementTemplateResponseDTO getPolicyAgreementTemplateById(Long countryId, BigInteger id) {
        PolicyAgreementTemplateResponseDTO exist = policyAgreementTemplateRepository.getPolicyAgreementWithDataById(countryId,id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);
    }


    public List<PolicyAgreementTemplateResponseDTO> getPolicyAgreementTemplateWithData() {
        List<PolicyAgreementTemplateResponseDTO> exist = policyAgreementTemplateRepository.getPolicyAgreementWithData(UserContext.getCountryId());
        if (exist.size() != 0) {
            return exist;
        }
        throw new DataNotExists("policy agreement template ");
    }


    public Boolean deletePolicyAgreementTemplate(BigInteger id) {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByid(id);
        if (Optional.ofNullable(exist).isPresent()) {
            exist.setDeleted(true);
            save(exist);
            return true;
        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);

    }


    public PolicyAgreementTemplate updatePolicyAgreementTemplate(Long countryId,BigInteger id, PolicyAgreementTemplateDTO policyAgreementTemplateDto) throws RepositoryException {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);
        } else {

            List<AgreementSection> agreementSection = policyAgreementTemplateDto.getAgreementSections();
            Map<String, Object> sections=new HashMap<>();
            Set<BigInteger> accountTypeIds = policyAgreementTemplateDto.getAccountTypes();

            PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate();
            if (accountTypeService.getAccountTypeList(countryId,accountTypeIds).size()!=0) {
                policyAgreementTemplate.setAccountTypes(accountTypeIds);
                if (policyAgreementTemplateDto.getOrganizationTypes() != null && policyAgreementTemplateDto.getOrganizationTypes().size() != 0) {
                    policyAgreementTemplate.setOrganizationTypes(policyAgreementTemplateDto.getOrganizationTypes());

                }
                if (policyAgreementTemplateDto.getOrganizationSubTypes() != null && policyAgreementTemplateDto.getOrganizationSubTypes().size() != 0) {
                    policyAgreementTemplate.setOrganizationSubTypes(policyAgreementTemplateDto.getOrganizationSubTypes());

                }
                if (policyAgreementTemplateDto.getOrganizationServices() != null && policyAgreementTemplateDto.getOrganizationServices().size() != 0) {
                    policyAgreementTemplate.setOrganizationServices(policyAgreementTemplateDto.getOrganizationServices());

                }
                if (policyAgreementTemplateDto.getOrganizationSubServices() != null && policyAgreementTemplateDto.getOrganizationSubServices().size() != 0) {
                    policyAgreementTemplate.setOrganizationSubServices(policyAgreementTemplateDto.getOrganizationTypes());

                }
                if (agreementSection.size() != 0) {
                    sections = agreementSectionService.createAgreementSections(agreementSection);
                    policyAgreementTemplate.setAgreementSections((Set<BigInteger>) sections.get("ids"));
                }
                exist.setAccountTypes(accountTypeIds);
                exist.setName(policyAgreementTemplateDto.getName());
                exist.setDescription(policyAgreementTemplateDto.getDescription());
                jackrabbitService.agreementTemplateVersioning(id, exist, (List<AgreementSectionResponseDTO>) sections.get("section"));
            }
            else {
                exceptionService.illegalArgumentException("account type not exist ");
            }
            return save(exist);

        }


    }


    public StringBuffer getPolicyTemplateVersion(BigInteger id, String version) throws RepositoryException {
        return jackrabbitService.getpolicyTemplateVersion(id, version);
    }


    public List<String> getPolicyTemplateAllVersionList(Long countryId,BigInteger id) throws RepositoryException {

        return jackrabbitService.getPolicyTemplateVersions(id);

    }


}
