package com.kairos.persistance.repository.master_data_management.questionnaire_template;

import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface MasterQuestionMongoRepository extends MongoRepository<MasterQuestion, BigInteger> {

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
