package com.kairos.persistence.repository.cta;

import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 30/7/18
 */
@Repository
public interface CTARuleTemplateRepository extends MongoBaseRepository<CTARuleTemplate,BigInteger>{

    @Query(value = "{name:?1,countryId:?0}",exists = true)
    Boolean isCTARuleTemplateExistWithSameName(Long countryId, String name);

    @Query("{countryId:?0,deleted:false}")
    List<CTARuleTemplateDTO> findByCountryIdAndDeletedFalse(Long countryId);


    @Query("{ruleTemplateCategoryId:?0,deleted:false}")
    List<CTARuleTemplate> findAllByCategoryId(BigInteger ruleTemplateCategoryId);

    @Query("{'_id':{'$in':?0},'deleted':false}")
    List<CTARuleTemplate> findAllByIdAndDeletedFalse(List<BigInteger> ids);
}
