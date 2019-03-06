package com.kairos.persistence.repository.payroll_setting;

import com.kairos.dto.activity.payroll_setting.PayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;

import java.time.LocalDate;
import java.util.List;

public interface CustomPayrollSettingMongoRepository {

    List<PayrollSettingDTO> getPayrollPeriodByYearAndPayrollFrequency(Long unitId, PayrollFrequency payrollFrequency, LocalDate startDate, LocalDate endDate);

}
