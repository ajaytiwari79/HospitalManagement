package com.kairos.service.position;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.position.PositionName;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.position.PositionNameGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 27/7/17.
 */

@Transactional
@Service
public class PositionNameService extends UserBaseService {

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private PositionNameGraphRepository positionNameGraphRepository;


    public PositionName createPositionName(Long unitId, PositionName positionName) {
        PositionName position = null;

        String name = "(?i)" + positionName.getName();
        //check if duplicate
        position = positionNameGraphRepository.checkDuplicatePositionName(unitId, name);
        if (position != null) {
            throw new DuplicateDataException("PositionName already exist");
        }

        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Organization not found");
        }



        List<PositionName> positionNameList = organization.getPositionNameList();
        positionNameList = (positionNameList == null) ? new ArrayList<PositionName>() : positionNameList;
        positionNameList.add(positionName);
        organization.setPositionNameList(positionNameList);
        save(organization);

        return positionName;
    }


    public PositionName updatePositionName(Long unitId, Long positionNameId, PositionName positionName) {

        PositionName oldPositionName = positionNameGraphRepository.findOne(positionNameId);

        if (oldPositionName == null) {
            return null;
        }

        //check if new Name already exist
        if (!(oldPositionName.getName().equalsIgnoreCase(positionName.getName())) &&
                (positionNameGraphRepository.checkDuplicatePositionName(unitId, positionName.getName()) != null)) {
            throw new DuplicateDataException("PositionName can't be updated");
        }

        oldPositionName.setName(positionName.getName());
        oldPositionName.setDescription(positionName.getDescription());
        oldPositionName.setEnabled(positionName.isEnabled());

        save(oldPositionName);

        return oldPositionName;
    }


    public boolean deletePositionName(Long positionId) {
        PositionName position = positionNameGraphRepository.findOne(positionId);
        if (position == null) {
            return false;
        }
        position.setEnabled(false);
        save(position);
        if (positionNameGraphRepository.findOne(positionId).isEnabled()){
            return false;
        }
        return true;
    }


    public PositionName getPositionName(Long positionId) {
        return positionNameGraphRepository.findOne(positionId);
    }


    public List<PositionName> getAllPositionName(Long unitId) {
        return  organizationGraphRepository.getPositionNames(unitId);
    }




}
