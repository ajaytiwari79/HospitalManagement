package com.kairos.service.clause_tag;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.model.clause_tag.dto.ClauseTagDto;
import com.kairos.persistance.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

        ClauseTag exist = clauseTagMongoRepository.findByName(clauseTag);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("clause tag already exist for  " + clauseTag);
        } else {
            ClauseTag newClauseTag = new ClauseTag();
            newClauseTag.setName(clauseTag);
            return save(newClauseTag);
        }
    }


    public List<ClauseTag> getAllClauseTag() {
        List<ClauseTag> result = clauseTagMongoRepository.findAll();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("ClauseTag not exist please create purpose ");
    }


    public ClauseTag getClauseTagById(BigInteger id) {

        ClauseTag exist = clauseTagMongoRepository.findByid(id);
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
            clauseTagMongoRepository.delete(exist);
            return true;

        }
    }


    public ClauseTag updateClauseTag(BigInteger id, String clauseTag) {

        if (StringUtils.isEmpty(clauseTag)) {
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


    public List<ClauseTag> addClauseTagAndGetClauseTagList(List<ClauseTagDto> tagList) {

        List<ClauseTag> clauseTagList = new ArrayList<>();
        for (ClauseTagDto tagDto : tagList) {


            if (tagDto.getId() == null) {

                if(clauseTagMongoRepository.findByName(tagDto.getName())!=null)
                {
                    throw new DuplicateDataException("tag with name "+tagDto.getName()+" already exist");

                }
                ClauseTag newClauseTag = new ClauseTag();
                newClauseTag.setName(tagDto.getName());
                clauseTagList.add(save(newClauseTag));
                continue;

            }
        clauseTagList.add(getClauseTagById(tagDto.getId()));
        }
        return clauseTagList;

    }


}
