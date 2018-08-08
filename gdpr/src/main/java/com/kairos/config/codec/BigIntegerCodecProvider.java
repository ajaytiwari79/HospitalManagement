package com.kairos.config.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.math.BigInteger;

public class BigIntegerCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == BigInteger.class) {
            // construct DocumentCodec with a CodecRegistry
            return (Codec<T>) new BigIntegerCodec() ;
        }

        // CodecProvider returns null if it's not a provider for the requested Class
        return null;
    }
}
