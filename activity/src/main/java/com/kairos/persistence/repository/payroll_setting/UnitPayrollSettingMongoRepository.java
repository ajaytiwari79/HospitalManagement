package com.kairos.persistence.repository.payroll_setting;

import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.payroll_setting.UnitPayrollSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface UnitPayrollSettingMongoRepository extends MongoBaseRepository<UnitPayrollSetting,BigInteger>,CustomUnitPayrollSettingMongoRepository {

    @Query(value = "{deleted:false,published:false,unitId:?0 ,payrollFrequency:?1}",delete = true)
    Long findAndDeletePayrollPeriodByStartDate(Long unitId,PayrollFrequency payrollFrequency);

    @Query(value = "{deleted:false,unitId:?0,_id:?1 }")
    UnitPayrollSetting findPayrollPeriodById(Long unitId, BigInteger id);

    @Query(value = "{deleted:false,unitId:?0,_id:?1,payrollFrequency:?2 ,published:true}")
    UnitPayrollSetting findPayrollPeriodByIdAndPayrollFrequency(Long unitId, BigInteger id,PayrollFrequency payrollFrequency);

    @Query(value = "{'payrollPeriods.endDate':{$lte:?2},deleted:false,unitId:?0,published:false,payrollFrequency:?1 }")
    UnitPayrollSetting findDraftPayrollPeriodByUnitId(Long unitId,PayrollFrequency payrollFrequency,LocalDate endDate);

    @Query(value = "{deleted:false,_id:{$ne:?0},unitId:?1,payrollFrequency:?2,published:false,parentPayrollId:{$exists:false} }")
    UnitPayrollSetting findDraftPayrollPeriodByUnitIdAndNotExistParentId(BigInteger id,Long unitId,PayrollFrequency payrollFrequency);


    @Query(value = "{deleted:false,unitId:?0,payrollFrequency:?1 }")
    List<UnitPayrollSetting> findAllByunitIdAndFrequency(Long unitId, PayrollFrequency payrollFrequency);

    @Query(value = "{deleted:false,unitId:?0,_id:?1,published:false}",delete = true)
    Long removeDraftpayrollPeriod(Long unitId,BigInteger id);

    @Query(value = "{'payrollPeriods.startDate':?1,deleted:false,published:true,unitId:?0 }",exists = true)
    boolean findPayrollPeriodByStartDate(Long unitId,LocalDate startDate);






}
