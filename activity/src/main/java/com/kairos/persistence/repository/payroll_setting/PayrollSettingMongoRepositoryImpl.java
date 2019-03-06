package com.kairos.persistence.repository.payroll_setting;

import com.kairos.dto.activity.payroll_setting.PayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.payroll_setting.PayrollSetting;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class PayrollSettingMongoRepositoryImpl implements CustomPayrollSettingMongoRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<PayrollSettingDTO> getPayrollPeriodByYearAndPayrollFrequency(Long unitId, PayrollFrequency payrollFrequency, LocalDate startDate, LocalDate endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("unitId").is(unitId)),
                unwind("payrollPeriods"),
                match(new Criteria().and("payrollPeriods.endDate").gt(startDate).and("payrollPeriods.startDate").lt(endDate)),
                group("$id").first("published").as("published")
                        .first("unitId").as("unitId").first("payrollFrequency").as("payrollFrequency")
                        .first("accessGroupsPriority").as("accessGroupsPriority")
                        .addToSet("payrollPeriods").as("payrollPeriods"),
                project().and("_id").as("id").and("unitId").as("unitId").and("published").as("published")
                        .and("payrollFrequency").as("payrollFrequency").and("accessGroupsPriority").as("accessGroupsPriority")
                        .and("payrollPeriods").as("payrollPeriods")
        );
        AggregationResults<PayrollSettingDTO> results = mongoTemplate.aggregate(aggregation, PayrollSetting.class, PayrollSettingDTO.class);
        return results.getMappedResults();

    }
}
