package com.kairos.persistance.repository.master_data_management.questionnaire_template;


import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface MasterQuestionnaireSectionRepository extends MongoRepository<MasterQuestionnaireSection, BigInteger> ,CustomMasterQuestionSectionRepository {


    @Query("{deleted:false,countryId:?0,organizationId:?1,title:?2}")
    MasterQuestionnaireSection findMasterQuestionByTitleAndCountryId(Long countryId,Long organizationId,String name);

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    MasterQuestionnaireSection findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,_id:{$in:?2},deleted:false}")
    List<MasterQuestionnaireSection> getQuestionnniareSectionListByIds(Long countryId,Long organizationId,List<BigInteger> ids);

    MasterQuestionnaireSection findByid(BigInteger id);


}
