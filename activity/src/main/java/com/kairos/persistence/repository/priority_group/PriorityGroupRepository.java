package com.kairos.persistence.repository.priority_group;

import com.kairos.persistence.model.priority_group.PriorityGroup;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.open_shift.priority_group.PriorityGroupDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface PriorityGroupRepository extends MongoBaseRepository<PriorityGroup,BigInteger>,CustomPriorityGroupRepository {


    PriorityGroup findByIdAndCountryIdAndDeletedFalse(BigInteger priorityGroupId,long countryId);

    PriorityGroup findByIdAndUnitIdAndDeletedFalse(BigInteger priorityGroupId,long unitId);

    boolean existsByCountryId(long countryId);

    PriorityGroupDTO findByIdAndDeletedFalse(BigInteger priorityGroupId);

    List<PriorityGroupDTO> findByUnitIdAndRuleTemplateIdAndOrderIdIsNullAndDeletedFalse(Long unitId,BigInteger ruleTemplateId);

    List<PriorityGroup> findAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(Long countryId);

    List<PriorityGroup> findAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNullAndOrderIdIsNull(long unitId);

    List<PriorityGroupDTO> findByUnitIdAndOrderIdAndDeletedFalse(Long unitId,BigInteger orderId);

    List<PriorityGroupDTO> findByCountryIdAndRuleTemplateIdAndDeletedFalse(Long countryId,BigInteger ruleTemplateId);

    List<PriorityGroupDTO> getAllByCountryIdAndDeletedFalseAndRuleTemplateIdIsNull(Long countryId);

    List<PriorityGroupDTO> getAllByUnitIdAndDeletedFalseAndRuleTemplateIdIsNullAndOrderIdIsNull(long unitId);

    List<PriorityGroup> findAllByRuleTemplateIdInAndCountryIdAndDeletedFalse(Set<BigInteger> ruleTemplateIds,Long countryId);
}
