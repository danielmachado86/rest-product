package io.dmcapps.dshopping.product;

import javax.validation.constraints.NotNull;

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
public class ProductCategory extends PanacheMongoEntityBase {

    private static final Logger LOGGER = Logger.getLogger(
            ProductCategory.class);

    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    public ObjectId id;
    @NotNull
    public String name;
    public int parent;

    public ProductCategory() {
    }

    public ProductCategory(String name, int parent) {
        this.name = name;
        this.parent = parent;
    }

    public ProductCategory(String name) {
        this.name = name;
        this.parent = -1;
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
