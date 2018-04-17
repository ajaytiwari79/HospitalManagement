package com.planning.repository;

import java.util.List;

import org.springframework.stereotype.Repository;




@Repository
public class TaskPlanningMongoRepository {
	
/*	@Autowired
	MongoTemplate mongoTemplate;

    void delete(String id,Class _className){
    	Query query = new Query(Criteria.where("_Id").is(id));
    	mongoTemplate.remove(query, _className);
    };
    
    public Object getAll(Class className) {
		return mongoTemplate.find(new Query(), className);
	}

    public <T> List<T> findAll(Class<T> _class) {
		return (List<T>) mongoTemplate.find(new Query(), _class);
	}

    Object findOne(String id,Class className){
    	Query query = new Query(Criteria.where("_id").is(id));
    	return mongoTemplate.findOne(query, className);
    };
    
    public Object findOne(String field,String data,Class className){
    	Query query = new Query(Criteria.where(field).is(data));
    	return mongoTemplate.findOne(query, className);
    };

    public Object save(Object object){
		mongoTemplate.save(object);
		return object;
    }
    
    public void saveList(List object){
    	for (Object object2 : object) {
			mongoTemplate.save(object2);
		}
    };*/
    

}
