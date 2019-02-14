package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface PolicyAgreementRepository extends CustomGenericRepository<PolicyAgreementTemplate> {

    @Query(value = "Select PAT from PolicyAgreementTemplate PAT where PAT.countryId = ?1 and PAT.id = ?2 and PAT.deleted = false")
    List<PolicyAgreementTemplate> getAllAgreementSectionsAndSubSectionByCountryIdAndAgreementTemplateId(Long countryId, Long agreementTemplateId);


    @Query(value = "Select PAT from PolicyAgreementTemplate PAT where PAT.organizationId = ?1 and PAT.id = ?2 and PAT.deleted = false")
    List<PolicyAgreementTemplate> getAllAgreementSectionsAndSubSectionByOrganizationIdAndAgreementTemplateId(Long orgId, Long agreementTemplateId);


}
