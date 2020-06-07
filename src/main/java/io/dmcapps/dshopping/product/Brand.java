package io.dmcapps.dshopping.product;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;

public class Brand extends PanacheMongoEntityBase{

    private static final Logger LOGGER = Logger.getLogger(
            Brand.class);
    
    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    public ObjectId id;
    @NotNull
    public String name;
    @NotNull
    public String picture;
    @NotNull
    public String description;

    public Brand() {
        
    }
    public Brand(String name, String picture, String description) {
        this.name = name;
        this.picture = picture;
        this.description = description;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = null;
        try {
            jsonResult = mapper
                .writeValueAsString(this);
            
        } catch ( JsonProcessingException  e) {
            LOGGER.error(e.getMessage());
        }
        return jsonResult;
    }
}