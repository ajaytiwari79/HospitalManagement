package com.kairos.util.file_operations;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public final class CSVOperations {

    public static CSVParser readCSVFromFile(File file) throws IOException {
        return CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
    }


}
