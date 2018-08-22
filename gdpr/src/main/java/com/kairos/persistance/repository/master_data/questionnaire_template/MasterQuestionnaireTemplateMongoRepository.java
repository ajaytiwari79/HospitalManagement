package com.kairos.persistance.repository.master_data.questionnaire_template;


import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface MasterQuestionnaireTemplateMongoRepository extends MongoBaseRepository<MasterQuestionnaireTemplate,BigInteger>,CustomQuestionnaireTemplateRepository {

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    MasterQuestionnaireTemplate findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    MasterQuestionnaireTemplate findByid(BigInteger id);

}
