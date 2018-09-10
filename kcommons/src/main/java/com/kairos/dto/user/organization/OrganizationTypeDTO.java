package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * Created by oodles on 14/9/16.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

    public class OrganizationTypeDTO {

        private Long id;
        @NotEmpty(message = "error.OrganizationType.name.notEmpty") @NotNull(message = "error.OrganizationType.name.notnull")
        private String name;
        private List<Long> levels;
        private String description;

        public OrganizationTypeDTO() {
            //default constructor
        }

        public OrganizationTypeDTO(String name, List<Long> levels) {
            this.name = name;
            this.levels = levels;
        }

    public OrganizationTypeDTO(Long id) {
        this.id = id;
    }

    public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Long> getLevels() {
            return levels;
        }

        public void setLevels(List<Long> levels) {
            this.levels = levels;
        }
    }

