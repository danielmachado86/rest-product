package io.dmcapps.dshopping.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductDescription {
  
    public double weight;
    public String sku;

    public ProductDescription() {
    }
    
    public ProductDescription(double weight, String sku) {

        this.weight = weight;
        this.sku = sku;
        
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = null;
        try {
            jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(this);
            
        } catch ( JsonProcessingException  e) {
            //TODO: handle exception
        }
        return jsonResult;
    }
    
}