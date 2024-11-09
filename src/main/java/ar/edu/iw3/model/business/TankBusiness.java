package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Tank;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ITankBusiness;
import ar.edu.iw3.model.persistence.TankRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TankBusiness implements ITankBusiness {

    @Autowired
    private TankRepository tankDAO;

    @Override
    public Tank find(String externalId) throws NotFoundException, BusinessException {
        Optional<Tank> tank;
        try {
            tank = tankDAO.findByExternalId(externalId);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (tank.isEmpty()){
            throw NotFoundException.builder().message("Tank not found, external id = " + externalId).build();
        }
        return tank.get();
    }

    @Override
    public List<Tank> add(List<Tank> tanks) throws BusinessException, FoundException {
        try {
            for (Tank t : tanks){
                find(t.getExternalId());
                throw FoundException.builder().message("Tank exist, external id = " + t.getExternalId()).build();
            }
        } catch (NotFoundException ignored){
        }
        try {
            return tankDAO.saveAll(tanks);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Tank update(Tank tank) throws NotFoundException, BusinessException, FoundException {
        find(tank.getExternalId());
        Optional<Tank> t;
        try {
            t = tankDAO.findByExternalId(tank.getExternalId());
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (t.isPresent()) {
            throw FoundException.builder().message("Tank exist, external id = " + tank.getExternalId()).build();
        }
        try {
            return tankDAO.save(tank);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}
