package ar.edu.iw3.model.business;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.model.deserializers.OrderJsonDeserializer;
import ar.edu.iw3.model.persistence.OrderRepository;
import ar.edu.iw3.util.JsonUtiles;
import ar.edu.iw3.util.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    public Map<String, Object> getDetailsOrder(String id) throws NotFoundException, BusinessException {
        Map<String, Object> response = new HashMap<>();
        response.put("avgTemperature", loadDataBusiness.avgTemperature(Long.parseLong(id)));
        response.put("avgDensity", loadDataBusiness.avgDensity(Long.parseLong(id)));
        response.put("avgCaudal", loadDataBusiness.avgCaudal(Long.parseLong(id)));

        return response;
    }

    @Override
    public Map<String, Object> countOrders() throws BusinessException {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("total", orderDAO.count());

            List<Map<String, Object>> states = new ArrayList<>();
            states.add(new HashMap<>() {{
                put("state", "received");
                put("count", orderDAO.countOrderByState(Order.State.RECEIVED));
            }});
            states.add(new HashMap<>() {{
                put("state", "weighed");
                put("count", orderDAO.countOrderByState(Order.State.FIRST_WEIGHING));
            }});
            states.add(new HashMap<>() {{
                put("state", "charged");
                put("count", orderDAO.countOrderByState(Order.State.CHARGED));
            }});
            states.add(new HashMap<>() {{
                put("state", "finished");
                put("count", orderDAO.countOrderByState(Order.State.FINAL_WEIGHING));
            }});

            response.put("states", states);

            return response;
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
            throw BusinessException.builder().message("Error al crear entidades: " + e.getMessage()).build();
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw NotFoundException.builder().message("Error al buscar la entidad.").ex(e).build();
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
            Date date = JsonUtiles.parseDate(String.valueOf(LocalDateTime.now()));
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
            Date date = JsonUtiles.parseDate(String.valueOf(LocalDateTime.now()));
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

    public Order validatePassword(Integer password) throws BusinessException, NotFoundException, StateException, PasswordException {
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
        System.out.println(order.get().getState());

        if (order.get().getState() != Order.State.FIRST_WEIGHING){
            throw StateException.builder().message("This order is not compatible with this operation.").build();
        }

        if(password.equals(order.get().getPassword())){
            return order.get();
        }else{
            throw PasswordException.builder().message("Incorrect password.").build();
        }
    }

    @Autowired
    private SimpMessagingTemplate loadTruckWS;

    public Order beginTruckLoading(long id, LoadData loadData) throws BusinessException, NotFoundException, StateException, TruckloadException, FoundException {
        Optional<Order> tmp = orderDAO.findById(id);

        if (tmp.isEmpty()){
            throw NotFoundException.builder().message("Order not found.").build();
        }

        Order order = tmp.get();

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

        if(order.getLastAccumulatedMass() >= order.getPreset()){
            order.setDateFinalCharge(currentTime);
            orderDAO.save(order);
            return order;

        }

        order.setLastTimestamp(currentTime);
        order.setLastAccumulatedMass(loadData.getAccumulatedMass());
        order.setLastDensity(loadData.getDensity());
        order.setLastCaudal(loadData.getCaudal());
        order.setLastTemperature(loadData.getTemperature());

        order = loadDataBusiness.createLoadData(currentTime, loadData, order);
        
        orderDAO.save(order);

        //loadTruckWS.convertAndSend("/topic/loadTruck", order);
        return order;

    }

    public void finishTruckLoading(long orderId) throws BusinessException, NotFoundException, StateException {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Optional<Order> order = orderDAO.findById(orderId);

        if (order.isEmpty()){
            throw NotFoundException.builder().message("Order not found.").build();
        }

        order.get().setDateFinalCharge(currentTime);
        if (order.get().getState() != Order.State.FIRST_WEIGHING) {
            throw StateException.builder().message("This order is not compatible with the closing operation.").build();
        }
        order.get().setState(Order.State.CHARGED);
        orderDAO.save(order.get()); // TODO: si esto falla, no me tira un BusinessException
    }

    @Override
    public List<Map<String, Object>> countOrdersByMonth() throws BusinessException {
        List<Map<String, Object>> response = new ArrayList<>();
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        try {
            for (int i = 0; i < months.length; i++) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", months[i]);
                monthData.put("count", orderDAO.countOrderByDateReceived(i + 1));
                response.add(monthData);
            }
            return response;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public List<Map<String, Object>> countProducts() throws BusinessException{
        List<Map<String, Object>> response = new ArrayList<>();
        try {
            List<Object[]> data = orderDAO.findProductOrderCounts();

            for (Object[] row : data) {
                Map<String, Object> productData = new HashMap<>();

                productData.put("productName", row[0]);
                productData.put("count", row[1]);

                response.add(productData);
            }

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public List<Map<String, Object>> countAllClients() throws BusinessException{
        List<Map<String, Object>> response = new ArrayList<>();

        try {
            List<Object[]> data = orderDAO.findClientOrderCounts();

            for (Object[] row : data) {
                Map<String, Object> clientData = new HashMap<>();

                clientData.put("clientName", row[0]);
                clientData.put("count", row[1]);

                response.add(clientData);
            }

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Page<Order> getOrders(int currentPage, Order.State state, int pageSize) throws BusinessException{
        try {
            Pageable pageable = PageRequest.of(currentPage, pageSize);
            if (state == null) {
                return orderDAO.findAll(pageable);
            }
            return orderDAO.findAllByState(state, pageable);
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
