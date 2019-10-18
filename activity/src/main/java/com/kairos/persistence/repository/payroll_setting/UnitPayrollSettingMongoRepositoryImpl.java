package com.kairos.persistence.repository.payroll_setting;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.payroll_setting.UnitPayrollSetting;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
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
import static com.kairos.constants.CommonConstants.UNIT_ID;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class UnitPayrollSettingMongoRepositoryImpl implements CustomUnitPayrollSettingMongoRepository {

    public static final String PAYROLL_FREQUENCY = "payrollFrequency";
    public static final String PAYROLL_PERIODS = "payrollPeriods";
    public static final String PUBLISHED = "published";
    public static final String ACCESS_GROUPS_PRIORITY = "accessGroupsPriority";
    public static final String PARENT_PAYROLL_ID = "parentPayrollId";
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<UnitPayrollSettingDTO> getPayrollPeriodByYearAndPayrollFrequency(Long unitId, PayrollFrequency payrollFrequency, LocalDate startDate, LocalDate endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(PAYROLL_FREQUENCY).is(payrollFrequency)),
                unwind(PAYROLL_PERIODS),
                match(new Criteria().and("payrollPeriods.endDate").gt(startDate).and("payrollPeriods.startDate").lt(endDate)),
                group("$id").first(PUBLISHED).as(PUBLISHED)
                        .first(UNIT_ID).as(UNIT_ID).first(PAYROLL_FREQUENCY).as(PAYROLL_FREQUENCY)
                        .first(ACCESS_GROUPS_PRIORITY).as(ACCESS_GROUPS_PRIORITY).first(PARENT_PAYROLL_ID).as(PARENT_PAYROLL_ID)
                        .addToSet(PAYROLL_PERIODS).as(PAYROLL_PERIODS),
                project().and("_id").as("id").and(UNIT_ID).as(UNIT_ID).and(PUBLISHED).as(PUBLISHED)
                        .and(PAYROLL_FREQUENCY).as(PAYROLL_FREQUENCY).and(ACCESS_GROUPS_PRIORITY).as(ACCESS_GROUPS_PRIORITY)
                        .and(PAYROLL_PERIODS).as(PAYROLL_PERIODS).and(PARENT_PAYROLL_ID).as(PARENT_PAYROLL_ID),
                sort(Sort.Direction.ASC, "id")


        );
        AggregationResults<UnitPayrollSetting> results = mongoTemplate.aggregate(aggregation, UnitPayrollSetting.class, UnitPayrollSetting.class);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(results.getMappedResults(), UnitPayrollSettingDTO.class);

    }

    @Override
    public List<UnitPayrollSetting> getAllPayrollPeriodSettingOfUnitsByPayrollFrequency(List<PayrollFrequency> payrollFrequency, Long unitId) {
        String addFieldOperation = "{'$addFields':{'endDate':{ '$arrayElemAt': [ '$payrollPeriods.endDate', -1 ]}}}";
        String sortByEndDate = "{'$sort':{'unitId':-1,'endDate':-1}}";
        Criteria criteria = Criteria.where(PAYROLL_FREQUENCY).in(payrollFrequency).and(PUBLISHED).is(true);
        if (isNotNull(unitId)) {
            criteria.and(UNIT_ID).is(unitId);
        }
        Aggregation aggregation = newAggregation(
                match(criteria),
                new CustomAggregationOperation(Document.parse(addFieldOperation)),
                new CustomAggregationOperation(Document.parse(sortByEndDate)),
                group(UNIT_ID, PAYROLL_FREQUENCY).first("$$ROOT").as("data"),
                replaceRoot("data")
        );
        AggregationResults<UnitPayrollSetting> results = mongoTemplate.aggregate(aggregation, UnitPayrollSetting.class, UnitPayrollSetting.class);
        return results.getMappedResults();
    }
}
