package com.kairos.persistence.repository.break_settings;

import org.springframework.data.mongodb.core.MongoTemplate;

import javax.inject.Inject;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
public class BreakSettingMongoRepositoryImpl implements CustomBreakSettingsMongoRepository{
    @Inject
    MongoTemplate mongoTemplate;

}
