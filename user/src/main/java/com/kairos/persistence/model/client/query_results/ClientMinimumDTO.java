package com.kairos.persistence.model.client.query_results;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.client.ClientEnum;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by oodles on 23/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class ClientMinimumDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String cprnumber;
    private String name;
    private Boolean hasSameAddress;
    private boolean updateAddressOfAllHouseholdMembers;
    private ClientEnum clientType=ClientEnum.INDIVIDUAL;
    public ClientMinimumDTO() {

    }
    public ClientMinimumDTO(String firstName, String lastName, String cprNumber, boolean updateAddressOfAllHouseholdMembers) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cprnumber = cprNumber;
        this.updateAddressOfAllHouseholdMembers = updateAddressOfAllHouseholdMembers;
    }

    public ClientMinimumDTO(long id, String firstName, String lastName, Boolean hasSameAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hasSameAddress = hasSameAddress;
    }

    public Boolean getHasSameAddress() {
        return hasSameAddress;
    }

    public void setHasSameAddress(Boolean hasSameAddress) {
        this.hasSameAddress = hasSameAddress;
    }

    public String getCprnumber() {
        return cprnumber;
    }

    public void setCprnumber(String cprnumber) {
        this.cprnumber = cprnumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return this.firstName+" "+this.lastName;
    }

    public boolean getUpdateAddressOfAllHouseholdMembers() {
        return updateAddressOfAllHouseholdMembers;
    }

    public void setUpdateAddressOfAllHouseholdMembers(boolean updateAddressOfAllHouseholdMembers) {
        this.updateAddressOfAllHouseholdMembers = updateAddressOfAllHouseholdMembers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUpdateAddressOfAllHouseholdMembers() {
        return updateAddressOfAllHouseholdMembers;
    }

    public ClientEnum getClientType() {
        return clientType;
    }

    public void setClientType(ClientEnum clientType) {
        this.clientType = clientType;
    }
}
