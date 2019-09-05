package com.kairos.shiftplanning.utils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

    public  class JodaTimeConverter implements Converter
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
                writer.setValue( ((DateTime)source).toString("yyyy-MM-dd HH:mm:ss.S zzz") );
            }/*else{
                writer.setValue( source.toString() );
            }*/
        }
        @Override
        @SuppressWarnings("unchecked")
        public Object unmarshal( HierarchicalStreamReader reader,
                                 UnmarshallingContext context )
        {
            //return new DateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S zzz").parseDateTime(reader.getValue()));
        	try{
        		return new DateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S zzz").parseDateTime(reader.getValue()));
        	}catch(Exception e){
                return new DateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S 'IST'").parseDateTime(reader.getValue()));

        	}
        	//return new DateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S 'IST'").

        }
}
