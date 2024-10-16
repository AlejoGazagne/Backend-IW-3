package ar.edu.iw3.model.business;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.LoadDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoadDataBusiness implements ILoadDataBusiness {
    @Autowired
    private LoadDataRepository loadDataDAO;

    @Override
    public LoadData find(long id) throws NotFoundException, BusinessException {
        Optional<LoadData> loadData;
        try {
            loadData = loadDataDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(loadData.isEmpty()) {
            throw NotFoundException.builder().message("Truck not found, id = " + id).build();
        }
        return loadData.get();
    }

    @Override
    public LoadData add(LoadData loadData) throws FoundException, BusinessException {
        try {
            find(loadData.getId());
            throw FoundException.builder().message("Truck exist, id = " + loadData.getId()).build();
        } catch(NotFoundException ignored){
        }
        try {
            return loadDataDAO.save(loadData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public List<LoadData> list() throws BusinessException {
        try {
            return loadDataDAO.findAll();
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }
}
