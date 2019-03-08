package com.kairos.persistence.repository.payroll_setting;

import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;

import java.time.LocalDate;
import java.util.List;

public interface CustomUnitPayrollSettingMongoRepository {

    List<UnitPayrollSettingDTO> getPayrollPeriodByYearAndPayrollFrequency(Long unitId, PayrollFrequency payrollFrequency, LocalDate startDate, LocalDate endDate);

}
