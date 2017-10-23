package com.kairos.client;

import com.kairos.UserServiceApplication;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by prabjot on 17/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HouseHoldTest {

    @Value("${server.host.http.url}")
    private String url ;

    @Test
    @Ignore
    public void addHouseHold(){
        String baseUrl=getBaseUrl(1L,1L);

    }

    public   final String getBaseUrl(Long organizationId,Long unitId){
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
