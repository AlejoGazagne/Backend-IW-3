package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.model.persistence.AlarmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AlarmBusiness implements IAlarmBusiness {

    @Autowired
    private AlarmRepository alarmDAO;

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
    
    

}