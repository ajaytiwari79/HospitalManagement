package com.kairos.dto.activity.cta;

import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;

import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * @author pradeep
 * @date - 8/8/18
 */

public class CTAWTAAndAccumulatedTimebankWrapper {

    private List<CTAResponseDTO> cta = new ArrayList<>();
    private List<WTAResponseDTO> wta = new ArrayList<>();
    //This is a Map of employmentId and employmentLine and AccumulatedTimebank in Minutes
    private Map<Long, Map<Long,Long>> employmentLineAndTimebankMinuteMap;

    public CTAWTAAndAccumulatedTimebankWrapper(List<CTAResponseDTO> cta, List<WTAResponseDTO> wta) {
        this.cta = cta;
        this.wta = wta;
    }

    public CTAWTAAndAccumulatedTimebankWrapper() {
    }

    public List<CTAResponseDTO> getCta() {
        return cta;
    }

    public void setCta(List<CTAResponseDTO> cta) {
        this.cta = cta;
    }

    public List<WTAResponseDTO> getWta() {
        return wta;
    }

    public void setWta(List<WTAResponseDTO> wta) {
        this.wta = wta;
    }

    public Map<Long, Map<Long, Long>> getEmploymentLineAndTimebankMinuteMap() {
        return employmentLineAndTimebankMinuteMap;
    }

    public void setEmploymentLineAndTimebankMinuteMap(Map<Long, Map<Long, Long>> employmentLineAndTimebankMinuteMap) {
        this.employmentLineAndTimebankMinuteMap = isNull(employmentLineAndTimebankMinuteMap) ? new HashMap<>() : employmentLineAndTimebankMinuteMap;
    }
}
