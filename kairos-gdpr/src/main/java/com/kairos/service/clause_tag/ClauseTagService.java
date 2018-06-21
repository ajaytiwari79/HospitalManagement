package com.kairos.service.clause_tag;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.dto.master_data.ClauseTagDto;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
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


    public ClauseTag createClauseTag(Long countryId, String clauseTag) {
        if (StringUtils.isEmpty(clauseTag)) {
            throw new InvalidRequestException("requested paran name is null or empty");

        }
        ClauseTag exist = clauseTagMongoRepository.findByNameAndCountryId(countryId, clauseTag);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("tag already exist for  " + clauseTag);
        } else {
            ClauseTag newClauseTag = new ClauseTag();
            newClauseTag.setName(clauseTag);
            newClauseTag.setCountryId(countryId);
            return save(newClauseTag);
        }
    }


    public List<ClauseTag> getAllClauseTag() {
        List<ClauseTag> result = clauseTagMongoRepository.findAllClauseTag(UserContext.getCountryId());
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("ClauseTag not exist please create purpose ");
    }


    public ClauseTag getClauseTagById(Long countryId, BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("clause tag not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteClauseTagById(BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public ClauseTag updateClauseTag(BigInteger id, String clauseTag) {
        if (StringUtils.isBlank(clauseTag)) {
            throw new InvalidRequestException("requested paran name is null or empty");

        }
        ClauseTag exist = clauseTagMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setName(clauseTag);
            return save(exist);

        }
    }

    //add tags in clause if tag exist then simply add and create new tag and add
    public List<ClauseTag> addClauseTagAndGetClauseTagList(List<ClauseTagDto> tagList) {

        List<ClauseTag> clauseTagList = new ArrayList<>();
        List<BigInteger> existClauseTagIds = new ArrayList<>();
        List<String> clauseTagsName = new ArrayList<>();
        for (ClauseTagDto tagDto : tagList) {

            if (tagDto.getId() == null) {
                if (clauseTagsName.contains(tagDto.getName())) {

                    throw new DuplicateDataException("tags with duplicate name");
                }
                clauseTagsName.add(tagDto.getName());
                ClauseTag newTag = new ClauseTag();
                newTag.setCountryId(UserContext.getCountryId());
                newTag.setName(tagDto.getName());
                clauseTagList.add(newTag);

            } else {
                existClauseTagIds.add(tagDto.getId());
            }
        }
        List<ClauseTag> exists = clauseTagMongoRepository.findTagByNames(UserContext.getCountryId(), clauseTagsName);
        if (exists.size() != 0) {

            throw new DuplicateDataException("tag is already with name " + exists.get(0).getName());
        }
        if (clauseTagList.size() != 0) {
            clauseTagList = save(clauseTagList);
        }
        clauseTagList.addAll(clauseTagMongoRepository.findAllClauseTagByIds(UserContext.getCountryId(), existClauseTagIds));
        return clauseTagList;
    }


}
