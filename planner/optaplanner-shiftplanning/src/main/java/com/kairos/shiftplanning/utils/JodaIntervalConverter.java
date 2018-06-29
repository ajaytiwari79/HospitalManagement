package com.kairos.shiftplanning.utils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public  class JodaIntervalConverter implements Converter
{
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert( final Class type )
    {
        return LocalDate.class.isAssignableFrom( type );
    }
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        writer.setValue( ((Interval)source).getStart().toString("yyyy-MM-dd HH:mm:ss.S zzz") +"__"+((Interval)source).getEnd().toString("yyyy-MM-dd HH:mm:ss.S zzz"));
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context )
    {
        String[] atra=reader.getValue().split("__");
            return new Interval(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S zzz").parseDateTime(atra[0]),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S zzz").parseDateTime(atra[1]));

    }
}
