package io.dmcapps.dshopping.product;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.mongodb.BasicDBObject;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
        String[] splittedQuery = query.split(" ");
        String paramName = "";
        String paramCategoryName = "";
        for (String s: splittedQuery) {           
            paramName += String.format("{'name':{'$regex':/.*%s.*/i}},", s);
            paramCategoryName += String.format("{'category.name':{'$regex':/.*%s.*/i}},", s);
        }
        String param = paramName + paramCategoryName;
        PanacheQuery<Product> results = Product.find("{'$or':[" + param + "]}");
        results.page(Page.ofSize(100));
        List<Product> productsPage = results.list();
        return productsPage;
    }

    public Product persistProduct(Product product) {
        Product.persist(product);
        return product;
    }

    public Product updateProduct(Product product) {
        Product entity = Product.findById(product.id);
        entity.name = product.name;
        entity.brand = product.brand;
        entity.picture = product.picture;
        entity.description = product.description;
        return entity;
    }

    public void deleteProduct(ObjectId id) {
        Product
            .findById(id)
            .delete();
    }
}