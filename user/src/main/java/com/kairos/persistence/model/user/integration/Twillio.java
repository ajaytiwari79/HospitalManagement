package com.kairos.persistence.model.user.integration;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.beans.BeanUtils;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
@Getter
@Setter
public class Twillio  extends UserBaseEntity {
    private String accountId;
    private String authToken;
    private String number;
    private Long organizationId;

    public static Twillio getInstance(){
        return new Twillio();
    }

    public static Twillio copyProperties(Twillio source, Twillio target){
        BeanUtils.copyProperties(source,target);
        return target;
    }

}
