package com.kairos.service.country.tag;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.response.dto.web.tag.ShowCountryTagSetting;
import com.kairos.response.dto.web.tag.TagDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by prerna on 28/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TagServiceIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    TagService tagService;

    static String nameOfTag = "Test Skill";
    static Long createdTagId ;
    static Long createdOrgTagId;
    static Long orgId = 71L;
    static Long countryId = 53L;
    static Long unitId = 145L;
    static MasterDataTypeEnum masterDataTypeEnum = MasterDataTypeEnum.SKILL;


    @Test
    public void addCountryTag() throws Exception {
        String baseUrl=getBaseUrl(orgId,countryId, null);
        TagDTO tagDTO = new TagDTO(nameOfTag, masterDataTypeEnum);
        HttpEntity<TagDTO> requestBodyData = new HttpEntity<>(tagDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Tag>> response = restTemplate.exchange(
                baseUrl+"/tag",
                HttpMethod.POST, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if(HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdTagId = response.getBody().getData().getId();
        }

    }

    @Test
    public void updateCountryTag() throws Exception {
        if(createdTagId == null){
            logger.info("=======================Tag Id is null");
            Tag tag = tagService.getCountryTagByName(countryId, nameOfTag, masterDataTypeEnum);
            createdTagId = tag.getId();
        }
        String baseUrl=getBaseUrl(orgId,countryId, null);
        TagDTO tagDTO = new TagDTO(createdTagId, nameOfTag, masterDataTypeEnum);
        HttpEntity<TagDTO> requestBodyData = new HttpEntity<>(tagDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Tag>> response = restTemplate.exchange(
                baseUrl+"/tag/"+createdTagId,
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }

    @Test
    public void getListOfCountryTags() throws Exception {
        String baseUrl=getBaseUrl(orgId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<HashMap<String,Object>>> response = restTemplate.exchange(
                baseUrl+"/tag",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    public void deleteCountryTag() throws Exception {

        if(createdTagId == null){
            logger.info("=======================Tag Id is null");
            Tag tag = tagService.getCountryTagByName(countryId, nameOfTag, masterDataTypeEnum);
            createdTagId = tag.getId();
        }
        String baseUrl=getBaseUrl(orgId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/tag/"+createdTagId,
                HttpMethod.DELETE, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    public void addOrganizationTag() throws Exception {
        String baseUrl=getBaseUrl(orgId,null, unitId);
        TagDTO tagDTO = new TagDTO(nameOfTag, masterDataTypeEnum);
        HttpEntity<TagDTO> requestBodyData = new HttpEntity<>(tagDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Tag>> response = restTemplate.exchange(
                baseUrl+"/tag",
                HttpMethod.POST, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if(HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdOrgTagId = response.getBody().getData().getId();
        }

    }

    @Test
    public void updateOrganizationTag() throws Exception {
        if(createdOrgTagId == null){
            logger.info("=======================Org Tag Id is null");
            Tag tag = tagService.getOrganizationTagByName(unitId, nameOfTag, masterDataTypeEnum);
            createdOrgTagId = tag.getId();
        }
        String baseUrl=getBaseUrl(orgId,null, unitId);
        TagDTO tagDTO = new TagDTO(createdOrgTagId, nameOfTag, masterDataTypeEnum);
        HttpEntity<TagDTO> requestBodyData = new HttpEntity<>(tagDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Tag>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Tag>> response = restTemplate.exchange(
                baseUrl+"/tag/"+createdOrgTagId,
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }

    @Test
    public void deleteOrganizationTag() throws Exception {
        if(createdOrgTagId == null){
            logger.info("=======================Org Tag Id is null");
            Tag tag = tagService.getOrganizationTagByName(unitId, nameOfTag, masterDataTypeEnum);
            createdOrgTagId = tag.getId();
        }
        String baseUrl=getBaseUrl(orgId,null, unitId);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/tag/"+createdOrgTagId,
                HttpMethod.DELETE, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    public void getListOfOrganizationTags() throws Exception {
        String baseUrl=getBaseUrl(orgId,null, unitId);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<HashMap<String,Object>>> response = restTemplate.exchange(
                baseUrl+"/tag",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

    }

    @Test
    public void updateShowCountryTagSettingOfOrganization() throws Exception {
        String baseUrl=getBaseUrl(orgId,null, unitId);
        ShowCountryTagSetting showCountryTagSetting = new ShowCountryTagSetting(true);
        HttpEntity<ShowCountryTagSetting> requestBodyData = new HttpEntity<>(showCountryTagSetting);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/tag_setting",
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

   /* @Test
    public void getTagsByIds() throws Exception {

    }

    @Test
    public void getCountryTagsByIdsAndMasterDataType() throws Exception {

    }*/

    /*@Test
    public void getCountryTagsOfSkill() throws Exception {

    }

    @Test
    public void getCountryTagsOfExpertise() throws Exception {

    }

    @Test
    public void getCountryTagsOfWTA() throws Exception {

    }

    @Test
    public void getCountryTagsOfRuleTemplateCategory() throws Exception {

    }*/

    @Test
    public void getListOfMasterDataType() throws Exception {

        String baseUrl=getBaseUrl(orgId,null, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<ArrayList<HashMap<String,String>>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<ArrayList<HashMap<String,String>>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<ArrayList<HashMap<String,String>>>> response = restTemplate.exchange(
                baseUrl+"/tag_category",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    public final String getBaseUrl(Long organizationId,Long countryId, Long unitId){
        if(organizationId!=null && countryId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if(organizationId!=null && unitId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else if(organizationId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else{
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }
}