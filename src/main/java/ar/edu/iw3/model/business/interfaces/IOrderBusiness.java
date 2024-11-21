package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.PasswordException;
import ar.edu.iw3.model.business.exceptions.StateException;
import ar.edu.iw3.model.business.exceptions.TruckloadException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface IOrderBusiness {
    public Order find(String externalId) throws NotFoundException, BusinessException;

    public Order add(Order order) throws FoundException, BusinessException, NotFoundException;

    //public Order findById(long id) throws NotFoundException, BusinessException;

    public List<Order> list() throws BusinessException;

    public void delete(String externalId) throws NotFoundException, BusinessException;

    public Order update(Order order) throws NotFoundException, BusinessException;

    public void firstWeighing(String externalId, float tare) throws NotFoundException, BusinessException, StateException, PasswordException;

    public Order addExternal(String json) throws BusinessException, FoundException, NotFoundException;

    public Map<String, Object> finalWeighing(String externalId, float finalWeight) throws NotFoundException, BusinessException, StateException;

    public Map<String, Object> conciliationJson(Order order) throws NotFoundException, BusinessException, StateException;

    public byte[] conciliationPdf(String externalId) throws NotFoundException, BusinessException, StateException;

    public Order beginTruckLoading(long orderId, LoadData loadData) throws BusinessException, NotFoundException, StateException, TruckloadException, FoundException;

    public Order validatePassword(Integer password) throws BusinessException, NotFoundException, StateException, PasswordException;

    public void finishTruckLoading(long orderId) throws BusinessException, NotFoundException, StateException;

}
