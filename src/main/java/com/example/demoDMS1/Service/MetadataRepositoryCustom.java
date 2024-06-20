package com.example.demoDMS1.Service;

import java.util.List;
import java.util.Map;

public interface MetadataRepositoryCustom {

    List<String> findObjectKeysByMetadata(String key, String value);

    String findObjectKeyByUUID(String uuid);

    Map<String,String> findMetadataByUUID(String uuid);
}