package com.kairos.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by prabjot on 1/11/16.
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil(){}

    public static void writeFile(String path, MultipartFile multipartFile) {
        byte[] buf = new byte[1024];
        try (InputStream inputStream = multipartFile.getInputStream(); FileOutputStream fileOutputStream = new FileOutputStream(new File(path))) {
            int numRead = 0;
            while ((numRead = inputStream.read(buf)) >= 0) {
                fileOutputStream.write(buf, 0, numRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDirectory(String dirPath) {
        File theDir = new File(dirPath);
        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                logger.info(se.toString());
            }
        }
    }
}
