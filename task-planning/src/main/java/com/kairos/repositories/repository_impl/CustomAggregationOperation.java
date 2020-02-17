package com.kairos.repositories.repository_impl;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * Created by pankaj on 27/12/16.
 */
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