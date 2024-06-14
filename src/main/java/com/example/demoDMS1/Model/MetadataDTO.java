package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataDTO {
    private String key;
    private Map<String,String> metadata;
}
