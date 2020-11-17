package com.kairos.shiftplanning.utils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public  class LocalTimeConverter implements Converter
{
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert( final Class type )
    {
        return LocalTime.class.isAssignableFrom( type );
    }
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        writer.setValue( ((LocalTime)source).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context ){
            return LocalTime.parse(reader.getValue(),DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
