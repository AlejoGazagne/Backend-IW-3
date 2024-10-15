package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Client;
import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class DriverBusiness implements IDriverBusiness{
    @Autowired
    private DriverRepository driverDAO;

    @Override
    public Driver find(long id) throws NotFoundException, BusinessException {
        Optional<Driver> driver;
        try {
            driver = driverDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(driver.isEmpty()) {
            throw NotFoundException.builder().message("Truck not found, id = " + id).build();
        }
        return driver.get();
    }

    @Override
    public Driver add(Driver driver) throws FoundException, BusinessException {
        try {
            find(driver.getId());
            throw FoundException.builder().message("Truck exist, id = " + driver.getId()).build();
        } catch(NotFoundException ignored){
        }
        try {
            return driverDAO.save(driver);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
