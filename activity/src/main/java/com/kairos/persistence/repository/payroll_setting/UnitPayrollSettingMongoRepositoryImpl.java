package com.kairos.persistence.repository.payroll_setting;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.payroll_setting.UnitPayrollSetting;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import org.apache.commons.collections.ComparatorUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class UnitPayrollSettingMongoRepositoryImpl implements CustomUnitPayrollSettingMongoRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<UnitPayrollSettingDTO> getPayrollPeriodByYearAndPayrollFrequency(Long unitId, PayrollFrequency payrollFrequency, LocalDate startDate, LocalDate endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("unitId").is(unitId).and("payrollFrequency").is(payrollFrequency)),
                unwind("payrollPeriods"),
                match(new Criteria().and("payrollPeriods.endDate").gt(startDate).and("payrollPeriods.startDate").lt(endDate)),
                group("$id").first("published").as("published")
                        .first("unitId").as("unitId").first("payrollFrequency").as("payrollFrequency")
                        .first("accessGroupsPriority").as("accessGroupsPriority").first("parentPayrollId").as("parentPayrollId")
                        .addToSet("payrollPeriods").as("payrollPeriods"),
                project().and("_id").as("id").and("unitId").as("unitId").and("published").as("published")
                        .and("payrollFrequency").as("payrollFrequency").and("accessGroupsPriority").as("accessGroupsPriority")
                        .and("payrollPeriods").as("payrollPeriods").and("parentPayrollId").as("parentPayrollId"),
                sort(Sort.Direction.ASC, "id")


        );
        AggregationResults<UnitPayrollSetting> results = mongoTemplate.aggregate(aggregation, UnitPayrollSetting.class, UnitPayrollSetting.class);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(results.getMappedResults(), UnitPayrollSettingDTO.class);

    }

    @Override
    public List<UnitPayrollSetting> getAllPayrollPeriodSettingOfUnitsByPayrollFrequency(List<PayrollFrequency> payrollFrequency, Long unitId) {
        String addFieldOperation = "{'$addFields':{'endDate':{ '$arrayElemAt': [ '$payrollPeriods.endDate', -1 ]}}}";
        String sortByEndDate = "{'$sort':{'unitId':-1,'endDate':-1}}";
        Criteria criteria = Criteria.where("payrollFrequency").in(payrollFrequency).and("published").is(true);
        if (isNotNull(unitId)) {
            criteria.and("unitId").is(unitId);
        }
        Aggregation aggregation = newAggregation(
                match(criteria),
                new CustomAggregationOperation(Document.parse(addFieldOperation)),
                new CustomAggregationOperation(Document.parse(sortByEndDate)),
                group("unitId","payrollFrequency").first("$$ROOT").as("data"),
                replaceRoot("data")
        );
        AggregationResults<UnitPayrollSetting> results = mongoTemplate.aggregate(aggregation, UnitPayrollSetting.class, UnitPayrollSetting.class);
        return results.getMappedResults();
    }
}
