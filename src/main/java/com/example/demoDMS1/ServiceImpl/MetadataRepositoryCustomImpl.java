package com.example.demoDMS1.ServiceImpl;

import com.example.demoDMS1.Entity.MetadataEntity;
import com.example.demoDMS1.Service.MetadataRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MetadataRepositoryCustomImpl implements MetadataRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MetadataRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> findObjectKeysByMetadataExists(String key) {
        Query query = new Query();
        query.addCriteria(Criteria.where("metadata." + key).exists(true));
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);

        return entities.stream()
                .map(MetadataEntity::getObjectKey)
                .collect(Collectors.toList());
    }
}
