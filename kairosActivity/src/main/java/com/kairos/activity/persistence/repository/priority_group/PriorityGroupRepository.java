package com.kairos.activity.persistence.repository.priority_group;

import com.kairos.activity.persistence.model.priority_group.PriorityGroup;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface PriorityGroupRepository extends MongoBaseRepository<PriorityGroup,BigInteger>,CustomPriorityGroupRepository {

    List<PriorityGroupDTO> findByCountryIdAndDeletedFalse(long countryId);

    PriorityGroup findByIdAndCountryIdAndDeletedFalse(BigInteger priorityGroupId,long countryId);

    List<PriorityGroup> findAllByCountryIdAndDeActivatedFalseAndDeletedFalse(long countryId);

    List<PriorityGroupDTO> findByUnitIdAndDeletedFalse(long unitId);

    PriorityGroup findByIdAndUnitIdAndDeletedFalse(BigInteger priorityGroupId,long unitId);

    boolean existsByCountryId(long countryId);

    List<PriorityGroup> findAllByUnitIdAndDeActivatedFalseAndDeletedFalse(long unitId);

    PriorityGroupDTO findByIdAndDeletedFalse(BigInteger priorityGroupId);

    List<PriorityGroupDTO> findByUnitIdAndRuleTemplateIdAndDeletedFalse(Long unitId,BigInteger ruleTemplateId);

    List<PriorityGroup> findAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(Long countryId);

    List<PriorityGroup> findAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNullAndOrderIdIsNull(long unitId);

    List<PriorityGroupDTO> findByUnitIdAndOrderIdAndDeletedFalse(Long unitId,BigInteger orderId);

    List<PriorityGroupDTO> findByCountryIdAndRuleTemplateIdAndDeletedFalse(Long countryId,BigInteger ruleTemplateId);

    List<PriorityGroupDTO> getAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(Long countryId);

    List<PriorityGroupDTO> getAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(long unitId);
}
