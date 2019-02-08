package com.kairos.persistence.repository.data_inventory.Assessment;


import com.kairos.custom_exception.JpaCustomDatabaseException;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

public class AssessmentRepositoryImpl implements CustomAssessmentRepository {


    @PersistenceContext
    private EntityManager entityManager;

    private static Logger LOGGER = LoggerFactory.getLogger(AssessmentRepositoryImpl.class);


    private List<AssessmentStatus> assessmentStatusList = Arrays.asList(AssessmentStatus.NEW, AssessmentStatus.IN_PROGRESS);


    @Override
    public List<Assessment> findByOrgIdAndQuestionnaireTemplateIdAndAssessmentStatusNewOrInProgress(Long orgId, Long questionnaireTemplateId) {


        LOGGER.debug("findByOrgIdAndQuestionnaireTemplateIdAndAssessmentStatusNewOrInProgress()   method call");
        try {
            TypedQuery<Assessment> query = entityManager.createQuery("Select As.name from Assessment As " +
                    "where As.deleted = false and As.assessmentStatus in :assessmentStatus and As.organizationId = :orgId" +
                    " and As.questionnaireTemplate.id :questionnaireTemplateId", Assessment.class);
            query.setParameter("questionnaireTemplateId", questionnaireTemplateId);
            query.setParameter("orgId", orgId);
            query.setParameter("assessmentStatus", assessmentStatusList);
            return query.getResultList();

        } catch (Exception e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method findPublishedRiskTemplateByAssociatedEntityAndOrgId()   cause {}", e.getCause());
            throw new JpaCustomDatabaseException(e.getMessage());
        }
    }
}
