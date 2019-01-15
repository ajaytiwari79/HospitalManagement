package com.kairos.service.staffing_level;

import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.phase.PhaseService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class StaffingLevelImportExportUnitTest {

    @InjectMocks
    StaffingLevelService staffingLevelService;

    @Mock
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Mock
    private ActivityService activityTypeService;
    @Mock
    private PhaseService phaseService;
    @Mock
    private EnvConfig envConfig;
    @Mock
    private OrganizationRestClient organizationRestClient;
    @Mock
    private ActivityMongoRepository activityTypeMongoRepository;



    @Test
    public void processStaffingLevel() throws IOException {
        File file = new File("/home/vipul/Downloads/Staffing_levels.csv");

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        staffingLevelService.processStaffingLevel(multipartFile,163);
        assert file.exists();
    }
}
