package com.kairos.persistence.repository.wta.rule_template;


import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by pawanmandhan on 5/8/17.
 */
@Repository
public interface WTABaseRuleTemplateMongoRepository extends MongoBaseRepository<WTABaseRuleTemplate, BigInteger> {

    @Query("{ruleTemplateCategoryId:?0,deleted:false}")
    List<WTABaseRuleTemplate> findAllByCategoryId(BigInteger categoryId);

     @Query("{countryId:?0,deleted:false}")
    List<WTABaseRuleTemplate> getWTABaseRuleTemplateByCountryId(Long countryId);


    @Query("{countryId:?0,name:?1,deleted:false}")
    WTABaseRuleTemplate existsByName(Long countryId, String name);

    List<WTABaseRuleTemplateDTO> findAllByIdIn(List<BigInteger> ids);

}
