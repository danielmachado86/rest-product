package io.dmcapps.dshopping.product;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.jboss.logging.Logger;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;

@MongoEntity(collection = "brands")
public class Brand extends PanacheMongoEntityBase{

    private static final Logger LOGGER = Logger.getLogger(
            Brand.class);
    
    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    public String id;
    @NotNull
    public String picture;
    @NotNull
    public String description;
    public ArrayList<Product> topProducts = new ArrayList<Product>();
    public ArrayList<Product> newProducts = new ArrayList<Product>();

    public Brand() {
        
    }
    public Brand(String name, String picture, String description) {
        this.id = name;
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