package com.kairos.service.payroll_setting;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_setting.PayRollAccessGroupSettingDTO;
import com.kairos.dto.activity.payroll_setting.PayRollPeriodSettingDTO;
import com.kairos.dto.activity.payroll_setting.PayrollSettingDTO;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.payroll_setting.PayrollAccessGroups;
import com.kairos.persistence.model.payroll_setting.PayrollPeriod;
import com.kairos.persistence.model.payroll_setting.PayrollSetting;
import com.kairos.persistence.repository.payroll_setting.PayrollSettingMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.DurationType.DAYS;

@Service
public class PayrollSettingService extends MongoBaseService {

    @Inject
    private PayrollSettingMongoRepository payrollSettingMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    public List<LocalDate> getLocalDatesOfPayrollPeriod(Long unitId){
        List<PayrollSetting> payrollSettings=payrollSettingMongoRepository.findAllByunitId(unitId);
        return isCollectionNotEmpty(payrollSettings)?payrollSettings.stream().distinct().map(payrollSetting -> payrollSetting.getPayrollPeriods().get(0).getStartDate()).collect(Collectors.toList()):new ArrayList<>();
    }

    public List<PayrollSettingDTO> getPayrollPeriodByUnitIdAndDateAndDurationType(Long unitId,LocalDate startDate,DurationType durationType){
        return payrollSettingMongoRepository.findAllPayrollPeriodByStartDate(unitId,startDate,durationType);

    }

    public PayrollSettingDTO createPayrollPeriod(PayrollSettingDTO payrollSettingDTO,Long unitId){
        if (!validateStartDateForpayrollPeriodCreation(payrollSettingDTO.getStartDate(), payrollSettingDTO.getDurationType())) {
            exceptionService.actionNotPermittedException("error.payroll.period.start.date.invalid");
        }
        if(Optional.ofNullable(payrollSettingMongoRepository.findPayrollPeriodByStartDate(unitId,payrollSettingDTO.getStartDate())).isPresent()){
            exceptionService.actionNotPermittedException("message.payroll.period.date.alreadyexists");
        }
        LocalDate payrollEndDate=payrollSettingDTO.getStartDate().plusYears(1);
        LocalDate payrollStartDate=payrollSettingDTO.getStartDate();
        List<PayrollPeriod> payrollPeriods = new ArrayList<>();
        while (payrollStartDate.isBefore(payrollEndDate)){
            payrollPeriods.add(new PayrollPeriod(payrollStartDate,getNextStartDate(payrollStartDate,payrollSettingDTO.getDurationType()).minusDays(1)));
            payrollStartDate=getNextStartDate(payrollStartDate,payrollSettingDTO.getDurationType());
        }
        PayrollSetting payrollSetting=new PayrollSetting(false,unitId,payrollPeriods,payrollSettingDTO.getDurationType());
        payrollSettingMongoRepository.save(payrollSetting);
        return ObjectMapperUtils.copyPropertiesByMapper(payrollSetting,PayrollSettingDTO.class);
    }

    public PayrollSettingDTO updatePayrollPeriod(PayrollSettingDTO payrollSettingDTO,Long unitId){
        PayrollSetting payrollSetting=payrollSettingMongoRepository.findPayrollPeriodById(unitId,payrollSettingDTO.getId());
        payrollSetting.setAccessGroupsPriority(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollSettingDTO.getAccessGroupsPriority(), PayrollAccessGroups.class));
        payrollSetting.setPublished(payrollSettingDTO.isPublished());
        payrollSetting.setDurationType(payrollSettingDTO.getDurationType());
        return null;
    }

    private List<PayrollPeriod> validatePayrollPeriod(List<PayRollPeriodSettingDTO> payRollPeriodSettingDTOS,List<PayrollPeriod>  payrollPeriods){
        Map<LocalDate,PayrollPeriod> startDateAndPayrollPeriodMap=payrollPeriods.stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        for (PayRollPeriodSettingDTO payRollPeriodSettingDTO : payRollPeriodSettingDTOS) {
            if(startDateAndPayrollPeriodMap.containsKey(payRollPeriodSettingDTO.getStartDate())){
               if(isNotNull(payRollPeriodSettingDTO.getDeadlineDate())){
                   int daysTillDeadlineDate= (int)ChronoUnit.DAYS.between(payRollPeriodSettingDTO.getEndDate(),payRollPeriodSettingDTO.getDeadlineDate());
                   if(veryfyGracePeriodOfAccessGroup(payRollPeriodSettingDTO.getPayrollAccessGroups(),daysTillDeadlineDate)){
                       startDateAndPayrollPeriodMap.get(payRollPeriodSettingDTO.getStartDate()).getPayrollAccessGroups().addAll(ObjectMapperUtils.copyPropertiesOfListByMapper(payRollPeriodSettingDTO.getPayrollAccessGroups(),PayrollAccessGroups.class));
                   }
               }
            }
        }
        return new ArrayList<>();
    }

    private boolean veryfyGracePeriodOfAccessGroup(List<PayRollAccessGroupSettingDTO> payRollAccessGroupSettingDTOS, int daysTillDeadlineDate) {
        int sumOfGracePeriod = payRollAccessGroupSettingDTOS.stream().mapToInt(payRollAccessGroupSettingDTOs -> payRollAccessGroupSettingDTOs.getGracePeriod()).sum();
        return sumOfGracePeriod == daysTillDeadlineDate;
    }

    private LocalDate getNextStartDate(LocalDate oldDate, DurationType durationType){
        return DurationType.MONTHS.equals(durationType)?oldDate.plusMonths(1):oldDate.plusWeeks(2);
    }

    private boolean validateStartDateForpayrollPeriodCreation(LocalDate startDate, DurationType durationType) {
        if (durationType.equals(DurationType.WEEKS)) {
            return startDate.getDayOfWeek().equals(DayOfWeek.MONDAY);
        } else {
            return startDate.equals(startDate.withDayOfMonth(1));
        }
    }

}
