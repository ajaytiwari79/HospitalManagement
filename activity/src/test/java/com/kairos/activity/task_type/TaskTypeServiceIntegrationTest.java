package com.kairos.activity.task_type;

import com.kairos.KairosActivityApplication;
import com.kairos.dto.activity.task_type.TaskTypeCopyDTO;
import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.persistence.model.task_type.TaskTypeResource;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.wrapper.task_type.TaskTypeResourceDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.Arrays;
import java.util.List;



/**
 * Created by prabjot on 16/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TaskTypeServiceIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;
    static Long resourceId = 10599L;

    @Test
    public void createCopiesForTaskType(){
        String baseUrl=getBaseUrl(95L,null);

        TaskTypeCopyDTO taskTypeCopyDTO = new TaskTypeCopyDTO(Arrays.asList("Personal care","Health care"));
        HttpEntity<TaskTypeCopyDTO> entity = new HttpEntity<>(taskTypeCopyDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TaskTypeDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TaskTypeDTO>>>(){};
        ResponseEntity<RestTemplateResponseEnvelope<List<TaskTypeDTO>>> response = restTemplate.exchange(
                baseUrl+"/task_type/19/clone",
                HttpMethod.POST, entity, typeReference);
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
        Assert.assertEquals(taskTypeCopyDTO.getTaskTypeNames().size(),response.getBody().getData().size());
    }

    @Test
    public void saveResources(){
        String baseUrl=getBaseUrl(95L,null);
        TaskTypeResourceDTO taskTypeResourceDTO = new TaskTypeResourceDTO();
        TaskTypeResource taskTypeResource = new TaskTypeResource();
        taskTypeResource.setResourceId(resourceId);
        taskTypeResource.setFeatures(Arrays.asList(11368L));
        taskTypeResourceDTO.setResources(Arrays.asList(taskTypeResource));
        HttpEntity<TaskTypeResourceDTO> entity = new HttpEntity<>(taskTypeResourceDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>
                typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>(){};
        ResponseEntity<RestTemplateResponseEnvelope<Object>> response = restTemplate.exchange(
                baseUrl+"/task_types/19/resources",
                HttpMethod.POST, entity, typeReference);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }


    public final String getBaseUrl(Long organizationId,Long unitId){
        if(organizationId!=null &&unitId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();                    ;
            return baseUrl;
        }else if(organizationId!=null){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        }else{
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }

}
