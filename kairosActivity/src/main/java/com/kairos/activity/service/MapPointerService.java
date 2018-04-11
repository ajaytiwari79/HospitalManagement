package com.kairos.activity.service;

import com.kairos.activity.persistence.model.task_type.MapPointer;
import com.kairos.activity.persistence.repository.task_type.MapPointerMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by oodles on 23/11/16.
 */
@Service
public class MapPointerService extends MongoBaseService {

    @Inject
    MapPointerMongoRepository mapPointerMongoRepository;

    public MapPointer addMapPointer(MapPointer mapPointer){
        return save(mapPointer);
    }


    public MapPointer getMapPointer(String id){
        return mapPointerMongoRepository.findOne(new BigInteger(id));
    }

    public List<MapPointer> getAllMapPointer(){
        return mapPointerMongoRepository.findAll();
    }

    public MapPointer updateMapPointer(MapPointer mapPointer){
        MapPointer mp =mapPointerMongoRepository.findOne(mapPointer.getId());
        mp.setName(mapPointer.getName());
        mp.setIconURL(mapPointer.getIconURL());
        return save(mp);

    }

    public boolean deleteMapPointer(String stringId){
        BigInteger id = new BigInteger(stringId);
        mapPointerMongoRepository.deleteById(id);
        return !mapPointerMongoRepository.existsById(id);
    }
}
