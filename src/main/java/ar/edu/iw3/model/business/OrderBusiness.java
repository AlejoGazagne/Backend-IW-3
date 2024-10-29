package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Tank;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.model.deserializers.OrderJsonDeserializer;
import ar.edu.iw3.model.persistence.OrderRepository;
import ar.edu.iw3.util.JsonUtiles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ar.edu.iw3.util.RandomNumberGenerator.generateFourDigitRandom;

@Service
@Slf4j
public class OrderBusiness implements IOrderBusiness {
    @Autowired
    private OrderRepository orderDAO;
    @Autowired
    private OrderRepository orderRepository;

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

    @Autowired
    private IDriverBusiness driverBusiness;

    @Autowired
    private IClientBusiness clientBusiness;

    @Autowired
    private ITruckBusiness truckBusiness;

    @Autowired
    private ITankBusiness tankBusiness;

    @Autowired
    private IProductBusiness productBusiness;

    @Override
    public Order add(Order order) throws FoundException, BusinessException {
        try {
            find(order.getId());
            throw FoundException.builder().message("Order exists, id = " + order.getId()).build();
        } catch(NotFoundException ignored){
        }
        // Validate and findOrCreate related entities
        try {
            order.setDriver(driverBusiness.findOrCreate(order.getDriver()));
            order.setClient(clientBusiness.findOrCreate(order.getClient()));
            Truck truck = order.getTruck();
            List<Tank> tank = order.getTruck().getTanks();
            order.setTruck(truckBusiness.findOrCreate(order.getTruck()));
            order.setProduct(productBusiness.find(order.getProduct().getName()));
            order.setState(Order.State.RECEIVED);
            order.setDateReceived(JsonUtiles.parseDate(String.valueOf(LocalDateTime.now())));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().message("Error al guardar la orden").ex(e).build();
        }
    }

    @Override
    public Order addExternal(String json) throws BusinessException, FoundException {
        ObjectMapper mapper = JsonUtiles.getObjectMapper(Order.class, new OrderJsonDeserializer(Order.class, clientBusiness, driverBusiness, truckBusiness, productBusiness, tankBusiness), null);
        Order order;
        try {
            order = mapper.readValue(json, Order.class);
        } catch (OrderDeserializationException e){
            log.error(e.getMessage());
            throw new OrderDeserializationException(e.getMessage());
        } catch (Exception e) {
            throw BusinessException.builder().ex(e).build();
        }
        return add(order);
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
            Integer pass;
            int attempts = 0;
            boolean isUnique;
            do {
                pass = generateFourDigitRandom();
                isUnique = !orderDAO.existsByPassword(pass);
                attempts++;
                if (attempts >= 10) {
                    throw BusinessException.builder().message("Error al generar password unica.").build();
                }
            } while (!isUnique);
            Date date = JsonUtiles.parseDate(String.valueOf(LocalDateTime.now())); // todo: ver esta fecha
            order.setDateFirstWeighing(date);
            order.setPassword(pass);
            order.setTare(tare);
            order.setState(Order.State.FIRST_WEIGHING);
            orderDAO.save(order);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    public void finalWeighing(long id, float finalWeight) throws NotFoundException, BusinessException, StateException {
        Order order;
        try {
            order = find(id);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
        if (order.getState() != Order.State.CHARGED) {
            throw StateException.builder().message("This order is not compatible with this operation.").build();
        }
        try {
            order.setFinalWeight(finalWeight);
            order.setState(Order.State.FINAL_WEIGHING);
            Date date = JsonUtiles.parseDate(String.valueOf(LocalDateTime.now())); // todo: ver esta fecha
            order.setDateFinalWeighing(date);
            orderDAO.save(order);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
