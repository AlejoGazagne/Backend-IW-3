package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IAlarmBusiness {

    public List<Alarm> pendingAlarms() throws BusinessException;

    public Alarm add(Alarm alarm) throws BusinessException, FoundException;

    public Alarm find(Long id) throws BusinessException, NotFoundException;

    public Boolean isAlarmAccepted(Long id);
}