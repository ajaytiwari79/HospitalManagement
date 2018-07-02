package com.kairos.service.javers;

import com.kairos.service.exception.ExceptionService;
import org.bson.types.ObjectId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JaversCommonService {


    @Inject
    private ExceptionService exceptionService;


    public List<Map<String, Object>> getHistoryOfVersions(List<CdoSnapshot> cdoSnapshotsList) {

        List<Map<String, Object>> versionHistory = new ArrayList<>();
        if (cdoSnapshotsList.isEmpty()) {
            exceptionService.invalidRequestException("message.invalid.request", "no version Available");
        } else {

            for (CdoSnapshot snapshot : cdoSnapshotsList) {
                Map<String, Object> result = new HashMap<>();
                List<String> changedProperties = snapshot.getChanged();
                result.put("Version", snapshot.getVersion());
                result.put("Type", snapshot.getType());
                result.put("Author", snapshot.getCommitMetadata().getAuthor());
                //result.put("version",snapshot.get());
                result.put("ChangedProperties", getListOfChangedProperties(changedProperties, snapshot));
                versionHistory.add(result);
            }
        }
        return versionHistory;
    }


    public Map<String, Object> getListOfChangedProperties(List<String> properties, CdoSnapshot snapshot) {
        Map<String, Object> changedProperties = new HashMap<>();
        properties.forEach(s -> {
            if (s.equals("id")) {
                changedProperties.put(s, snapshot.getPropertyValue(s).toString());
            } else {
                changedProperties.put(s, snapshot.getPropertyValue(s));
            }
        });
        return changedProperties;

    }


}
