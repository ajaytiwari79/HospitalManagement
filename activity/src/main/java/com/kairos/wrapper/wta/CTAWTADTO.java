package com.kairos.wrapper.wta;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;

public class CTAWTADTO {
    private CTAResponseDTO cta;
    private WTAQueryResultDTO wta;

    public WTAQueryResultDTO getWta() {
        return wta;
    }

    public void setWta(WTAQueryResultDTO wta) {
        this.wta = wta;
    }


    public CTAResponseDTO getCta() {
        return cta;
    }

    public void setCta(CTAResponseDTO cta) {
        this.cta = cta;
    }



}
