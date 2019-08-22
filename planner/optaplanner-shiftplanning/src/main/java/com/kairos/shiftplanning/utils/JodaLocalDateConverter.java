package com.kairos.shiftplanning.utils;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public  class JodaLocalDateConverter implements Converter
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
        writer.setValue( ((LocalDate)source).toString("MM/dd/yyyy") );
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context )
    {
            return new LocalDate(DateTimeFormat.forPattern("MM/dd/yyyy").parseDateTime(reader.getValue()));

    }
}
