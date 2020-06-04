package io.dmcapps.dshopping.product;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import org.bson.types.ObjectId;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;

@ApplicationScoped
@Transactional(REQUIRED)
public class ProductService {

    @Transactional(SUPPORTS)
    public Product findProductById(ObjectId id) {
        return Product.findById(id);
    }
    
    @Transactional(SUPPORTS)
    public List<Product> findAllProducts() {
        return Product.listAll();
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