package com.planner.util.wta;

import com.kairos.shiftplanning.utils.JodaLocalDateConverter;
import com.kairos.shiftplanning.utils.JodaLocalTimeConverter;
import com.kairos.shiftplanning.utils.JodaTimeConverter;
import com.planner.service.taskPlanningService.PlanningService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ShiftPlanningUtil {
    private static Logger log= LoggerFactory.getLogger(PlanningService.class);
    public static void toXml(Object solution, String fileName) {
        try {
            XStream xstream = new XStream(new PureJavaReflectionProvider());
            //xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
            xstream.setMode(XStream.ID_REFERENCES);
            xstream.registerConverter(new JodaTimeConverter());
            xstream.registerConverter(new JodaLocalTimeConverter());
            xstream.registerConverter(new JodaLocalDateConverter());
            // xstream.registerConverter(new JodaTimeConverterNoTZ());
            xstream.registerConverter(new HardMediumSoftLongScoreXStreamConverter());
            String xmlString = xstream.toXML(solution);
            writeXml(xmlString, fileName);
        }catch(Throwable e){
            log.error("soe:",e);
            throw e;
        }
    }
    public static  void writeXml(String xmlString,String fileName){
        PrintWriter out = null;
        try {
            out = new PrintWriter(new File("" +fileName+".xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        out.write(xmlString);
        out.close();
    }

    public static void copyFileContent(File baseFile, File file) {
        FileReader fr=null;
        FileWriter fw=null;
        try {
            fr=new FileReader(baseFile);
            fw=new FileWriter(file);
            int b=0;
            while ((b=fr.read())!=-1){
                fw.write(b);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch(IOException e) {
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch(IOException e) {
                }
            }
        }

    }
}
