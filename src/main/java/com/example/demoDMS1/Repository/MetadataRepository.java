package com.example.demoDMS1.Repository;

import com.example.demoDMS1.Entity.MetadataEntity;
import com.example.demoDMS1.Service.MetadataRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MetadataRepository extends MongoRepository<MetadataEntity, String>, MetadataRepositoryCustom {
}
