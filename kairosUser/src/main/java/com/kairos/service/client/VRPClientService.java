package com.kairos.service.client;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.client.VRPClient;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.client.VRPClientGraphRepository;
import com.kairos.response.dto.web.client.VRPClientDTO;
import com.kairos.service.UserBaseService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@Service
public class VRPClientService  extends UserBaseService {


    @Inject private VRPClientGraphRepository vrpClientGraphRepository;
    @Inject private OrganizationGraphRepository organizationGraphRepository;

    public List<VRPClient> importDateFromXLSXFile(MultipartFile multipartFile,Organization organization) {
        InputStream stream = null;
        XSSFWorkbook workbook = null;
        List<VRPClient> clients = null;
        try {
            stream = multipartFile.getInputStream();
            //Get the workbook instance for XLS file
            workbook = new XSSFWorkbook(stream);

            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            clients = new ArrayList<>();
            int i=0;
            while (rows.hasNext()) {
                Row row = rows.next();
                VRPClient client = new VRPClient();
                client.setFirstName("Client "+i);
                client.setIntallationNo((int) row.getCell(5).getNumericCellValue());
                client.setLattitude(row.getCell(14).getNumericCellValue());
                client.setLongitude(row.getCell(15).getNumericCellValue());
                client.setBlock(row.getCell(9).getStringCellValue());
                client.setCity(row.getCell(13).getStringCellValue());
                client.setDuration((int) row.getCell(0).getNumericCellValue());
                client.setFloorNo((int) row.getCell(10).getNumericCellValue());
                client.setHouseNo((int) row.getCell(8).getNumericCellValue());
                client.setPost(new Integer(row.getCell(12).getStringCellValue()));
                client.setStreetName(row.getCell(7).getStringCellValue());
                client.setOrganization(organization);
                clients.add(client);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clients;
    }


    public List<VRPClientDTO> importClient(Long unitId,MultipartFile multipartFile){
        Optional<Organization> organization = organizationGraphRepository.findById(unitId,0);
        List<VRPClient> vrpClients = importDateFromXLSXFile(multipartFile,organization.get());
        save(vrpClients);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(vrpClients, VRPClientDTO.class);
    }

    public List<VRPClientDTO> getAllClient(Long unitId){
        List<VRPClient> vrpClients = vrpClientGraphRepository.getAllClient(unitId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(vrpClients, VRPClientDTO.class);
    }


}
