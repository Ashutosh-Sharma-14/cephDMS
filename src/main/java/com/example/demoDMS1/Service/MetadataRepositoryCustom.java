package com.example.demoDMS1.Service;

import java.util.List;

public interface MetadataRepositoryCustom {
    List<String> findObjectKeysByMetadataExists(String key);
}