package com.kairos.planning.utils;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public  class JodaTimeConverterNoTZ implements Converter
{
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert( final Class type )
    {
        return DateTime.class.isAssignableFrom( type );
    }
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        if(source instanceof  DateTime){
            writer.setValue( ((DateTime)source).toString("MM/dd/yyyy HH:mm:ss") );
        }else{
            writer.setValue( source.toString() );
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context )
    {
        return new DateTime(DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").parseDateTime(reader.getValue()));
    }
}
