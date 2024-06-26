package com.example.demoDMS1.Service;

import java.util.List;
import java.util.Map;

public interface MetadataRepositoryCustom {

    List<String> findObjectKeysByMetadata(String key, String value);

    boolean doesKeyExist(String objectKey);

    void deleteByObjectKey(String objectKey);

    String findObjectKeyByUUID(String uuid);

    String findUUIDByObjectKey(String objectKey);

    Map<String,String> findMetadataByUUID(String uuid);
}