package com.kairos.persistence.model.client.relationships;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.language.Language;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Created by oodles on 17/1/17.
 */
@RelationshipEntity(type = "KNOWS")
public class ClientLanguageRelation extends UserBaseEntity {


    @StartNode
    private Client client;

    @EndNode
    private Language language;

    private long speakLevel;

    private long writeLevel;

    private long readLevel;

    public ClientLanguageRelation(Client currentClient, Language language, long readLevel, long writeLevel, long speakLevel) {
        this.client = currentClient;
        this.language = language;
        this.speakLevel = speakLevel;
        this.writeLevel = writeLevel;
        this.readLevel = readLevel;
    }

    public ClientLanguageRelation() {
    }

    public ClientLanguageRelation(Client currentClient, Language language) {
        this.client = currentClient;
        this.language = language;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public long getSpeakLevel() {
        return speakLevel;
    }

    public void setSpeakLevel(long speakLevel) {
        this.speakLevel = speakLevel;
    }

    public long getWriteLevel() {
        return writeLevel;
    }

    public void setWriteLevel(long writeLevel) {
        this.writeLevel = writeLevel;
    }

    public long getReadLevel() {
        return readLevel;
    }

    public void setReadLevel(long readLevel) {
        this.readLevel = readLevel;
    }
}
