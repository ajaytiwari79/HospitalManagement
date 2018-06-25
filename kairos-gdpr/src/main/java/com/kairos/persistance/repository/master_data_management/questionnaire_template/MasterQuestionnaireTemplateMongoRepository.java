package com.kairos.persistance.repository.master_data_management.questionnaire_template;


import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MasterQuestionnaireTemplateMongoRepository extends MongoRepository<MasterQuestionnaireTemplate,BigInteger> ,CustomQuestionnaireTemplateRepository {

    @Query("{deleted:false,countryId:?0,_id:?1}")
    MasterQuestionnaireTemplate findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{'name':{$regex:?1,$options:'i'}, 'deleted':false, 'countryId':?0}")
    MasterQuestionnaireTemplate findByCountryIdAndName(Long countryId,String name);

    MasterQuestionnaireTemplate findByid(BigInteger id);

}
