package com.kairos.service.javers;


import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistance.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class JaversCommonService {


    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private Javers javers;


    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    public List<Map<String, Object>> getHistoryMap(List<CdoSnapshot> auditHistoryList, BigInteger ownerId, Class clazz) throws ClassNotFoundException {
        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        int currPosition = 0;
        for (CdoSnapshot snapShot : auditHistoryList) {
            Map<String, Object> historyMap = new HashMap<>();
            historyMap.put("commitMetaData", snapShot.getCommitMetadata());
            historyMap.put("fields", snapShot.getChanged());
            historyMap.put("values", getFieldsValue(snapShot, snapShot.getChanged(), ownerId, clazz));
            historyMap.put("oldValues", getOldFieldsValues(snapShot.getChanged(), auditHistoryList, currPosition, (int) snapShot.getVersion(), ownerId, clazz));
            historyMap.put("version", (int) snapShot.getVersion());
            if (currPosition == 0) {
                historyMap.put("isCreated", true);
            } else
                historyMap.put("isCreated", false);
            auditHistoryListData.add(historyMap);
            currPosition++;
        }
        Collections.reverse(auditHistoryListData);
        return auditHistoryListData;
    }

    private List<Object> getOldFieldsValues(List<String> fields, List<CdoSnapshot> auditHistoryList, int index, int version, BigInteger ownerId, Class ownerClass) throws ClassNotFoundException {
        List<Object> oldValues = new ArrayList<Object>();
        for (String field : fields) {
            if (version >= 2) {
                if (auditHistoryList.get(index - 1).getState().getPropertyValue(field) != null && auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof InstanceId) {
                    Class<?> clazz = Class.forName(((InstanceId) auditHistoryList.get(index - 1).getState().getPropertyValue(field)).getTypeName());
                    oldValues.add(findOne(new Query(Criteria.where("_id").is(((InstanceId) auditHistoryList.get(index - 1).getState().getPropertyValue(field)).getCdoId())), clazz));
                } else if (auditHistoryList.get(index - 1).getState().getPropertyValue(field) != null && auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof ArrayList) {
                    ArrayList valueObjectList = (ArrayList) auditHistoryList.get(index - 1).getState().getPropertyValue(field);
                    if (valueObjectList.get(0) instanceof ValueObjectId) {
                        ValueObjectId valueObject = (ValueObjectId) valueObjectList.get(0);
                        List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(valueObject.masterObjectId(), ownerClass, valueObject.getFragment()).build());
                        oldValues.add(getvalueObjectOrganizationTypeSubTypeServiceCategoryAndSubService(valueObjectCdoSnapshots));
                    } else {
                        Class<?> clazz = Class.forName(((ArrayList<InstanceId>) auditHistoryList.get(index - 1).getState().getPropertyValue(field)).get(0).getTypeName());
                        // fieldValues.add(getAll("id", getListOfIds((ArrayList<InstanceId>)historyMap.getState().getPropertyValue(field)), clazz));
                    }
                } else {

                    oldValues.add((Object) auditHistoryList.get(index - 1).getState().getPropertyValue(field));

                }
            }
        }
        return oldValues;
    }

    private List<Object> getFieldsValue(CdoSnapshot historyMap, List<String> fields, BigInteger ownerId, Class ownerClass) throws ClassNotFoundException {
        List<Object> fieldValues = new ArrayList<Object>();
        for (String field : fields) {
            if (historyMap.getState().getPropertyValue(field) != null && historyMap.getState().getPropertyValue(field) instanceof InstanceId) {
                Class<?> clazz = Class.forName(((InstanceId) historyMap.getState().getPropertyValue(field)).getTypeName());
                fieldValues.add(findOne(new Query(Criteria.where("id").is(((InstanceId) historyMap.getState().getPropertyValue(field)).getCdoId())), clazz));
            } else if (historyMap.getState().getPropertyValue(field) != null && historyMap.getState().getPropertyValue(field) instanceof ArrayList) {
                ArrayList valueObjectList = (ArrayList) historyMap.getState().getPropertyValue(field);
                if (valueObjectList.get(0) instanceof ValueObjectId) {
                    ValueObjectId valueObject = (ValueObjectId) valueObjectList.get(0);
                    List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(ownerId, ownerClass, valueObject.getFragment()).build());
                    fieldValues.add(getvalueObjectOrganizationTypeSubTypeServiceCategoryAndSubService(valueObjectCdoSnapshots));
                } else {
                    Class<?> clazz = Class.forName(((ArrayList<InstanceId>) historyMap.getState().getPropertyValue(field)).get(0).getTypeName());
                    // fieldValues.add(getAll("id", getListOfIds((ArrayList<InstanceId>)historyMap.getState().getPropertyValue(field)), clazz));
                }
            } else {
                fieldValues.add((Object) historyMap.getState().getPropertyValue(field));
            }
        }
        return fieldValues;
    }


    public <T> T findOne(Query query, Class<T> clazz) {
        return mongoTemplate.findOne(query, clazz);
    }


    public List<Map<String, Object>> getvalueObjectOrganizationTypeSubTypeServiceCategoryAndSubService(List<CdoSnapshot> valueObjectSnapShot) {


        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        for (CdoSnapshot snapShot : valueObjectSnapShot) {
            Map<String, Object> historyMap = new HashMap<>();
            List<String> changed = snapShot.getChanged();
            for (int i = 0; i < changed.size(); i++) {
                historyMap.put(changed.get(i), snapShot.getPropertyValue(changed.get(i)));
            }
            auditHistoryListData.add(historyMap);
        }
        return auditHistoryListData;
    }








}
