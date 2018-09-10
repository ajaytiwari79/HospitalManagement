package com.kairos.service.staffing_level;

import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by yatharth on 13/3/18.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffingLevelImportITTest {
    @Autowired
    StaffingLevelService staffingLevelService;
    @Autowired
    StaffingLevelMongoRepository staffingLevelMongoRepository;

    @org.junit.Test
    public void processStaffingLevel() throws IOException {

        MultipartFile multipartFile = new MockMultipartFile("123.csv", new FileInputStream(new File("/home/yatharth/dev/resources/123.csv")));


        staffingLevelService.processStaffingLevel(multipartFile, 163);

        DateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sourceFormat.parse("15-01-2018");
            endDate = sourceFormat.parse("21-01-2018");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.getStaffingLevelsByUnitIdAndDate(new Long(163), startDate, endDate);

        assertTrue(staffingLevels.size() == 7);
        StaffingLevel staffingLevel = staffingLevels.get(5);
        List<StaffingLevelInterval> staffingLevelIntervalList = staffingLevel.getPresenceStaffingLevelInterval();
        Duration duration = null;

        LocalTime excelFrom = LocalTime.of(16, 0);
        for (StaffingLevelInterval interval : staffingLevelIntervalList) {

            duration = interval.getStaffingLevelDuration();
            if (duration.getFrom().compareTo(LocalTime.of(16, 0)) == 0) {
                assertTrue(interval.getMinNoOfStaff() == 8);
                assertTrue(interval.getMaxNoOfStaff() == 0);
            }
        }
    }
}
