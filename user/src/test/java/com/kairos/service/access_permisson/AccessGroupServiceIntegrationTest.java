package com.kairos.service.access_permisson;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessGroupQueryResult;
import com.kairos.persistence.model.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.service.country.CountryService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.dto.user.access_group.CountryAccessGroupDTO;
import com.kairos.dto.user.access_permission.AccessGroupPermissionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.access_permission.AccessPermissionDTO;
import com.kairos.dto.user.organization.OrganizationCategoryDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by prerna on 5/3/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

public class AccessGroupServiceIntegrationTest {

    private Logger logger = LoggerFactory.getLogger(AccessGroupServiceIntegrationTest.class);

    @Value("${server.host.http.url}")
    private String url ;
    @Inject
    TestRestTemplate restTemplate;

    @Inject AccessGroupService accessGroupService;
    @Inject CountryService countryService;
    @Inject OrganizationService organizationService;

    static String nameOfAccessGroup = "PLANNER";
    static Long createdAccessGroupId = null;
    static Long countryId = null;
    static Long organizationId = null;
    static OrganizationCategory category = OrganizationCategory.HUB;

    @Before
    public void setUp() throws Exception {

        // Fetch country
        Country country = countryService.getCountryByName("Denmark");
        countryId = country == null ? null : country.getId();

        // Fetch parent unit
        Organization org = organizationService.getOneParentUnitByCountry(countryId);
        organizationId = org == null ? null : org.getId();
    }

