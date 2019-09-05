package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.PaymentSettings;
import com.kairos.persistence.model.organization.PaymentSettingsQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAYMENT_SETTINGS;

/**
 * Created by vipul on 12/4/18.
 */
@Repository
public interface PaymentSettingRepository extends Neo4jBaseRepository<PaymentSettings, Long> {
    @Query("match(paymentSettings:PaymentSettings{deleted:false})<-[" + HAS_PAYMENT_SETTINGS + "]-(unit:Unit) where id(unit)={0}" +
            "return id(paymentSettings) as id," +
            "paymentSettings.weeklyPayDay as weeklyPayDay, " +
            "paymentSettings.fornightlyPayDay as fornightlyPayDay," +
            "paymentSettings.lastFornightlyPayDate as lastFornightlyPayDate, " +
            "paymentSettings.monthlyPayDate as monthlyPayDate")
    PaymentSettingsQueryResult getPaymentSettingByUnitId(Long unitId);

    @Query("match(paymentSettings:PaymentSettings{deleted:false})<-[" + HAS_PAYMENT_SETTINGS + "]-(unit:Unit) where id(unit)={0} AND id(paymentSettings)={1}" +
            " return paymentSettings")
    PaymentSettings getPaymentSettingByUnitId(Long unitId, Long paymentSettingId);

}
