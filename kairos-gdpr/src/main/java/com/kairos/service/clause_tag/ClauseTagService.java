package com.kairos.service.clause_tag;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.dto.ClauseTagDto;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClauseTagService extends MongoBaseService {


    @Inject
    ClauseTagMongoRepository clauseTagMongoRepository;


    public ClauseTag createClauseTag(String clauseTag) {
        if (StringUtils.isEmpty(clauseTag)) {
            throw new InvalidRequestException("requested paran name is null or empty");

        }
        ClauseTag exist = clauseTagMongoRepository.findByName(UserContext.getCountryId(), clauseTag);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("tag already exist for  " + clauseTag);
        } else {
            ClauseTag newClauseTag = new ClauseTag();
            newClauseTag.setName(clauseTag);
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


    public ClauseTag getClauseTagById(Long countryId,BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByIdAndNonDeleted(countryId,id);
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

//add tags in clause and
    public List<ClauseTag> addClauseTagAndGetClauseTagList(List<ClauseTagDto> tagList) {

        List<ClauseTag> clauseTagList = new ArrayList<>();
        List<BigInteger> existClauseTagids = new ArrayList<>();
        List<String> clauseTagsName = new ArrayList<>();
        for (ClauseTagDto tagDto : tagList) {

            if (tagDto.getId() == null) {
                if (clauseTagsName.contains(tagDto.getName())) {

                    throw new DuplicateDataException("tags with duplicate name not excepted");
                }
                ClauseTag newTag = new ClauseTag();
                newTag.setCountryId(UserContext.getCountryId());
                newTag.setName(tagDto.getName());
                clauseTagList.add(newTag);

            }
            existClauseTagids.add(tagDto.getId());
        }
        List<ClauseTag> exists = clauseTagMongoRepository.findClauseTagsByNames(UserContext.getCountryId(), clauseTagsName);
        if (exists.size() != 0) {

            throw new DuplicateDataException("tag is already with name " + exists.get(0).getName());
        }
//add new CLause tags
        clauseTagList = save(clauseTagList);


//add existing clause to response list
        clauseTagList.addAll(clauseTagMongoRepository.findClauseTagsByIds(UserContext.getCountryId(), existClauseTagids));
        return clauseTagList;
    }


}
