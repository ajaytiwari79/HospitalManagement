package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShiftRuleTemplate;
import com.kairos.activity.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;
import java.util.List;

public interface OpenShiftRuleTemplateRepository extends MongoBaseRepository<OpenShiftRuleTemplate,BigInteger> {
    OpenShiftRuleTemplate findByIdAndCountryIdAndDeletedFalse(BigInteger id,long countryId);

    List<OpenShiftRuleTemplateDTO> findAllRuleTemplateByCountryIdAndDeletedFalse(long countryId);

    List<OpenShiftRuleTemplate> findAllByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndDeletedFalse(long countryId,long organizationTypeId,long organizationSubTypeId);

    List<OpenShiftRuleTemplateDTO> findByUnitIdAndDeletedFalse(long unitId);

    OpenShiftRuleTemplate findByIdAndUnitIdAndDeletedFalse(BigInteger id,long unitId);


}
