package com.kairos.dto.activity.cta;

import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * @author pradeep
 * @date - 8/8/18
 */
@Getter
@Setter
@NoArgsConstructor
public class CTAWTAAndAccumulatedTimebankWrapper {

    private List<CTAResponseDTO> cta = new ArrayList<>();
    private List<WTAResponseDTO> wta = new ArrayList<>();
    //This is a Map of employmentId and employmentLine and AccumulatedTimebank in Minutes
    private Map<Long, Map<Long,Long>> employmentLineAndTimebankMinuteMap;

    public CTAWTAAndAccumulatedTimebankWrapper(List<CTAResponseDTO> cta, List<WTAResponseDTO> wta) {
        this.cta = cta;
        this.wta = wta;
    }

    public Set<BigInteger> getCtaIds() {
        return cta.stream().map(ctaResponseDTO -> ctaResponseDTO.getId()).collect(Collectors.toSet());
    }

    public Set<BigInteger> getWtaIds() {
        return wta.stream().map(wtaResponseDTO -> wtaResponseDTO.getId()).collect(Collectors.toSet());
    }

    public void setEmploymentLineAndTimebankMinuteMap(Map<Long, Map<Long, Long>> employmentLineAndTimebankMinuteMap) {
        this.employmentLineAndTimebankMinuteMap = isNull(employmentLineAndTimebankMinuteMap) ? new HashMap<>() : employmentLineAndTimebankMinuteMap;
    }
}
