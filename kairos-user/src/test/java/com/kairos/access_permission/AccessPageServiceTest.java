package com.kairos.access_permission;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.user.auth.StaffPermissionDTO;
import com.kairos.persistence.model.user.auth.StaffPermissionQueryResult;
import com.kairos.service.access_permisson.AccessPageService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by prabjot on 31/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AccessPageServiceTest {

    @Autowired
    AccessPageService accessPageService;

    @Test
    public void permissionShouldNotNull() {
      //  List<StaffPermissionDTO> accessPermission = accessPageService.getPermissionOfUserInUnit(25L, 5431L);
       // System.out.println(accessPermission);
        //Assert.assertNotNull(accessPermission);

    }
}
