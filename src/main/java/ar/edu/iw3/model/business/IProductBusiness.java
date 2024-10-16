package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IProductBusiness {
    public Product find(long id) throws NotFoundException, BusinessException;

    public Product add(Product product) throws FoundException, BusinessException;
}
