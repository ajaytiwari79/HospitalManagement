package com.kairos.service.excel;

import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author pradeep
 * @date - 13/6/18
 */


@Service
public class ExcelService {

    public List<Row> getRowsByXLSXFile(MultipartFile multipartFile, int sheetNo) {
        InputStream stream;
        XSSFWorkbook workbook;
        List<Row> rows = null;
        try {
            stream = multipartFile.getInputStream();
            //Get the workbook instance for XLS file
            workbook = new XSSFWorkbook(stream);
            //Get sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(sheetNo);
            rows = Lists.newArrayList(sheet.iterator());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
    }

}
