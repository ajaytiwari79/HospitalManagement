package com.kairos.persistence.model.organization.union;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 14/2/18.
 */
public class UnionWrapper {
    private List<UnionResponseDTO> allUnions = new ArrayList<>();
    private List<UnionResponseDTO> selectedUnions = new ArrayList<>();

    public UnionWrapper() {
    }

    public List<UnionResponseDTO> getAllUnions() {
        return allUnions;
    }

    public void setAllUnions(List<UnionResponseDTO> allUnions) {
        this.allUnions = allUnions;
    }

    public List<UnionResponseDTO> getSelectedUnions() {
        return selectedUnions;
    }

    public void setSelectedUnions(List<UnionResponseDTO> selectedUnions) {
        this.selectedUnions = selectedUnions;
    }
}
