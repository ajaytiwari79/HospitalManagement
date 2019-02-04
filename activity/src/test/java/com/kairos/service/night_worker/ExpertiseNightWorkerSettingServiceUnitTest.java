package com.kairos.service.night_worker;

import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.enums.CalculationUnit;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigInteger;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpertiseNightWorkerSettingServiceUnitTest {

    @InjectMocks
    ExpertiseNightWorkerSettingService expertiseNightWorkerSettingService;
    @Mock private ExceptionService exceptionService;
    @Mock private ShiftMongoRepository shiftMongoRepository;
    @Mock private GenericIntegrationService genericIntegrationService;
    @Mock private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;
    @Mock private MongoBaseService mongoBaseService;
    @Mock private MongoTemplate mongoTemplate;
    @Mock private MongoSequenceRepository mongoSequenceRepository;

    ExpertiseNightWorkerSetting expertiseNightWorkerSetting= null;
    ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettingDTO=null;
    @Before
    public void setUp() throws Exception {
        expertiseNightWorkerSetting= new ExpertiseNightWorkerSetting(null,300,DurationType.DAYS,15,15,CalculationUnit.HOURS,null,1075L);
        expertiseNightWorkerSettingDTO=new ExpertiseNightWorkerSettingDTO(null,300,DurationType.DAYS,15,15,CalculationUnit.HOURS,null,1075L);
        expertiseNightWorkerSetting.setId(new BigInteger("10"));
        expertiseNightWorkerSettingDTO.setId(new BigInteger("10"));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getExpertiseNightWorkerSettingsForUnit() {
        when(expertiseNightWorkerSettingRepository.findByExpertiseIdAndUnitId( 1075L,1075L)).thenReturn(expertiseNightWorkerSetting);
        when(expertiseNightWorkerSettingRepository.findByExpertiseIdAndDeletedFalseAndCountryIdExistsTrue( 1075L)).thenReturn(expertiseNightWorkerSetting);
        ExpertiseNightWorkerSettingDTO result=expertiseNightWorkerSettingService.getExpertiseNightWorkerSettingsForUnit(1075L,1075L);
        Assert.assertEquals(expertiseNightWorkerSettingDTO.getExpertiseId(),result.getExpertiseId());

    }

    @Test
    public void updateNightWorkerStatusByUnitId() {
        expertiseNightWorkerSetting.setCountryId(1012L);
        when(expertiseNightWorkerSettingRepository.findOne(new BigInteger("10"))).thenReturn(expertiseNightWorkerSetting);
        when(mongoSequenceRepository.nextSequence("")).thenReturn(new BigInteger("15"));
        when(expertiseNightWorkerSettingRepository.save(expertiseNightWorkerSetting)).thenReturn(expertiseNightWorkerSetting);
        ExpertiseNightWorkerSettingDTO result=expertiseNightWorkerSettingService.updateExpertiseNightWorkerSettingsInUnit(1075L,1075L,expertiseNightWorkerSettingDTO);
        Assert.assertEquals(null,result.getId());


    }
    @Test
    public void updateNightWorkerStatusByUnitId2() {

        expertiseNightWorkerSettingDTO.setIntervalUnitToCheckNightWorker(DurationType.HOURS);
        when(expertiseNightWorkerSettingRepository.findOne(new BigInteger("10"))).thenReturn(expertiseNightWorkerSetting);
        when(expertiseNightWorkerSettingRepository.save(expertiseNightWorkerSetting)).thenReturn(expertiseNightWorkerSetting);
        ExpertiseNightWorkerSettingDTO result=expertiseNightWorkerSettingService.updateExpertiseNightWorkerSettingsInUnit(1075L,1075L,expertiseNightWorkerSettingDTO);
        Assert.assertEquals(DurationType.HOURS,result.getIntervalUnitToCheckNightWorker());


    }


}