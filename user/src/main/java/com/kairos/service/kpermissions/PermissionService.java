package com.kairos.service.kpermissions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.kpermissions.*;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.kpermissions.*;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.repository.kpermissions.CommonRepository;
import com.kairos.persistence.repository.kpermissions.PermissionFieldRepository;
import com.kairos.persistence.repository.kpermissions.PermissionModelRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.context.EntityRowModelMapper;
import org.neo4j.ogm.context.GraphEntityMapper;
import org.neo4j.ogm.context.MappingContext;
import org.neo4j.ogm.context.ResponseMapper;
import org.neo4j.ogm.cypher.query.*;
import org.neo4j.ogm.metadata.ClassInfo;
import org.neo4j.ogm.metadata.FieldInfo;
import org.neo4j.ogm.metadata.reflect.ReflectionEntityInstantiator;
import org.neo4j.ogm.model.GraphModel;
import org.neo4j.ogm.model.RowModel;
import org.neo4j.ogm.request.GraphModelRequest;
import org.neo4j.ogm.request.RowModelRequest;
import org.neo4j.ogm.response.Response;
import org.neo4j.ogm.session.LoadStrategy;
import org.neo4j.ogm.session.Neo4jSession;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.request.strategy.LoadClauseBuilder;
import org.neo4j.ogm.session.request.strategy.QueryStatements;
import org.neo4j.ogm.session.request.strategy.impl.*;
import org.neo4j.ogm.transaction.Transaction;
import org.neo4j.ogm.utils.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ApplicationConstants.*;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_DATANOTFOUND;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_PERMISSION_FIELD;
import static org.neo4j.ogm.session.LoadStrategy.PATH_LOAD_STRATEGY;
import static org.neo4j.ogm.session.LoadStrategy.SCHEMA_LOAD_STRATEGY;

