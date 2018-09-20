package com.kairos.persistence.repository.master_data.questionnaire_template;

import com.kairos.persistence.model.master_data.questionnaire_template.MasterQuestion;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterQuestionMongoRepository extends MongoBaseRepository<MasterQuestion, BigInteger> {

    @Query("{countryId:?0,name:?1,deleted:false}")
    MasterQuestion findByNameAndCountryId(Long countryId, String name);

    @Query("{countryId:?0,_id:?1,deleted:false}")
    MasterQuestion findByIdAndNonDeleted(Long countryId, BigInteger id);

    MasterQuestion findByid(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<MasterQuestion> getAllMasterQuestion(Long countryId);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<MasterQuestion> getMasterQuestionListByIds(Long countryId, List<BigInteger> ids);



}
