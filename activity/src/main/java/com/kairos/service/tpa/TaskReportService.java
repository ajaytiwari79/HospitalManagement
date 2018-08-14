package com.kairos.service.tpa;
import com.kairos.persistence.model.task.TaskReport;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.task_type.TaskReportMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.mail.MailService;
import com.kairos.util.external_plateform_shift.TaskReportWrapper;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class TaskReportService extends MongoBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Inject
    private TaskReportMongoRepository taskReportMongoRepository;

   /* @Inject
    private StaffGraphRepository staffGraphRepository;*/

    @Inject
    private MailService mailService;
    @Inject
    MongoSequenceRepository mongoSequenceRepository;



    public File generateReport(List<TaskReportWrapper> payLoad, String fileName,String workBookName, String OrganizationName){
        String excelFileName= fileName+".xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFFont headerRowFont = workbook.createFont();
        headerRowFont.setBold(true);
        XSSFCellStyle headerRowStyle = workbook.createCellStyle();
        headerRowStyle.setFont(headerRowFont);
        headerRowStyle.setWrapText(true);
        XSSFSheet sheet = workbook.createSheet(workBookName);
        String userHome = System.getProperty("user.home");
        File file = new File(userHome,excelFileName);
        XSSFRow firstRow = sheet.createRow(0);
        firstRow.createCell(0).setCellStyle(headerRowStyle);
        firstRow.createCell(0).setCellValue(OrganizationName);
        XSSFRow staffRow;
        // Iterating over data
        int i = 0;
        int rowCounter = 2;
        for (TaskReportWrapper wrapper: payLoad) {

            logger.info("Loop Counter: "+i);
            logger.info("Row Counter: "+rowCounter);
            logger.info("Staff-> "+wrapper.getStaffName()+" Number of reports: "+wrapper.getTaskReports().size());
            staffRow = sheet.createRow(rowCounter);
            staffRow.createCell(0).setCellValue(wrapper.getStaffName());
            rowCounter++;
            // Header
            XSSFRow headerRow = sheet.createRow(rowCounter);

            headerRow.createCell(0).setCellValue("Changes Done On");
            headerRow.createCell(1).setCellValue("Previous From");
            headerRow.createCell(2).setCellValue("Previous To");
            headerRow.createCell(3).setCellValue("Previous Duration");
            headerRow.createCell(4).setCellValue("Previous Activity");
            headerRow.createCell(6).setCellValue("Current From");
            headerRow.createCell(7).setCellValue("Current To");
            headerRow.createCell(8).setCellValue("Current Duration");
            headerRow.createCell(9).setCellValue("Current Activity");
            headerRow.setRowStyle(headerRowStyle);
            rowCounter++;
            for (TaskReport report: wrapper.getTaskReports()) {
                logger.info("Row counter :"+rowCounter);
                XSSFRow dataRow = sheet.createRow(rowCounter);
                XSSFCell updateDate =dataRow.createCell(0);
                XSSFCell previousFromCell = dataRow.createCell(1);
                XSSFCell previousToCell = dataRow.createCell(2);
                XSSFCell previousDurationCell = dataRow.createCell(3);
                XSSFCell previousActivityCell = dataRow.createCell(4);
                XSSFCell currentFromCell = dataRow.createCell(6);
                XSSFCell currentToCell = dataRow.createCell(7);
                XSSFCell currentBreakCell = dataRow.createCell(8);
                XSSFCell currentActivityCell = dataRow.createCell(9);


                updateDate.setCellValue(report.getUpdateDate());

                previousToCell.setCellValue(report.getPreviousTo());
                previousFromCell.setCellValue(report.getPreviousFrom());
                previousDurationCell.setCellValue(report.getPreviousDuration());
                previousActivityCell.setCellValue(report.getPreviousActivity());

                if(report.getCurrentFrom() != null )    currentFromCell.setCellValue(report.getCurrentFrom());
                if(report.getCurrentTo() != null)  currentToCell.setCellValue(report.getCurrentTo());
                if(report.getCurrentDuration() != null)  currentBreakCell.setCellValue(report.getCurrentDuration());
                if(report.getCurrentActivity() != null)  currentActivityCell.setCellValue(report.getCurrentActivity());

                rowCounter++;
            }
            rowCounter = rowCounter+2;
            i++;
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            logger.info("Sending file as attachment: "+file.getAbsolutePath());
            // If Mail Sent
            if (file.exists()){
                return file;
            }
            return null;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

   /* public List<TaskReportWrapper> fetchStaffReports(List<Long> staffIds, Date startDate ,Date endDate){
        TaskReportWrapper taskReportWrapper;
        List<TaskReportWrapper> taskReportWrapperList = new ArrayList<>();
        for (Long staffId: staffIds) {
            List<TaskReport> taskReportList = taskReportMongoRepository.findByStaffId(staffId,startDate,endDate);
            Staff staff = staffGraphRepository.findById(staffId);
            taskReportWrapper= new TaskReportWrapper(staff.getFirstName()+" "+staff.getLastName() , taskReportList);
            taskReportWrapperList.add(taskReportWrapper);
        }
        return taskReportWrapperList;
    }
*/
    public boolean mailStaffTaskReport(File file,String[] receivers,String message,String subject){
        return mailService.sendMailWithAttachment(receivers,message,subject,file);
    }


    public List<String> getPlannersOfUnit(Long unitId){
        return null;
    }


/*

    public void generateCitizenList(long unitId){
        logger.debug("generating data");
        String excelFileName= "citizens.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFFont headerRowFont = workbook.createFont();
        headerRowFont.setBold(true);
        XSSFCellStyle headerRowStyle = workbook.createCellStyle();
        headerRowStyle.setFont(headerRowFont);
        headerRowStyle.setWrapText(true);
        XSSFSheet sheet = workbook.createSheet("citizens");
        String userHome = System.getProperty("user.home");
        File file = new File(userHome,excelFileName);

        XSSFRow staffRow;
        // Iterating over data
        XSSFRow headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("CPR NUMBER");
        headerRow.createCell(2).setCellValue("First Name");
        headerRow.createCell(3).setCellValue("Last Name");

        int i = 1;
        int rowCounter = 2;
        XSSFRow citizenRow ;
        for (Map<String,Object> citizen:organizationGraphRepository.getClientsOfOrganizationForReport(unitId) ) {
            Map cd = (Map)citizen.get("Client");
            logger.info("Loop Counter: "+i);
            logger.info("Row Counter: "+cd.toString());
            citizenRow =  sheet.createRow(i);
            citizenRow.createCell(0).setCellValue(cd.get("id").toString());
            citizenRow.createCell(1).setCellValue(cd.get("cprNumber").toString());
            citizenRow.createCell(2).setCellValue(cd.get("firstName").toString());
            citizenRow.createCell(3).setCellValue(cd.get("lastName").toString());
            i++;
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            logger.info("Sending file as attachment: "+file.getAbsolutePath());

            if (file.exists()){
                logger.debug("file createad");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

*/

    /*public void generateStaffList(long unitId){
        logger.debug("generating data");
        String excelFileName= "staff"+unitId+".xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFFont headerRowFont = workbook.createFont();
        headerRowFont.setBold(true);
        XSSFCellStyle headerRowStyle = workbook.createCellStyle();
        headerRowStyle.setFont(headerRowFont);
        headerRowStyle.setWrapText(true);
        XSSFSheet sheet = workbook.createSheet("staff");
        String userHome = System.getProperty("user.home");
        File file = new File(userHome,excelFileName);

        XSSFRow staffRow;
        // Iterating over data
        XSSFRow headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");

        int i = 1;
        int rowCounter = 2;
        XSSFRow citizenRow ;
        for (Map<String,Object> citizen:staffService.getStaffWithBasicInfo(unitId)) {
            Map cd = (Map)citizen.get("data");
            logger.info("Loop Counter: "+i);
            logger.info("Row Counter: "+cd.toString());
            citizenRow =  sheet.createRow(i);
            citizenRow.createCell(0).setCellValue(cd.get("id").toString());
            citizenRow.createCell(1).setCellValue(cd.get("firstName").toString());
            citizenRow.createCell(2).setCellValue(cd.get("lastName").toString());
            i++;
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            logger.info("Sending file as attachment: "+file.getAbsolutePath());

            if (file.exists()){
                logger.debug("file createad");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
}



