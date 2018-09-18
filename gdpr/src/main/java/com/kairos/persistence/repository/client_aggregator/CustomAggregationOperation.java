package com.kairos.persistence.repository.client_aggregator;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;


public class CustomAggregationOperation implements AggregationOperation {


    private Document operation;

    public CustomAggregationOperation(Document operation) {
        this.operation = operation;
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return context.getMappedObject(operation);
    }
}
