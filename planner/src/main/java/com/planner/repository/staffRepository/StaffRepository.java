package com.planner.repository.staffRepository;

import com.planner.domain.staff.Staff;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Repository("NewStaffRepository")
public class StaffRepository  {

    @Inject
    private Session session;

   public  String  findStaffNameById(Long staffId)
    {
        String result="Not executed";
        if(session!=null)
        {
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("staffId", staffId);
            String cypherQuery = "Match(s:Staff) where id(s)=1576 return s.email";
            //Result queryResult = session.query(cypherQuery, queryMap);
           result=session.queryForObject(String.class,cypherQuery,queryMap);
        }else if(session==null)
        {
            result="session is null";
        }

        return result;
    }
}
