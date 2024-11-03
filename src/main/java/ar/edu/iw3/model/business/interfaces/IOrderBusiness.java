package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.PasswordException;
import ar.edu.iw3.model.business.exceptions.StateException;
import ar.edu.iw3.model.business.exceptions.TruckloadException;

import java.util.List;

public interface IOrderBusiness {
    public Order find(long id) throws NotFoundException, BusinessException;

    public Order add(Order order) throws FoundException, BusinessException;

    public List<Order> list() throws BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public Order update(Order order) throws NotFoundException, BusinessException;

    public void firstWeighing(long id, float tare) throws NotFoundException, BusinessException, StateException;

    public Order addExternal(String json) throws BusinessException, FoundException;

    public void finalWeighing(long orderId, float finalWeight) throws NotFoundException, BusinessException, StateException;

    public Order beginTruckLoading(long id, LoadData loadData) throws BusinessException, NotFoundException, StateException, TruckloadException, FoundException;

    public Order validatePassword(long orderId, Integer password) throws BusinessException, NotFoundException, StateException, PasswordException;

    public Order finishTruckLoading(long id) throws BusinessException, NotFoundException, StateException;
}
