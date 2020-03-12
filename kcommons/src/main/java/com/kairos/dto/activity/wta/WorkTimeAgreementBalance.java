package com.kairos.dto.activity.wta;

import com.kairos.enums.TimeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimeAgreementBalance {

    private List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances;

    public List<WorkTimeAgreementRuleTemplateBalancesDTO> getWorkTimeAgreementRuleTemplateBalances() {
        return isNullOrElse(workTimeAgreementRuleTemplateBalances,new ArrayList());
    }

}