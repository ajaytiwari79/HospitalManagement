package com.kairos.service.client;

import com.kairos.activity.response.dto.task.VRPTaskDTO;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.ObjectUtils;
import com.kairos.client.TaskServiceRestClient;
import com.kairos.client.TomTomRestClient;
import com.kairos.client.dto.TaskAddress;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.client.VRPClient;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.client.VRPClientGraphRepository;
import com.kairos.response.dto.web.client.VRPClientDTO;
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



    private double getValue(Cell cell){
        Double value = null;
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
        for (int i = 2;i<rows.size();i++){
            Row row = rows.get(i);
            VRPClient client = new VRPClient();
            client.setFirstName("Client "+(i-1));
            client.setInstallationNo((int) getValue(row.getCell(5)));
            client.setLattitude(getValue(row.getCell(14)));
            client.setLongitude(getValue(row.getCell(14)));
            client.setBlock(row.getCell(9).getStringCellValue());
            client.setCity(row.getCell(13).getStringCellValue());
            client.setDuration((int) row.getCell(0).getNumericCellValue());
            client.setFloorNo((int) row.getCell(10).getNumericCellValue());
            client.setHouseNo((int) row.getCell(8).getNumericCellValue());
            client.setPostCode(new Integer(row.getCell(12).getStringCellValue()));
            client.setStreetName(row.getCell(7).getStringCellValue());
            client.setOrganization(organization);
            VRPTaskDTO vrpTaskDTO = new VRPTaskDTO();
            vrpTaskDTO.setAddress(new TaskAddress(client.getPostCode(),client.getCity(),client.getStreetName(),""+client.getHouseNo(),client.getLattitude().toString(),client.getLattitude().toString(),client.getBlock(),client.getFloorNo()));
            vrpTaskDTO.setInstallationNo(client.getInstallationNo());
            vrpTaskDTO.setSkill(row.getCell(16).getStringCellValue());
            vrpTasks.add(vrpTaskDTO);
            vrpClients.add(client);
        }
        vrpClients = vrpClients.stream().filter(ObjectUtils.distinctByKey(vrpClient -> vrpClient.getInstallationNo())).collect(Collectors.toList());
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
            request.put("postalCode",""+vrpClient.getPostCode());
            request.put("countryCode","DK");
            Map response = tomTomRestClient.getfromTomtom(request);
            if(response!=null){
                vrpClientList.add(vrpClient);
            }

        });
        save(vrpClientList);
        createTask((List<VRPTaskDTO>)objects[1],vrpClientList);

        return ObjectMapperUtils.copyPropertiesOfListByMapper(vrpClientList, VRPClientDTO.class);
    }

    public void createTask(List<VRPTaskDTO> vrpTaskDTOS,List<VRPClient> vrpClients){
        Map<Integer,VRPClient> clientIdAndInstallationNo = vrpClients.stream().collect(Collectors.toMap(c->c.getInstallationNo(), c->c));
        for (VRPTaskDTO taskDTO : vrpTaskDTOS) {
            VRPClient vrpClient = clientIdAndInstallationNo.get(taskDTO.getInstallationNo());
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
        vrpClient.setFloorNo(vrpClientDTO.getFloorNo());
        vrpClient.setHouseNo(vrpClientDTO.getHouseNo());
        vrpClient.setPostCode(vrpClientDTO.getPost());
        vrpClient.setStreetName(vrpClientDTO.getStreetName());
        save(vrpClient);
        return true;
    }

}
