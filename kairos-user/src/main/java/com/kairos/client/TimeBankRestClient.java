package com.kairos.client;

import com.kairos.client.dto.OrgTaskTypeAggregateResult;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.TimeBankDTO;
import com.kairos.persistence.model.user.position.UnitEmploymentPosition;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.service.position.UnitEmploymentPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.List;

public class TimeBankRestClient {





    private static final Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);

    @Autowired
    private UnitEmploymentPositionService unitEmploymentPositionService;
    @Autowired
    private RestTemplate restTemplate;

    public TimeBankDTO createTimeBank(UnitEmploymentPosition unitEmploymentPosition) {
        try {
            HttpEntity<TimeBankDTO> request = new HttpEntity<>(getTimeBankDTO(unitEmploymentPosition));
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeBankDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeBankDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<TimeBankDTO>> restExchange =
                    restTemplate.exchange(
                            "http://zuulservice/kairos/activity/api/v1/organization/{organizationId}/unit/{unitId}/timeBank/createTimeBank",
                            HttpMethod.POST,request, typeReference);
            RestTemplateResponseEnvelope<TimeBankDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }
    }

    private TimeBankDTO getTimeBankDTO(UnitEmploymentPosition unitEmploymentPosition){
       // float totalWeekHr = unitEmploymentPosition.getTotalWeeklyHours();
        TimeBankDTO timeBankDTO = new TimeBankDTO();
        timeBankDTO.setStaffId(BigInteger.valueOf(unitEmploymentPosition.getStaff().getId()));
        timeBankDTO.setUnitEmpPositionId(unitEmploymentPosition.getId());
        //timeBankDTO.setTotalWeeklyHours(Long.valueOf(Math.round(totalWeekHr)*60));
        timeBankDTO.setWorkingDaysInWeek(unitEmploymentPosition.getWorkingDaysInWeek());
        return timeBankDTO;
    }

}
