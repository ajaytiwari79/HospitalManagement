package com.kairos.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.kairos.constaints.JsonConstaints.DEFAULT_JSON_FILE_PATH;

/**
 * pradeep
 * 30/4/19
 */
public class FileReaderUtil {

    public static String getFileDataAsString(String fileName){
        String json = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DEFAULT_JSON_FILE_PATH+fileName)));
            StringBuffer stringBuffer = new StringBuffer();
            bufferedReader.lines().forEach(line->stringBuffer.append(line));
            json = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
