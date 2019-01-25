package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSectionMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface QuestionnaireSectionMDRepository extends JpaRepository<QuestionnaireSectionMD, Long> {

    @Query(value = "SELECT qs FROM QuestionnaireSectionMD qs WHERE qs.id = ?1 and qs.deleted = ?2")
    QuestionnaireSectionMD findByIdAndDeleted(Long id,  boolean deleted);
    /*@Query("{countryId:?0,_id:?1,deleted:false}")
    QuestionnaireSection findByCountryIdAndId(Long countryId, BigInteger id);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    QuestionnaireSection findQuestionnaireSectionByUnitIdAndId(Long unitId, BigInteger questionnaireSectionId);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<QuestionnaireSection> findSectionByCountryIdAndIds(Long countryId, Set<BigInteger> ids);

    @Query("{organizationId:?0,_id:{$in:?1},deleted:false}")
    List<QuestionnaireSection> findSectionByUnitIdAndIds(Long countryId, Set<BigInteger> ids);

    QuestionnaireSection findByid(BigInteger id);*/


}
