package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface ITruckBusiness {
    public Truck find(long id) throws NotFoundException, BusinessException;

    public Truck add(Truck truck) throws FoundException, BusinessException;

    public Truck findOrCreate(Truck truck) throws BusinessException;

    public Truck update(Truck truck) throws NotFoundException, BusinessException, FoundException;
}
