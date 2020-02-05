package com.kairos.service.filter;

import com.kairos.dto.activity.common.StaffFilterDataDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.wta.WorkTimeAgreementService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created By G.P.Ranjan on 15/1/20
 **/
@Service
public class StaffFilterService {
    @Inject
    private WorkTimeAgreementService workTimeAgreementService;
    @Inject
    private CostTimeAgreementService costTimeAgreementService;

    public StaffFilterDataDTO getAllFilterData(long unitId) {
        List<WTAResponseDTO> wtadtos = workTimeAgreementService.getAllWTAByUnitId(unitId);
        List<CTAResponseDTO> ctadtos = costTimeAgreementService.getAllCTAByUnitId(unitId);
        return new StaffFilterDataDTO(wtadtos,ctadtos);
    }
}
