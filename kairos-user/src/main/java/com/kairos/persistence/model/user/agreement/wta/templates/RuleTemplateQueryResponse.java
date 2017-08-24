package com.kairos.persistence.model.user.agreement.wta.templates;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by pawanmandhan on 9/8/17.
 */
@QueryResult
public class RuleTemplateQueryResponse {

    //private List<WTABaseRuleTemplate> data;
    private List<WTABaseRuleTemplateDTO> data;


    public List<WTABaseRuleTemplateDTO> getData() {
        return data;
    }

    public void setData(List<WTABaseRuleTemplateDTO> data) {
        this.data = data;
    }
}
