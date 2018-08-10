package com.kairos.config.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.math.BigInteger;

public class BigIntegerCodec implements Codec<BigInteger> {
    @Override
    public BigInteger decode(final BsonReader reader, DecoderContext decoderContext) {
        return BigInteger.valueOf(Long.parseLong(reader.readString()));
    }

    @Override
    public void encode(final BsonWriter writer, final BigInteger value, EncoderContext encoderContext) {
        writer.writeString(String.valueOf(value));
    }

    @Override
    public Class<BigInteger> getEncoderClass() {
        return BigInteger.class;
    }
}
