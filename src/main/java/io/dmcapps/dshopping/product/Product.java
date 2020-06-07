package io.dmcapps.dshopping.product;

import java.util.ArrayList;
import java.util.HashMap;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;

@MongoEntity(collection = "products")
public class Product extends PanacheMongoEntityBase {

    private static final Logger LOGGER = Logger.getLogger(
            Product.class);

    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    public ObjectId id;
    @NotNull
    @Size(min = 3, max = 120)
    public String name;
    @NotNull
    public ProductCategory category;
    @NotNull
    public Brand brand;
    public String picture;
    public String barcode;
    public HashMap<String, Object> description;
    public ArrayList<String> tags;


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