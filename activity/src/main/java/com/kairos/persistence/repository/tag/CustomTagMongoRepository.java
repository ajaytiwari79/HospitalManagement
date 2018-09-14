package com.kairos.persistence.repository.tag;

import com.kairos.dto.user.country.tag.TagDTO;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prerna on 7/12/17.
 */
public interface CustomTagMongoRepository {

    public List<TagDTO> getTagsById(List<BigInteger> tagIds);
}
