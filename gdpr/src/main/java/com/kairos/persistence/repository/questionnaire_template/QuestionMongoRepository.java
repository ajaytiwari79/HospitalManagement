package com.kairos.persistence.repository.questionnaire_template;

import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface QuestionMongoRepository extends MongoBaseRepository<Question, BigInteger> {

    @Query("{countryId:?0,name:?1,deleted:false}")
    Question findByNameAndCountryId(Long countryId, String name);

    @Query("{countryId:?0,_id:?1,deleted:false}")
    Question findByCountryIdAndId(Long countryId, BigInteger id);

    Question findByid(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<Question> getAllMasterQuestion(Long countryId);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<Question> getMasterQuestionByCountryIdAndIds(Long countryId, List<BigInteger> questionIds);

    @Query("{organizationId:?0,_id:{$in:?1},deleted:false}")
    List<Question> getQuestionByUnitIdAndIds(Long unitId, List<BigInteger> questionIds);


}
