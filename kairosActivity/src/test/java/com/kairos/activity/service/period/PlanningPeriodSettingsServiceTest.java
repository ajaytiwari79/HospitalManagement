package com.kairos.activity.service.period;

import com.kairos.activity.KairosActivityApplication;
import com.kairos.activity.config.OrderTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * Created by prerna on 6/4/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PlanningPeriodSettingsServiceTest {

    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @Ignore
    public void createDefaultPeriodSettings() throws Exception {
        String baseUrl = getBaseUrl(71L, null,53L);
    }


    @Test
    public void getPeriodSettings() throws Exception {

    }

    @Test
    public void updatePeriodSettings() throws Exception {

    }

    public final String getBaseUrl(Long organizationId, Long unitId, Long countryId) {
        if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }

}