package com.kairos.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by prabjot on 1/11/16.
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil(){}

    public static void writeFile(String path, MultipartFile multipartFile) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream;
        try {
            inputStream = multipartFile.getInputStream();
            byte[] buf = new byte[1024];
            fileOutputStream = new FileOutputStream(new File(path));
            int numRead = 0;
            while ((numRead = inputStream.read(buf)) >= 0) {
                fileOutputStream.write(buf, 0, numRead);
            }
            if(inputStream!=null){
                inputStream.close();
            }
            if(fileOutputStream!=null){
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDirectory(String dirPath) {
        File theDir = new File(dirPath);
        if (!theDir.exists()) {
            logger.info("creating directory: " + dirPath);
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                logger.info(se.toString());
            }
            if (result) {
                logger.info("DIR created");
            }
        }
    }
}