    @Test
    @OrderTest(order = 1)
    public void createCountryAccessGroup() throws Exception {

        String baseUrl=getBaseUrl(organizationId,countryId, null);
        CountryAccessGroupDTO accessGroupDTO = new CountryAccessGroupDTO(nameOfAccessGroup, null, category, AccessGroupRole.STAFF);
        logger.info("Access Group name : "+accessGroupDTO.getName());
        HttpEntity<CountryAccessGroupDTO> requestBodyData = new HttpEntity<>(accessGroupDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroup>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroup>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<AccessGroup>> response = restTemplate.exchange(
                baseUrl+"/access_group",
                HttpMethod.POST, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if(HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdAccessGroupId = response.getBody().getData().getId();
        }
    }

    @Test
    @OrderTest(order = 2)
    public void updateCountryAccessGroup() throws Exception {

        logger.info("Access Group name : "+nameOfAccessGroup);
        String baseUrl=getBaseUrl(organizationId,countryId, null);

        if(createdAccessGroupId == null){
            logger.info("Access Group Id is null");
            AccessGroup accessGroup = accessGroupService.getCountryAccessGroupByName(countryId, category, nameOfAccessGroup);
            createdAccessGroupId = accessGroup.getId();
        }
        CountryAccessGroupDTO accessGroupDTO = new CountryAccessGroupDTO(nameOfAccessGroup, null, OrganizationCategory.HUB, AccessGroupRole.STAFF);

        HttpEntity<CountryAccessGroupDTO> requestBodyData = new HttpEntity<>(accessGroupDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroup>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroup>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<AccessGroup>> response = restTemplate.exchange(
                baseUrl+"/access_group/"+ createdAccessGroupId,
                HttpMethod.PUT, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }

    @Test
    @OrderTest(order = 3)
    public void deleteCountryAccessGroup() throws Exception {

        if(createdAccessGroupId == null){
            logger.info("Access Group Id is null");
            AccessGroup accessGroup = accessGroupService.getCountryAccessGroupByName(countryId, category, nameOfAccessGroup);
            createdAccessGroupId = accessGroup.getId();
        }
        logger.info("createdAccessGroupId : "+ createdAccessGroupId);
        String baseUrl=getBaseUrl(organizationId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/access_group/"+ createdAccessGroupId,
                HttpMethod.DELETE, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    @OrderTest(order = 4)
    public void getListOfOrgCategoryWithCountryAccessGroupCount() throws Exception {
        String baseUrl=getBaseUrl(organizationId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationCategoryDTO>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationCategoryDTO>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<OrganizationCategoryDTO>>> response = restTemplate.exchange(
                baseUrl+"/organization_category",
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    @OrderTest(order = 5)
    public void getCountryAccessGroups() throws Exception{
        String baseUrl=getBaseUrl(organizationId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationCategoryDTO>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationCategoryDTO>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<OrganizationCategoryDTO>>> response = restTemplate.exchange(
                baseUrl+"/access_group/organization_category/"+category.toString(),
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    @OrderTest(order = 6)
    public void getAccessPageHierarchy() throws Exception{
        String baseUrl=getBaseUrl(organizationId,countryId, null);
        if(createdAccessGroupId == null){
            logger.info("Access Group Id is null");
            AccessGroup accessGroup = accessGroupService.getCountryAccessGroupByName(countryId, category, nameOfAccessGroup);
            createdAccessGroupId = accessGroup.getId();
        }
        logger.info("createdAccessGroupId : "+ createdAccessGroupId);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessPageQueryResult>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessPageQueryResult>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<AccessPageQueryResult>>> response = restTemplate.exchange(
                baseUrl+"/access_group/"+ createdAccessGroupId +"/access_page",
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    @OrderTest(order = 7)
    public void setAccessPagePermissions() throws Exception {

        String baseUrl=getBaseUrl(organizationId,countryId, null);

        if(createdAccessGroupId == null){
            logger.info("Access Group Id is null");
            AccessGroup accessGroup = accessGroupService.getCountryAccessGroupByName(countryId, category, nameOfAccessGroup);
            createdAccessGroupId = accessGroup.getId();
        }
        AccessGroupPermissionDTO accessGroupPermissionDTO = new AccessGroupPermissionDTO();
        accessGroupPermissionDTO.setSelected(true);
        accessGroupPermissionDTO.setAccessPageIds(accessGroupService.getAccessPageIdsByAccessGroup(createdAccessGroupId));

        HttpEntity<AccessGroupPermissionDTO> requestBodyData = new HttpEntity<>(accessGroupPermissionDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/access_group/"+ createdAccessGroupId +"/access_page",
                HttpMethod.PUT, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }


    @Test
    @OrderTest(order = 8)
    public void updatePermissionsForAccessTabsOfAccessGroup() throws Exception {

        String baseUrl=getBaseUrl(organizationId,countryId, null);

        if( !Optional.ofNullable(createdAccessGroupId).isPresent()){
            logger.info("Access Group Id is null");
            AccessGroup accessGroup = accessGroupService.getCountryAccessGroupByName(countryId, category, nameOfAccessGroup);
            createdAccessGroupId = accessGroup.getId();
            if( !Optional.ofNullable(createdAccessGroupId).isPresent() ){
                logger.info("Could not find Access Group");
                return;
            }
        }
        AccessPermissionDTO accessGroupPermissionDTO = new AccessPermissionDTO();
        accessGroupPermissionDTO.setRead(true);
        accessGroupPermissionDTO.setWrite(true);
        Long accessPageId = accessGroupService.getAccessPageIdByAccessGroup(createdAccessGroupId);
        if(accessPageId == null){
            logger.info("No access page linked with Access Group");
            return;
        }
//        accessGroupPermissionDTO.setAccessPageIds(accessGroupService.getAccessPageIdsByAccessGroup(createdAccessGroupId));

        HttpEntity<AccessPermissionDTO> requestBodyData = new HttpEntity<>(accessGroupPermissionDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/access_group/"+ createdAccessGroupId +"/access_page/"+accessPageId+"?updateChildren=false",
                HttpMethod.PUT, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }


    @Test
    @OrderTest(order = 9)
    public void getAccessGroupsToCreateOrganization() throws Exception{
        String baseUrl=getBaseUrl(organizationId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, List<AccessGroupQueryResult>>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, List<AccessGroupQueryResult>>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Map<String, List<AccessGroupQueryResult>>>> response = restTemplate.exchange(
                baseUrl+"/access_group/hub_and_organization",
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
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