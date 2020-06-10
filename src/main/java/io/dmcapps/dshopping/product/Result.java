package io.dmcapps.dshopping.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.jboss.logging.Logger;

public class Result implements Comparable<Result> {

    private static final Logger LOGGER = Logger.getLogger(
            Product.class);

    public Product product;
    public Double similarity;

    public Result(String query, Product product){
        this.product = product;
        this.similarity = calculateJaroWinkler(query, product);
    }

    private double calculateJaroWinkler(String query, Product product) {
        JaroWinklerDistance distanceContainer = new JaroWinklerDistance();
            Double distanceIndex = 
            distanceContainer.apply(
                query.toLowerCase(), product.name.toLowerCase()
            );
        return distanceIndex;
        
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

	@Override
	public int compareTo(Result o) {
        return similarity.compareTo(o.similarity);
	}
}