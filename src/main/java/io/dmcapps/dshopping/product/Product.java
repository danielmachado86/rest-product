package io.dmcapps.dshopping.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;

@MongoEntity(collection = "products")
public class Product extends PanacheMongoEntityBase {

    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    public ObjectId id;
    
    public String name;
    public int brand;
    public String picture;

    public ProductDescription description;


    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = null;
        try {
            jsonResult = mapper
                .writeValueAsString(this);
            
        } catch ( JsonProcessingException  e) {
            //TODO: handle exception
        }
        return jsonResult;
    }

}