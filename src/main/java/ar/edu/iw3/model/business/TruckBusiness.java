package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Tank;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ITankBusiness;
import ar.edu.iw3.model.business.interfaces.ITruckBusiness;
import ar.edu.iw3.model.persistence.TankRepository;
import ar.edu.iw3.model.persistence.TruckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.SynthTextAreaUI;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TruckBusiness implements ITruckBusiness {
    @Autowired
    private TruckRepository truckDAO;

    @Autowired
    private ITankBusiness tankBusiness;

    @Override
    public Truck find(String externalId) throws NotFoundException, BusinessException {
        Optional<Truck> truck;
        try {
            truck = truckDAO.findByExternalId(externalId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(truck.isEmpty()) {
            throw NotFoundException.builder().message("Truck not found, external id = " + externalId).build();
        }
        return truck.get();
    }

    @Override
    public Truck find(Truck truck) throws NotFoundException, BusinessException {
        Optional<Truck> t;
        try {
            t = truckDAO.findByExternalId(truck.getExternalId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (t.isEmpty()) {
            throw NotFoundException.builder().message("Truck not found, external ID = " + truck.getExternalId()).build();
        }
        return t.get();
    }

    @Override
    public Truck add(Truck truck) throws FoundException, BusinessException {
        try {
            find(truck.getExternalId());
            throw FoundException.builder().message("Truck exist, id = " + truck.getExternalId()).build();
        } catch(NotFoundException ignored){
        }
        try {
            return truckDAO.save(truck);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Truck findOrCreate(Truck truck) throws BusinessException {
        Optional<Truck> tmp;
        try {
            tmp = truckDAO.findByExternalId(truck.getExternalId());
            return tmp.orElseGet(() -> truckDAO.save(truck));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al crear Truck.").build();
        }
    }

    @Override
    public Truck update(Truck truck) throws FoundException, NotFoundException, BusinessException {
        find(truck.getExternalId());
        Optional<Truck> truckExistente;
        try {
            truckExistente = truckDAO.findByExternalId(truck.getExternalId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (truckExistente.isPresent()) {
            throw FoundException.builder().message("Se encontró el Truck con id externo = " + truck.getExternalId() ).build();
        }
        try {
            return truckDAO.save(truck);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}
