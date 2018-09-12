package com.kairos.service.clause;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.dto.gdpr.master_data.ClauseDTO;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.service.clause_tag.ClauseTagService;
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
public class ClauseService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseService.class);

    @Inject
    private ClauseMongoRepository clauseMongoRepository;


    @Inject
    private ClauseTagService clauseTagService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private TemplateTypeService templateTypeService;


    /**
     * @param countryId
     * @param organizationId
     * @param clauseDto      contain data about clause and template type which belong to clause
     * @return clause  object , specific to organization type ,sub types ,Service Category and Sub Service Category
     * @throws DuplicateDataException: if clause already exist for id ,{@link com.kairos.custom_exception.InvalidRequestException if account type is not selected}
     * @desciption this method create clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public Clause createClause(Long countryId, Long organizationId, ClauseDTO clauseDto) {

        if (clauseMongoRepository.findByTitle(countryId, organizationId, clauseDto.getTitle()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        if (clauseDto.getAccountTypes().size() == 0) {
            exceptionService.invalidRequestException("message.invalid.request", "Select account Type");
        }
        List<ClauseTag> tagList = new ArrayList<>();
        //templateTypeService.getTemplateByIdsList(templateTypesIds, countryId);
        Clause newClause = new Clause(clauseDto.getTitle(), clauseDto.getDescription(), countryId, clauseDto.getOrganizationTypes(), clauseDto.getOrganizationSubTypes()
                , clauseDto.getOrganizationServices(), clauseDto.getOrganizationSubServices());
        newClause.setOrganizationId(organizationId);
        newClause.setAccountTypes(clauseDto.getAccountTypes());
        newClause.setTemplateTypes(clauseDto.getTemplateTypes());

        try {
            tagList = clauseTagService.addClauseTagAndGetClauseTagList(countryId, organizationId, clauseDto.getTags());
            newClause.setTags(tagList);
            newClause = clauseMongoRepository.save(newClause);
            return newClause;
        } catch (DuplicateDataException e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.debug(e.getMessage());
            throw new DuplicateDataException(e.getMessage());
        } catch (Exception e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.debug(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }


    /**
     * @param countryId
     * @param organizationId
     * @param clauseId       clause id
     * @param clauseDto      contain update data for clause
     * @return updated clause object
     * @throws DataNotFoundByIdException: if clause not found for particular id, {@link DuplicateDataException if clause already exist with same name}
     * @description this method update clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     */
    public Clause updateClause(Long countryId, Long organizationId, BigInteger clauseId, ClauseDTO clauseDto) {

        Clause clause = clauseMongoRepository.findByTitle(countryId, organizationId, clauseDto.getTitle());
        if (Optional.ofNullable(clause).isPresent() && !clause.getId().equals(clauseId)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        clause = clauseMongoRepository.findByIdAndNonDeleted(countryId, organizationId, clauseId);
        if (!Optional.ofNullable(clause).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + clauseId);
        }
        List<ClauseTag> tagList = new ArrayList<>();
        clause.setAccountTypes(clauseDto.getAccountTypes());
        List<BigInteger> templateTypesIds=clauseDto.getTemplateTypes();
        //templateTypeService.getTemplateByIdsList(templateTypesIds, countryId);
        try {
            tagList = clauseTagService.addClauseTagAndGetClauseTagList(countryId, organizationId, clauseDto.getTags());
            clause.setOrganizationTypes(clauseDto.getOrganizationTypes());
            clause.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
            clause.setOrganizationServices(clauseDto.getOrganizationServices());
            clause.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
            clause.setTitle(clauseDto.getTitle());
            clause.setDescription(clauseDto.getDescription());
            clause.setTags(tagList);
            clause.setTemplateTypes(clauseDto.getTemplateTypes());
            // exists.setOrganizationList(clauseDto.getOrganizationList());
            clause = clauseMongoRepository.save(clause);
        } catch (Exception e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return clause;
    }


    public List<Clause> getClauseList(Long countryId, Long organizationId, List<BigInteger> clausesId) {
        return clauseMongoRepository.findClauseByCountryIdAndIdList(countryId, organizationId, clausesId);
    }


    /**
     * @param countryId
     * @param organizationId
     * @param clauseBasicDTOS         List od Clause Dto contain basic detail ,title and description of clause.
     * @param policyAgreementTemplate - policy agreement template contain list or organization types,Sub types,Service Category and Sub Service Category and Account types.
     * @return
     * @description this method create is used in Agreement section Service for creating new Clauses on creation of sections in policy agreement template.
     */
    public List<Clause> createNewClauseUsingAgreementTemplateMetadata(Long countryId, Long organizationId, List<ClauseBasicDTO> clauseBasicDTOS, PolicyAgreementTemplate policyAgreementTemplate) {


        List<String> clauseTitles = new ArrayList<>();
        clauseBasicDTOS.forEach(
                clauseBasicDTO -> {
                    if (clauseTitles.contains(clauseBasicDTO.getTitle())) {
                        exceptionService.duplicateDataException("message.duplicate", "Clause title ", clauseBasicDTO.getTitle());
                    }
                    clauseTitles.add(clauseBasicDTO.getTitle());
                }
        );
        List<Clause> existingClause = clauseMongoRepository.findClausesByTitle(countryId, organizationId, clauseTitles);
        if (!existingClause.isEmpty()) {
            exceptionService.duplicateDataException("message.duplicate", " Clause " + existingClause.get(0).getTitle());
        }
        List<Clause> newClauseList = new ArrayList<>();
        for (ClauseBasicDTO clauseBasicDTO : clauseBasicDTOS) {
            Clause clause = new Clause(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription(), countryId, policyAgreementTemplate.getOrganizationTypes(), policyAgreementTemplate.getOrganizationSubTypes()
                    , policyAgreementTemplate.getOrganizationServices(), policyAgreementTemplate.getOrganizationSubServices());

            List<BigInteger> templateTypes=new ArrayList<>();
            templateTypes.add(policyAgreementTemplate.getTemplateType());
            clause.setTemplateTypes(templateTypes);
            clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
            clause.setOrganizationId(organizationId);
            clause.setAccountTypes(policyAgreementTemplate.getAccountTypes());
            newClauseList.add(clause);

        }
        return clauseMongoRepository.saveAll(getNextSequence(newClauseList));
    }


    /**
     * @param countryId
     * @param organizationId
     * @return return clause with account type basic response,org types ,sub types,service category ,sub service category and tags
     * @description
     */
    public List<ClauseResponseDTO> getAllClauses(Long countryId, Long organizationId) {
        return clauseMongoRepository.findAllClauseWithTemplateType(countryId, organizationId);
    }


    public ClauseResponseDTO getClauseById(Long countryId, Long organizationId, BigInteger id) {
        ClauseResponseDTO clause = clauseMongoRepository.findClauseWithTemplateTypeById(countryId, organizationId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        }
        return clause;
    }




    /**
     * @param countryId
     * @param organizationId
     * @param id
     * @return boolean true if data deleted successfully
     * @throws DataNotFoundByIdException; if clause not found for id
     */
    public Boolean deleteClause(Long countryId, Long organizationId, BigInteger id) {

        Clause clause = clauseMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + id);
        }
        delete(clause);
        return true;
    }


}
