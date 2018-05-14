package com.kairos.service.agreement_template;


import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.persistance.model.agreement_template.AgreementTemplate;
import com.kairos.persistance.model.agreement_template.dto.AgreementTemplateDto;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.repository.agreement_template.AgreementTemplateMongoRepository;
import com.kairos.persistance.repository.clause.AccountTypeMongoRepository;
import com.kairos.persistance.repository.organization.OrganizationServiceMongoRepository;
import com.kairos.persistance.repository.organization.OrganizationTypeMongoRepository;
import com.kairos.response.dto.AgreementQueryResult;
import com.kairos.service.MongoBaseService;
import com.kairos.service.clause.AccountTypeService;
import com.kairos.service.clause.ClauseService;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class AgreementTemplateService extends MongoBaseService {


    @Inject
    private AgreementTemplateMongoRepository agreementTemplateMongoRepository;

    @Inject
    private OrganizationTypeMongoRepository organizationTypeMongoRepository;

    @Inject
    private OrganizationServiceMongoRepository organizationServiceMongoRepository;

    @Inject
    private AccountTypeService accountTypeService;

    @Inject
    private ClauseService clauseService;


    public AgreementTemplate createAgrementTemplate(AgreementTemplateDto agreementTemplateDto) {
        AgreementTemplate newAgreementTemplate = new AgreementTemplate();
        Long orgTypeId, orgServiceId, countryId;
        orgTypeId = agreementTemplateDto.getOrganisationTypeid();
        orgServiceId = agreementTemplateDto.getOrgServiceid();
        countryId = agreementTemplateDto.getCountryId();
        BigInteger accountType = agreementTemplateDto.getAccountTypeId();
        Boolean isDefault = true;
        List<BigInteger> clauseids = agreementTemplateDto.getClauseIds();
        if (orgTypeId != null) {
            newAgreementTemplate.setOrganisationType(organizationTypeMongoRepository.findById(orgTypeId.toString()));
            isDefault = false;
        }
        if (orgServiceId != null) {
            newAgreementTemplate.setOrgService(organizationServiceMongoRepository.findById(orgServiceId.toString()));
            isDefault = false;
        }
        if (accountType != null) {

            newAgreementTemplate.setAccountType(accountTypeService.getAccountTypeById(accountType));
            isDefault = false;
        }
        if (clauseids.size()!= 0) {
             clauseService.getClausesByIds(clauseids);
            newAgreementTemplate.setClauses(clauseids);
            isDefault = false;
        }
           newAgreementTemplate.setDefault(isDefault);
        newAgreementTemplate.setName(agreementTemplateDto.getName());
        newAgreementTemplate.setDescription(agreementTemplateDto.getDescription());
        return save(newAgreementTemplate);

    }


    public AgreementTemplate createDefaultAgrementTemplate(AgreementTemplateDto agreementTemplateDto) {

        AgreementTemplate newAgreementTemplate = new AgreementTemplate();
        newAgreementTemplate.setName(agreementTemplateDto.getName());
        newAgreementTemplate.setDescription(agreementTemplateDto.getDescription());
        List<BigInteger> clauseids = agreementTemplateDto.getClauseIds();
        if (clauseids.size() > 0) {
            List<Clause> clauses = clauseService.getClausesByIds(agreementTemplateDto.getClauseIds());
            newAgreementTemplate.setClauses(clauseids);
        }
        return save(newAgreementTemplate);

    }


    public AgreementQueryResult getAgreementTemplateById(BigInteger id) {

        AgreementQueryResult agreementTemplate = agreementTemplateMongoRepository.findById(id);
        if (Optional.ofNullable(agreementTemplate).isPresent()) {
            return agreementTemplate;
        } else
            throw new DataNotFoundByIdException("agreement policy templet not exist for id " + id);

    }


    public Boolean deleteById(BigInteger id) {


        AgreementTemplate exists = agreementTemplateMongoRepository.findAgreementById(id);
        if (exists != null) {
            agreementTemplateMongoRepository.delete(exists);

            return true;
        } else
            throw new DataNotFoundByIdException("agreement policy template not exist for id " + id);
    }


    public AgreementTemplate updateAgreementTemplateclauses(BigInteger id,List<BigInteger> clausesIds){


        AgreementTemplate exist=agreementTemplateMongoRepository.findAgreementById(id);
        if (Optional.ofNullable(exist).isPresent())
        {
           List<Clause> clauses=clauseService.getClausesByIds(clausesIds);
           exist.setClauses(clausesIds);
            return  save(exist);
        }
        else
            throw new DataNotFoundByIdException("agreement policy template not exist for id " + id);

    }


}
