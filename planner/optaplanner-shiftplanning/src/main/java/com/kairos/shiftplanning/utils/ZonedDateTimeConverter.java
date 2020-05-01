package com.kairos.shiftplanning.utils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public  class ZonedDateTimeConverter implements Converter{
        @Override
        @SuppressWarnings("unchecked")
        public boolean canConvert( final Class type )
        {
            return ZonedDateTime.class.isAssignableFrom( type );
        }
        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context )
        {
            if(source instanceof  ZonedDateTime){
                writer.setValue( ((ZonedDateTime)source).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S zzz")) );
            }/*else{
                writer.setValue( source.toString() );
            }*/
        }
        @Override
        @SuppressWarnings("unchecked")
        public Object unmarshal( HierarchicalStreamReader reader,
                                 UnmarshallingContext context ){
            //return new DateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S zzz").parseDateTime(reader.getValue()));
        	try{
        		return ZonedDateTime.parse(reader.getValue(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S zzz"));
        	}catch(Exception e){
                return ZonedDateTime.parse(reader.getValue(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S 'IST'"));
        	}
        	//return new DateTime(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S 'IST'").

        }
}
