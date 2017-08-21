package com.kairos.persistence.model.user.region;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by neuron on 12/6/17.
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalAreaTag extends UserBaseEntity{

    @NotNull
    private String name;

    @Relationship(type = LAT_LNG)
    private List<LatLng> paths = new ArrayList<>();

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    private boolean isDeleted;

    public LocalAreaTag(){

    }

    public LocalAreaTag(String name, List<LatLng> paths) {
        this.name = name;
        this.paths = paths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LatLng> getPaths() {
        return paths;
    }

    public void setPaths(List<LatLng> paths) {
        this.paths = paths;
    }

    public String toString(){
        return this.name + "-with coordinates-"+this.paths;
    }
}
