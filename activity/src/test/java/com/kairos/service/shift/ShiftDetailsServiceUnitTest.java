package com.kairos.service.shift;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShiftDetailsServiceUnitTest {

    @Mock
    private ShiftMongoRepository shiftMongoRepository;
    @Mock
    private UserIntegrationService userIntegrationService;
    @Mock
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;
    @InjectMocks
    ShiftDetailsService shiftDetailsService;

    Long unitId;
    List<BigInteger> shiftIds;

    @Before
    public void setUp() throws Exception {
        unitId = 958l;
        shiftIds = new ArrayList<BigInteger>() {{
            add(new BigInteger("297"));
        }};

    }

    @Test
    public void shiftDetailsById() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        String jsonString = "{'id':297,'name':null,'activities':[{'startDate':'2019-01-09T17:15:00.000+0000', 'endDate':'2019-01-09T23:00:00.000+0000','activityName':'Devops','absenceReasonCodeId':4,'id':834,'description':null,'wtaRuleViolations':[" +
                "{'ruleTemplateId':462,'name':'Maximum shift length','counter':3}]}],'startDate':'2019-01-09T17:15:00.000+0000'," +
                "'endDate':'2019-01-09T23:00:00.000+0000'}";
        ShiftWithActivityDTO shiftWithActivityDTO = mapper.readValue(jsonString, ShiftWithActivityDTO.class);

        ReasonCodeDTO reasonCodeDTO = new ReasonCodeDTO(4l, "testReasonCode");
        Map<String, Object> contactAddressData = new HashMap<>();
        contactAddressData.put("municipalityId", 20991);
        contactAddressData.put("city", "Glostrup");
        contactAddressData.put("latitude", 0);
        contactAddressData.put("regionName", "hovedstaden");
        contactAddressData.put("houseNumber", "1245");
        contactAddressData.put("province", "Københavns omegn");
        contactAddressData.put("street", "central");
        contactAddressData.put("floorNumber", 0);
        ReasonCodeWrapper reasonCodeWrapper = new ReasonCodeWrapper(Collections.singletonList(reasonCodeDTO), contactAddressData);
        List<NameValuePair> requestParam = new ArrayList<>();
        Set<Long> reasonCodeIds= new HashSet<Long>(){{add(4l);}};
        requestParam.add(new BasicNameValuePair("absenceReasonCodeIds", reasonCodeIds.toString()));
        when(userIntegrationService.getUnitInfoAndReasonCodes(unitId,requestParam)).thenReturn(reasonCodeWrapper);
        when(shiftMongoRepository.findAllShiftsByIds(shiftIds)).thenReturn(Arrays.asList(shiftWithActivityDTO));
        List<ShiftViolatedRules> shiftViolatedRules =new ArrayList<>();
        when(shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds,false)).thenReturn(shiftViolatedRules);
        List<ShiftWithActivityDTO> response=shiftDetailsService.shiftDetailsById(unitId, shiftIds,false);
        Assert.assertEquals(response.get(0).getActivities().get(0).getReasonCode().getId(),reasonCodeDTO.getId());
    }
}