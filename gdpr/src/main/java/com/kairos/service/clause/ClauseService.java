package com.kairos.service.clause;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistance.repository.account_type.AccountTypeMongoRepository;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.dto.master_data.ClauseDTO;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.template_type.TemplateTypeService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;


@Service
public class ClauseService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseService.class);

    @Autowired
    private ClauseMongoRepository clauseRepository;

    @Inject
    private AccountTypeService accountTypeService;
    @Inject
    private MongoTemplate mongoTemplate;


    @Inject
    private AccountTypeMongoRepository accountTypeMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;


    @Inject
    private ClauseTagService clauseTagService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private TemplateTypeService templateTypeService;


    /**@desciption  this method create clause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     * @param countryId
     * @param organizationId
     * @param clauseDto contain data about clause and template type which belong to clause
     * @return clause  object , specific to organization type ,sub types ,Service Category and Sub Service Category
     * @exception DuplicateDataException: if clause already exist for id ,{@link com.kairos.custom_exception.InvalidRequestException if account type is not selected}
     */
    public Clause createClause(Long countryId, Long organizationId, ClauseDTO clauseDto) {

        if (clauseRepository.findByTitle(countryId, organizationId, clauseDto.getTitle()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "clause", clauseDto.getTitle().toLowerCase());
        }
        if (clauseDto.getAccountTypes().size() == 0) {
            exceptionService.invalidRequestException("message.invalid.request", "Select account Type");
        }
        List<ClauseTag> tagList=new ArrayList<>();
        // templateTypeService.getTemplateByById(clauseDto.getTemplateType(), countryId);
        Clause newClause = new Clause( clauseDto.getTitle(), clauseDto.getDescription(),countryId,clauseDto.getOrganizationTypes(),clauseDto.getOrganizationSubTypes()
        ,clauseDto.getOrganizationServices(),clauseDto.getOrganizationSubServices());
        newClause.setOrganizationId(organizationId);
        newClause.setAccountTypes(accountTypeService.getAccountTypeList(countryId, clauseDto.getAccountTypes()));
        //newClause.setOrganizationList(clauseDto.getOrganizationList());
        // newClause.setTemplateType(clauseDto.getTemplateType());

        try {
            tagList = clauseTagService.addClauseTagAndGetClauseTagList(countryId, organizationId, clauseDto.getTags());
            newClause.setTags(tagList);
            newClause = clauseRepository.save(sequenceGenerator(newClause));
            return newClause;
        } catch (DuplicateDataException e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.debug(e.getMessage());
            throw new DuplicateDataException(e.getMessage());
        }
        catch (Exception e)
        {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.debug(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    public Clause getClause(Long countryId, Long organizationId, BigInteger id) {
        Clause clause = clauseRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            throw new DataNotFoundByIdException("message.clause.data.not.found.for " + id);
        } else
            return clause;
    }


    /**
     * @description this method updateclause ,and add tags to clause if tag already exist then simply add tag and if not then create tag and then add to clause
     * @param countryId
     * @param organizationId
     * @param clauseId clause id
     * @param clauseDto contain update data for clause
     * @return updated clause object
     * @throws  DataNotFoundByIdException: if clause not found for particular id, {@link DuplicateDataException if clause already exist with same name}
     */
    public Clause updateClause(Long countryId, Long organizationId, BigInteger clauseId, ClauseDTO clauseDto) {

        Clause exists = clauseRepository.findByTitle(countryId, organizationId, clauseDto.getTitle());
        if (Optional.ofNullable(exists).isPresent() && !exists.getId().equals(clauseId)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause", clauseDto.getTitle());
        }
        exists = clauseRepository.findByIdAndNonDeleted(countryId, organizationId, clauseId);
        if (!Optional.ofNullable(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + clauseId);
        }
        List<ClauseTag> tagList =new ArrayList<>();
        exists.setAccountTypes(accountTypeService.getAccountTypeList(countryId, clauseDto.getAccountTypes()));
       // templateTypeService.getTemplateByById(clauseDto.getTemplateType(), countryId);
        try {
            tagList= clauseTagService.addClauseTagAndGetClauseTagList(countryId, organizationId, clauseDto.getTags());
            exists.setOrganizationTypes(clauseDto.getOrganizationTypes());
            exists.setOrganizationSubTypes(clauseDto.getOrganizationSubTypes());
            exists.setOrganizationServices(clauseDto.getOrganizationServices());
            exists.setOrganizationSubServices(clauseDto.getOrganizationSubServices());
            exists.setTitle(clauseDto.getTitle());
            exists.setDescription(clauseDto.getDescription());
            exists.setTags(tagList);
           //exists.setTemplateType(clauseDto.getTemplateType());
           // exists.setOrganizationList(clauseDto.getOrganizationList());
            exists = clauseRepository.save(sequenceGenerator(exists));
        } catch (Exception e) {
            clauseTagMongoRepository.deleteAll(tagList);
            LOGGER.warn(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return exists;
    }


    public List<Clause> getClauseList(Long countryId, Long organizationId, Set<BigInteger> clausesId) {
        return clauseRepository.getClauseListByIds(countryId, organizationId, clausesId);
    }


    /**
     * @description
     * @param countryId
     * @param organizationId
     * @return return caluse with account type basic response,org types ,sub types,service category ,sub service category and tags
     */
    public List<ClauseResponseDTO> getAllClauses(Long countryId, Long organizationId) {
        return clauseRepository.findAllClause(countryId, organizationId);
    }


    /**
     *
     * @param countryId
     * @param organizationId
     * @param id
     * @return bollean true if data deleted successfully
     * @exception DataNotFoundByIdException; if clause not found for id
     */
    public Boolean deleteClause(Long countryId, Long organizationId, BigInteger id) {

        Clause clause = clauseRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(clause).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.clause" + id);
        }
        delete(clause);
        return true;
    }




}
