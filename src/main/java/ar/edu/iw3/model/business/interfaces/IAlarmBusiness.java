package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface IAlarmBusiness {

    public List<Alarm> pendingAlarms() throws BusinessException;

    public Alarm add(Alarm alarm) throws BusinessException, FoundException;

    public Alarm find(Long id) throws BusinessException, NotFoundException;

    public Boolean isAlarmAccepted(Long id);

    //public Alarm find(Long id) throws BusinessException;

    public void updateAlarmStatus(Long id, Alarm.State status) throws BusinessException, NotFoundException;

    public void acceptedAlarm(String json) throws BusinessException, NotFoundException, JsonProcessingException;

    public Page<String> getAlarms(int currentPage, int pageSize) throws BusinessException;

    public Map<String, Object> countAlarms() throws BusinessException;

    public List<Map<String, Object>> countAlarmsByMonth() throws BusinessException;

    public List<Map<String, Object>> countAlarmsByMonthAndProduct() throws BusinessException;
}