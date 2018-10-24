package com.planner.domain.tomtomResponse;

import com.planner.domain.common.MongoBaseEntity;

import java.util.List;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class TomTomResponse extends MongoBaseEntity{

    private String formatVersion;
    private List<List<Matrix>> matrix;
    private Summary summary;

    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }

    public List<List<Matrix>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<Matrix>> matrix) {
        this.matrix = matrix;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }
}
