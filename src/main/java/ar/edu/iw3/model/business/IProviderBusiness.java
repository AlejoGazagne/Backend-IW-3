package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Provider;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;


public interface IProviderBusiness {
    public List<Provider> list() throws BusinessException;

    public Provider load(long id) throws NotFoundException, BusinessException;

    public Provider load(String provider) throws NotFoundException, BusinessException;

    public Provider add(Provider provider) throws FoundException, BusinessException, NotFoundException;

    public Provider update(Provider provider) throws NotFoundException, BusinessException, FoundException;

    public void delete(Provider provider) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;
}
