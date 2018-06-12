package com.kairos.persistance.repository.master_data_management.questionnaire_template;


import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterQuestionnaireTemplateMongoRepository extends MongoRepository<MasterQuestionnaireTemplate,BigInteger> ,CustomQuestionnaireTemplateRepository {



    @Query("{deleted:false,countryId:?0,_id:?1}")
    MasterQuestionnaireTemplate findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0,name:?1}")
    MasterQuestionnaireTemplate findByCountryIdAndName(Long countryId,String name);

    MasterQuestionnaireTemplate findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<MasterQuestionnaireTemplateResponseDto> getAllBasicMasterQuestionnaireTemplate(Long countryId);



}
