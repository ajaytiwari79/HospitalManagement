package com.kairos.persistence.repository.open_shift;

import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplate;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface OpenShiftRuleTemplateRepository extends MongoBaseRepository<OpenShiftRuleTemplate,BigInteger>,CustomOpenShiftRuleTemplateRepository {
    OpenShiftRuleTemplate findByIdAndCountryIdAndDeletedFalse(BigInteger id,long countryId);

    List<OpenShiftRuleTemplateDTO> findAllRuleTemplateByCountryIdAndDeletedFalse(long countryId);

    List<OpenShiftRuleTemplate> findAllByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndDeletedFalse(long countryId,long organizationTypeId,long organizationSubTypeId);

    @Query("{'deleted': false, 'unitId' : ?0}")
    List<OpenShiftRuleTemplateDTO> findByUnitIdAndDeletedFalse(long unitId);

    OpenShiftRuleTemplate findByIdAndUnitIdAndDeletedFalse(BigInteger id,long unitId);

    @Query("{'deleted': false,  'activitiesPerTimeTypes.selectedActivities' : ?0, 'unitId' : ?1}")
    List<OpenShiftRuleTemplateDTO> findByUnitIdAndActivityId(BigInteger activityId,Long unitId);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndCountryId(String name,Long countryId);

    OpenShiftRuleTemplateDTO getByIdAndCountryIdAndDeletedFalse(BigInteger id,long countryId);

    OpenShiftRuleTemplateDTO getByIdAndUnitIdAndDeletedFalse(BigInteger id,long unitId);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndUnitId(String name,Long unitId);


}
