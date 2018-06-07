package com.planner.service.excel;

import com.google.common.collect.Lists;
import com.kairos.dto.planner.TaskDTO;
import com.kairos.response.dto.web.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class ExcelService {

    public void readExcelFile() {
        //File file = getFile(multipartFile);


    }

    public List<TaskDTO> readTaskList(){
        List<TaskDTO> taskList = new ArrayList<>(1500);
        try {
            InputStream stream = new FileInputStream(new File("/home/pradeep/Downloads/POCKamstrupVRP.xlsx"));
            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            List<Row> rowIterator = Lists.newArrayList(sheet.iterator());
            Cell cell;
            Row row;
            for (int i=2;i<rowIterator.size();i++){
                row = rowIterator.get(i);
                if (row.getRowNum() > 0) {
                    try {
                        TaskDTO task = new TaskDTO();
                        task.setIntallationNo((int) row.getCell(5).getNumericCellValue());
                        task.setLattitude(row.getCell(14).getNumericCellValue());
                        task.setLongitude(row.getCell(15).getNumericCellValue());
                        taskList.add(task);
                    }catch (IllegalStateException e){
                        System.out.println("error in "+i +" line "+e.getMessage());
                    }
                }

            }
            System.out.println("asdsadasd "+ taskList.size());
        } catch (IOException e) {

        }
        List<Task> tasks = getUniqueTask(taskList);
        return taskList;
    }

    private List<Task> getUniqueTask(List<TaskDTO> taskList){
        List<Task> tasks = new ArrayList<>();
        Map<Integer,Integer> intallationandDuration = taskList.stream().collect(groupingBy(TaskDTO::getIntallationNo,summingInt(TaskDTO::getDuration)));
        Map<Integer,Set<String>> intallationandSkill = taskList.stream().collect(groupingBy(TaskDTO::getIntallationNo,mapping(TaskDTO::getSkill,toSet())));
        return null;
    }

    public File getFile(MultipartFile multipartFile) {
        if (!multipartFile.isEmpty()) {
            File file = new File("");
            try {
                byte[] bytes = multipartFile.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(file));
                stream.write(bytes);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}