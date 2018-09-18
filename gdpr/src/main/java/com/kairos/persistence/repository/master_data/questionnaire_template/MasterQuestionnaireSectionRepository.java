package com.kairos.persistence.repository.master_data.questionnaire_template;


import com.kairos.persistence.model.master_data.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface MasterQuestionnaireSectionRepository extends MongoBaseRepository<MasterQuestionnaireSection, BigInteger>,CustomMasterQuestionSectionRepository {


    @Query("{deleted:false,countryId:?0,title:?1}")
    MasterQuestionnaireSection findMasterQuestionByTitleAndCountryId(Long countryId,String name);

    @Query("{countryId:?0,_id:?1,deleted:false}")
    MasterQuestionnaireSection findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<MasterQuestionnaireSection> getQuestionnaireSectionListByIds(Long countryId,List<BigInteger> ids);

    MasterQuestionnaireSection findByid(BigInteger id);


}
