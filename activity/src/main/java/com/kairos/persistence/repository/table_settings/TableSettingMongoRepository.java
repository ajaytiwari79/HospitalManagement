package com.kairos.persistence.repository.table_settings;

import com.kairos.persistence.model.table_settings.TableSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Created by prabjot on 1/5/17.
 */
@Repository
public interface TableSettingMongoRepository extends MongoRepository<TableSetting, BigInteger>, CustomTableSettingMongoRepository {

}
