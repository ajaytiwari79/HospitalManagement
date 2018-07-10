package com.kairos.service.agreement_template;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.master_data.AgreementSectionDTO;
import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.response.dto.master_data.AgreementSectionResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.AGREEMENT_SECTION_LIST;


@Service
public class AgreementSectionService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementSectionService.class);

    @Inject
    private AgreementSectionMongoRepository agreementSectionMongoRepository;

    @Inject
    private ClauseMongoRepository clauseMongoRepository;
    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;
    @Inject
    private ExceptionService exceptionService;

    public void createAndAddAgreementSectionsAndClausesToAgreementTemplate(Long countryId, Long organizationId, BigInteger agreementTemplateId, List<AgreementSectionDTO> agreementSectionDTOs) {

        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndNonDeleted(countryId, organizationId, agreementTemplateId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", agreementTemplateId);
        }
        Boolean flag = false;
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOs) {

            if (Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                flag = true;
                break;
            }

        }
        Map<String, Object> agreementSections = new HashMap<>();
        if (flag) {


        } else {

            agreementSections = createNewAggrementTemplateSection(countryId, organizationId, agreementSectionDTOs,policyAgreementTemplate);
        }


    }


    public Map<String, Object> createNewAggrementTemplateSection(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOS,PolicyAgreementTemplate policyAgreementTemplate) {

        checkForDuplicacyInTitleOfAgreementSections(agreementSectionDTOS);
        List<AgreementSection> agreementSectionList=new ArrayList<>();
        Map<String,Object> result=new HashMap<>();
        agreementSectionDTOS.forEach(agreementSectionDTO -> {


            AgreementSection agreementSection=new AgreementSection();











        });












        return null;
    }


    public Boolean deleteAgreementSection(BigInteger id) {

        AgreementSection exist = agreementSectionMongoRepository.findByid(id);
        if (Optional.ofNullable(exist).isPresent()) {
            exist.setDeleted(true);
            sequenceGenerator(exist);
            return true;
        }
        throw new DataNotFoundByIdException(" agreement section for id " + id + " not exist");

    }


    public AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId, BigInteger id) {

        AgreementSectionResponseDTO exist = agreementSectionMongoRepository.getAgreementSectionWithDataById(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("agreement section for id " + id + " not exist");

    }


    public List<AgreementSectionResponseDTO> getAllAgreementSection(Long countryId) {

        List<AgreementSectionResponseDTO> result = agreementSectionMongoRepository.getAllAgreementSectionWithData(countryId);
        if (result.size() != 0) {
            return result;
        }
        throw new DataNotExists("agreement section not exist create new sections");

    }


    public List<AgreementSectionResponseDTO> getAgreementSectionWithDataList(Long countryId, Set<BigInteger> ids) {
        return agreementSectionMongoRepository.getAgreementSectionWithDataList(countryId, ids);

    }


    public void checkForDuplicacyInTitleOfAgreementSections(List<AgreementSectionDTO> agreementSectionDTOS) {
        List<String> titles = new ArrayList<>();
        for (AgreementSectionDTO questionnaireSectionDto : agreementSectionDTOS) {
            if (titles.contains(questionnaireSectionDto.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "questionnaire section", questionnaireSectionDto.getName());
            }
            titles.add(questionnaireSectionDto.getName().toLowerCase());
        }
    }


}
