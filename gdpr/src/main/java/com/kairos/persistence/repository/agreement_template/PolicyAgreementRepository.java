package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplateMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
////@JaversSpringDataAuditable
public interface PolicyAgreementRepository extends CustomGenericRepository<PolicyAgreementTemplateMD> {

    @Query(value = "Select PAT from PolicyAgreementTemplateMD PAT where PAT.countryId = ?1 and PAT.id = ?2 and PAT.deleted = false")
    List<PolicyAgreementTemplateMD> getAllAgreementSectionsAndSubSectionByCountryIdAndAgreementTemplateId(Long countryId, Long agreementTemplateId);


    @Query(value = "Select PAT from PolicyAgreementTemplateMD PAT where PAT.organizationId = ?1 and PAT.id = ?2 and PAT.deleted = false")
    List<PolicyAgreementTemplateMD> getAllAgreementSectionsAndSubSectionByOrganizationIdAndAgreementTemplateId(Long orgId, Long agreementTemplateId);


}
