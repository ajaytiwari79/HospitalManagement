package com.kairos.service.agreement_template;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.dto.PolicyAgreementTemplateDTO;
import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.model.template_type.TemplateType;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.common.MongoSequenceRepository;
import com.kairos.persistance.repository.template_type.TemplateTypeMongoRepository;
import com.kairos.response.dto.master_data.PolicyAgreementTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
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
    private TemplateTypeMongoRepository templateTypeMongoRepository;

    //todo working on it
    public PolicyAgreementTemplate createPolicyAgreementTemplate(Long countryId, Long organizationId, PolicyAgreementTemplateDTO policyAgreementTemplateDto)  {

        String name = policyAgreementTemplateDto.getName();
        if (policyAgreementTemplateRepository.findByName(name) != null) {
            throw new DuplicateDataException("policy document template With name " + name + " already exist");
        } else {


            List<AgreementSection> agreementSection = policyAgreementTemplateDto.getAgreementSections();
            Set<BigInteger> accountTypeIds = policyAgreementTemplateDto.getAccountTypes();
            Map<String, Object> sections = new HashMap<>();
            PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate(countryId, name, policyAgreementTemplateDto.getDescription());

            if(policyAgreementTemplateDto.getTemplateTypeId()!=null){
                TemplateType exits =templateTypeMongoRepository.findByid(policyAgreementTemplateDto.getTemplateTypeId());
                if (java.util.Optional.ofNullable(exits).isPresent()) {
                    policyAgreementTemplate.setTemplateTypeId(policyAgreementTemplateDto.getTemplateTypeId());
                } else {
                    throw new DataNotExists("Template Id ->" + exits + " Not exists");
                }
            }
            else {
                throw new DataNotExists("Template Id  Not Null or Empty");
            }

            if (accountTypeService.getAccountTypeList(countryId, accountTypeIds).size() != 0) {
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
                    policyAgreementTemplate.setOrganizationSubServices(policyAgreementTemplateDto.getOrganizationSubServices());

                }
                if (agreementSection.size() != 0) {
                    sections = agreementSectionService.createAgreementSections(agreementSection);
                    policyAgreementTemplate.setAgreementSections((Set<BigInteger>) sections.get("ids"));
                }
                policyAgreementTemplate.setCountryId(countryId);
                policyAgreementTemplate = sequenceGenerator(policyAgreementTemplate);
            } else {
                exceptionService.illegalArgumentException("account type not exist ");
            }
            return policyAgreementTemplate;

        }

    }


    public PolicyAgreementTemplateResponseDTO getPolicyAgreementTemplateById(Long countryId, BigInteger id) {
        PolicyAgreementTemplateResponseDTO exist = policyAgreementTemplateRepository.getPolicyAgreementWithDataById(countryId, id);
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
            sequenceGenerator(exist);
            return true;
        }
        throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);

    }


    public PolicyAgreementTemplate updatePolicyAgreementTemplate(Long countryId, Long organizationId, BigInteger id, PolicyAgreementTemplateDTO policyAgreementTemplateDto) {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("policy agreement template not exist for id " + id);
        } else {
            PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate();
            List<AgreementSection> agreementSection = policyAgreementTemplateDto.getAgreementSections();
            Map<String, Object> sections = new HashMap<>();
            Set<BigInteger> accountTypeIds = policyAgreementTemplateDto.getAccountTypes();

            if(policyAgreementTemplateDto.getTemplateTypeId()!=null){
                TemplateType exits =templateTypeMongoRepository.findByid(policyAgreementTemplateDto.getTemplateTypeId());
                if (java.util.Optional.ofNullable(exits).isPresent()) {
                    policyAgreementTemplate.setTemplateTypeId(policyAgreementTemplateDto.getTemplateTypeId());
                } else {
                    throw new DataNotExists("Template Id ->" + exits + " Not exists");
                }
            }
            else {
                throw new DataNotExists("Template Id  Not Null or Empty");
            }

            if (accountTypeService.getAccountTypeList(countryId, accountTypeIds).size() != 0) {
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
                    policyAgreementTemplate.setOrganizationSubServices(policyAgreementTemplateDto.getOrganizationSubServices());

                }
                if (agreementSection.size() != 0) {
                    sections = agreementSectionService.createAgreementSections(agreementSection);
                    policyAgreementTemplate.setAgreementSections((Set<BigInteger>) sections.get("ids"));
                }

                exist.setAccountTypes(accountTypeIds);
                exist.setName(policyAgreementTemplateDto.getName());
                exist.setDescription(policyAgreementTemplateDto.getDescription());
            } else {
                exceptionService.illegalArgumentException("account type not exist ");
            }
            return sequenceGenerator(exist);

        }


    }



}