@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    @Inject
    private PermissionModelRepository permissionModelRepository;

    @Inject
    private AccessGroupRepository accessGroupRepository;

    @Inject
    private PermissionFieldRepository permissionFieldRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject private AccessGroupService accessGroupService;
    @Inject private CommonRepository commonRepository;

    @Autowired
    //@Qualifier("PermissionSessionFactory")
    private SessionFactory sessionFactory;

    @Inject
    private ApplicationContext applicationContext;

    private static final Pattern WRITE_CYPHER_KEYWORDS = Pattern.compile("\\b(CREATE|MERGE|SET|DELETE|REMOVE|DROP)\\b");

    public List<ModelDTO> createPermissionSchema(List<ModelDTO> modelDTOS){
        Map<String,KPermissionModel> modelNameAndModelMap = StreamSupport.stream(permissionModelRepository.findAll().spliterator(), false).filter(it -> !it.isPermissionSubModel()).collect(Collectors.toMap(k->k.getModelName().toLowerCase(),v->v));
        List<KPermissionModel> kPermissionModels = buildPermissionModelData(modelDTOS, modelNameAndModelMap, false);
        permissionModelRepository.save(kPermissionModels,2);
        return modelDTOS;
    }

    private List<KPermissionModel> buildPermissionModelData(List<ModelDTO> modelDTOS, Map<String,KPermissionModel> modelNameAndModelMap, boolean isSubModel){
        List<ModelDTO> newModelDTO = new ArrayList<>();
        List<KPermissionModel> kPermissionModels = new ArrayList<>();
        modelDTOS.forEach(modelDTO -> {
            if(modelNameAndModelMap.containsKey(modelDTO.getModelName().toLowerCase())){
                KPermissionModel kPermissionModel = updateModelSchemma(modelNameAndModelMap, modelDTO);
                kPermissionModel.setPermissionSubModel(isSubModel);
                updateSubmodelSchema(modelDTO, kPermissionModel);
                kPermissionModels.add(kPermissionModel);
            }else{
                updateModel(isSubModel, modelDTO);
                newModelDTO.add(modelDTO);

            }

        });
        kPermissionModels.addAll(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(newModelDTO, KPermissionModel.class));
        return kPermissionModels;
    }

    private void updateModel(boolean isSubModel, ModelDTO modelDTO) {
        modelDTO.setOrganizationCategories(new HashSet<>());
        modelDTO.getFieldPermissions().forEach(fieldDTO -> fieldDTO.setOrganizationCategories(new HashSet<>()));
        modelDTO.setPermissionSubModel(isSubModel);
        if(isCollectionNotEmpty(modelDTO.getSubModelPermissions())){
            modelDTO.getSubModelPermissions().forEach(modelDTO1 ->updateModel(true,modelDTO1));
        }
    }

    private KPermissionModel updateModelSchemma(Map<String, KPermissionModel> modelNameAndModelMap, ModelDTO modelDTO) {
        KPermissionModel kPermissionModel = modelNameAndModelMap.get(modelDTO.getModelName().toLowerCase());
        kPermissionModel.setOrganizationCategories(new HashSet<>());
        Set<String> fields = kPermissionModel.getFieldPermissions().stream().map(KPermissionField::getFieldName).collect(Collectors.toSet());
        modelDTO.getFieldPermissions().forEach(fieldDTO -> {
            if(!fields.contains(fieldDTO.getFieldName())){
                kPermissionModel.getFieldPermissions().add(new KPermissionField(fieldDTO.getFieldName(),new HashSet<>()));
            }
        });
        return kPermissionModel;
    }

    private void updateSubmodelSchema(ModelDTO modelDTO, KPermissionModel kPermissionModel) {
        if (!modelDTO.getSubModelPermissions().isEmpty()) {
            Map<String,KPermissionModel> subModelNameAndModelMap = new HashMap<>();
            if(isCollectionNotEmpty(kPermissionModel.getSubModelPermissions())){
                subModelNameAndModelMap = kPermissionModel.getSubModelPermissions().stream().collect(Collectors.toMap(k->k.getModelName().toLowerCase(), v->v));
            }
            kPermissionModel.getSubModelPermissions().addAll(buildPermissionModelData(modelDTO.getSubModelPermissions(), subModelNameAndModelMap, true));
        }
    }

    public List<ModelDTO> getPermissionSchema(){
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(kPermissionModels, ModelDTO.class);
    }

    public Map<String, Object> getPermissionSchema(List<Long> accessGroupIds){
        Map<String, Object> permissionSchemaMap = new HashMap<>();
        List<KPermissionModel> kPermissionModels = getkPermissionModels();
        permissionSchemaMap.put(PERMISSIONS_SCHEMA,ObjectMapperUtils.copyPropertiesOfCollectionByMapper(kPermissionModels, ModelDTO.class));
        permissionSchemaMap.put(PERMISSIONS, FieldLevelPermission.values());
        permissionSchemaMap.put(PERMISSION_DATA, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(getModelPermission(newArrayList(),accessGroupIds,false),ModelDTO.class));
            return permissionSchemaMap;
    }

    private List<KPermissionModel> getkPermissionModels() {
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().forEach(kPermissionModel -> {
            if(!kPermissionModel.isPermissionSubModel()){
                kPermissionModels.add(kPermissionModel);
            }
        });
        return kPermissionModels;
    }

    private Map[] getMapOfPermission(Collection<Long> accessGroupIds,boolean hubMember) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = permissionModelRepository.getAllModelPermission(accessGroupIds);
        List<FieldPermissionQueryResult> fieldLevelPermissions = permissionModelRepository.getAllFieldPermission(accessGroupIds);
        Map<Long,Set<FieldLevelPermission>> fieldLevelPermissionMap = new HashMap<>();
        Map<Long,Set<FieldLevelPermission>> modelPermissionMap = new HashMap<>();
        if(isCollectionNotEmpty(modelPermissionQueryResults)){
            modelPermissionMap = modelPermissionQueryResults.stream().collect(Collectors.toMap(ModelPermissionQueryResult::getId,v->getFieldPermissionByPriority(v.getPermissions(),hubMember)));
        }
        if(isCollectionNotEmpty(fieldLevelPermissions)){
            fieldLevelPermissionMap = fieldLevelPermissions.stream().collect(Collectors.toMap(FieldPermissionQueryResult::getId,v->getFieldPermissionByPriority(v.getPermissions(),hubMember)));
        }
        return new Map[]{modelPermissionMap,fieldLevelPermissionMap};
    }

    private Set<FieldLevelPermission> getFieldPermissionByPriority(Set<FieldLevelPermission> fieldLevelPermissions,boolean hubMember){
        /*if(fieldLevelPermissions.size()>1){
            if(fieldLevelPermissions.contains(FieldLevelPermission.WRITE)){
                fieldLevelPermissions.removeIf(fieldLevelPermission->!FieldLevelPermission.WRITE.equals(fieldLevelPermission));
            }else if(fieldLevelPermissions.contains(FieldLevelPermission.READ)){
                fieldLevelPermissions.remove(FieldLevelPermission.HIDE);
            }
        }*/
        return hubMember ? newHashSet(FieldLevelPermission.WRITE,FieldLevelPermission.READ) : fieldLevelPermissions;
    }

    public List<ModelPermissionQueryResult> getModelPermission(List<String> modelNames,Collection<Long> accessGroupIds,boolean hubMember){
        Map[] permissionMap = getMapOfPermission(accessGroupIds,hubMember);
        Map<Long,Set<FieldLevelPermission>> modelPermissionMap = permissionMap[0];
        Map<Long,Set<FieldLevelPermission>> fieldLevelPermissionMap = permissionMap[1];
        OrganizationCategory organizationCategory = UserContext.getUserDetails().getLastSelectedOrganizationCategory();
        List<KPermissionModel> kPermissionModels;
        if(isCollectionNotEmpty(modelNames)){
            kPermissionModels = permissionModelRepository.getAllPermissionModelByName(modelNames);
        }else {
            kPermissionModels = getkPermissionModels();
        }
        return getModelPermissionQueryResults(kPermissionModels, modelPermissionMap, fieldLevelPermissionMap,organizationCategory,hubMember);
    }

    private List<ModelPermissionQueryResult> getModelPermissionQueryResults(List<KPermissionModel> kPermissionModels, Map<Long, Set<FieldLevelPermission>> modelPermissionMap, Map<Long, Set<FieldLevelPermission>> fieldLevelPermissionMap,OrganizationCategory organizationCategory, boolean hubMember) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = new ArrayList<>();
        for (KPermissionModel kPermissionModel : kPermissionModels) {
           // if(isValidOrganizationCategory(organizationCategory,hubMember,kPermissionModel.getOrganizationCategories()) || true) {
                Set<FieldLevelPermission> modelPermission = hubMember ? newHashSet(FieldLevelPermission.WRITE,FieldLevelPermission.READ) : modelPermissionMap.getOrDefault(kPermissionModel.getId(),new HashSet<>());
                modelPermissionQueryResults.add(new ModelPermissionQueryResult(kPermissionModel.getId(), kPermissionModel.getModelName(), getFieldLevelPermissionQueryResult(fieldLevelPermissionMap, kPermissionModel.getFieldPermissions(), organizationCategory, hubMember), getModelPermissionQueryResults(kPermissionModel.getSubModelPermissions(), modelPermissionMap, fieldLevelPermissionMap, organizationCategory, hubMember), modelPermission));
            //}
        }
        return modelPermissionQueryResults;
    }

    private boolean isValidOrganizationCategory(OrganizationCategory organizationCategory, boolean hubMember, Set<OrganizationCategory> organizationCategories) {
        return hubMember ? hubMember : organizationCategories.contains(organizationCategory);
    }

    private List<FieldPermissionQueryResult> getFieldLevelPermissionQueryResult(Map<Long,Set<FieldLevelPermission>> fieldLevelPermissionMap,List<KPermissionField> fields,OrganizationCategory organizationCategory, boolean hubMember){
        List<FieldPermissionQueryResult> fieldPermissionQueryResults = new ArrayList<>();
        for (KPermissionField field : fields) {
            //if(isValidOrganizationCategory(organizationCategory,hubMember,field.getOrganizationCategories()) || true) {
                Set<FieldLevelPermission> fieldLevelPermissions = hubMember ? newHashSet(FieldLevelPermission.WRITE,FieldLevelPermission.READ) : fieldLevelPermissionMap.getOrDefault(field.getId(), new HashSet<>());
                fieldPermissionQueryResults.add(new FieldPermissionQueryResult(field.getId(), field.getFieldName(), fieldLevelPermissions));
            //}
        }
        return fieldPermissionQueryResults;
    }

    public PermissionDTO createPermissions(PermissionDTO permissionDTO,boolean updateOrganisationCategories){
        updateOrganisationCategoryOrPermissions(permissionDTO.getModelPermissions(), permissionDTO.getAccessGroupIds(),updateOrganisationCategories);
        return permissionDTO;
    }


    public void updateOrganisationCategoryOrPermissions(List<ModelDTO> modelPermissionDTOS, List<Long> accessGroupIds, boolean updateOrganisationCategories){
        modelPermissionDTOS.forEach(modelPermissionDTO -> {
            KPermissionModel kPermissionModel = null;
            for(FieldDTO fieldDTO : modelPermissionDTO.getFieldPermissions()){
                KPermissionFieldQueryResult kPermissionFieldQueryResult = getkPermissionFieldQueryResult(modelPermissionDTO, fieldDTO);
                kPermissionModel = kPermissionFieldQueryResult.getKPermissionModel();
                KPermissionField kPermissionField = kPermissionFieldQueryResult.getKPermissionField();
                updatePermissionOrOrganisationCategory(accessGroupIds, updateOrganisationCategories, fieldDTO, kPermissionField);
            }
            updateModelPermissionOrOrganisationCategory(accessGroupIds, updateOrganisationCategories, modelPermissionDTO, kPermissionModel);
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                updateOrganisationCategoryOrPermissions(modelPermissionDTO.getSubModelPermissions(), accessGroupIds,updateOrganisationCategories);
            }
        });
    }

    private void updatePermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, FieldDTO fieldDTO, KPermissionField kPermissionField) {
        if(updateOrganisationCategories){
            kPermissionField.setOrganizationCategories(fieldDTO.getOrganizationCategories());
        }else {
            if(kPermissionField == null){
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_PERMISSION_FIELD, fieldDTO.getId());
            }else{
                permissionModelRepository.createAccessGroupPermissionFieldRelationshipType(kPermissionField.getId(),accessGroupIds,fieldDTO.getPermissions());
            }
        }
    }

    private void updateModelPermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, ModelDTO modelDTO, KPermissionModel kPermissionModel) {
        if(updateOrganisationCategories){
            kPermissionModel.setOrganizationCategories(modelDTO.getOrganizationCategories());
        }else {
            permissionModelRepository.createAccessGroupPermissionModelRelationship(kPermissionModel.getId(), accessGroupIds, modelDTO.getPermissions());
        }
    }

    private KPermissionFieldQueryResult getkPermissionFieldQueryResult(ModelDTO modelDTO, FieldDTO fieldDTO) {
        KPermissionFieldQueryResult kPermissionFieldQueryResult = permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelDTO.getId(), fieldDTO.getId());
        if (kPermissionFieldQueryResult == null) {
            exceptionService.dataNotFoundByIdException("message.permission.KPermissionFieldQueryResult");
        }
        return kPermissionFieldQueryResult;
    }

    public <T extends UserBaseEntity,E extends UserBaseEntity> List<T> updateModelBasisOfPermission(List<T> objects){
        try {
            Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
            Set<String> modelNames = objects.stream().map(model->model.getClass().getSimpleName()).collect(Collectors.toSet());
            List<AccessGroup> accessGroups =  accessGroupService.validAccessGroupByDate(unitId,getDate());
            boolean hubMember = UserContext.getUserDetails().isHubMember();
            List<ModelPermissionQueryResult> modelPermissionQueryResults = getModelPermission(new ArrayList(modelNames),accessGroups.stream().map(accessGroup -> accessGroup.getId()).collect(Collectors.toSet()),hubMember);
            List<ModelDTO> modelDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(modelPermissionQueryResults,ModelDTO.class);
            Map<String,ModelDTO> modelMap = modelDTOS.stream().collect(Collectors.toMap(k->k.getModelName(),v->v));
            Collection<Long> objectIds = objects.stream().filter(model->isNotNull(model.getId())).map(model->model.getId()).collect(Collectors.toList());
            List<E> dataBaseObjects = loadAll(Staff.class,objectIds,new SortOrder(),null,2).stream().map(staff -> (E)staff).collect(Collectors.toList());
            Map<Long,E> mapOfDataBaseObject = dataBaseObjects.stream().collect(Collectors.toMap(k->k.getId(),v->v));
            updateObjectsPropertiesBeforeSave(mapOfDataBaseObject,modelMap,objects);
           // neo4jSession.transactionManager().clear();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return objects;
    }

    public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, Collection<ID> ids, SortOrder sortOrder,
                                                              Pagination pagination, int depth) {

        String entityLabel = sessionFactory.metaData().entityType(type.getName());
        if (entityLabel == null) {
            LOGGER.warn("Unable to find database label for entity " + type.getName()
                    + " : no results will be returned. Make sure the class is registered, "
                    + "and not abstract without @NodeEntity annotation");
        }
        QueryStatements<ID> queryStatements = queryStatementsFor(type, depth);

        PagingAndSortingQuery qry = queryStatements.findAllByType(entityLabel, ids, depth)
                .setSortOrder(sortOrder)
                .setPagination(pagination);

        GraphModelRequest request = new DefaultGraphModelRequest(qry.getStatement(), qry.getParameters());
            try (Response<GraphModel> response = sessionFactory.getDriver().request().execute(request)) {
                Iterable<T> mapped = new GraphEntityMapper(sessionFactory.metaData(), new MappingContext(sessionFactory.metaData()), new ReflectionEntityInstantiator(sessionFactory.metaData())).map(type, response);

                if (sortOrder.sortClauses().isEmpty()) {
                    return sortResultsByIds(type, ids, mapped);
                }
                Set<T> results = new LinkedHashSet<>();
                for (T entity : mapped) {
                    if (includeMappedEntity(ids, entity)) {
                        results.add(entity);
                    }
                }
                return results;
            }
    }

    private <T, ID extends Serializable> boolean includeMappedEntity(Collection<ID> ids, T mapped) {

        final ClassInfo classInfo = sessionFactory.metaData().classInfo(mapped);
        final FieldInfo primaryIndexField = classInfo.primaryIndexField();

        if (primaryIndexField != null) {
            final Object primaryIndexValue = primaryIndexField.read(mapped);
            if (ids.contains(primaryIndexValue)) {
                return true;
            }
        }
        Object id = EntityUtils.identity(mapped, sessionFactory.metaData());
        return ids.contains(id);
    }

    private <T, ID extends Serializable> Set<T> sortResultsByIds(Class<T> type, Collection<ID> ids,
                                                                 Iterable<T> mapped) {
        Map<ID, T> items = new HashMap<>();
        ClassInfo classInfo = sessionFactory.metaData().classInfo(type.getName());

        FieldInfo idField = classInfo.primaryIndexField();
        if (idField == null) {
            idField = classInfo.identityField();
        }

        for (T t : mapped) {
            Object id = idField.read(t);
            if (id != null) {
                items.put((ID) id, t);
            }
        }

        Set<T> results = new LinkedHashSet<>();
        for (ID id : ids) {
            T item = items.get(id);
            if (item != null) {
                results.add(item);
            }
        }
        return results;
    }

    public <T, ID extends Serializable> QueryStatements<ID> queryStatementsFor(Class<T> type, int depth) {
        final FieldInfo fieldInfo = sessionFactory.metaData().classInfo(type.getName()).primaryIndexField();
        String primaryIdName = fieldInfo != null ? fieldInfo.property() : null;
        if (sessionFactory.metaData().isRelationshipEntity(type.getName())) {
            return new RelationshipQueryStatements<>(primaryIdName, loadRelationshipClauseBuilder(depth,LoadStrategy.PATH_LOAD_STRATEGY));
        } else {
            return new NodeQueryStatements<>(primaryIdName, loadNodeClauseBuilder(depth,LoadStrategy.PATH_LOAD_STRATEGY));
        }
    }

    private LoadClauseBuilder loadRelationshipClauseBuilder(int depth,LoadStrategy loadStrategy) {
        if (depth < 0) {
            throw new IllegalArgumentException("Can't load unlimited depth for relationships");
        }

        switch (loadStrategy) {
            case PATH_LOAD_STRATEGY:
                return new PathRelationshipLoadClauseBuilder();

            case SCHEMA_LOAD_STRATEGY:
                return new SchemaRelationshipLoadClauseBuilder(sessionFactory.metaData().getSchema());

            default:
                throw new IllegalStateException("Unknown loadStrategy " + loadStrategy);
        }
    }

    private LoadClauseBuilder loadNodeClauseBuilder(int depth,LoadStrategy loadStrategy) {
        if (depth < 0) {
            return new PathNodeLoadClauseBuilder();
        }

        switch (loadStrategy) {
            case PATH_LOAD_STRATEGY:
                return new PathNodeLoadClauseBuilder();

            case SCHEMA_LOAD_STRATEGY:
                return new SchemaNodeLoadClauseBuilder(sessionFactory.metaData().getSchema());

            default:
                throw new IllegalStateException("Unknown loadStrategy " + loadStrategy);
        }
    }

   public <T extends UserBaseEntity,E extends UserBaseEntity> void updateObjectsPropertiesBeforeSave(Map<Long,E> mapOfDataBaseObject,Map<String,ModelDTO> modelMap,List<T> objects){
        for (T object : objects) {
            ObjectMapperUtils.copySpecificPropertiesByMapper(object,mapOfDataBaseObject.get(object.getId()),modelMap.get(object.getClass().getSimpleName()));
        }
    }

     /*private <T> Iterable<T> executeAndMap(Class<T> type, String cypher, Map<String, ?> parameters,
                                          ResponseMapper mapper) {
        if (type != null && sessionFactory.metaData().classInfo(type.getSimpleName()) != null) {
            GraphModelRequest request = new DefaultGraphModelRequest(cypher, parameters);
            try (Response<GraphModel> response = sessionFactory.getDriver().request().execute(request)) {
                return new GraphEntityMapper(sessionFactory.metaData(), new MappingContext(sessionFactory.metaData()), new ReflectionEntityInstantiator(sessionFactory.metaData())).map(type, response);
            }
        } else {
            RowModelRequest request = new DefaultRowModelRequest(cypher, parameters);
            try (Response<RowModel> response = sessionFactory.getDriver().request().execute(request)) {
                return mapper.map(type, response);
            }
        }
    }

    public <T> Iterable<T> query(Class<T> type, String cypher, Map<String, ?> parameters) {
        validateQuery(cypher, parameters, false); //we'll allow modifying statements
        if (type == null || type.equals(Void.class)) {
            throw new RuntimeException("Supplied type must not be null or void.");
        }
        return executeAndMap(type, cypher, parameters, new EntityRowModelMapper());
    }

    private void validateQuery(String cypher, Map<String, ?> parameters, boolean readOnly) {

        if (readOnly && !isReadOnly(cypher)) {
            throw new RuntimeException("Cypher query must not modify the graph if readOnly=true");
        }

        if (StringUtils.isEmpty(cypher)) {
            throw new RuntimeException("Supplied cypher statement must not be null or empty.");
        }

        if (parameters == null) {
            throw new RuntimeException("Supplied Parameters cannot be null.");
        }
    }

    private boolean isReadOnly(String cypher) {
        Matcher matcher = WRITE_CYPHER_KEYWORDS.matcher(cypher.toUpperCase());
        return !matcher.find();
    }*/
}
