package com.example.demoDMS1.Entity;

import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "metadata")
public class MetadataEntity {
    private String objectKey;
    private Map<String,String> metadata;
}
