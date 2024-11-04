package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Tank;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.model.deserializers.OrderJsonDeserializer;
import ar.edu.iw3.model.persistence.OrderRepository;
import ar.edu.iw3.util.JsonUtiles;
import ar.edu.iw3.util.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static ar.edu.iw3.util.RandomNumberGenerator.generateFourDigitRandom;

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
    public Order add(Order order) throws FoundException, BusinessException, NotFoundException {
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
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw NotFoundException.builder().message("Error al buscar o crear entidades relacionadas.").ex(e).build(); //todo: ver si se puede mejorar el mensaje
        }

        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().message("Error al guardar la orden").ex(e).build();
        }
    }

    @Override
    public Order addExternal(String json) throws BusinessException, FoundException, NotFoundException {
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

    @Override
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

    @Override
    public Map<String, Object> finalWeighing(long id, float finalWeight) throws NotFoundException, BusinessException, StateException {
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
            return conciliationJson(order);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Autowired
    private ILoadDataBusiness loadDataBusiness;

    @Override
    public Map<String, Object> conciliationJson(Order order) throws NotFoundException, BusinessException, StateException {
        Map<String, Object> conciliation = new HashMap<>();

        if (order.getState() != Order.State.FINAL_WEIGHING) {
            throw StateException.builder().message("This order is not compatible with this operation.").build();
        }

        float initialWeight = order.getTare();
        float finalWeight = order.getFinalWeight();
        float finalAccumulatedMass = order.getLastAccumulatedMass();
        float netWeight = finalWeight - initialWeight;
        float differenceWeight = netWeight - finalAccumulatedMass;
        //String productName = order.getProduct().getName();
        //float avgTemperature = loadDataBusiness.avgTemperature(orderId);
        //float avgDensity = loadDataBusiness.avgDensity(orderId);
        //float avgCaudal = loadDataBusiness.avgCaudal(orderId);

        conciliation.put("initialWeight", initialWeight);
        conciliation.put("finalWeight", finalWeight);
        conciliation.put("productName", order.getProduct().getName());
        conciliation.put("finalAccumulatedMass", finalAccumulatedMass);
        conciliation.put("netWeight", netWeight);
        conciliation.put("differenceWeight", differenceWeight);
        conciliation.put("avgTemperature", loadDataBusiness.avgTemperature(order.getId()));
        conciliation.put("avgDensity", loadDataBusiness.avgDensity(order.getId()));
        conciliation.put("avgCaudal", loadDataBusiness.avgCaudal(order.getId()));

        return conciliation;
    }

    @Autowired
    private PdfService pdfService;

    @Override
    public byte[] conciliationPdf(long orderId) throws NotFoundException, BusinessException, StateException {
        Order order = find(orderId);
        if (order.getState() != Order.State.FINAL_WEIGHING) {
            throw StateException.builder().message("This order is not compatible with this operation.").build();
        }

        try {
            return pdfService.conciliationPDF(order);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
