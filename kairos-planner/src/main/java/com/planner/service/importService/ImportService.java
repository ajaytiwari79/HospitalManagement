package com.planner.service.importService;

import com.google.common.collect.Lists;
import com.planner.domain.task.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * @author pradeep
 * @date - 7/6/18
 */
public class ImportService {

    public void readExcelFile() {
        //File file = getFile(multipartFile);


    }

    public List<Task> readTaskList(){
        List<Task> taskList = new ArrayList<>(1500);
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
                        Task task = new Task();
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

    private List<Task> getUniqueTask(List<Task> taskList){
        List<Task> tasks = new ArrayList<>();
        Map<Integer,Integer> intallationandDuration = taskList.stream().collect(groupingBy(Task::getIntallationNo,summingInt(Task::getDuration)));
        Map<Integer,Set<String>> intallationandSkill = taskList.stream().collect(groupingBy(Task::getIntallationNo,mapping(Task::getSkill,toSet())));
        return null;
    }
}