package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.StateException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.persistence.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderBusiness implements IOrderBusiness {
    @Autowired
    private OrderRepository orderDAO;

    @Override
    public Order find(long id) throws NotFoundException, BusinessException {
        Optional<Order> order;
        try {
            order = orderDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(order.isEmpty()) {
            throw NotFoundException.builder().message("Order not found, id = " + id).build();
        }
        return order.get();
    }

    @Override
    public Order add(Order order) throws FoundException, BusinessException {
        try {
            find(order.getId());
            throw FoundException.builder().message("Order exists, id = " + order.getId()).build();
        } catch(NotFoundException ignored){
        }
        // TODO: validation of the rest of entities

        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public List<Order> list() throws BusinessException {
        try {
            return orderDAO.findAll();
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        find(id);
        try {
            orderDAO.deleteById(id);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Order update(Order order) throws NotFoundException, BusinessException {
        find(order.getId());
        try {
            return orderDAO.save(order);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    public void firstWeighing(long id, float tare) throws NotFoundException, BusinessException, StateException {
        Order order;
        try {
            order = find(id);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }

        if (order.getState() != Order.State.RECEIVED) {
            throw StateException.builder().message("This order has already begun or is already finished.").build();
        }

        try {
            order.setTare(tare);
            order.setState(Order.State.FIRST_WEIGHING);
            orderDAO.save(order);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
        
    }
}
