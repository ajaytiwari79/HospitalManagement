package com.kairos.service.tree_structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.persistence.model.common.QueryResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by prabjot on 30/12/16.
 */
@Service
public class TreeStructureService {

    /**
     * @author prabjot
     * @param queryResults //class which will contain result from query
     * @return list of organizations format of tree structure
     */
    public QueryResult getTreeStructure(List<QueryResult> queryResults) {



        QueryResult prev = null;
        QueryResult firstRecord = null;
        int index = 0;
        for (QueryResult queryResult : queryResults) {
            if (prev != null) {
                setChildren(queryResults, prev);
            }

            if (index == 0) {
                firstRecord = queryResult;
            }
            prev = queryResult;
            index++;
        }
        if(firstRecord == null){
            return QueryResult.getInstance();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(firstRecord,QueryResult.class);
    }

    private void setChildren(List<QueryResult> queryResults, QueryResult prev) {
        for (QueryResult children : prev.getChildren()) {
            for(QueryResult queryResult1 : queryResults){
                if (queryResult1.getId() == children.getId()) {
                    children.setChildren(queryResult1.getChildren());
                }
            }
        }
    }
}
