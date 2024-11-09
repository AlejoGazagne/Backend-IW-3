package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Client;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IClientBusiness {
    public Client find(String externalId) throws NotFoundException, BusinessException;

    public Client find(Client client) throws NotFoundException, BusinessException;

    public Client add(Client client) throws FoundException, BusinessException;

    public Client update(Client client) throws FoundException, NotFoundException, BusinessException;

    public Client findOrCreate(Client client) throws BusinessException;
}
