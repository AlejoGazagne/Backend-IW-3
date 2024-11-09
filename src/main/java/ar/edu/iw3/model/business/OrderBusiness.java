package ar.edu.iw3.model.business;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.model.deserializers.OrderJsonDeserializer;
import ar.edu.iw3.model.persistence.OrderRepository;
import ar.edu.iw3.model.serializers.OrderJsonSerializer;
import ar.edu.iw3.util.JsonUtiles;
import ar.edu.iw3.util.PdfService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.*;

import static ar.edu.iw3.util.RandomNumberGenerator.generateFiveDigitRandom;

@Service
@Slf4j
public class OrderBusiness implements IOrderBusiness {
    @Autowired
    private OrderRepository orderDAO;

    @Autowired
    private ILoadDataBusiness loadDataBusiness;


    @Override
    public Order find(String externalId) throws NotFoundException, BusinessException {
        Optional<Order> order;
        try {
            order = orderDAO.findByExternalId(externalId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(order.isEmpty()) {
            throw NotFoundException.builder().message("Order not found, id = " + externalId).build();
        }
        return order.get();
    }

    @Override
    public Order findById(long id) throws NotFoundException, BusinessException {
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
    public void delete(String externalId) throws NotFoundException, BusinessException {
        find(externalId);
        try {
            orderDAO.deleteByExternalId(externalId);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Order update(Order order) throws NotFoundException, BusinessException {
        find(order.getExternalId());
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
            find(order.getExternalId());
            throw FoundException.builder().message("Order exists, id = " + order.getId()).build();
        } catch(NotFoundException ignored){
        }
        // Validate and findOrCreate related entities
        try {
            order.setDriver(driverBusiness.findOrCreate(order.getDriver()));
            order.setClient(clientBusiness.findOrCreate(order.getClient()));
            order.setTruck(truckBusiness.findOrCreate(order.getTruck()));
            order.setProduct(productBusiness.find(order.getProduct().getName()));
            order.setState(Order.State.RECEIVED);
            order.setDateReceived(JsonUtiles.parseDate(String.valueOf(LocalDateTime.now())));
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw NotFoundException.builder().message("Error al buscar o crear entidades relacionadas.").ex(e).build();
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
    public void firstWeighing(String externalId, float tare) throws NotFoundException, BusinessException, StateException, PasswordException {
        Order order;
        try {
            order = find(externalId);
        } catch (NotFoundException e){
            throw NotFoundException.builder().message("Order not found, id = " + externalId).build();
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
                pass = generateFiveDigitRandom();
                isUnique = !orderDAO.existsByPassword(pass);
                attempts++;
                if (attempts >= 10) {
                    throw PasswordException.builder().message("Error al generar password unica.").build();
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
    public Map<String, Object> finalWeighing(String externalId, float finalWeight) throws NotFoundException, BusinessException, StateException {
        Order order;
        try {
            order = find(externalId);
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
            Map<String, Object> conciliation = conciliationJson(order);
            orderDAO.save(order);
            return conciliation;
        } catch (NotFoundException e){
            log.error(e.getMessage());
            throw NotFoundException.builder().message("Load data not found").build();
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Map<String, Object> conciliationJson(Order order) throws NotFoundException, BusinessException, StateException {
        Map<String, Object> conciliation = new HashMap<>();

        if (order.getState() != Order.State.FINAL_WEIGHING) {
            throw StateException.builder().message("This order is not compatible with this operation.").build();
        }

        try {
            List<LoadData> data = order.getLoadData();
            if (data.isEmpty()) {
                throw NotFoundException.builder().build();
            }
            float initialWeight = order.getTare();
            float finalWeight = order.getFinalWeight();
            float finalAccumulatedMass = order.getLastAccumulatedMass();
            float netWeight = finalWeight - initialWeight;
            float differenceWeight = netWeight - finalAccumulatedMass;

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
        } catch(NotFoundException e){
            log.error(e.getMessage());
            throw NotFoundException.builder().ex(e).build();
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Autowired
    private PdfService pdfService;

    @Override
    public byte[] conciliationPdf(String externalId) throws NotFoundException, BusinessException, StateException {
        Order order = find(externalId);
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

    public String validatePassword(Integer password) throws BusinessException, NotFoundException, StateException, PasswordException, JsonProcessingException {
        Optional<Order> order;
        try {
            order = orderDAO.findByPassword(password);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
        if(order.isEmpty()) {
            throw NotFoundException.builder().message("Order not found.").build();
        }

        if (order.get().getState() != Order.State.FIRST_WEIGHING){
            throw StateException.builder().message("This order is not compatible with this operation.").build();
        }

        if(password.equals(order.get().getPassword())){
            StdSerializer<Order> serializer = new OrderJsonSerializer(Order.class);
            return JsonUtiles.getObjectMapper(Order.class, serializer, null).writeValueAsString(order.get());
        }else{
            throw PasswordException.builder().message("Incorrect password.").build();
        }
    }

    public void beginTruckLoading(long id, LoadData loadData) throws BusinessException, NotFoundException, StateException, TruckloadException, FoundException {
        Order order = findById(id);

        if (order.getState() != Order.State.FIRST_WEIGHING) {
            throw StateException.builder().message("This order is not compatible with this operation.").build();
        }
        if(loadData.getCaudal()<0){
            throw TruckloadException.builder().message("Error: no flow.").build();
        }
        if(loadData.getAccumulatedMass() < order.getLastAccumulatedMass()){
            throw TruckloadException.builder().message("Error: amount of liquid mass is invalid.").build();
        }

        /*if(loadData.getTemperature() > order.getProduct().getLimitTemperature()){
            HACER LOGICA DE ALARMA
        }*/

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if(order.getDateInitialCharge() == null){
            order.setDateInitialCharge(currentTime);
        }

        order.setLastTimestamp(currentTime);
        order.setLastAccumulatedMass(loadData.getAccumulatedMass());
        order.setLastDensity(loadData.getDensity());
        order.setLastCaudal(loadData.getCaudal());
        order.setLastTemperature(loadData.getTemperature());

        order = loadDataBusiness.createLoadData(currentTime, loadData, order);

        orderDAO.save(order);
    }

    public void finishTruckLoading(String externalId) throws BusinessException, NotFoundException, StateException {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Order order = find(externalId);
        order.setDateFinalCharge(currentTime);
        if (order.getState() != Order.State.FIRST_WEIGHING) {
            throw StateException.builder().message("This order is not compatible with the closing operation.").build();
        }
        order.setState(Order.State.CHARGED);
        orderDAO.save(order);
    }

}
