package com.kairos.activity.cta;

import com.kairos.activity.wta.basic_details.WTAResponseDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 8/8/18
 */

public class CTAWTAWrapper {

    private List<CTAResponseDTO> cta;
    private List<WTAResponseDTO> wta;


    public CTAWTAWrapper(List<CTAResponseDTO> cta, List<WTAResponseDTO> wta) {
        this.cta = cta;
        this.wta = wta;
    }

    public CTAWTAWrapper() {
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
}
