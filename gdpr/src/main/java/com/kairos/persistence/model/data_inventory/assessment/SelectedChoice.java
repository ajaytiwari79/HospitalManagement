package com.kairos.persistence.model.data_inventory.assessment;




import javax.persistence.*;

@Entity
public  class SelectedChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SelectedChoice() {


    }
}
