package com.kairos.service.client;

import com.kairos.vrp.task.VRPTaskDTO;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.ObjectUtils;
import com.kairos.client.TaskServiceRestClient;
import com.kairos.client.TomTomRestClient;
import com.kairos.client.dto.TaskAddress;
import com.kairos.user.organization.Organization;
import com.kairos.user.client.VRPClient;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.client.VRPClientGraphRepository;
import com.kairos.user.client.VRPClientDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.excel.ExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@Service
public class VRPClientService  extends UserBaseService {


    @Inject private VRPClientGraphRepository vrpClientGraphRepository;
    @Inject private OrganizationGraphRepository organizationGraphRepository;
    @Inject private TomTomRestClient tomTomRestClient;
    @Inject private ExcelService excelService;
    @Inject private TaskServiceRestClient taskServiceRestClient;



    private Double getValue(Cell cell){
        Double value;
        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
            value =  cell.getNumericCellValue();
        }else {
            value = new Double(cell.getStringCellValue().replaceAll(",","."));

        }
        return value;
    }

    private Object[] getVrpClientByRows(List<Row> rows,Organization organization){
        List<VRPClient> vrpClients = new ArrayList<>();
        List<VRPTaskDTO> vrpTasks = new ArrayList<>();
        List<Long> clientIntallationNo = vrpClientGraphRepository.getAllClientInstalltionNo(organization.getId());
        for (int i = 2;i<rows.size();i++){
            Row row = rows.get(i);
            VRPClient client = new VRPClient();
            client.setFirstName("Client "+(i-1));
            client.setInstallationNumber( getValue(row.getCell(5)).longValue());
            client.setLatitude(getValue(row.getCell(14)));
            client.setLongitude(getValue(row.getCell(15)));
            client.setBlock(row.getCell(9).getStringCellValue());
            client.setCity(row.getCell(13).getStringCellValue());
            client.setDuration((int) row.getCell(0).getNumericCellValue());
            client.setFloorNumber((int) row.getCell(10).getNumericCellValue());
            client.setHouseNumber((int) row.getCell(8).getNumericCellValue());
            client.setZipCode(new Integer(row.getCell(12).getStringCellValue()));
            client.setStreetName(row.getCell(7).getStringCellValue());
            client.setOrganization(organization);
            VRPTaskDTO vrpTaskDTO = new VRPTaskDTO();
            vrpTaskDTO.setAddress(new TaskAddress(client.getZipCode(),client.getCity(),client.getStreetName(),""+client.getHouseNumber(),client.getLongitude().toString(),client.getLatitude().toString(),client.getBlock(),client.getFloorNumber()));
            vrpTaskDTO.setInstallationNumber(client.getInstallationNumber());
            vrpTaskDTO.setSkill(row.getCell(16).getStringCellValue());
            vrpTaskDTO.setDuration((int) row.getCell(0).getNumericCellValue());
            vrpTasks.add(vrpTaskDTO);
            vrpClients.add(client);
        }
        vrpClients = vrpClients.stream().filter(vrpClient -> !clientIntallationNo.contains(vrpClient.getInstallationNumber())).filter(ObjectUtils.distinctByKey(vrpClient -> vrpClient.getInstallationNumber())).collect(Collectors.toList());
        return new Object[]{vrpClients,vrpTasks};
    }

    public List<VRPClientDTO> importClients(Long unitId, MultipartFile multipartFile){
        Optional<Organization> organization = organizationGraphRepository.findById(unitId,0);
        List<Row> rows = excelService.getRowsByXLSXFile(multipartFile,0);
        Object objects[] = getVrpClientByRows(rows,organization.get());
        List<VRPClient> vrpClients = (List<VRPClient>)objects[0];
        List<VRPClient> vrpClientList = new ArrayList<>();
        vrpClients.forEach(vrpClient -> {
            Map<String,String> request = new HashMap<>();
            request.put("streetName",vrpClient.getStreetName());
            request.put("postalCode",""+vrpClient.getZipCode());
            request.put("countryCode","DK");
            /*Map response = tomTomRestClient.getfromTomtom(request);
            if(response!=null){
                vrpClientList.add(vrpClient);
            }*/
            vrpClientList.add(vrpClient);

        });
        save(vrpClientList);
        createTask((List<VRPTaskDTO>)objects[1],unitId);

        return ObjectMapperUtils.copyPropertiesOfListByMapper(vrpClientList, VRPClientDTO.class);
    }

    public void createTask(List<VRPTaskDTO> vrpTaskDTOS,Long unitId){
        List<VRPClient> vrpClients = vrpClientGraphRepository.getAllClient(unitId);
        Map<Long,VRPClient> clientIdAndInstallationNo = vrpClients.stream().collect(Collectors.toMap(c->c.getInstallationNumber(), c->c));
        for (VRPTaskDTO taskDTO : vrpTaskDTOS) {
            VRPClient vrpClient = clientIdAndInstallationNo.get(taskDTO.getInstallationNumber());
            taskDTO.setCitizenId(vrpClient.getId());
            taskDTO.setCitizenName(vrpClient.getFirstName());
        }
        taskServiceRestClient.createTaskBYExcel(vrpTaskDTOS);
    }

    public List<VRPClientDTO> getAllClient(Long unitId){
        List<VRPClient> vrpClients = vrpClientGraphRepository.getAllClient(unitId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(vrpClients, VRPClientDTO.class);
    }

    public VRPClientDTO getClient(Long clientId){
        VRPClient vrpClient = vrpClientGraphRepository.findOne(clientId);
        return ObjectMapperUtils.copyPropertiesByMapper(vrpClient, VRPClientDTO.class);
    }

    public boolean deleteClient(Long clientId){
        VRPClient vrpClient = vrpClientGraphRepository.findOne(clientId);
        vrpClient.setDeleted(true);
        save(vrpClient);
        return true;
    }



    public boolean updateClient(Long clientId,VRPClientDTO vrpClientDTO){
        VRPClient vrpClient = vrpClientGraphRepository.findOne(clientId,0);
        vrpClient.setBlock(vrpClientDTO.getBlock());
        vrpClient.setCity(vrpClientDTO.getCity());
        vrpClient.setFloorNumber(vrpClientDTO.getFloorNumber());
        vrpClient.setHouseNumber(vrpClientDTO.getHouseNumber());
        vrpClient.setZipCode(vrpClientDTO.getZipCode());
        vrpClient.setStreetName(vrpClientDTO.getStreetName());
        save(vrpClient);
        return true;
    }

}
