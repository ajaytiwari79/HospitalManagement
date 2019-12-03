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

    public static final String YYYY_MM_DD_HH_MM_SS_S_ZZZ = "yyyy-MM-dd HH:mm:ss.S zzz";

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert( final Class type )
    {
        return LocalDate.class.isAssignableFrom( type );
    }
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        writer.setValue( ((Interval)source).getStart().toString(YYYY_MM_DD_HH_MM_SS_S_ZZZ) +"__"+((Interval)source).getEnd().toString(YYYY_MM_DD_HH_MM_SS_S_ZZZ));
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context )
    {
        String[] atra=reader.getValue().split("__");
            return new Interval(DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS_S_ZZZ).parseDateTime(atra[0]),DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS_S_ZZZ).parseDateTime(atra[1]));

    }
}
