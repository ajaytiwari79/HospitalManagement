package com.kairos.service.data_inventory.data_category_element;


import com.kairos.dto.master_data.DataElementDTO;
import com.kairos.persistance.repository.master_data.data_category_element.DataElementMongoRepository;
import com.kairos.service.common.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class OrganizationDataElementService extends MongoBaseService {






    @Inject
    private DataElementMongoRepository dataElementMongoRepository;



    public void createDataElements(Long unitId, List<DataElementDTO> dataElementDTOS){








    }



}
