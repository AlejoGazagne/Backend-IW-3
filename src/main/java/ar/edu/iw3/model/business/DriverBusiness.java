package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IDriverBusiness;
import ar.edu.iw3.model.persistence.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class DriverBusiness implements IDriverBusiness {
    @Autowired
    private DriverRepository driverDAO;

    @Override
    public Driver find(String externalId) throws NotFoundException, BusinessException {
        Optional<Driver> driver;
        try {
            driver = driverDAO.findByExternalId(externalId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(driver.isEmpty()) {
            throw NotFoundException.builder().message("Driver not found, id = " + externalId).build();
        }
        return driver.get();
    }

    @Override
    public Driver find(Driver driver) throws NotFoundException, BusinessException {
        Optional<Driver> d;
        try {
            d = driverDAO.findByExternalId(driver.getExternalId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(d.isEmpty()) {
            throw NotFoundException.builder().message("Driver not found, name = " + driver).build();
        }
        return d.get();
    }

    @Override
    public Driver add(Driver driver) throws FoundException, BusinessException {
        try {
            find(driver.getExternalId());
            throw FoundException.builder().message("Driver exist, id = " + driver.getExternalId()).build();
        } catch(NotFoundException ignored){
        }
        try {
            return driverDAO.save(driver);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Driver findOrCreate(Driver driver) throws BusinessException {
        Optional<Driver> tmp;
        try {
            tmp = driverDAO.findByExternalId(driver.getExternalId());
            return tmp.orElseGet(() -> driverDAO.save(driver));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al crear Driver.").build();
        }
    }

    @Override
    public Driver update(Driver driver) throws FoundException, NotFoundException, BusinessException {
        find(driver.getExternalId());

        try {
            return driverDAO.save(driver);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

}
