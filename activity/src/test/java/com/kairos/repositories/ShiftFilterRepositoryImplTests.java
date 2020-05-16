//package com.kairos.repositories;
//
//import com.kairos.persistence.model.shift.Shift;
//import com.kairos.persistence.repository.shift.ShiftFilterRepositoryImpl;
//import com.kairos.rest_client.UserIntegrationService;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.inject.Inject;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@RunWith(SpringRunner.class)
//@ContextConfiguration
//@DataMongoTest
//@EntityScan(basePackageClasses = Shift.class)
//public class ShiftFilterRepositoryImplTests {
//
//    @Inject
//    private ShiftFilterRepositoryImpl shiftFilterRepositoryImpl;
//
//    private Long unitId;
//
//
//    @Before
//    public void setup() {
//        unitId = 2403L;
//    }
//
//    @Test
//    public void checkStaffListAsId() {
//        Set<String> dayOffTypes = new HashSet<>();
//        dayOffTypes.add("FULL_DAY");
//        dayOffTypes.add("FULL_WEEK");
//        //Assert.assertEquals(shiftFilterRepositoryImpl.getStaffListAsIdForRealtimeCriteria(unitId, dayOffTypes), List.class);
//    }
//}
