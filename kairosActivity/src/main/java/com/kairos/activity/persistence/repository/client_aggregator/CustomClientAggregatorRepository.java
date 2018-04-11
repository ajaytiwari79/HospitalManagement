package com.kairos.activity.persistence.repository.client_aggregator;

import com.kairos.activity.persistence.model.client_aggregator.ClientAggregator;
import com.mongodb.DBCursor;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/8/17.
 */
public interface CustomClientAggregatorRepository {
    List<Map> getCitizenAggregationData(Long unitId);

    DBCursor fetchAllClientAggregationData();

    long getCountOfAggregateData(long unitId);

    List<ClientAggregator> getAggregateDataByUnit(long unitId,int skip,int limit);
}
