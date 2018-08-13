package com.kairos.persistance.repository.master_data.questionnaire_template;

import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestion;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterQuestionMongoRepository extends MongoBaseRepository<MasterQuestion, BigInteger> {

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    MasterQuestion findByNameAndCountryId(Long countryId,Long organizationId, String name);

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    MasterQuestion findByIdAndNonDeleted(Long countryId,Long organizationId, BigInteger id);

    MasterQuestion findByid(BigInteger id);

    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<MasterQuestion> getAllMasterQuestion(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,_id:{$in:?2},deleted:false}")
    List<MasterQuestion> getMasterQuestionListByIds(Long countryId,Long organizationId, List<BigInteger> ids);



}
