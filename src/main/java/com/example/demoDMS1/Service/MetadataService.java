package com.example.demoDMS1.Service;

import com.example.demoDMS1.Entity.MetadataEntity;
import com.example.demoDMS1.Repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataService {

    private final MetadataRepository metadataRepository;

    @Autowired
    public MetadataService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public List<String> findObjectKeysByMetadata(String key,String value) {
        return metadataRepository.findObjectKeysByMetadata(key,value);
    }

    public String findObjectKeyByUUID(String uuid){
        return metadataRepository.findObjectKeyByUUID(uuid);
    }
}
