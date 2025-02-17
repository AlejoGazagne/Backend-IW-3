package ar.edu.iw3.model.business;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ILoadDataBusiness;
import ar.edu.iw3.model.persistence.LoadDataRepository;
import ar.edu.iw3.websockets.wrappers.LoadDataWsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
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
            throw NotFoundException.builder().message("LoadData not found, id = " + id).build();
        }
        return loadData.get();
    }

    @Override
    public LoadData add(LoadData loadData) throws FoundException, BusinessException {
        try {
            find(loadData.getId());
            throw FoundException.builder().message("LoadData exist, id = " + loadData.getId()).build();
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

    @Override
    public List<LoadData> list(long orderId) throws BusinessException {
        Optional<List<LoadData>> loadDatas;
        try {
            loadDatas = loadDataDAO.findByOrderId(orderId);

            if(loadDatas.isEmpty()) {
                throw NotFoundException.builder().message("LoadData not found, id = " + orderId).build();
            }
            return loadDatas.get();
        } catch (Exception e){
            log.error(e.getMessage());
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Double avgTemperature(long orderId) throws BusinessException, NotFoundException {
        return loadDataDAO.avgTemperature(orderId);
    }

    @Override
    public Double avgDensity(long orderId) throws BusinessException, NotFoundException {
        return loadDataDAO.avgDensity(orderId);
    }

    @Override
    public Double avgCaudal(long orderId) throws BusinessException, NotFoundException {
        return loadDataDAO.avgCaudal(orderId);
    }

    @Autowired
    SimpMessagingTemplate truckLoadws;


    public Order createLoadData(Timestamp currentTime, LoadData loadData, Order order) throws FoundException, BusinessException, NotFoundException{

        Optional<List<LoadData>> loadDataList = loadDataDAO.findByOrderId(order.getId());

        if (loadDataList.isPresent() && !loadDataList.get().isEmpty()){
            Date dateFinalCharge = order.getDateFinalCharge();
            if (checkFrequency(currentTime, dateFinalCharge)){
                loadData.setTimestampLoad(currentTime);
                loadData.setExternalId(order.getExternalId());
                
                add(loadData);
                order.setDateFinalCharge(currentTime);
                
                LoadDataWsWrapper loadDataWsWrapper = getLoadDataWsWrapper(loadData);

                truckLoadws.convertAndSend("/topic/loadTruck/data", loadDataWsWrapper);
            }

        }else{
            loadData.setTimestampLoad(currentTime);
            loadData.setOrder(order);
            loadData.setExternalId(order.getExternalId());

            add(loadData);
            order.setDateFinalCharge(currentTime);

            LoadDataWsWrapper loadDataWsWrapper = getLoadDataWsWrapper(loadData);
            truckLoadws.convertAndSend("/topic/loadTruck/data", loadDataWsWrapper);
        }

        return order;
    }
    
    private static LoadDataWsWrapper getLoadDataWsWrapper(LoadData loadData) {
        LoadDataWsWrapper loadDataWsWrapper = new LoadDataWsWrapper();
        loadDataWsWrapper.setId(loadData.getId());
        loadDataWsWrapper.setAccumulatedMass(loadData.getAccumulatedMass());
        loadDataWsWrapper.setDensity(loadData.getDensity());
        loadDataWsWrapper.setTemperature(loadData.getTemperature());
        loadDataWsWrapper.setCaudal(loadData.getCaudal());
        loadDataWsWrapper.setTimestampLoad(loadData.getTimestampLoad());
        loadDataWsWrapper.setOrderId(loadData.getOrder().getId());
        return loadDataWsWrapper;
    }

    private static final long FREQUENCY_MS = 5000;

    private boolean checkFrequency(Timestamp currentTime, Date lastFinalCharge){
        return currentTime.getTime() - lastFinalCharge.getTime() >= FREQUENCY_MS ;

    }

}
