package com.kairos.service.country.feature;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.tag.TagDTO;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

/**
 * Created by prerna on 4/12/17.
 */
@Service
@Transactional
public class FeatureService extends UserBaseService{

    @Inject
    CountryGraphRepository countryGraphRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*public Tag addCountryFeature(Long countryId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagGraphRepository.isCountryTagExistsWithSameNameAndDataType(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false) ){
            throw new DuplicateDataException("Tag already exists with same name " +tagDTO.getName() );
        }
        return tagGraphRepository.createCountryTag(countryId,tagDTO.getName(), tagDTO.getMasterDataType().toString(), new Date().getTime());
    }*/
}
