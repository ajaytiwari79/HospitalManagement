//package com.kairos.activity;
//
//import com.kairos.persistence.repository.activity.ActivityMongoRepository;
//import com.kairos.wrapper.activity.ActivityTimeTypeWrapper;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.hasSize;
//
////@RunWith(SpringRunner.class)
////@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
////@ActiveProfiles("test")
//public class KairosActivityApplicationTests {
//
//	@Autowired
//	private ActivityMongoRepository activityMongoRepository;
//
//	@Test
//	public void checkIfActivityTimeTypePathIsFound() {
//		String activityId = "10";
//		List<ActivityTimeTypeWrapper> timeTypeWrappers = activityMongoRepository.getActivityPath(activityId);
//		assertThat(timeTypeWrappers, hasSize(1));
//	}
//
//}
