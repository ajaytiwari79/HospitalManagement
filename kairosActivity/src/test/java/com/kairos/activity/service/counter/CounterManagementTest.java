package com.kairos.activity.service.counter;
import com.kairos.activity.KairosActivityApplication;
import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CounterManagementTest {
    @Inject
    private TestRestTemplate restTemplate;

    @Test
    public void testRestClient(){
        String url = "http://xyz.example.com/kairos/user/api/v1/organization/349/country/4/tab";
        url = "http://xyz.example.com/kairos/user/api/v1/organization/349/unit/349/access_group";
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<Map<String, Object>>>> response = restTemplate
                .exchange(url, HttpMethod.GET, null, typeReference);
                //.exchange(baseUrl + "/activity", HttpMethod.GET, null, typeReference);

        System.out.println("resp: "+response.getBody().getData());
    }
}
