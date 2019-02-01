package com.kairos.service.clause_tag;

import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.master_data.ClauseTagDTO;
import com.kairos.persistence.model.clause_tag.ClauseTagMD;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClauseTagService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseTagService.class);

    @Inject
    private
    MessageSource messageSource;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseTagRepository clauseTagRepository;

    /**
     * @param countryId
     * @param //clauseTag tag name
     * @return tag object
     * @description method create tag and if tag already exist with same name then throw exception
     */
    //TODO
    /*public ClauseTag createClauseTag(Long countryId, String clauseTag) {
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
    }*/


    public List<ClauseTagMD> getAllClauseTagByCountryId(Long countryId) {
        return clauseTagRepository.findAllByCountryId(countryId);
    }

    public List<ClauseTagMD> getAllClauseTagByUnitId(Long unitId) {
        return clauseTagRepository.findAllClauseTagByUnitId(unitId);
    }

//TODO
    /*public ClauseTag getClauseTagById(Long countryId, BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("clause tag not exist for id " + id);
        } else {
            return exist;

        }
    }*/


   /* public Boolean deleteClauseTagById(Long countryId, BigInteger id) {

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
*/
    /**
     * @param referenceId
     * @param tagList     list of clause tags
     * @return list of clause Tags
     * @throws DuplicateDataException if tag with same name is present in tagList
     * @description method new create tags and if tag already exist with same name then simply add tag id to  existClauseTagIds which later add to clause ,
     */
    public List<ClauseTagMD> saveClauseTagList(Long referenceId, boolean isUnitId, List<ClauseTagDTO> tagList) {

        List<ClauseTagMD> clauseTagList = new ArrayList<>();
        List<Long> existClauseTagIds = new ArrayList<>();
        Set<String> clauseTagsName = new HashSet<>();
        for (ClauseTagDTO tagDto : tagList) {
            if (tagDto.getId() == null) {
                if (clauseTagsName.contains(tagDto.getName())) {
                    exceptionService.duplicateDataException("message.duplicate", "message.tag", tagDto.getName());
                }
                clauseTagsName.add(tagDto.getName());
                ClauseTagMD clauseTag = new ClauseTagMD(tagDto.getName());
                if (isUnitId)
                    clauseTag.setOrganizationId(referenceId);
                else
                    clauseTag.setCountryId(referenceId);
                clauseTagList.add(clauseTag);

            } else {
                existClauseTagIds.add(tagDto.getId());
            }
        }
        if(!clauseTagsName.isEmpty()){
        Set<String> nameInLowerCase = clauseTagsName.stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
        List<ClauseTagMD> previousClauseTags = isUnitId ? clauseTagRepository.findByUnitIdAndTitles(referenceId, nameInLowerCase) : clauseTagRepository.findByCountryIdAndTitles(referenceId, nameInLowerCase);
        if (CollectionUtils.isNotEmpty(previousClauseTags)) {
            exceptionService.duplicateDataException("message.duplicate", "message.tag", previousClauseTags.get(0).getName());
        }}
        if(!existClauseTagIds.isEmpty()) {
            clauseTagList.addAll(clauseTagRepository.findAllClauseTagByIds(existClauseTagIds));
        }
        return clauseTagList;
    }


}
