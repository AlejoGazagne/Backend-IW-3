package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface ITruckBusiness {
    public Truck find(long id) throws NotFoundException, BusinessException;

    public Truck add(Truck truck) throws FoundException, BusinessException;
}
