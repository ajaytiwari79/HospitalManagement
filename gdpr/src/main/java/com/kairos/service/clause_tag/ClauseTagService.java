package com.kairos.service.clause_tag;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.dto.gdpr.master_data.ClauseTagDTO;
import com.kairos.persistence.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ClauseTagService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseTagService.class);

    @Inject
    ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private
    MessageSource messageSource;

    @Inject
    private ExceptionService exceptionService;

    /**
     * @param countryId
     * @param clauseTag tag name
     * @return tag object
     * @description method create tag and if tag already exist with same name then throw exception
     */
    public ClauseTag createClauseTag(Long countryId, String clauseTag) {
        if (StringUtils.isEmpty(clauseTag)) {
            throw new InvalidRequestException("requested param name is null or empty");
        }
        ClauseTag exist = clauseTagMongoRepository.findByNameAndCountryId(countryId, clauseTag);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("tag already exist for  " + clauseTag);
        } else {
            ClauseTag newClauseTag = new ClauseTag();
            newClauseTag.setName(clauseTag);
            newClauseTag.setCountryId(countryId);
            return clauseTagMongoRepository.save(newClauseTag);
        }
    }


    public List<ClauseTag> getAllClauseTagByCountryId(Long countryId) {
        return clauseTagMongoRepository.findAllClauseTagByCountryId(countryId);
    }

    public List<ClauseTag> getAllClauseTagByUnitId(Long unitId) {
        return clauseTagMongoRepository.findAllClauseTagByUnitId(unitId);
    }


    public ClauseTag getClauseTagById(Long countryId, BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("clause tag not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteClauseTagById(Long countryId, BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            delete(exist);
            return true;

        }
    }


    public ClauseTag updateClauseTag(Long countryId, BigInteger id, String clauseTag) {
        if (StringUtils.isBlank(clauseTag)) {
            throw new InvalidRequestException("requested param name is null or empty");
        }
        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        }
        clauseTagMongoRepository.save(exist);
        return exist;


    }

    /**
     * @param referenceId
     * @param tagList     list of clause tags
     * @return list of clause Tags
     * @throws DuplicateDataException if tag with same name is present in tagList
     * @description method new create tags and if tag already exist with same name then simply add tag id to  existClauseTagIds which later add to clause ,
     */
    public List<ClauseTag> saveClauseTagList(Long referenceId, boolean isUnitId, List<ClauseTagDTO> tagList) {

        List<ClauseTag> clauseTagList = new ArrayList<>();
        List<BigInteger> existClauseTagIds = new ArrayList<>();
        Set<String> clauseTagsName = new HashSet<>();
        for (ClauseTagDTO tagDto : tagList) {
            if (tagDto.getId() == null) {
                if (clauseTagsName.contains(tagDto.getName())) {
                    exceptionService.duplicateDataException("message.duplicate", "message.tag", tagDto.getName());
                }
                clauseTagsName.add(tagDto.getName());
                ClauseTag clauseTag = new ClauseTag(tagDto.getName());
                if (isUnitId)
                    clauseTag.setOrganizationId(referenceId);
                else
                    clauseTag.setCountryId(referenceId);
                clauseTagList.add(clauseTag);

            } else {
                existClauseTagIds.add(tagDto.getId());
            }
        }
        List<ClauseTag> previousClauseTags = isUnitId ? clauseTagMongoRepository.findByUnitIdAndTitles(referenceId, clauseTagsName) : clauseTagMongoRepository.findByCountryIdAndTitles(referenceId, clauseTagsName);
        if (CollectionUtils.isNotEmpty(previousClauseTags)) {
            exceptionService.duplicateDataException("message.duplicate", "message.tag", previousClauseTags.get(0).getName());
        }
        if (CollectionUtils.isNotEmpty(clauseTagList)) {
            clauseTagList = clauseTagMongoRepository.saveAll(getNextSequence(clauseTagList));
        }
        clauseTagList.addAll(clauseTagMongoRepository.findAllClauseTagByIds( existClauseTagIds));
        return clauseTagList;
    }


}
