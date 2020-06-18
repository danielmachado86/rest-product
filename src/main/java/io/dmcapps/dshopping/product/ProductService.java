package io.dmcapps.dshopping.product;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;


import org.bson.types.ObjectId;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Transactional(REQUIRED)
public class ProductService {

    @Transactional(SUPPORTS)
    public Product findProductById(ObjectId id) {
        return Product.findById(id);
    }
    
    @Transactional(SUPPORTS)
    public List<Product> returnAllProducts() {
        return Product.listAll();
    }
    
    @Transactional(SUPPORTS)
    public List<Product> searchProducts(String query) {
        String param = queryStringProcessor(query);
        Stream<Product> results = Product.stream(param);
        List<Product> processedResults = processResults(query, results);
        return processedResults;
    }

    private List<Product> processResults(String query, Stream<Product> results) {
        List<Result> processedResults = results
            .map(product -> Adapter.productToResult(query, product))
            .sorted()
            .collect(Collectors.toList());
        Collections.sort(processedResults, Collections.reverseOrder());

        List<Product> products = processedResults.stream()
            .map(result -> result.product)
            .collect(Collectors.toList());

        return products;
    }

    private String queryStringProcessor(String query) {
        String[] splittedQuery = query.split(" ");
        String paramName = "";
        String paramCategoryName = "";
        for (String s: splittedQuery) {           
            paramName += String.format("{'name':{'$regex':/.*%s.*/i}},", s);
            paramCategoryName += String.format("{'category._id':{'$regex':/.*%s.*/i}},", s);
        }
        String param = "{ $or: [ " + paramName + paramCategoryName + "] }";
        return param;
    }

    public Product persistProduct(Product product) {
        Brand brand = checkBrandExists(product.brand.id);
        if(brand == null){
            Brand.persist(product.brand);
        } else {
            product.brand = brand;
        }
        Product.persist(product);
        return product;
    }

    @Transactional(SUPPORTS)
    private Brand checkBrandExists(String id) {
        Brand brand = Brand.findById(id);
		return brand;
	}

	public Product updateProduct(Product product) {
        Product entity = findProductById(product.id);
        entity.name = product.name;
        entity.category = product.category;
        entity.brand = product.brand;
        entity.picture = product.picture;
        entity.description = product.description;
        entity.update();
        return entity;
    }

    public void deleteProduct(ObjectId id) {
        Product
            .findById(id)
            .delete();
    }
}