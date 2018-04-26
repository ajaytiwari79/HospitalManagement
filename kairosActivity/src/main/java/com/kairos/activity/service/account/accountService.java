package com.kairos.activity.service.account;

import com.kairos.activity.client.dto.TimeBankRestClient;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;

@Transactional
@Service
public class accountService {

    @Inject
    private TimeBankRestClient timeBankRestClient;

    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement(Long unitPositionId) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = timeBankRestClient.getCTAbyUnitEmployementPosition(unitPositionId);
        return unitPositionWithCtaDetailsDTO;
    }

    public void savePayAccount(Long unitPositionId, Shift shift){
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
    }
}
