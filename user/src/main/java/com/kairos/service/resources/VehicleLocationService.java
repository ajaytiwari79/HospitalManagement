package com.kairos.service.resources;

import com.kairos.persistence.model.user.resources.VehicleLocation;
import com.kairos.persistence.repository.user.resources.VehicleLocationRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 13/12/17.
 */
@Service
@Transactional
public class VehicleLocationService {

    @Inject
    private VehicleLocationRepository vehicleLocationRepository;

    @Inject
    private ExceptionService exceptionService;

    public VehicleLocation createVehicleLocation(VehicleLocation vehicleLocation) {

        return vehicleLocationRepository.save(vehicleLocation);
    }

    public VehicleLocation updateVehicleLocation(VehicleLocation vehicleLocation, Long vehicleLocationId) {

        VehicleLocation existingVehicleLocation = vehicleLocationRepository.findOne(vehicleLocationId);
        if (Optional.ofNullable(existingVehicleLocation).isPresent()) {
            existingVehicleLocation.setName(vehicleLocation.getName());
            existingVehicleLocation.setDescription(vehicleLocation.getDescription());
            return vehicleLocationRepository.save(existingVehicleLocation);
        } else {
            exceptionService.dataNotFoundByIdException("message.vehiclelocationservices.id.notFound");

        }
        return null;
    }

    /**
     * Safe-Delete a VehicleLocation by id
     *
     * @param vehicleLocationId
     */
    public boolean deleteVehicleLocation(Long vehicleLocationId) {
        VehicleLocation vehicleLocation = vehicleLocationRepository.findOne(vehicleLocationId);
        if (Optional.ofNullable(vehicleLocation).isPresent()) {
            vehicleLocation.setEnabled(false);
            return vehicleLocationRepository.save(vehicleLocation) != null;
        } else{
            exceptionService.dataNotFoundByIdException("message.vehiclelocationservices.id.notFound");
    }
return false;
}
    public List<VehicleLocation> getAllVehicleLocations() {
        return vehicleLocationRepository.findAll();
    }
}
