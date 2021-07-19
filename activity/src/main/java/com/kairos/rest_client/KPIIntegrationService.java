package com.kairos.rest_client;

import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.rest_client.RestClientUrlType;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.ApiConstants.*;

@Service
public class KPIIntegrationService {

    @Inject
    private GenericRestClient genericRestClient;


    public List<TabKPIMappingDTO> getTabKPIByTabIdsAndKpiIds(List<String> tabIds, List<BigInteger> kpiIds, Long staffId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tabIds",tabIds);
        requestBody.put("kpiIds",kpiIds);
        requestBody.put("staffId",staffId);
        return genericRestClient.publishRequestToKPIService(requestBody, UserContext.getUserDetails().getLastSelectedOrganizationId(), RestClientUrlType.UNIT, HttpMethod.POST, TAB_AND_KPI, new ArrayList<>(), new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TabKPIMappingDTO>>>() {
        });
    }
    public List<CounterDTO> getAllCounterBySupportedModule(ModuleType openShift) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("moduleType", openShift.toString()));
        return new ArrayList<>();
        //genericRestClient.publishRequestToKPIService(null, UserContext.getUserDetails().getLastSelectedOrganizationId(), RestClientUrlType.UNIT, HttpMethod.GET, STAFF_COUNTER_MODULES, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<CounterDTO>>>() {});
    }
    public void copyKPISets(Long unitId, List<Long> subTypeIds, Long countryId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("countryId",countryId);
        requestBody.put("subTypeIds",subTypeIds);
        genericRestClient.publishRequestToKPIService(null, unitId, RestClientUrlType.UNIT, HttpMethod.POST, DEFAULT_DATA, new ArrayList<>(), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }
}
