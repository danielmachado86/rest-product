package io.dmcapps.dshopping.product;

public class Adapter {

    public static Result productToResult(String query, Product product){
        return new Result(query, product);
    }

    
}