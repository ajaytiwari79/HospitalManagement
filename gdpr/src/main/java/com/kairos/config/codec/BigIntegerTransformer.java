package com.kairos.config.codec;

import org.bson.Transformer;

import java.math.BigInteger;

public class BigIntegerTransformer implements Transformer {


    @Override
    public Object transform(Object objectToTransform) {

        BigInteger value=(BigInteger)objectToTransform;

        return value.toString();
    }
}

