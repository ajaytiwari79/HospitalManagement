package com.kairos.service.clause_tag;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.dto.master_data.ClauseTagDTO;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.javers.JaversCommonService;
import org.javers.spring.annotation.JaversAuditable;
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


    @Inject
    private JaversCommonService javersCommonService;


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
            return clauseTagMongoRepository.save(sequenceGenerator(newClauseTag));
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


    public ClauseTag updateClauseTag(Long countryId,Long organizationId,BigInteger id, String clauseTag) {
        if (StringUtils.isBlank(clauseTag)) {
            throw new InvalidRequestException("requested paran name is null or empty");
        }
        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        }
        clauseTagMongoRepository.save(exist);
return exist;


    }

    //add tags in clause if tag exist then simply add and create new tag and add
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
            clauseTagList = clauseTagMongoRepository.saveAll(sequenceGenerator(clauseTagList));
        }
        clauseTagList.addAll(clauseTagMongoRepository.findAllClauseTagByIds(countryId, organizationId, existClauseTagIds));
        return clauseTagList;
    }


}
