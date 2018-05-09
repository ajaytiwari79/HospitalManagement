package com.kairos.persistance.model.asset;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "global_asset")
public class GlobalAsset extends MongoBaseEntity {


    @NotNull
    @NotEmpty
    private  String name;

    @NotNull
    @NotEmpty
    private String description;
    private List<Long> organisationType;
    private String organisationTypeName;
    private List <Long> organisationSubtype;
    private List <Long>orgService;
    private List <Long> subCategory;

}
