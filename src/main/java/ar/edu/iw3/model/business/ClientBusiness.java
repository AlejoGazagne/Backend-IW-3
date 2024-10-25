package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Client;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IClientBusiness;
import ar.edu.iw3.model.persistence.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ClientBusiness implements IClientBusiness {
    @Autowired
    private ClientRepository clientDAO;

    @Override
    public Client find(long id) throws NotFoundException, BusinessException {
        Optional<Client> client;
        try {
            client = clientDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(client.isEmpty()) {
            throw NotFoundException.builder().message("Client not found, id = " + id).build();
        }
        return client.get();
    }

    @Override
    public Client add(Client client) throws FoundException, BusinessException {
        try {
            find(client.getId());
            throw FoundException.builder().message("Client exist, id = " + client.getId()).build();
        } catch(NotFoundException ignored){
        }
        try {
            return clientDAO.save(client);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
