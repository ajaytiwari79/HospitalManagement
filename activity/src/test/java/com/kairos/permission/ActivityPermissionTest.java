package com.kairos.permission;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.kpermissions.FieldPermissionUserData;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.service.kpermissions.PermissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ActivityPermissionTest {
    private Long unitId;
    @InjectMocks
    private PermissionService permissionService;
    private PermissionService.FieldPermissionHelperDTO fieldPermissionHelperDTO;

    private FieldPermissionUserData fieldPermissionUserData=new FieldPermissionUserData();

    @Before
    public void setUp() {

        List<Activity> dbActivities=new ArrayList<>();
        Activity activity=new Activity();
        activity.setId(new BigInteger("12"));
        activity.setName("team A");

        Activity activity1=new Activity();
        activity.setId(new BigInteger("14"));
        activity.setName("team B");

        dbActivities.add(activity);
        dbActivities.add(activity1);


        unitId = 2403L;
        fieldPermissionHelperDTO=null;//new PermissionService.FieldPermissionHelperDTO(activityDTOS,newHashSet(FieldLevelPermission.READ),fieldPermissionUserData);

    }

    @Test
    public void updatePropertiesBeforeSend() throws NoSuchFieldException, IllegalAccessException {

        List<ActivityDTO> activityDTOS=new ArrayList<>();
        ActivityDTO activityDTO=new ActivityDTO(new BigInteger("12"),"team A",null);
        ActivityDTO activityDTO1=new ActivityDTO(new BigInteger("15"),"team B",null);
        activityDTOS.add(activityDTO);
        activityDTOS.add(activityDTO1);

        //permissionService.updateObjectsPropertiesBeforeSend(fieldPermissionHelperDTO,newHashSet(FieldLevelPermission.READ));
    }

    @Test
    public void updatePropertiesBeforeSave(){

        List<ActivityDTO> activityDTOS=new ArrayList<>();
        ActivityDTO activityDTO=new ActivityDTO(new BigInteger("12"),"team A Changed",null);
        ActivityDTO activityDTO1=new ActivityDTO(new BigInteger("15"),"team B Changed",null);
        activityDTOS.add(activityDTO);
        activityDTOS.add(activityDTO1);
        //permissionService.updateObjectsPropertiesBeforeSave(fieldPermissionHelperDTO,newHashSet(FieldLevelPermission.WRITE));
    }

}
