package com.kairos.service.counter;


import com.kairos.KairosActivityApplication;
import com.kairos.activity.counter.DefalutKPISettingDTO;
import com.kairos.activity.counter.KPIDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.activity.counter.distribution.category.InitialKPICategoryDistDataDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.activity.counter.distribution.tab.KPIPosition;
import com.kairos.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.activity.enums.counter.CounterSize;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CounterDistIntegrationTest {
    private Logger logger = LoggerFactory.getLogger(CounterDistService.class);
    @Autowired
    ExceptionService exceptionService;
    @org.springframework.beans.factory.annotation.Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    public void getAvailableKPIsListForCountry() {
        String baseUrl = getBaseUrl(2567l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<KPIDTO>>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/counters/", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getAvailableKPIsListForUnit() {
        String baseUrl = getBaseUrl(2567l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<KPIDTO>>> response = testRestTemplate.exchange(baseUrl + "/unit/19449/counter/dist/counters/", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getAvailableKPIsListForStaff() {
        String baseUrl = getBaseUrl(2567l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<KPIDTO>>> response = testRestTemplate.exchange(baseUrl + "/unit/4/staff/801/counter/dist/counters/", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }
    //category kpi test case

    @Test
    public void getInitialCategoryKPIDistributionDataForCountry() {
        String baseUrl = getBaseUrl(2567l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<InitialKPICategoryDistDataDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<InitialKPICategoryDistDataDTO>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<InitialKPICategoryDistDataDTO>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/category", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getInitialCategoryKPIDistributionDataForUnit() {
        String baseUrl = getBaseUrl(2567l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<InitialKPICategoryDistDataDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<InitialKPICategoryDistDataDTO>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<InitialKPICategoryDistDataDTO>> response = testRestTemplate.exchange(baseUrl + "/unit/4/counter/dist/category", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void saveCategoryKPIDistributionForCountry() {
        String baseUrl = getBaseUrl(2567l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>>() {
        };
        CategoryKPIsDTO categoryKPIsDTO=new CategoryKPIsDTO(BigInteger.valueOf(15),Arrays.asList(BigInteger.valueOf(29)));
        HttpEntity<CategoryKPIsDTO> requestBodyData=new HttpEntity<>(categoryKPIsDTO);
        ResponseEntity<RestTemplateResponseEnvelope<List<KPIDTO>>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/category", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void saveCategoryKPIDistributionUnit() {
        String baseUrl = getBaseUrl(2567l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIDTO>>>() {
        };
        CategoryKPIsDTO categoryKPIsDTO=new CategoryKPIsDTO();
        HttpEntity<CategoryKPIsDTO> requestBodyData=new HttpEntity<>(categoryKPIsDTO);
        ResponseEntity<RestTemplateResponseEnvelope<List<KPIDTO>>> response = testRestTemplate.exchange(baseUrl + "/unit/4/counter/dist/category", HttpMethod.GET, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }



    // tab kpi api test case
    @Test
    public void getInitialTabKPIDistConfForCountry() {
        String baseUrl = getBaseUrl(256l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<BigInteger>>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/module/1", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getInitialTabKPIDistConfForUnit() {
        String baseUrl = getBaseUrl(253l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<BigInteger>>> response = testRestTemplate.exchange(baseUrl + "/unit/13347/counter/dist/module/1", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getInitialTabKPIDistConfForStaff() {
        String baseUrl = getBaseUrl(253l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<BigInteger>>> response = testRestTemplate.exchange(baseUrl + "/staff/13348/counter/dist/module/1", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addTabKPIsEntryForCounty() {
        String baseUrl = getBaseUrl(152l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        TabKPIEntryConfDTO tabKPIEntryConfDTO = new TabKPIEntryConfDTO(Arrays.asList("1"), Arrays.asList(BigInteger.valueOf(1)));
        HttpEntity<TabKPIEntryConfDTO> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/module/create_dist_entry", HttpMethod.POST, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addTabKPIsEntryForUnit() {
        String baseUrl = getBaseUrl(152l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        TabKPIEntryConfDTO tabKPIEntryConfDTO = new TabKPIEntryConfDTO(Arrays.asList("1"), Arrays.asList(BigInteger.valueOf(1)));
        HttpEntity<TabKPIEntryConfDTO> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/unit/1245/counter/dist/module/create_dist_entry", HttpMethod.POST, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addTabKPIsEntryForStaff() {
        String baseUrl = getBaseUrl(152l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        };
        TabKPIEntryConfDTO tabKPIEntryConfDTO = new TabKPIEntryConfDTO(Arrays.asList("1"), Arrays.asList(BigInteger.valueOf(1)));
        HttpEntity<TabKPIEntryConfDTO> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = testRestTemplate.exchange(baseUrl + "/unit/1452/staff/1245/counter/dist/module/create_dist_entry", HttpMethod.POST, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void updateTabKPIsEntryForCounty() {
        String baseUrl = getBaseUrl(152l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        };
        List<TabKPIMappingDTO> tabKPIEntryConfDTO =new ArrayList<>();
        tabKPIEntryConfDTO.add(new TabKPIMappingDTO("1",BigInteger.valueOf(23), CounterSize.SIZE_2X2,new KPIPosition(1,4)));
        HttpEntity<List<TabKPIMappingDTO>> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/module/remove_dist_entry", HttpMethod.PUT, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }
    @Test
    public void updateTabKPIsEntryForUnit() {
        String baseUrl = getBaseUrl(152l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        };
        List<TabKPIMappingDTO> tabKPIEntryConfDTO =new ArrayList<>();
        tabKPIEntryConfDTO.add(new TabKPIMappingDTO("1",BigInteger.valueOf(23), CounterSize.SIZE_2X2,new KPIPosition(1,4)));
        HttpEntity<List<TabKPIMappingDTO>> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/module/remove_dist_entry", HttpMethod.PUT, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }
    @Test
    public void updateTabKPIsEntryForStaff() {
        String baseUrl = getBaseUrl(152l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        };
        List<TabKPIMappingDTO> tabKPIEntryConfDTO =new ArrayList<>();
        tabKPIEntryConfDTO.add(new TabKPIMappingDTO("1",BigInteger.valueOf(23), CounterSize.SIZE_2X2,new KPIPosition(1,4)));
        HttpEntity<List<TabKPIMappingDTO>> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/module/remove_dist_entry", HttpMethod.PUT, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void removeTabKPIEntryForCountry() {
        String baseUrl = getBaseUrl(152l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        };
        TabKPIMappingDTO tabKPIEntryConfDTO = new TabKPIMappingDTO("1", BigInteger.valueOf(1));
        HttpEntity<TabKPIMappingDTO> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/module/remove_dist_entry", HttpMethod.PUT, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void removeTabKPIEntryForUnit() {
        String baseUrl = getBaseUrl(152l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        };
        TabKPIMappingDTO tabKPIEntryConfDTO = new TabKPIMappingDTO("1", BigInteger.valueOf(1));
        HttpEntity<TabKPIMappingDTO> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = testRestTemplate.exchange(baseUrl + "/unit/1452/staff/1245/counter/dist/module/create_dist_entry", HttpMethod.PUT, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void removeTabKPIEntryForStaff() {
        String baseUrl = getBaseUrl(152l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        };
        TabKPIMappingDTO tabKPIEntryConfDTO = new TabKPIMappingDTO("1", BigInteger.valueOf(25));
        HttpEntity<TabKPIMappingDTO> reqestBodyDate = new HttpEntity<>(tabKPIEntryConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = testRestTemplate.exchange(baseUrl + "/staff/13348/counter/dist/module/remove_dist_entry", HttpMethod.PUT, reqestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    //organization type KPI test Case

    @Test
    public void getInitialDataForOrgTypeKPIConf() {
        String baseUrl = getBaseUrl(153l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<BigInteger>>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/org_type/345", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addOrgTypeKPIEntry() {
        String baseUrl = getBaseUrl(15l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        OrgTypeKPIConfDTO orgTypeKPIConfDTO = new OrgTypeKPIConfDTO(Arrays.asList(14108l), Arrays.asList(BigInteger.valueOf(1)));
        HttpEntity<OrgTypeKPIConfDTO> requestBodyDate = new HttpEntity<>(orgTypeKPIConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/org_type/create_dist_entry", HttpMethod.POST, requestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void removeOrgTypeKPIEntry() {
        String baseUrl = getBaseUrl(15l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        OrgTypeMappingDTO orgTypeMappingDTO = new OrgTypeMappingDTO(345l, BigInteger.valueOf(26));
        HttpEntity<OrgTypeMappingDTO> requestBodyDate = new HttpEntity<>(orgTypeMappingDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/org_type/remove_dist_entry", HttpMethod.PUT, requestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    // accessGroup KPI Test case

    @Test
    public void getInitialDataForAccessGroupKPIConfOfCountry() {
        String baseUrl = getBaseUrl(15l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<BigInteger>>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/access_group/82", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getInitialDataForAccessgroupKPIConfOfUnit(){
        String baseUrl = getBaseUrl(15l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BigInteger>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<BigInteger>>> response = testRestTemplate.exchange(baseUrl + "/unit/4/counter/dist/access_group/10", HttpMethod.GET, null, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addAccessGroupKPIEntryForCountry(){
        String baseUrl = getBaseUrl(15l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        AccessGroupKPIConfDTO accessGroupKPIConfDTO = new AccessGroupKPIConfDTO(Arrays.asList(14108l), Arrays.asList(BigInteger.valueOf(1)));
        HttpEntity<AccessGroupKPIConfDTO> requestBodyDate = new HttpEntity<>(accessGroupKPIConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/access_group/create_dist_entry", HttpMethod.POST, requestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addAccessGroupKPIEntryForUnit(){
        String baseUrl = getBaseUrl(15l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        AccessGroupKPIConfDTO accessGroupKPIConfDTO = new AccessGroupKPIConfDTO(Arrays.asList(14108l), Arrays.asList(BigInteger.valueOf(1)));
        HttpEntity<AccessGroupKPIConfDTO> requestBodyData = new HttpEntity<>(accessGroupKPIConfDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/unit/4/counter/dist/access_group/create_dist_entry", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void removeAccessGroupKPIEntryByCounty(){
        String baseUrl = getBaseUrl(15l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        AccessGroupMappingDTO accessGroupMappingDTO=new AccessGroupMappingDTO(83l,BigInteger.valueOf(25));
        HttpEntity<AccessGroupMappingDTO> requestBodyData=new HttpEntity<>(accessGroupMappingDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/counter/dist/access_group/remove_dist_entry", HttpMethod.PUT, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void removeAccessGroupKPIEntryByUnit(){
        String baseUrl = getBaseUrl(15l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        AccessGroupMappingDTO accessGroupMappingDTO=new AccessGroupMappingDTO(19413l,BigInteger.valueOf(25));
        HttpEntity<AccessGroupMappingDTO> requestBodyData=new HttpEntity<>(accessGroupMappingDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/unit/19394/counter/dist/access_group/remove_dist_entry", HttpMethod.PUT, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    //defalut setting api test case

    @Test
    public void createDefaluSettingForUnit(){
        String baseUrl = getBaseUrl(15l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        Map<Long, Long> countryAndOrgAccessGroupIdsMap=new HashMap<>();
        countryAndOrgAccessGroupIdsMap.put(82l,101101l);
        DefalutKPISettingDTO defalutKPISettingDTO=new DefalutKPISettingDTO(Arrays.asList(14108l),4l,19449l,countryAndOrgAccessGroupIdsMap);
        HttpEntity<DefalutKPISettingDTO> requestBodyData = new HttpEntity<>(defalutKPISettingDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/unit/404/counter/dist/default_kpi_setting", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void createDefaluSettingForStaff(){
        String baseUrl = getBaseUrl(15l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<String>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        };
        DefalutKPISettingDTO defalutKPISettingDTO=new DefalutKPISettingDTO(Arrays.asList(801l));
        HttpEntity<DefalutKPISettingDTO> requestBodyData = new HttpEntity<>(defalutKPISettingDTO);
        ResponseEntity<RestTemplateResponseEnvelope<String>> response = testRestTemplate.exchange(baseUrl + "/unit/19394/counter/dist/staff_default_kpi_setting", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    //getbaseurl
    public final String getBaseUrl(Long organizationId, Long countryId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }


}
