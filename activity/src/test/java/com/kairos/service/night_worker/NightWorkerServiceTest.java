package com.kairos.service.night_worker;

import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.enums.CalculationUnit;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NightWorkerServiceTest {

    @InjectMocks
    private NightWorkerService nightWorkerService;
    @Mock
    private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;
    @Mock
    private ShiftMongoRepository shiftMongoRepository;

    Map<Long,Long> employmentAndExpertiseIdMap;
    Map<Long,Long> employmentIdAndStaffIdMap;
    List<ExpertiseNightWorkerSetting> expertiseNightWorkerSettings;
    List<ShiftDTO> shiftDTOS;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }

    @Before
    public void init(){
        employmentAndExpertiseIdMap = new HashMap<>();
        employmentAndExpertiseIdMap.put(145l,156l);
        employmentIdAndStaffIdMap = new HashMap<>();
        employmentIdAndStaffIdMap.put(145l,160l);
        expertiseNightWorkerSettings = newArrayList(new ExpertiseNightWorkerSetting(new TimeSlot(23,7),120,DurationType.WEEKS,1,2760,CalculationUnit.HOURS,180l,156l));
        shiftDTOS = new ArrayList();
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now().minusDays(4), LocalTime.of(15,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(3,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(4), LocalTime.of(15,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(3,0))))));
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now().minusDays(3), LocalTime.of(18,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(23,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(3), LocalTime.of(18,0)),asDate(LocalDate.now().minusDays(3), LocalTime.of(23,0))))));
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now().minusDays(2), LocalTime.of(14,0)),asDate(LocalDate.now().minusDays(1), LocalTime.of(1,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(2), LocalTime.of(14,0)),asDate(LocalDate.now().minusDays(1), LocalTime.of(1,0))))));
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now().minusDays(1), LocalTime.of(12,0)),asDate(LocalDate.now().minusDays(1), LocalTime.of(17,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().minusDays(1), LocalTime.of(12,0)),asDate(LocalDate.now().minusDays(1), LocalTime.of(17,0))))));
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now(), LocalTime.of(7,0)),asDate(LocalDate.now(), LocalTime.of(18,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now(), LocalTime.of(7,0)),asDate(LocalDate.now(), LocalTime.of(18,0))))));
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now().plusDays(1), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(3,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().plusDays(1), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(3,0))))));
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now().plusDays(2), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(21,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().plusDays(2), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(2), LocalTime.of(21,0))))));
        shiftDTOS.add(new ShiftDTO(asDate(LocalDate.now().plusDays(3), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(4), LocalTime.of(2,0)),newArrayList(new ShiftActivityDTO(asDate(LocalDate.now().plusDays(3), LocalTime.of(15,0)),asDate(LocalDate.now().plusDays(4), LocalTime.of(2,0))))));
    }

    @Test
    public void updateNightWorkers(){
        when(expertiseNightWorkerSettingRepository.findAllByExpertiseIdsOfUnit(anyCollection())).thenReturn(expertiseNightWorkerSettings);
        //when(shiftMongoRepository.findAllShiftBetweenDuration(any(Long.class), any(Date.class), any(Date.class))).thenReturn(shiftDTOS);
        Map[] nightWorkerMap = nightWorkerService.getNightWorkerDetails(employmentAndExpertiseIdMap,employmentIdAndStaffIdMap,new HashMap<>(0));
        Map<Long,Boolean> staffIdAndNightWorkerMap = nightWorkerMap[0];
         Assert.assertEquals(staffIdAndNightWorkerMap.get(160l).booleanValue(),false);
        expertiseNightWorkerSettings.get(0).setMinShiftsUnitToCheckNightWorker(CalculationUnit.PERCENTAGE);
        expertiseNightWorkerSettings.get(0).setMinShiftsValueToCheckNightWorker(50);
        nightWorkerMap = nightWorkerService.getNightWorkerDetails(employmentAndExpertiseIdMap,employmentIdAndStaffIdMap,new HashMap<>(0));
        staffIdAndNightWorkerMap = nightWorkerMap[0];
        Assert.assertEquals(staffIdAndNightWorkerMap.get(160l).booleanValue(),false);
    }

    //Negative test case
    @Test
    public void updateNightWorkersWithNegativeDetails(){
        expertiseNightWorkerSettings.get(0).setMinShiftsValueToCheckNightWorker(3000);
        when(expertiseNightWorkerSettingRepository.findAllByExpertiseIdsOfUnit(anyCollection())).thenReturn(expertiseNightWorkerSettings);
        //when(shiftMongoRepository.findAllShiftBetweenDuration(any(Long.class), any(Date.class), any(Date.class))).thenReturn(shiftDTOS);
        Map[] nightWorkerMap = nightWorkerService.getNightWorkerDetails(employmentAndExpertiseIdMap,employmentIdAndStaffIdMap,new HashMap<>(0));
        Map<Long,Boolean> staffIdAndNightWorkerMap = nightWorkerMap[0];
        Assert.assertEquals(staffIdAndNightWorkerMap.get(160l).booleanValue(),false);
        expertiseNightWorkerSettings.get(0).setMinShiftsUnitToCheckNightWorker(CalculationUnit.PERCENTAGE);
        expertiseNightWorkerSettings.get(0).setMinShiftsValueToCheckNightWorker(70);
        nightWorkerMap = nightWorkerService.getNightWorkerDetails(employmentAndExpertiseIdMap,employmentIdAndStaffIdMap,new HashMap<>(0));
        staffIdAndNightWorkerMap = nightWorkerMap[0];
        Assert.assertEquals(staffIdAndNightWorkerMap.get(160l).booleanValue(),false);
    }

}