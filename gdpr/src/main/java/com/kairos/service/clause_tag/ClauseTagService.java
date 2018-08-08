package com.kairos.service.clause_tag;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.dto.master_data.ClauseTagDTO;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.service.common.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClauseTagService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseTagService.class);

    @Inject
    ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private
    MessageSource messageSource;

    /**
     * @param countryId
     * @param organizationId
     * @param clauseTag      tag name
     * @return tag object
     * @description method create tag and if tag already exist with same name then throw exception
     */
    public ClauseTag createClauseTag(Long countryId, Long organizationId, String clauseTag) {
        if (StringUtils.isEmpty(clauseTag)) {
            throw new InvalidRequestException("requested param name is null or empty");
        }
        ClauseTag exist = clauseTagMongoRepository.findByNameAndCountryId(countryId, organizationId, clauseTag);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("tag already exist for  " + clauseTag);
        } else {
            ClauseTag newClauseTag = new ClauseTag();
            newClauseTag.setName(clauseTag);
            newClauseTag.setCountryId(countryId);
            newClauseTag.setOrganizationId(organizationId);
            return clauseTagMongoRepository.save(newClauseTag);
        }
    }


    public List<ClauseTag> getAllClauseTag(Long countryId, Long organizationId) {
        return clauseTagMongoRepository.findAllClauseTag(countryId, organizationId);
    }


    public ClauseTag getClauseTagById(Long countryId, Long organizationId, BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("clause tag not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteClauseTagById(Long countryId, Long organizationId, BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            delete(exist);
            return true;

        }
    }


    public ClauseTag updateClauseTag(Long countryId, Long organizationId, BigInteger id, String clauseTag) {
        if (StringUtils.isBlank(clauseTag)) {
            throw new InvalidRequestException("requested param name is null or empty");
        }
        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        }
        clauseTagMongoRepository.save(exist);
        return exist;


    }

    /**
     * @param countryId
     * @param organizationId
     * @param tagList        list of clause tags
     * @return list of clause Tags
     * @throws DuplicateDataException if tag with same name is present in tagList
     * @description method new create tags and if tag already exist with same name then simply add tag id to  existClauseTagIds which later add to clause ,
     */
    public List<ClauseTag> addClauseTagAndGetClauseTagList(Long countryId, Long organizationId, List<ClauseTagDTO> tagList) {

        List<ClauseTag> clauseTagList = new ArrayList<>();
        List<BigInteger> existClauseTagIds = new ArrayList<>();
        List<String> clauseTagsName = new ArrayList<>();
        for (ClauseTagDTO tagDto : tagList) {

            if (tagDto.getId() == null) {
                if (clauseTagsName.contains(tagDto.getName())) {
                    throw new DuplicateDataException("tags with duplicate name");
                }
                clauseTagsName.add(tagDto.getName());
                ClauseTag newTag = new ClauseTag();
                newTag.setCountryId(countryId);
                newTag.setOrganizationId(organizationId);
                newTag.setName(tagDto.getName());
                clauseTagList.add(newTag);

            } else {
                existClauseTagIds.add(tagDto.getId());
            }
        }
        List<ClauseTag> exists = clauseTagMongoRepository.findTagByNames(countryId, organizationId, clauseTagsName);
        if (exists.size() != 0) {
            throw new DuplicateDataException("tag is already exist with name " + exists.get(0).getName());
        }
        if (clauseTagList.size() != 0) {
            clauseTagList = clauseTagMongoRepository.saveAll(getNextSequence(clauseTagList));
        }
        clauseTagList.addAll(clauseTagMongoRepository.findAllClauseTagByIds(countryId, organizationId, existClauseTagIds));
        return clauseTagList;
    }


}
