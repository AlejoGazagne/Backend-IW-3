package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Provider;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProviderBusiness implements IProviderBusiness{

    @Autowired
    private ProviderRepository providerDAO;

    @Override
    public List<Provider> list() throws BusinessException {
        try {
            return providerDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Provider load(long id) throws NotFoundException, BusinessException {
        Optional<Provider> r;

        try {
            r = providerDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if(r.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Proveedor id = " + id).build();

        return r.get();
    }

    @Override
    public Provider load(String provider) throws NotFoundException, BusinessException {
        Optional<Provider> r;

        try {
            r = providerDAO.findByProvider(provider);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw BusinessException.builder().ex(e).build();
        }
        if(r.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Proveedor denominado " + provider).build();

        return r.get();
    }

//    @Override
//    public Provider add(Provider provider) throws FoundException, BusinessException {
//        try {
//            load(provider.getId());
//            throw FoundException.builder().message("Se encontró el proveedor id = " + provider.getId()).build();
//        } catch (NotFoundException e) {
//
//        }
//        try {
//            load(provider.getProvider());
//            throw FoundException.builder().message("Se encontró el proveedor " + provider.getProvider()).build();
//        } catch (NotFoundException e) {
//
//        }
//        try {
//            return providerDAO.save(provider);
//        } catch (Exception e) {
//            log.error(e.getMessage(),e);
//            throw BusinessException.builder().ex(e).build();
//        }
//    }

    @Override
    public Provider add(Provider provider) throws BusinessException, FoundException {

        try {
            load(provider.getId());
            throw FoundException.builder().message("Se encontró el proveedor id = " + provider.getId()).build();
        } catch (NotFoundException ignored) {

        }
        try {
            load(provider.getProvider());
            throw FoundException.builder().message("Se encontró el proveedor " + provider.getProvider()).build();
        } catch (NotFoundException ignored) {

        }

        try {
            return providerDAO.save(provider);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Provider update(Provider provider) throws NotFoundException, BusinessException, FoundException {
        load(provider.getId());
        Optional<Provider> r;
        try {
            r = providerDAO.findByProviderAndIdNot(provider.getProvider(), provider.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isPresent()){
            throw FoundException.builder().message("Se encontro un Provider con el nombre " + provider.getProvider()).build();
        }
        try {
            return providerDAO.save(provider);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(Provider provider) throws NotFoundException, BusinessException {
        delete(provider.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            providerDAO.deleteById(id);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}
