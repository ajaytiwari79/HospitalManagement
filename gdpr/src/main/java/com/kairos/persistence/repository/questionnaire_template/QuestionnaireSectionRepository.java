package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface QuestionnaireSectionRepository extends MongoBaseRepository<QuestionnaireSection, BigInteger>,CustomQuestionSectionRepository {


    @Query("{countryId:?0,_id:?1,deleted:false}")
    QuestionnaireSection findByCountryIdAndId(Long countryId, BigInteger id);

    @Query("{countryId:?0,_id:?1,deleted:false}")
    QuestionnaireSection findByUnitIdAndId(Long unitId, BigInteger id);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<QuestionnaireSection> findSectionByCountryIdAndIds(Long countryId, Set<BigInteger> ids);

    @Query("{organizationId:?0,_id:{$in:?1},deleted:false}")
    List<QuestionnaireSection> findSectionByUnitIdAndIds(Long countryId, Set<BigInteger> ids);

    QuestionnaireSection findByid(BigInteger id);


}
