package com.planner.repository.customRepository;


import com.planner.domain.common.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BaseRepository {
    private static Logger log = LoggerFactory.getLogger(BaseRepository.class);

   // @Autowired
    //CassandraTemplate cassandraTemplate;

    public <T> T findById(String id, Class class_Name) {
       // return (T)cassandraTemplate.selectOneById(id, class_Name);
        return null;
    }

    public Object findByIds(List<String> ids, Class class_Name) {
        return null;//cassandraTemplate.selectBySimpleIds(class_Name, ids);
    }

    public <T> List getAll(Class class_Name){
       /* Select select = QueryBuilder.select().from(class_Name.getSimpleName()).allowFiltering();
        select.where(QueryBuilder.eq("isDeleted", false));
        return cassandraTemplate.select(select,class_Name);*/
        return null;
    }

    public <T> T findByExternalId(long externalId, long unitId, Class class_Name) {
       /* Select select = QueryBuilder.select().from(class_Name.getSimpleName()).allowFiltering();
        select.where(QueryBuilder.eq("externalid", externalId));
        select.where(QueryBuilder.eq("unitid", unitId));
        select.where(QueryBuilder.eq("isDeleted", false));
        return (T) findOne(select, class_Name);*/
        return null;
    }

    public <T> List findAllByExternalId(List<Long> externalIds, long unitId, Class class_Name) {
        /*Select select = QueryBuilder.select().from(class_Name.getSimpleName()).allowFiltering();
        select.where(QueryBuilder.in("externalid", externalIds));
        select.where(QueryBuilder.eq("unitid", unitId));
        select.where(QueryBuilder.eq("isDeleted", false));
        return findAllByQuery(select, class_Name);*/
        return null;
    }

    public <T> List getAllByUnitId(long unitId,Class class_Name){
        /*Select select = QueryBuilder.select().from(class_Name.getSimpleName()).allowFiltering();
        select.where(QueryBuilder.eq("unitid", unitId));
        select.where(QueryBuilder.eq("isDeleted", false));
        return findAllByQuery(select,class_Name);*/
        return null;
    }

    public <T extends BaseEntity> boolean deleteByExternalId(Long externalId, Long unitId, Class class_Name) {
        if (externalId == null || unitId == null) return false;
        T entity = findByExternalId(externalId, unitId, class_Name);
        entity.setDeleted(true);
        saveEntity(entity);
        return true;
    }

    public <T extends BaseEntity> T saveEntity(T entity) {
        /*log.info("saveEntity Object SuccessFully");
        if (entity.getId() == null) {
            entity.setId(UUIDs.random().toString());
        }
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(new Date());
        } else {
            entity.setUpdatedDate(new Date());
        }*/
        return null;//cassandraTemplate.insert(entity);
    }

    public boolean saveEntity(Object object, Class class_Name) {
      //  return cassandraTemplate.exists(object,class_Name);
        return false;
    }

    public <T extends BaseEntity> List<T> saveList(List<T> entities) {
      /*  if (100>=entities.size()) {
            for (T entity : entities) {
                if (entity.getId() == null) {
                    entity.setId(UUIDs.random().toString());
                }
                if (entity.getCreatedDate() == null) {
                    entity.setCreatedDate(new Date());
                } else {
                    entity.setUpdatedDate(new Date());
                }
                saveEntity(entity);
            }
            return null;//cassandraTemplate.insert(entities);
        }
        else {
            return saveListByPagination(entities);
        }*/
        return null;
    }

    public <T extends BaseEntity> List<T> saveListByPagination(List<T> entities) {
        int end = 100, start = 0;
        List<T> savedEntities = new ArrayList<>(entities.size());
        if (end < entities.size()) {
            while (end < entities.size()) {
                if (end > entities.size()) {
                    end = entities.size();
                }
                savedEntities.addAll(saveList(entities.subList(start, end)));
                start = end + 1;
                end = end + 100;
            }
            savedEntities.addAll(saveList(entities.subList(start, entities.size())));
        }
        return savedEntities;
    }

   /* public void addColumnInTable(DataType type, String columnName) {
        log.info("column " + columnName + " created Successfully");
        *//*AlterTableSpecification alterTableSpecification = new AlterTableSpecification();
        alterTableSpecification.add(columnName, type);
        cassandraTemplate.execute(alterTableSpecification);*//*
    }*/

    public void deleteById(Object id, Class class_Name) {
        log.info("Object id" + id + " Successfully " + class_Name);
       // cassandraTemplate.deleteById(id,class_Name);
    }

    public void deleteByObject(Object object) {
        log.info("delete object Successfully " + object.getClass());
       // cassandraTemplate.delete(object);
    }

    public void deleteList(List objects) {
        log.info("Delete List of object Successfully " + objects.getClass());
       // cassandraTemplate.delete(objects);
    }

 /*   public void delete(Update query) {
        log.info("delete by query " + query);
        //cassandraTemplate.execute(query);
    }
*/
    public void update(Object object) {
        log.info("Update Successfully " + object.getClass());
       // cassandraTemplate.update(object);
    }

    public void updateList(List objects) {
        log.info("update List Successfully " + objects.getClass());
       // cassandraTemplate.update(objects);
    }

   /* public void update(Update query) {
        log.info("update by query " + query);
        //cassandraTemplate.execute(query);
    }*/

  public List findAll(Class class_Name) {
        log.info("find All Successfully " + class_Name);
        return null;//cassandraTemplate.selectAll(class_Name);
    }
 /*
    public List findAllByQuery(Select select, Class class_Name) {
        log.info("find All by Query " + select + " " + class_Name);
        //return cassandraTemplate.select(select, class_Name);
        return null;
    }

    public Object findOne(Select select, Class class_Name) {
        log.info("find One by query " + select + " " + class_Name);
        //return cassandraTemplate.selectOne(select, class_Name);
        return null;
    }

    public Object findByField(Select select, Class class_Name) {
        log.info("find One by query " + select + " " + class_Name);
        //return cassandraTemplate.selectOne(select, class_Name);
        return null;
    }*/

}
