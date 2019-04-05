package com.kairos.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by prabjot on 1/11/16.
 */
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static void writeFile(String path, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(new File(path))) {
            byte[] buf = new byte[1024];
            int numRead = 0;
            while ((numRead = inputStream.read(buf)) >= 0) {
                fileOutputStream.write(buf, 0, numRead);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception orrured while uploading exception " + e);
        } catch (IOException e) {
            LOGGER.error("Exception orrured while uploading exception " + e);
        }
    }

    public static void createDirectory(String dirPath) {
        File theDir = new File(dirPath);
        if (!theDir.exists()) {
            LOGGER.info("creating directory: " + dirPath);
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                LOGGER.info(se.toString());
            }
            if (result) {
                LOGGER.info("DIR created");
            }
        }
    }

}
