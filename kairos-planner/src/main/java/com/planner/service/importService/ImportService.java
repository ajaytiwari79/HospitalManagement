package com.planner.service.importService;

import com.google.common.collect.Lists;
import com.planner.domain.staff.Employee;
import com.planner.domain.task.Task;
import com.planner.service.staffService.EmployeeService;
import com.planner.service.taskService.TaskService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class ImportService {

    @Autowired
    private TaskService taskService;
    @Autowired private EmployeeService employeeService;

    public void readExcelFile() {
        //File file = getFile(multipartFile);

        InputStream stream = null;
        XSSFWorkbook workbook = null;
        try {
            stream = new FileInputStream(new File(System.getProperty("user.home")+"/Downloads/POCKamstrupVRP.xlsx"));
            //Get the workbook instance for XLS file
            workbook = new XSSFWorkbook(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Get first sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);
        List<Row> rows = Lists.newArrayList(sheet.iterator());
        List<Task> taskList = readTaskList(rows);
        taskService.saveTasks(taskList);
        //Get second sheet from the workbook
        sheet = workbook.getSheetAt(1);
        rows = Lists.newArrayList(sheet.iterator());
        List<Employee> employees = getEmployee(rows);
        employeeService.saveEmployee(employees);

    }

    public List<Task> readTaskList(List<Row> rows) {
        List<Task> taskList = new ArrayList<>(1500);
        Cell cell;
        Row row;
        for (int i = 2; i < rows.size(); i++) {
            row = rows.get(i);
            if (row.getRowNum() > 0) {
                try {
                    Task task = new Task();
                    task.setIntallationNumber((int) row.getCell(5).getNumericCellValue());
                    task.setLattitude(row.getCell(14).getNumericCellValue());
                    task.setLongitude(row.getCell(15).getNumericCellValue());
                    task.setBlock(row.getCell(9).getStringCellValue());
                    task.setCity(row.getCell(13).getStringCellValue());
                    task.setDuration((int) row.getCell(0).getNumericCellValue());
                    task.setFloorNo((int)row.getCell(10).getNumericCellValue());
                    task.setHouseNo((int)row.getCell(8).getNumericCellValue());
                    task.setPost(new Integer(row.getCell(12).getStringCellValue()));
                    task.setSkills(row.getCell(16).getStringCellValue());
                    task.setStreetName(row.getCell(7).getStringCellValue());
                    taskList.add(task);
                } catch (IllegalStateException e) {
                    System.out.println("error in " + i + " line " + e.getMessage());
                }
            }

        }
        return taskList;
    }

    public List<Employee> getEmployee(List<Row> rows){
        List<Employee> employees = new ArrayList<>(20);
        Cell cell;
        Row row;
        for (int i = 1; i < 6; i++) {
            row = rows.get(i);
            if (row.getRowNum() > 0) {
                try {
                    Employee employee = new Employee(row.getCell(0).getStringCellValue(),getSkills(row.getCell(2).getStringCellValue()),(int)(row.getCell(1).getNumericCellValue()*100));

                    employees.add(employee);
                } catch (IllegalStateException e) {
                    System.out.println("error in " + i + " line " + e.getMessage());
                }
            }

        }
        return employees;
    }

    private Set<String> getSkills(String skillList){
        return new HashSet<>(Arrays.asList(skillList.split(",")));
    }


}