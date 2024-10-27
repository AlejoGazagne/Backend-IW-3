package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IDriverBusiness {
    public Driver find(long id) throws NotFoundException, BusinessException;

    public Driver find(String driver) throws NotFoundException, BusinessException;

    public Driver add(Driver driver) throws FoundException, BusinessException;

    public Driver update(Driver driver) throws FoundException, NotFoundException, BusinessException;

    public Driver findOrCreate(Driver driver) throws BusinessException;
}
