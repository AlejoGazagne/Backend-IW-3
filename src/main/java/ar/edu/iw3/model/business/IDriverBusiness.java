package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IDriverBusiness {
    public Driver find(long id) throws NotFoundException, BusinessException;

    public Driver add(Driver driver) throws FoundException, BusinessException;
}
