package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Sector;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@NodeEntity
@Getter
@Setter
public class ExpertiseLine {
    private LocalDate startDate;
    private LocalDate endDate;

    @Relationship(type = BELONGS_TO_SECTOR)
    private Sector sector;

    @Relationship(type = IN_ORGANIZATION_LEVEL)
    private Level organizationLevel;

    @Relationship(type = SUPPORTS_SERVICES)
    private Set<OrganizationService> organizationServices;

    @Relationship(type = SUPPORTED_BY_UNION)
    private Organization union;



    public ExpertiseLine (ExpertiseLineBuilder expertiseLineBuilder){
        this.startDate=expertiseLineBuilder.startDate;
        this.endDate=expertiseLineBuilder.endDate;
        this.organizationLevel=expertiseLineBuilder.organizationLevel;
        this.organizationServices=expertiseLineBuilder.organizationServices;
        this.sector=expertiseLineBuilder.sector;
        this.union=expertiseLineBuilder.union;
    }


    public static class ExpertiseLineBuilder{
        private LocalDate startDate;
        private LocalDate endDate;
        private Level organizationLevel;
        private Sector sector;
        private Set<OrganizationService> organizationServices;
        private Organization union;


        public ExpertiseLineBuilder setStartDate(LocalDate startDate){
            this.startDate=startDate;
            return this;
        }

        public ExpertiseLineBuilder setEndDate(LocalDate endDate){
            this.endDate=endDate;
            return this;
        }

        public ExpertiseLineBuilder setOrganizationLevel(Level organizationLevel){
            this.organizationLevel=organizationLevel;
            return this;
        }

        public ExpertiseLineBuilder setSector(Sector sector){
            this.sector=sector;
            return this;
        }

        public ExpertiseLineBuilder setOrganizationServices(Set<OrganizationService> organizationServices){
            this.organizationServices=organizationServices;
            return this;
        }

        public ExpertiseLineBuilder setUnion(Organization union){
            this.union=union;
            return this;
        }

        public ExpertiseLine createLine(){
            return new ExpertiseLine(this);
        }
    }


}
