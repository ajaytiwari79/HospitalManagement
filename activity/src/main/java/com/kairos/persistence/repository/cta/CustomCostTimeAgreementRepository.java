package com.kairos.persistence.repository.cta;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 3/8/18
 */

public interface CustomCostTimeAgreementRepository {

    CTAResponseDTO getOneCtaById(BigInteger ctaId);
}
