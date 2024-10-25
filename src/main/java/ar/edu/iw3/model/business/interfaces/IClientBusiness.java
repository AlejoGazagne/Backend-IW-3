package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Client;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IClientBusiness {
    public Client find(long id) throws NotFoundException, BusinessException;

    public Client add(Client client) throws FoundException, BusinessException;
}
