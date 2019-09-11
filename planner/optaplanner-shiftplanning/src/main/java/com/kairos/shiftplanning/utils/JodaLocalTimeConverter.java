package com.kairos.shiftplanning.utils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public  class JodaLocalTimeConverter implements Converter
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
        writer.setValue( ((LocalTime)source).toString("HH:mm:ss") );
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context )
    {
        //return new DateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S zzz").parseDateTime(reader.getValue()));
            return new LocalTime(DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(reader.getValue()));

    }
}
