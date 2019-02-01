package com.kairos.service.javers;


import org.springframework.stereotype.Service;


@Service
public class JaversCommonService {

   /* @Inject
    private Javers javers;

    @Inject
    private ExceptionService exceptionService;

    public List<Map<String, Object>> getHistoryMap(List<CdoSnapshot> auditHistoryList, BigInteger ownerId, Class clazz) {
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

    private List<Object> getOldFieldsValues(List<String> fields, List<CdoSnapshot> auditHistoryList, int index, int version, BigInteger ownerId, Class ownerClass) {
        List<Object> oldValues = new ArrayList<>();
        for (String field : fields) {
            if (version >= 2) {
                if (auditHistoryList.get(index - 1).getState().getPropertyValue(field) instanceof ValueObjectId) {
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
                        addReferencedDataOrDefaultValue(oldValues, field, auditHistoryList.get(index - 1));
                    }
                } else {

                    addReferencedDataOrDefaultValue(oldValues, field, auditHistoryList.get(index - 1));
                }
            }
        }
        return oldValues;
    }

    private List<Object> getFieldsValue(CdoSnapshot historyMap, List<String> fields, BigInteger ownerId, Class ownerClass) {
        List<Object> fieldValues = new ArrayList<>();
        for (String field : fields) {
            if (historyMap.getState().getPropertyValue(field) instanceof ValueObjectId) {
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
                    addReferencedDataOrDefaultValue(fieldValues, field, historyMap);
                }
            } else {
                addReferencedDataOrDefaultValue(fieldValues, field, historyMap);
            }
        }
        return fieldValues;
    }


    public List<Map<String, Object>> addChangedOrAuditValueObject(List<CdoSnapshot> valueObjectSnapShots) {


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

    public List<Map<String, Object>> addChangedOrAuditValueObject(CdoSnapshot valueObjectSnapShot) {

        List<Map<String, Object>> auditHistoryListData = new ArrayList<>();
        Map<String, Object> historyMap = new HashMap<>();
        List<String> changed = valueObjectSnapShot.getChanged();
        for (int i = 0; i < changed.size(); i++) {
            historyMap.put(changed.get(i), valueObjectSnapShot.getPropertyValue(changed.get(i)));
        }
        auditHistoryListData.add(historyMap);
        return auditHistoryListData;
    }

    public void addReferencedDataOrDefaultValue(List<Object> fieldValues, String field, CdoSnapshot historyMap) {

//TODO
        switch (field) {

            /*case ASSET_TYPE_KEY:
                fieldValues.add(assetTypeMongoRepository.findAssetTypeById((BigInteger) historyMap.getPropertyValue(field)));
                break;
            case ASSET_SUB_TYPE_KEY:
                fieldValues.add(assetTypeMongoRepository.findAssetTypeListByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case STORAGE_FORMAT_KEY:
                fieldValues.add(storageFormatMongoRepository.findStorageFormatByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case DATA_DISPOSAL_KEY:
                fieldValues.add(dataDisposalMongoRepository.findDataDisposalByid((BigInteger) historyMap.getPropertyValue(field)));
                break;
            case ORG_SECURITY_MEASURE_KEY:
                fieldValues.add(organizationalSecurityMeasureRepository.findOrganizationalSecurityMeasuresListByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case TECHNICAL_SECURITY_MEASURE_KEY:
                fieldValues.add(technicalSecurityMeasureMongoRepository.findTechnicalSecurityMeasuresListByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case HOSTING_PROVIDER_KEY:
                fieldValues.add(hostingProviderMongoRepository.findHostingProviderById((BigInteger) historyMap.getPropertyValue(field)));
                break;
            case HOSTING_TYPE_KEY:
                fieldValues.add(hostingTypeMongoRepository.findHostingTypeById((BigInteger) historyMap.getPropertyValue(field)));
                break;
            //processing activity keys
            case PROCESSING_PURPOSE_KEY:
                fieldValues.add(processingPurposeMongoRepository.findProcessingPurposeByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case DATA_SOURCE_KEY:
                fieldValues.add(dataSourceMongoRepository.findDataSourceByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case TRANSFER_METHOD_KEY:
                fieldValues.add(transferMethodMongoRepository.findTransferMethodByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case ACCESSOR_PARTY_KEY:
                fieldValues.add(accessorPartyMongoRepository.findAccessorPartyByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case PROCESSING_LEGAL_BASIS_KEY:
                fieldValues.add(processingLegalBasisMongoRepository.findProcessingLegalBasisByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            case RESPONSIBILITY_TYPE_KEY:
                fieldValues.add(responsibilityTypeMongoRepository.findResponsibilityTypeByid((BigInteger) historyMap.getPropertyValue(field)));
                break;
            case SUB_PROCESSING_ACTIVITY_KEY:
                fieldValues.add(processingActivityMongoRepository.findAllSubProcessingActivitiesByIds((List<BigInteger>) historyMap.getPropertyValue(field)));
                break;
            default:
                fieldValues.add(historyMap.getState().getPropertyValue(field));
                break;

        }

    }*/


}
