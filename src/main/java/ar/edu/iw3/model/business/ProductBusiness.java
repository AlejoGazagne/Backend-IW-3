package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IProductBusiness;
import ar.edu.iw3.model.persistence.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProductBusiness implements IProductBusiness {

    @Autowired
    private ProductRepository productDAO;

    @Override
    public Product find(long id) throws NotFoundException, BusinessException {
        Optional<Product> product;
        try {
            product = productDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(product.isEmpty()) {
            throw NotFoundException.builder().message("Truck not found, id = " + id).build();
        }
        return product.get();
    }

    @Override
    public Product add(Product product) throws FoundException, BusinessException {
        try {
            find(product.getId());
            throw FoundException.builder().message("Truck exist, id = " + product.getId()).build();
        } catch(NotFoundException ignored){
        }
        try {
            return productDAO.save(product);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
