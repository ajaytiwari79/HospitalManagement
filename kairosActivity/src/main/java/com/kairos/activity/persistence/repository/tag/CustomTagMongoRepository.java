package com.kairos.activity.persistence.repository.tag;

import com.kairos.activity.response.dto.tag.TagDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prerna on 7/12/17.
 */
public interface CustomTagMongoRepository {

    public List<TagDTO> getTagsById(List<BigInteger> tagIds);
}
