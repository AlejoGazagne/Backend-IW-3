package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.TruckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TruckBusiness implements ITruckBusiness{
    @Autowired
    private TruckRepository truckDAO;

    @Override
    public Truck find(long id) throws NotFoundException, BusinessException {
        Optional<Truck> truck;
        try {
            truck = truckDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(truck.isEmpty()) {
            throw NotFoundException.builder().message("Truck not found, id = " + id).build();
        }
        return truck.get();
    }

    @Override
    public Truck add(Truck truck) throws FoundException, BusinessException {
        try {
            find(truck.getId());
            throw FoundException.builder().message("Truck exist, id = " + truck.getId()).build();
        } catch(NotFoundException ignored){
        }
        try {
            return truckDAO.save(truck);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
