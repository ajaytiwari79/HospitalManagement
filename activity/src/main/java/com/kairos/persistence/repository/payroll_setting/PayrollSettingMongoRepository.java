package com.kairos.persistence.repository.payroll_setting;

import com.kairos.dto.activity.payroll_setting.PayrollSettingDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.payroll_setting.PayrollSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollSettingMongoRepository extends MongoBaseRepository<PayrollSetting,BigInteger>,CustomPayrollSettingMongoRepository {

    @Query(value = "{'payrollPeriods.startDate':?1,deleted:false,published:false,unitId:?0 }",delete = true)
    Long findAndDeletePayrollPeriodByStartDate(Long unitId, LocalDate startDate);

    @Query(value = "{deleted:false,unitId:?0,_id:?1 }")
    PayrollSetting findPayrollPeriodById(Long unitId,BigInteger id);

    @Query(value = "{deleted:false,unitId:?0,published:false }")
    PayrollSetting findDraftPayrollPeriodByUnitId(Long unitId);


    @Query(value = "{deleted:false,unitId:?0,payrollFrequency:?1 }")
    List<PayrollSetting> findAllByunitIdAndFrequency(Long unitId,PayrollFrequency payrollFrequency);

    @Query(value = "{deleted:false,unitId:?0,_id:?1,published:false}",delete = true)
    Long removeDraftpayrollPeriod(Long unitId,BigInteger id);


    @Query(value = "{'payrollPeriods.startDate':?1,deleted:false,unitId:?0,payrollFrequency:?2 }")
    List<PayrollSettingDTO> findAllPayrollPeriodByStartDate(Long unitId, LocalDate startDate,PayrollFrequency payrollFrequency);


//    @Query(value = "{'payrollPeriods:{'$elemMatch':{'startDate':{$gte:?1 , $lte:?2}}},deleted:false,unitId:?0,payrollFrequency:?3 }")
//    List<PayrollSettingDTO> findAllPayrollPeriodByStartDate(Long unitId, LocalDate startDate,LocalDate endDate,PayrollFrequency payrollFrequency);

}
