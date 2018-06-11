package com.kairos.persistance.repository.master_data_management.questionnaire_template;


import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MasterQuestionnaireTemplateMongoRepository extends MongoRepository<MasterQuestionnaireTemplate,BigInteger> {


}
