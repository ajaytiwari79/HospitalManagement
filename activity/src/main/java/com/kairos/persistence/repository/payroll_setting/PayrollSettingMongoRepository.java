package com.kairos.persistence.repository.payroll_setting;

import com.kairos.dto.activity.payroll_setting.PayrollSettingDTO;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.payroll_setting.PayrollSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollSettingMongoRepository extends MongoBaseRepository<PayrollSetting,BigInteger>,CustomPayrollSettingMongoRepository {

    @Query(value = "{'payrollPeriods.startDate':?1,deleted:false,published:true,unitId:?0 }")
    PayrollSetting findPayrollPeriodByStartDate(Long unitId,LocalDate startDate);

    @Query(value = "{deleted:false,unitId:?0,_id:?1 }")
    PayrollSetting findPayrollPeriodById(Long unitId,BigInteger id);

    @Query(value = "{deleted:false,unitId:?0,published:false }")
    PayrollSetting findDraftPayrollPeriodByUnitId(Long unitId);


    @Query(value = "{deleted:false,unitId:?0 }")
    List<PayrollSetting> findAllByunitId(Long unitId);

    @Query(value = "{deleted:false,unitId:?0,_id:?1,published:false}",delete = true)
    Long removeDraftpayrollPeriod(Long unitId,BigInteger id);


    @Query(value = "{'payrollPeriods.startDate':?1,deleted:false,unitId:?0,durationType:?2 }")
    List<PayrollSettingDTO> findAllPayrollPeriodByStartDate(Long unitId, LocalDate startDate,DurationType durationType);

}
