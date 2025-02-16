package ar.edu.iw3.model.business;

import ar.edu.iw3.auth.User;
import ar.edu.iw3.auth.model.persistence.UserRepository;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.model.persistence.AlarmRepository;
import ar.edu.iw3.model.persistence.ProductRepository;
import ar.edu.iw3.model.serializers.AlarmJsonSerializer;
import org.springframework.data.domain.PageImpl;
import ar.edu.iw3.util.JsonUtiles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AlarmBusiness implements IAlarmBusiness {

    @Autowired
    private AlarmRepository alarmDAO;

    @Autowired
    private UserRepository userDAO;

    @Autowired
    private ProductRepository productDAO;

    @Override
    public Alarm find(Long id) throws BusinessException, NotFoundException {
        Optional<Alarm> alarm;
        try {
            alarm = alarmDAO.findById(id);
            
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (alarm.isEmpty()) {
            throw NotFoundException.builder().message("No alarm found with id = " + id).build();
        }
        return alarm.get();


    }

    @Override
    public Boolean isAlarmAccepted(Long id){
        Optional<Alarm> response = alarmDAO.findByStatusAndOrder_Id(Alarm.State.PENDING, id);
        return response.isPresent();
    }

    @Override
    public List<Alarm> pendingAlarms() throws BusinessException {
        System.out.println("sexo98");
        System.out.println(alarmDAO);
        try {
            Optional<List<Alarm>> alarm = alarmDAO.findByStatus(Alarm.State.PENDING);
            if (alarm.isEmpty()) {
                throw new NotFoundException("No alarm found with status PENDING_REVIEW");
            }
            return alarm.get();
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Alarm add(Alarm alarm) throws BusinessException, FoundException {

        try {
            find(alarm.getId());
            throw FoundException.builder().message("Ya existe la Alarma id = " + alarm.getId()).build();
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            return alarmDAO.save(alarm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
    
    

    public void updateAlarmStatus(Long id, Alarm.State status) throws BusinessException, NotFoundException {
        try {
            System.out.println("----------- Estado de la alarma: " + status);
            Optional<Alarm> alarm = alarmDAO.findById(id);
            if (alarm.isEmpty()) {
                throw new NotFoundException("No alarm found with id = " + id);
            }
            Alarm alarmToUpdate = alarm.get();
            alarmToUpdate.setStatus(status);

            alarmDAO.save(alarmToUpdate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    public void acceptedAlarm(String json) throws NotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        Long id = jsonNode.get("id").asLong();
        String description = jsonNode.get("description").asText();
        Date dateResolved = objectMapper.convertValue(jsonNode.get("dateResolved"), Date.class);
        Long userId = jsonNode.get("id_user").asLong();

        // Find the Alarm entity by its id
        Optional<Alarm> alarmOptional = alarmDAO.findById(id);
        if (alarmOptional.isEmpty()) {
            throw new NotFoundException("No alarm found with id = " + id);
        }
        Alarm alarm = alarmOptional.get();

        // Update the fields
        alarm.setDescription(description);
        alarm.setDateResolved(dateResolved);

        // Assuming you have a method to find a User by its id
        Optional<User> userOptional = userDAO.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("No user found with id = " + userId);
        }
        alarm.setUser(userOptional.get());
        alarm.setStatus(Alarm.State.RESOLVED);
        // Save the updated Alarm entity
        alarmDAO.save(alarm);
    }

    public Page<String> getAlarms(int currentPage, int pageSize) throws BusinessException {
        try {
            Pageable pageable = PageRequest.of(currentPage, pageSize);
            Page<Alarm> alarmsTmp = alarmDAO.findByStatus(pageable, Alarm.State.RESOLVED);

            ObjectMapper mapper = JsonUtiles.getObjectMapper(Alarm.class, new AlarmJsonSerializer(Alarm.class), null);
            List<String> jsonAlarms = new ArrayList<>();

            for (Alarm alarm : alarmsTmp.getContent()) {
                String jsonAlarm = mapper.writeValueAsString(alarm);
                jsonAlarms.add(jsonAlarm);
            }

            return new PageImpl<>(jsonAlarms, pageable, alarmsTmp.getTotalElements());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    public Map<String, Object> countAlarms() throws BusinessException {
        try {
            return Map.of("totalAlarms", alarmDAO.countByStatus(Alarm.State.RESOLVED));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    public List<Map<String, Object>> countAlarmsByMonth() throws BusinessException {
        try {
            List<Map<String, Object>> response = new ArrayList<>();
            String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

            for(int i = 0; i < months.length; i++) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", months[i]);
                monthData.put("totalAlarms", alarmDAO.countByStatusAndMonth(Alarm.State.RESOLVED, i + 1));
                response.add(monthData);
                System.out.println(monthData);
            }
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    public List<Map<String, Object>> countAlarmsByMonthAndProduct() throws BusinessException {
        try {
            List<Product> products = productDAO.findAll();
            List<Map<String, Object>> response = new ArrayList<>();
            String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

            for (Product product : products) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("productName", product.getName());
                List<Map<String, Object>> monthlyData = new ArrayList<>();

                for (int i = 0; i < months.length; i++) {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("month", months[i]);
                    monthData.put("totalAlarms", alarmDAO.countByProductAndMonth(product.getId(), i + 1));
                    monthlyData.add(monthData);
                }

                productData.put("data", monthlyData);
                response.add(productData);
            }

            System.out.println(response);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}