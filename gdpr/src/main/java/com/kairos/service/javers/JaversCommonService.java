package com.kairos.service.javers;


import com.kairos.response.dto.common.JaversResponseMetadata;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
@Service
public class JaversCommonService {

    @Inject
    private Javers javers;

    @PersistenceContext
    private EntityManager entityManager;


    public List<Map<String, Object>> getHistoryMap(List<CdoSnapshot> auditHistoryList, Long ownerId, Class clazz) throws ClassNotFoundException {
        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        int currPosition = auditHistoryList.size();
        for (CdoSnapshot snapShot : auditHistoryList) {
            Map<String, Object> historyMap = new HashMap<>();
            historyMap.put("commitMetaData", snapShot.getCommitMetadata());
            historyMap.put("fields", snapShot.getChanged());
            historyMap.put("values", getFieldsValue(snapShot, snapShot.getChanged(), ownerId, clazz));
            historyMap.put("oldValues", getOldFieldsValues(snapShot.getChanged(), auditHistoryList, currPosition, (int) snapShot.getVersion(), ownerId, clazz));
            historyMap.put("version", (int) snapShot.getVersion());
            if (currPosition == auditHistoryList.size()) {
                historyMap.put("isCreated", true);
            } else
                historyMap.put("isCreated", false);
            auditHistoryListData.add(historyMap);
            currPosition--;
        }
        return auditHistoryListData;
    }

    private List<Object> getOldFieldsValues(List<String> fields, List<CdoSnapshot> auditHistoryList, int index, int version, Long ownerId, Class ownerClass) throws ClassNotFoundException {
        List<Object> oldValues = new ArrayList<>();
        for (String field : fields) {
            if (version >= 2) {

                if (auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof InstanceId) {
                    Class<?> clazz = Class.forName(((InstanceId) auditHistoryList.get(index - 1).getState().getPropertyValue(field)).getTypeName());
                    oldValues.add(findOne((long) ((InstanceId) auditHistoryList.get(index - 1).getState().getPropertyValue(field)).getCdoId(), clazz));
                } else if (auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof ValueObjectId) {
                    ValueObjectId valueObject = (ValueObjectId) auditHistoryList.get(index - 1).getState().getPropertyValue(field);
                    List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(ownerId, ownerClass, valueObject.getFragment()).build());
                    oldValues.add(addChangedOrAuditValueObject(valueObjectCdoSnapshots.get(0)).get(0));
                } else if (auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof ArrayList
                        && !((ArrayList) auditHistoryList.get(index - 1).getState().getPropertyValue(field)).isEmpty()) {
                    ArrayList valueObjectList = (ArrayList) auditHistoryList.get(index - 1).getState().getPropertyValue(field);
                    if (valueObjectList.get(0) instanceof ValueObjectId) {
                        ValueObjectId valueObject = (ValueObjectId) valueObjectList.get(0);
                        List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(valueObject.masterObjectId(), ownerClass, valueObject.getFragment()).build());
                        oldValues.add(addChangedOrAuditValueObject(valueObjectCdoSnapshots));
                    } else {
                        Class<?> clazz = Class.forName(((ArrayList<InstanceId>) auditHistoryList.get(index - 1).getState().getPropertyValue(field)).get(0).getTypeName());
                        oldValues.add(findAllByIds((ArrayList<InstanceId>) auditHistoryList.get(index - 1).getState().getPropertyValue(field), clazz));
                    }
                } else {

                    oldValues.add(auditHistoryList.get(index - 1).getState().getPropertyValue(field));
                }
            }
        }
        return oldValues;
    }


    private List<Object> getFieldsValue(CdoSnapshot historyMap, List<String> fields, Long ownerId, Class ownerClass) throws ClassNotFoundException {
        List<Object> fieldValues = new ArrayList<>();
        for (String field : fields) {
            if ( historyMap.getState().getPropertyValue(field) instanceof InstanceId) {
                Class<?> clazz = Class.forName(((InstanceId) historyMap.getState().getPropertyValue(field)).getTypeName());
                fieldValues.add(findOne(((long) ((InstanceId) historyMap.getPropertyValue(field)).getCdoId()), clazz));
            } else if (historyMap.getState().getPropertyValue(field) instanceof ValueObjectId) {
                ValueObjectId valueObject = (ValueObjectId) historyMap.getState().getPropertyValue(field);
                List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(ownerId, ownerClass, valueObject.getFragment()).build());
                fieldValues.add(addChangedOrAuditValueObject(valueObjectCdoSnapshots.get(0)).get(0));
            } else if (historyMap.getState().getPropertyValue(field) instanceof ArrayList && !((ArrayList) historyMap.getState().getPropertyValue(field)).isEmpty()) {
                ArrayList valueObjectList = (ArrayList) historyMap.getState().getPropertyValue(field);
                if (valueObjectList.get(0) instanceof ValueObjectId) {
                    ValueObjectId valueObject = (ValueObjectId) valueObjectList.get(0);
                    List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(ownerId, ownerClass, valueObject.getFragment()).build());
                    fieldValues.add(addChangedOrAuditValueObject(valueObjectCdoSnapshots));
                } else {
                    Class<?> clazz = Class.forName(((ArrayList<InstanceId>) historyMap.getState().getPropertyValue(field)).get(0).getTypeName());
                    fieldValues.add(findAllByIds((ArrayList<InstanceId>) historyMap.getState().getPropertyValue(field), clazz));
                }
            } else {
                fieldValues.add((Object) historyMap.getState().getPropertyValue(field));
            }
        }
        return fieldValues;
    }


    private JaversResponseMetadata findOne(Long id, Class clazz) {

        TypedQuery<JaversResponseMetadata> query = entityManager.createQuery("select NEW com.kairos.response.dto.common.JaversVO(t.id,t.name) from " + clazz.getSimpleName() + " t where t.id = :id", JaversResponseMetadata.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private  List<JaversResponseMetadata> findAllByIds(ArrayList<InstanceId> instanceIds, Class clazz) {
        List<Long> ids = new ArrayList<>();
        instanceIds.stream().map(InstanceId::getCdoId).forEach(o -> ids.add((long) o));
        TypedQuery<JaversResponseMetadata> query = entityManager.createQuery("select NEW com.kairos.response.dto.common.JaversVO(t.id,t.name)  from " + clazz.getSimpleName() + " t where t.id in (:ids)", JaversResponseMetadata.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }


    private List<Map<String, Object>> addChangedOrAuditValueObject(List<CdoSnapshot> valueObjectSnapShots) {


        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        for (CdoSnapshot snapShot : valueObjectSnapShots) {
            Map<String, Object> historyMap = new HashMap<>();
            List<String> changed = snapShot.getChanged();
            for (String s : changed) {
                historyMap.put(s, snapShot.getPropertyValue(s));
            }
            auditHistoryListData.add(historyMap);
        }
        return auditHistoryListData;
    }

    private List<Map<String, Object>> addChangedOrAuditValueObject(CdoSnapshot valueObjectSnapShot) {

        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        Map<String, Object> historyMap = new HashMap<>();
        List<String> changed = valueObjectSnapShot.getChanged();
        for (String s : changed) {
            historyMap.put(s, valueObjectSnapShot.getPropertyValue(s));
        }
        auditHistoryListData.add(historyMap);
        return auditHistoryListData;
    }
}
