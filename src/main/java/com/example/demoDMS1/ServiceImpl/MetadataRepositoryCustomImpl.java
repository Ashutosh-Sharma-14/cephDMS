package com.example.demoDMS1.ServiceImpl;

import com.example.demoDMS1.Entity.MetadataEntity;
import com.example.demoDMS1.Service.MetadataRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MetadataRepositoryCustomImpl implements MetadataRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MetadataRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> findObjectKeysByMetadata(String key, String value) {
        Query query = new Query();
//        query.addCriteria(Criteria.where("metadata." + key).exists(true));
        query.addCriteria(Criteria.where("metadata." + key).is(value));
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);

        return entities.stream()
                .map(MetadataEntity::getObjectKey)
                .collect(Collectors.toList());
    }
    @Override
    public boolean doesKeyExist(String objectKey) {
        Query query = new Query();
        query.addCriteria(Criteria.where("objectKey").is(objectKey));
        return mongoTemplate.exists(query, MetadataEntity.class);
    }

    @Override
    public void deleteByObjectKey(String objectKey){
        Query query = new Query();
        query.addCriteria(Criteria.where("objectKey").is(objectKey));
        mongoTemplate.remove(query,MetadataEntity.class);
    }

//    @Override
//    public List<String> findObjectKeysByMetadataValueExists(String value){
//        Query query = new Query();
//        query.addCriteria(Criteria.where("metadata." + key))
//    }

    @Override
    public String findObjectKeyByUUID(String uuid){
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        query.fields().include("objectKey");

        List<MetadataEntity> result = mongoTemplate.find(query,MetadataEntity.class);
        if(result.isEmpty()){
            return String.format("Unable to find uuid: %s in the database",uuid);
        }
        return result.get(0).getObjectKey();
    }

    @Override
    public String findUUIDByObjectKey(String objectKey) {
        Query query = new Query(Criteria.where("objectKey").is(objectKey));
        query.fields().include("uuid");

        MetadataEntity result = mongoTemplate.findOne(query, MetadataEntity.class);
        if (result == null) {
            return String.format("Unable to find object key: %s in the database", objectKey);
        }
        return result.getUuid();
    }


    @Override
    public Map<String,String> findMetadataByUUID(String uuid){
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        query.fields().include("metadata");

        List<MetadataEntity> result = mongoTemplate.find(query,MetadataEntity.class);
        if(result.isEmpty()){
            return null;
        }
        return result.get(0).getMetadata();
    }
}
