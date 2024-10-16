package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IOrderBusiness {
    public Order find(long id) throws NotFoundException, BusinessException;

    public Order add(Order order) throws FoundException, BusinessException;

    public List<Order> list() throws BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public Order update(Order order) throws NotFoundException, BusinessException;
}
