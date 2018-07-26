package com.kairos.service.javers;


import com.kairos.persistance.repository.master_data.asset_management.*;
import com.kairos.service.exception.ExceptionService;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.*;


@Service
public class JaversCommonService {


    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private Javers javers;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private StorageFormatMongoRepository storageFormatMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureRepository;

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;

    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;

    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;

    public List<Map<String, Object>> getHistoryMap(List<CdoSnapshot> auditHistoryList, BigInteger ownerId, Class clazz) throws ClassNotFoundException {
        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        int currPosition = auditHistoryList.size();
        for (CdoSnapshot snapShot : auditHistoryList) {
            Map<String, Object> historyMap = new HashMap<>();
            historyMap.put("commitMetaData", snapShot.getCommitMetadata());
            historyMap.put("fields", snapShot.getChanged());
            historyMap.put("values", getFieldsValue(snapShot, snapShot.getChanged(), ownerId, clazz));
            historyMap.put("oldValues", getOldFieldsValues(snapShot.getChanged(), auditHistoryList, currPosition, (int) snapShot.getVersion(), ownerId, clazz));
            historyMap.put("version", (int) snapShot.getVersion());
            if (currPosition==auditHistoryList.size()) {
                historyMap.put("isCreated", true);
            } else
                historyMap.put("isCreated", false);
            auditHistoryListData.add(historyMap);
            currPosition--;
        }
        Collections.reverse(auditHistoryListData);
        return auditHistoryListData;
    }

    private List<Object> getOldFieldsValues(List<String> fields, List<CdoSnapshot> auditHistoryList, int index, int version, BigInteger ownerId, Class ownerClass) {
        List<Object> oldValues = new ArrayList<Object>();
        for (String field : fields) {
            if (version >= 2) {
                if (auditHistoryList.get(index-1).getState().getPropertyValue(field) != null && auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof ValueObjectId) {
                    ValueObjectId valueObject = (ValueObjectId) auditHistoryList.get(index - 1).getState().getPropertyValue(field);
                    List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(ownerId, ownerClass, valueObject.getFragment()).build());
                    addChangedOrAuditvalueObject(valueObjectCdoSnapshots.get(0));
                } else if ( auditHistoryList.get(index-1).getState().getPropertyValue(field) != null && auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof ArrayList) {
                    ArrayList valueObjectList = (ArrayList) auditHistoryList.get(index - 1).getState().getPropertyValue(field);
                    if (valueObjectList.get(0) instanceof ValueObjectId) {
                        ValueObjectId valueObject = (ValueObjectId) valueObjectList.get(0);
                        List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(valueObject.masterObjectId(), ownerClass, valueObject.getFragment()).build());
                        oldValues.add(addChangedOrAuditvalueObject(valueObjectCdoSnapshots));
                    } else {
                        addRefrencedDataOrDefaultValue(oldValues, field, auditHistoryList.get(index - 1));
                    }
                } else {

                    addRefrencedDataOrDefaultValue(oldValues, field, auditHistoryList.get(index - 1));
                }
            }
        }
        return oldValues;
    }

    private List<Object> getFieldsValue(CdoSnapshot historyMap, List<String> fields, BigInteger ownerId, Class ownerClass) {
        List<Object> fieldValues = new ArrayList<Object>();
        for (String field : fields) {
            if ( historyMap.getState().getPropertyValue(field) != null  && historyMap.getState().getPropertyValue(field) instanceof ValueObjectId) {
                ValueObjectId valueObject = (ValueObjectId) historyMap.getState().getPropertyValue(field);
                List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(ownerId, ownerClass, valueObject.getFragment()).build());
                addChangedOrAuditvalueObject(valueObjectCdoSnapshots.get(0));

            } else if (historyMap.getState().getPropertyValue(field) != null  && historyMap.getState().getPropertyValue(field) instanceof ArrayList) {
                ArrayList valueObjectList = (ArrayList) historyMap.getState().getPropertyValue(field);
                if (valueObjectList.get(0) instanceof ValueObjectId) {
                    ValueObjectId valueObject = (ValueObjectId) valueObjectList.get(0);
                    List<CdoSnapshot> valueObjectCdoSnapshots = javers.findSnapshots(QueryBuilder.byValueObjectId(ownerId, ownerClass, valueObject.getFragment()).build());
                    fieldValues.add(addChangedOrAuditvalueObject(valueObjectCdoSnapshots));
                } else {
                    addRefrencedDataOrDefaultValue(fieldValues, field, historyMap);
                }
            } else {
                addRefrencedDataOrDefaultValue(fieldValues, field, historyMap);
            }
        }
        return fieldValues;
    }


    public List<Map<String, Object>> addChangedOrAuditvalueObject(List<CdoSnapshot> valueObjectSnapShots) {


        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        for (CdoSnapshot snapShot : valueObjectSnapShots) {
            Map<String, Object> historyMap = new HashMap<>();
            List<String> changed = snapShot.getChanged();
            for (int i = 0; i < changed.size(); i++) {
                historyMap.put(changed.get(i), snapShot.getPropertyValue(changed.get(i)));
            }
            auditHistoryListData.add(historyMap);
        }
        return auditHistoryListData;
    }

    public List<Map<String, Object>> addChangedOrAuditvalueObject(CdoSnapshot valueObjectSnapShot) {

        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        Map<String, Object> historyMap = new HashMap<>();
        List<String> changed = valueObjectSnapShot.getChanged();
        for (int i = 0; i < changed.size(); i++) {
            historyMap.put(changed.get(i), valueObjectSnapShot.getPropertyValue(changed.get(i)));
        }
        auditHistoryListData.add(historyMap);
        return auditHistoryListData;
    }

    public void addRefrencedDataOrDefaultValue(List<Object> fieldValues, String field, CdoSnapshot historyMap) {


        switch (field) {

            case ASSET_TYPE:
                fieldValues.add(assetTypeMongoRepository.findAssetTypeById((BigInteger) historyMap.getPropertyValue(field)));
                break;
            case ASSET_SUB_TYPE:
                fieldValues.add(assetTypeMongoRepository.findAssetTypeListByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case STORAGE_FORMAT:
                fieldValues.add(storageFormatMongoRepository.findAllStorageFormatByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case DATA_DISPOSAL:
                fieldValues.add(dataDisposalMongoRepository.findDataDisposalByid((BigInteger) historyMap.getPropertyValue(field)));
                break;
            case ORG_SECURITY_MEASURE:
                fieldValues.add(organizationalSecurityMeasureRepository.findOrganizationalSecurityMeasuresListByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case TECHNICAL_SECURITY_MEASURE:
                fieldValues.add(technicalSecurityMeasureMongoRepository.findTechnicalSecurityMeasuresListByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case HOSTING_PROVIDER:
                fieldValues.add(hostingProviderMongoRepository.findHostingProviderById((BigInteger) historyMap.getPropertyValue(field)));
                break;
            case HOSTING_TYPE:
                fieldValues.add(hostingTypeMongoRepository.findHostingTypeById((BigInteger) historyMap.getPropertyValue(field)));
                break;
            default:
                fieldValues.add((Object) historyMap.getState().getPropertyValue(field));
                break;


        }

    }


}
