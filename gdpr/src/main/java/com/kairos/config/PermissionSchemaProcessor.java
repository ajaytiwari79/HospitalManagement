package com.kairos.config;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.rest_client.UserRestClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PermissionSchemaProcessor  implements BeanPostProcessor {

    private UserRestClient userRestClient;

    private String userServiceUrl;

    public PermissionSchemaProcessor(List<Map<String, Object>> data, UserRestClient userRestClient, String userServiceUrl, String kpermissionDataPublish) {
        this.userRestClient = userRestClient;
        this.userServiceUrl = userServiceUrl;
        if("true".equalsIgnoreCase(kpermissionDataPublish)) {
             publishPermissionSchemaToUserService(userRestClient, data);
        }
    }

    private void publishPermissionSchemaToUserService(UserRestClient userRestClient, List<Map<String, Object>> data){
        /*userRestClient.publishRequest(data, userServiceUrl, IntegrationOperation.CREATE, "create_permission_schema", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });*/
    }
}
