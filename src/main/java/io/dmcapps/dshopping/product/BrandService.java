package io.dmcapps.dshopping.product;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.List;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(REQUIRED)
public class BrandService {

    @Transactional(SUPPORTS)
    public List<Brand> findAllBrands() {
        return Brand.listAll();
    }

    @Transactional(SUPPORTS)
    public Brand findBrandById(String id) {
        return Brand.findById(id);
    }

    public Brand persistBrand(@Valid Brand brand) {
        Brand.persist(brand);
        return brand;
    }

    public Brand updateBrand(@Valid Brand brand) {
        Brand entity = Brand.findById(brand.id);
        entity.id = brand.id;
        entity.picture = brand.picture;
        entity.description = brand.description;
        return entity;
    }

    public void deleteBrand(String id) {
        Brand brand = Brand.findById(id);
        brand.delete();
    }
}