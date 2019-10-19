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
public class Visitour  extends UserBaseEntity{
    private String serverName;
    private String username;
    private String password;
    private Long organizationId;

    public static Visitour getInstance(){
        return new Visitour();
    }

    public static Visitour copyProperties(Visitour source, Visitour target){
        BeanUtils.copyProperties(source,target);
        return target;
    }
}
