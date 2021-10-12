package com.kairos.persistence.model.keyboard_shortcuts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyboardShortcuts extends MongoBaseEntity {

    private String name;
    private String keyboardSortcut;
    private BigInteger countryId;
    private BigInteger unitId;
    private boolean delete;
}
